package org.respondeco.respondeco.service;

import com.mysema.query.types.ExpressionUtils;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.domain.QResourceMatch;
import org.respondeco.respondeco.domain.QResourceOffer;
import org.respondeco.respondeco.matching.MatchingEntity;
import org.respondeco.respondeco.matching.MatchingImpl;
import org.respondeco.respondeco.matching.MatchingTag;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.service.util.Assert;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Roman Kern on 15.11.14.
 * Definition for
 * Create/Update/Delete Resources Offer
 * Create/Update/Delete Resources Requirement
 * Manage new Tags
 */
@Service
public class ResourceService {

    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

    private static final String ERROR_REQUIREMENT_KEY = "resource.requirement.error";
    private static final String ERROR_OFFER_KEY = "resource.offer.error";

    private static final String ERROR_NO_ENTITY = "no_entity";
    private static final String ERROR_NO_PERMISSION = "no_permision";
    private static final String ERROR_EXISTING_MATCHES = "existing_matches";
    private static final String ERROR_AMOUNT_MODIFICATION = "amount_modification";
    private static final String ERROR_ORIGINAL_AMOUNT_MODIFICATION = "original_amount_modification";


    private ResourceOfferRepository resourceOfferRepository;
    private ResourceRequirementRepository resourceRequirementRepository;
    private ResourceTagService resourceTagService;
    private OrganizationRepository organizationRepository;
    private ProjectRepository projectRepository;
    private ResourceMatchRepository resourceMatchRepository;
    private ImageRepository imageRepository;
    private UserService userService;

    private ExceptionUtil.KeyBuilder requirementKey = new ExceptionUtil.KeyBuilder(ERROR_REQUIREMENT_KEY);
    private ExceptionUtil.KeyBuilder offerKey = new ExceptionUtil.KeyBuilder(ERROR_OFFER_KEY);

    @Inject
    public ResourceService(ResourceOfferRepository resourceOfferRepository,
                           ResourceRequirementRepository resourceRequirementRepository,
                           ResourceTagService resourceTagService,
                           OrganizationRepository organizationRepository,
                           ProjectRepository projectRepository,
                           ImageRepository imageRepository,
                           UserService userService,
                           ResourceMatchRepository resourceMatchRepository) {
        this.resourceOfferRepository = resourceOfferRepository;
        this.resourceRequirementRepository = resourceRequirementRepository;
        this.resourceTagService = resourceTagService;
        this.organizationRepository = organizationRepository;
        this.projectRepository = projectRepository;
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.resourceMatchRepository = resourceMatchRepository;
    }

    private void ensureUserIsPartOfOrganisation(Project project) throws ResourceNotFoundException {
        User user = userService.getUserWithAuthorities();
        if (project.getOrganization().getOwner() != user){
            throw new ResourceNotFoundException(String.format("Current user %s is not a part of Organisation or do not have enough rights for the operation", user.getLogin()));
        }
    }

    private void ensureUserIsPartOfOrganisation(Organization organization) throws ResourceNotFoundException {
        User user = userService.getUserWithAuthorities();
        if (organization.getOwner() != user){
            throw new ResourceNotFoundException(String.format("Current user %s is not a part of Organisation or do not have enough rights for the operation", user.getLogin()));
        }
    }

    /**
     * Creates, updates, or deletes ResourceRequirements of the existing project based on the updated requirements
     * if an updated requirement has no id, it will be created in the database
     * if an updated requirement has an id, it will be updated with the new values, only the name, the description
     * the logo, the tags and the isEssential flag can be updated on an existing requirement, changes to other
     * members will result in an error
     * if the project has requirements that are not present in the updated requirements, the requirements will be
     * deleted from the project if it has no existing matches
     * if a requirement which has matches is to be deleted, an exception is raised
     * @param existingProject an existing project, loaded from the database
     * @param updatedRequirements changed requirements
     * @return
     * @throws ResourceNotFoundException
     * @throws NoSuchEntityException
     */
    public List<ResourceRequirement> getUpdatedRequirements(Project existingProject,
                                                            List<ResourceRequirement> updatedRequirements)
        throws IllegalValueException {

        List<ResourceRequirement> results = new ArrayList<>();
        if(updatedRequirements == null) {
            deleteRequirements(existingProject.getResourceRequirements());
            return results;
        }
        List<ResourceRequirement> toDelete = existingProject.getResourceRequirements();
        log.debug("starting deletion of removed requirements, {}", toDelete);
        if(toDelete != null) {
            for(ResourceRequirement requirement : updatedRequirements) {
                if(requirement.getId() == null) {
                    continue;
                }
                toDelete.removeIf(
                    req -> req.getId().equals(requirement.getId())
                );
            }
        }
        log.debug("filtered removed requirements, deleting {}", toDelete);
        deleteRequirements(toDelete);

        for(ResourceRequirement requirement : updatedRequirements) {
            requirement.setProject(existingProject);
            ResourceRequirement updatedRequirement = null;
            if(requirement.getId() != null) {
                updatedRequirement = updateRequirement(requirement);
            } else {
                requirement.setOriginalAmount(requirement.getAmount());
                updatedRequirement = requirement;
            }
            updatedRequirement.setProject(existingProject);
            results.add(updatedRequirement);
        }

        results.forEach(
            result -> resourceRequirementRepository.save(result)
        );

        return results;
    }

    /**
     * Updates a Resource Requirement
     * @return updated Resource requirement
     * @throws org.respondeco.respondeco.service.exception.ResourceNotFoundException if resource requirement can't be found
     * @throws OperationForbiddenException if operation is forbidden
     * @throws NoSuchEntityException if project of the resource requirement can't be found
     */
    private ResourceRequirement updateRequirement(ResourceRequirement updatedRequirement)
        throws IllegalValueException {
        ResourceRequirement originalRequirement =
            this.resourceRequirementRepository.findByIdAndActiveIsTrue(updatedRequirement.getId());

        if(originalRequirement == null) {
            throw new NoSuchEntityException(
                requirementKey.from(ERROR_NO_ENTITY),
                String.format("A resource requirement with id %d does not exist.", updatedRequirement.getId()));
        }
        if(!originalRequirement.getProject().equals(updatedRequirement.getProject())) {
            throw new OperationForbiddenException(
                requirementKey.from(ERROR_NO_PERMISSION),
                "You have no permission to modify this resource"
            );
        }
        Assert.isEqualOrNull(originalRequirement.getOriginalAmount(), updatedRequirement.getOriginalAmount(),
            requirementKey.from(ERROR_ORIGINAL_AMOUNT_MODIFICATION),
            "The original amount of needed resources can not be changed");
        Assert.isEqualOrNull(originalRequirement.getAmount(), updatedRequirement.getAmount(),
            requirementKey.from(ERROR_AMOUNT_MODIFICATION),
            "The amount of needed resources can not be changed directly");
        originalRequirement.setName(updatedRequirement.getName());
        originalRequirement.setDescription(updatedRequirement.getDescription());
        originalRequirement.setLogo(updatedRequirement.getLogo());
        originalRequirement.setResourceTags(updatedRequirement.getResourceTags());
        originalRequirement.setIsEssential(updatedRequirement.getIsEssential());
        originalRequirement.setResourceTags(updatedRequirement.getResourceTags());

        return originalRequirement;
    }

    private void deleteRequirements(List<ResourceRequirement> requirements) {
        if(requirements == null) {
            return;
        }
        for(ResourceRequirement requirement : requirements) {
            Assert.isEmpty(requirement.getResourceMatches(), requirementKey.from(ERROR_EXISTING_MATCHES),
                String.format("Cannot delete requirement %s with id %d, there are existing matches",
                    requirement.getName(), requirement.getId()));
            log.debug("calling ResourceRequirementRepository#save() with argument {}", requirement);
            requirement.setActive(false);
            resourceRequirementRepository.save(requirement);
        }
    }

    /**
     * Delete the resource requirement with id
     * @param id id of the resource requirement
     * @throws org.respondeco.respondeco.service.exception.ResourceNotFoundException if the resource requirement can't be found
     */
    public void deleteRequirement(Long id) throws ResourceNotFoundException {
        ResourceRequirement requirement = this.resourceRequirementRepository.findByIdAndActiveIsTrue(id);
        if (requirement != null) {
            ensureUserIsPartOfOrganisation(requirement.getProject());
            requirement.setActive(false);
            resourceRequirementRepository.save(requirement);
        } else {
            throw new ResourceNotFoundException(String.format("No resource requirement found for the id: %d", id));
        }
    }

    /**
     * Return all Resource Requirements
     * @return list or resource requirements
     */
    public List<ResourceRequirement> getRequirements() {
        return resourceRequirementRepository.findByActiveIsTrue();
    }

    /**
     * Get all ResourceRequirements for a specific project given by id
     * @param projectId
     * @return List of ResourceRequirements
     */
    public List<ResourceRequirement> getRequirementsForProject(Long projectId) {
        return this.resourceRequirementRepository.findByProjectIdAndActiveIsTrue(projectId);
    }

    /**
     * Create a new ResourceOffer
     * @return created ResourceOffer
     */
    public ResourceOffer createOffer(ResourceOffer newOffer)
        throws NoSuchEntityException {
        Organization organization = organizationRepository.findByIdAndActiveIsTrue(newOffer.getOrganization().getId());
        if(organization == null) {
            throw new NoSuchEntityException(newOffer.getOrganization().getId());
        }
        if(organization.getVerified() == false) {
            throw new OperationForbiddenException("Organization (id: " + newOffer.getOrganization().getId() + ") not verified");
        }

        newOffer.setResourceTags(resourceTagService.getOrCreateTags(newOffer.getResourceTags()));
        this.resourceOfferRepository.save(newOffer);

        return newOffer;
    }

    /**
     *
     * @return updated Resource Offer
     * @throws org.respondeco.respondeco.service.exception.ResourceNotFoundException if resource offer with id can't be found
     * @throws ResourceTagException
     * @throws ResourceJoinTagException
     */
    public ResourceOffer updateOffer(ResourceOffer updatedOffer)
        throws IllegalValueException {
        ResourceOffer currentOffer = this.resourceOfferRepository.findByIdAndActiveIsTrue(updatedOffer.getId());

        if (currentOffer != null) {
            ensureUserIsPartOfOrganisation(organizationRepository.findByIdAndActiveIsTrue(
                updatedOffer.getOrganization().getId()));

            currentOffer.setName(updatedOffer.getName());
            currentOffer.setAmount(updatedOffer.getAmount());
            currentOffer.setDescription(updatedOffer.getDescription());
            currentOffer.setIsCommercial(updatedOffer.getIsCommercial());
            currentOffer.setPrice(updatedOffer.getPrice());
            currentOffer.setStartDate(updatedOffer.getStartDate());
            currentOffer.setEndDate(updatedOffer.getEndDate());
            currentOffer.setResourceTags(resourceTagService.getOrCreateTags(updatedOffer.getResourceTags()));
            this.resourceOfferRepository.save(currentOffer);
        }
        else{
            throw new ResourceNotFoundException(
                String.format("No resource offer found for the id: %d", updatedOffer.getId()));
        }

        return currentOffer;
    }

    /**
     * Delete ResourceOffer
     * @param offerId id of resourceOffer
     * @throws org.respondeco.respondeco.service.exception.ResourceNotFoundException if offer can't be found
     */
    public void deleteOffer(Long offerId) throws IllegalValueException {
        ResourceOffer resourceOffer = resourceOfferRepository.findByIdAndActiveIsTrue(offerId);

        if(resourceOffer == null) {
            throw new ResourceNotFoundException(String.format("No resource offer found for the id: %d", offerId));
        }

        resourceOffer.setActive(false);
        resourceOfferRepository.save(resourceOffer);
    }

    /**
     * Get all ResourceOffers, filtered by searchField (name or organization or tags) and isCommercial
     * @param searchField contains search parameters for resource name, organization name and tags
     * @param isCommercial if true return only commercial resources, if false return only non commercial ones
     * @param pageable A pageable to be used to query the database
     * @return List of active ResourceOffers filtered by set parameters. (searchField, isCommercial)
     */
    public Page<ResourceOffer> getOffers(String searchField, Boolean isCommercial, Pageable pageable) {
        User user = userService.getUserWithAuthorities();

        Page page;

        if(searchField.isEmpty() && isCommercial == null) {
            if (user.getOrganization() != null) {
                page = resourceOfferRepository.findByOrganizationNotAndActiveIsTrue(user.getOrganization(), null);
            } else {
                page = resourceOfferRepository.findByActiveIsTrue(pageable);
            }
        } else {
            String[] searchValues = searchField.split(" ");
            //create dynamic query with help of querydsl
            QResourceOffer resourceOffer = QResourceOffer.resourceOffer;

            List<Predicate> resourceNameOrOrgnameOrTagLike = new ArrayList<>();
            for(String sval : searchValues) {
                sval = sval.trim();
                if(sval.length() == 0) {
                    continue;
                }
                resourceNameOrOrgnameOrTagLike.add(resourceOffer.name.containsIgnoreCase(sval));
                resourceNameOrOrgnameOrTagLike.add(resourceOffer.resourceTags.any().name.containsIgnoreCase(sval));
                resourceNameOrOrgnameOrTagLike.add(resourceOffer.organization.name.containsIgnoreCase(sval));
            }

            BooleanExpression resourceCommercial = null;
            BooleanExpression isActive = resourceOffer.active.isTrue();

            if(isCommercial!=null && isCommercial == true) {
                resourceCommercial = resourceOffer.isCommercial.eq(true);
            } else if(isCommercial!=null && isCommercial == false) {
                resourceCommercial = resourceOffer.isCommercial.eq(false);
            }

            Predicate predicateAnyOf = ExpressionUtils.anyOf(resourceNameOrOrgnameOrTagLike);
            Predicate where = ExpressionUtils.allOf(predicateAnyOf, resourceCommercial, isActive);

            // if a user is currently logged in
            if (user.getOrganization() != null) {
                // remove all offers of the organization the user is in
                where = ExpressionUtils.and(where, resourceOffer.organization.eq(user.getOrganization()).not());
            }

            page = resourceOfferRepository.findAll(where, new PageRequest(0, 10000));

            log.debug("TOTALELEMENTS: " + page.getTotalElements());
            log.debug("TOTALPAGES: " + page.getTotalPages());
        }

        return orderByProbability(page, pageable);
    }

    private <T extends MatchingEntity> Page<T> orderByProbability(Page<T> page, Pageable pageable) {
        User user = userService.getUserWithAuthorities();
        List<T> entities;

        // check if a user is currently signed in
        if (user.getOrganization() != null) {
            Set<MatchingTag> matchingTags = new HashSet<>();
            Set<MatchingEntity> matchingEntities = new HashSet<>();

            Long orgId = user.getOrganization().getId();
            Page<Project> organizations = projectRepository.findByOrganization(orgId, new RestParameters(0, 100000).buildPageRequest());

            if (organizations != null) {
                List<Project> projects = organizations.getContent().stream().filter(p -> !p.isConcrete() || p.getStartDate().isBefore(LocalDate.now()) == false).collect(Collectors.toList());

                // add all projects of the organization to the matchingEntities
                // only add projects which are currently active and haven't started yet (projects which aren't concrete or the start date is in the future)
                matchingEntities.addAll(projects);

                // add all resource requests of all projects of the organization
                matchingEntities.addAll(
                    projects
                        .stream()
                        .map(p -> getRequirementsForProject(p.getId()))
                        .reduce(new ArrayList<ResourceRequirement>(), (a, b) -> {
                            a.addAll(b);
                            return a;
                        })
                );

                matchingEntities.forEach(p -> {
                    matchingTags.addAll(p.getTags());
                });

                // filter duplicates by lower case name
                TreeSet<MatchingTag> collect = matchingTags.stream().distinct().collect(Collectors.toCollection(TreeSet::new));

                Set<MatchingEntity> setToOrder = new HashSet<>(page.getContent());

                MatchingImpl matching = new MatchingImpl();
                matching.setEntities(matchingEntities);
                matching.setTags(collect);
                matching.setAPriori(1);
                entities = (List<T>) matching.evaluate(setToOrder).stream().map(p -> p.getMatchingEntity()).collect(Collectors.toList());

                int from = pageable.getPageNumber() * pageable.getPageSize();
                int to = from + pageable.getPageSize();

                if (to > entities.size()) {
                    to -= (to - entities.size());
                }

                List<T> pagedEntities = new ArrayList<>(entities.subList(from, to));

                page = new PageImpl<>(pagedEntities, pageable, entities.size());
            }
        }

        return page;
    }

    /**
     * Get all ResourceOffers for a specific organization given by id
     * @param organizationId Organization id
     * @return List of ResourceOffers
     */
    public List<ResourceOffer> getOffersForOrganization(Long organizationId) {
        List<ResourceOffer> entries = this.resourceOfferRepository.findByOrganizationIdAndActiveIsTrue(organizationId);

        return entries;
    }

    /**
     * Get ResourceOffer by given id
     * @param id resourceOffer id
     * @return ResourceOffer
     */
    public ResourceOffer getOfferById(Long id) throws NoSuchEntityException {
        ResourceOffer resourceOffer = resourceOfferRepository.getOne(id);
        if (!resourceOffer.isActive()) {
            throw new NoSuchEntityException("resource with given id is not active");
        }

        return resourceOffer;
    }

    /**
     * Creates a new ResourceMatch for claiming a ResourceOffer
     * @param resourceOfferId id of the claimed resourceoffer
     * @param resourceRequirementId id of the resourcerequirement, where the resourceoffer is used.
     * @return created ResourceMatch for the claimed ResourceOffer
     */
    public ResourceMatch createClaimResourceRequest(Long resourceOfferId, Long resourceRequirementId)
        throws IllegalValueException {

        ResourceMatch resourceMatch = new ResourceMatch();

        ResourceOffer resourceOffer = resourceOfferRepository.findByIdAndActiveIsTrue(resourceOfferId);
        if(resourceOffer == null) {
            throw new IllegalValueException(
                "resource.errors.offernotfound",
                String.format("No resource offer with id %s found", resourceOfferId.toString())
            );
        }

        Organization organization = resourceOffer.getOrganization();

        if(organization == null) {
            throw new IllegalValueException(
                "resource.errors.offer.noorganization",
                String.format("No organization for resource offer %s found", resourceOffer.toString())
            );
        }

        //check for authorization
        //checkAuthoritiesForResourceMatch(organization);

        ResourceRequirement resourceRequirement = resourceRequirementRepository.findByIdAndActiveIsTrue(resourceRequirementId);
        if(resourceRequirement == null) {
            throw new IllegalValueException(
                "resource.errors.norequirement",
                String.format("no resourceRequirement with id %s found", resourceRequirementId.toString())
            );
        }

        Project project = resourceRequirement.getProject();
        if(project == null) {
            throw new IllegalValueException(
                "resource.errors.norequirementproject",
                String.format("No project for resourceRequirement %s found", resourceRequirement.toString())
            );
        }

        if(project.getOrganization().getId().equals(organization.getId())) {
            throw new IllegalValueException(
                "resource.errors.claimownres",
                "cannot claim own resource offer " + resourceOffer.toString()
            );
        }

        List<ResourceMatch> result = resourceMatchRepository
            .findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(resourceOffer,
            resourceRequirement, organization, project);

        if(result.isEmpty()) {
            resourceMatch.setResourceOffer(resourceOffer);
            resourceMatch.setResourceRequirement(resourceRequirement);
            resourceMatch.setOrganization(organization);
            resourceMatch.setProject(project);
            resourceMatch.setMatchDirection(MatchDirection.ORGANIZATION_CLAIMED);

            resourceMatchRepository.save(resourceMatch);
        } else {
            throw new IllegalValueException("resource.errors.matchexists", "match already exists");
        }

        return resourceMatch;
    }

    /**
     * Check if user is authorized to answer a ResourceRequest
     * @param organization
     * @return
     * @throws OperationForbiddenException
     */
    private void checkAuthoritiesForResourceMatch(Organization organization) throws OperationForbiddenException{
        User user = userService.getUserWithAuthorities();
        if(user == null) {
            throw new OperationForbiddenException("no current user found");
        }

        if(user.getOrganization() == null) {
            throw new OperationForbiddenException("user is no member of any organization");
        }

        //has to be the owner of the project's organization or the project manager
        if(!user.equals(organization.getOwner())) {
            throw new OperationForbiddenException("user needs to be the owner of the organization");
        }
    }

    /**
     * Accept or decline the resource request
     * @param resourceMatchId resourceMatch id
     * @param accept true if accepted, false if declined
     * @return accepted or declined ResourceMatch
     */
    public ResourceMatch answerResourceRequest(Long resourceMatchId, boolean accept)
        throws IllegalValueException {

        ResourceMatch resourceMatch = resourceMatchRepository.findOne(resourceMatchId);

        if(resourceMatchId == null) {
            throw new IllegalValueException("resourcematch.error.idnull", "Resourcematch id must not be null");
        }
        if(resourceMatch == null) {
            throw new NoSuchEntityException(resourceMatchId);
        }

        Project project = resourceMatch.getProject();
        Organization organization = resourceMatch.getOrganization();

        if(project == null) {
            throw new NoSuchEntityException("can't find project for match with matchId " + resourceMatchId);
        }

        User user = userService.getUserWithAuthorities();

        //check if user authorized to change data
        if(resourceMatch.getMatchDirection() == MatchDirection.ORGANIZATION_CLAIMED) {
            checkAuthoritiesForResourceMatch(organization);
        }else{
            ensureUserIsPartOfOrganisation(project);
        }

        // set the accepted flag
        resourceMatch.setAccepted(accept);

        if(accept) {
            ResourceOffer offer = resourceMatch.getResourceOffer();
            if(offer == null) {
                throw new IllegalValueException("resourcematch.error.noresourceofferfound", "resourcematch has no resourceoffer: "+ resourceMatch);
            }
            ResourceRequirement req = resourceMatch.getResourceRequirement();
            if(req == null) {
                throw new IllegalValueException("resourcematch.error.noresourcerequirementfound","resourcematch has no resourcerequirement: "+ resourceMatch);
            }

            //check if resourceOffer amount is completely consumed by resourceRequirement
            //offer greater req amount -> keep offer active and lower amount
            if (offer.getAmount().compareTo(req.getAmount()) == 1) {
                offer.setAmount(offer.getAmount().subtract(req.getAmount()));
                resourceMatch.setAmount(req.getAmount());

            } else {
                //requirement consumes offer -> offer is no longer active
                resourceMatch.setAmount(offer.getAmount());
                offer.setActive(false);

                //actualize requirement amounts
                req.setAmount(req.getAmount().subtract(offer.getAmount()));
            }

            resourceOfferRepository.save(offer);
        }

        return resourceMatchRepository.save(resourceMatch);
    }

    /**
     * Find accepted ResourceMatches for project with given project id
     * @param projectId project id
     * @return List of Resource Matches which were accepted
     */
    public List<ResourceMatch> getResourceMatchesForProject(Long projectId) {
        return resourceMatchRepository.findByProjectIdAndAcceptedIsTrueAndActiveIsTrue(projectId);
    }


    /**
     * Get Resources for specific Organization with given id. (Claim/Apply Resource)
     * @param organizationId Organization id
     * @param pageable pageable to be used for retrieving entries from the database
     * @return List of ResourceMatches representing open resource claims/applies
     */
    @Transactional(readOnly=true)
    public List<ResourceMatch> getResourcesForOrganization(Long organizationId, Pageable pageable) {
        /// first of all, we have here organization that offered a resource for projects.
        QResourceMatch resourceMatch = QResourceMatch.resourceMatch;
        Collection<Long> projects = projectRepository.findByOrganizationId(organizationId);
        BooleanExpression exp1 = resourceMatch.project.id.in(projects);
        BooleanExpression exp2 = resourceMatch.active.isTrue();
        BooleanExpression exp3 = resourceMatch.matchDirection.eq(MatchDirection.ORGANIZATION_OFFERED);

        Predicate where = ExpressionUtils.allOf(exp1, exp2, exp3);
        List<ResourceMatch> first = resourceMatchRepository.findAll(where, pageable).getContent();

        //and here we have a Project that claim the resource from organization
        exp1 = resourceMatch.organization.id.eq(organizationId);
        exp3 = resourceMatch.matchDirection.eq(MatchDirection.ORGANIZATION_CLAIMED);
        where = ExpressionUtils.allOf(exp1, exp2, exp3);
        //we need to join both list
        List<ResourceMatch> second = resourceMatchRepository.findAll(where, pageable).getContent();
        ArrayList result = new ArrayList<ResourceMatch>();
        result.addAll(first);
        result.addAll(second);
        //List<ResourceMatch> result = resourceMatchRepository.findByOrganizationId(organizationId);

        return result;
    }

    // endregion

    /**
     * Create new Offer from organization for a specific requirement in project. Make it Transactional, for that no
     * one can write more than amount of the requirement
     * @param offerId from Organization
     * @param requirementId from project
     * @param organizationId that offer an resource
     * @param projectId that claim for offer
     * @return ResourceMatch Entity
     */
    public ResourceMatch createProjectApplyOffer(Long offerId, Long requirementId,
                                                 Long organizationId, Long projectId) throws IllegalValueException {

        ResourceMatch resourceMatch = new ResourceMatch();

        // get the linked property partners
        ResourceOffer resourceOffer = resourceOfferRepository.findByIdAndActiveIsTrue(offerId);
        ResourceRequirement resourceRequirement = resourceRequirementRepository.findByIdAndActiveIsTrue(requirementId);
        Organization organization = organizationRepository.findByIdAndActiveIsTrue(organizationId);
        Project project = projectRepository.findByIdAndActiveIsTrue(projectId);

        List<ResourceMatch> hasData = resourceMatchRepository.
            findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(resourceOffer,
                resourceRequirement, organization, project);

        // Check if user authorized
        ensureUserIsPartOfOrganisation(organization);

        if(!hasData.isEmpty()){
            throw new OperationForbiddenException("resourcematch.error.projectapply.offeralreadyexists", "Current offer has already been donated before");
        }

        if(organization == project.getOrganization()){
            throw new OperationForbiddenException("resourcematch.error.projectapply.ownproject", "Organization cannot offer resources to own project");
        }
        //,check if we need new apply
        if(resourceRequirement.getAmount().equals(BigDecimal.ZERO)){
            throw new OperationForbiddenException("resourcematch.error.projectapply.requestfulfilled", "Requirements are already fulfilled");
        }

        if(resourceOffer.getAmount().equals(BigDecimal.ZERO)){
            throw new OperationForbiddenException("resourcematch.error.projectapply.offerdepleted", "Current offer already depleted");
        }

        BigDecimal amount;

        // if the offer has more items than the requirement --> use the requirement amount
        if (resourceOffer.getAmount().compareTo(resourceRequirement.getAmount()) == 1) {
            amount = resourceRequirement.getAmount();
            /* apply offer should be manually.
            resourceOffer.setAmount(resourceOffer.getAmount().subtract(resourceRequirement.getAmount()));
            resourceRequirement.setAmount(new BigDecimal(0));
            */
        } else {
            amount = resourceOffer.getAmount();
            /* apply offer should be manually
            resourceRequirement.setAmount(resourceRequirement.getAmount().subtract(resourceOffer.getAmount()));
            resourceOffer.setAmount(new BigDecimal(0));*/
        }

        resourceMatch.setResourceOffer(resourceOffer);
        resourceMatch.setResourceRequirement(resourceRequirement);
        resourceMatch.setOrganization(organization);
        resourceMatch.setProject(project);
        resourceMatch.setAmount(amount);
        resourceMatch.setMatchDirection(MatchDirection.ORGANIZATION_OFFERED);

        resourceMatchRepository.save(resourceMatch);

        return resourceMatch;
    }


    /**
     * Find all donated Resources of an organization
     * @param orgId organization id for which the donated resources should be found
     * @param restParameters restParameters used for building the page request
     * @return Page containing ResourceMatches which represent donated resources
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if organization with id cannot be found
     */
    public Page<ResourceMatch> getDonatedResourcesForOrganization(Long orgId, RestParameters restParameters)
        throws NoSuchEntityException {

        PageRequest pageRequest = null;

        if (restParameters != null) {
            pageRequest = restParameters.buildPageRequest();
        }

        Organization organization = organizationRepository.findOne(orgId);
        if (organization == null) {
            throw new NoSuchEntityException("organization with id " + orgId + "can't be found");
        }

        Page<ResourceMatch> resourceMatches =
            resourceMatchRepository.findByOrganizationAndAcceptedIsTrueAndActiveIsTrue(organization, pageRequest);

        return resourceMatches;
    }

    public Page<ResourceMatch> getSupportedProjectsForOrganization(Long orgId, RestParameters restParameters)
        throws NoSuchEntityException {

        PageRequest pageRequest = null;

        if (restParameters != null) {
            pageRequest = restParameters.buildPageRequest();
        }

        Organization organization = organizationRepository.findOne(orgId);
        if (organization == null) {
            throw new NoSuchEntityException("organization with id " + orgId + "can't be found");
        }

        Page<ResourceMatch> resourceMatches =
            resourceMatchRepository.findByOrganizationAndAcceptedIsTrueAndActiveIsTrue(organization, pageRequest);

        return resourceMatches;

    }


}
