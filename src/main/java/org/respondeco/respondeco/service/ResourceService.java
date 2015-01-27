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
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.respondeco.respondeco.web.rest.util.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    private final Logger log = LoggerFactory.getLogger(ResourceService.class);
    private ResourceOfferRepository resourceOfferRepository;
    private ResourceRequirementRepository resourceRequirementRepository;
    private ResourceTagService resourceTagService;
    private OrganizationRepository organizationRepository;
    private ProjectRepository projectRepository;
    private ResourceMatchRepository resourceMatchRepository;
    private ImageRepository imageRepository;

    private UserService userService;

    private RestUtil restUtil;

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
        this.restUtil = new RestUtil();
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
     * Create a new ResourceRequirement
     * @param name name of Resource Requirement
     * @param amount amount of Resource Requirement
     * @param description description of Resource Requirement
     * @param projectId project id belonging to the Resource Requirement
     * @param isEssential true if requirement is essential for the project, false otherwise
     * @param resourceTags defined tags for the resource requirement
     * @return saved ResourceRequirement created resource requirement
     * @throws org.respondeco.respondeco.service.exception.ResourceNotFoundException if the resource can't be found
     * @throws NoSuchProjectException if project of the resource can't be found
     */
    public ResourceRequirement createRequirement(String name, BigDecimal amount, String description,
                                                 Long projectId, Boolean isEssential, List<String> resourceTags)
        throws ResourceNotFoundException, NoSuchProjectException {
        ResourceRequirement newRequirement = null;

        Project project = projectRepository.findByIdAndActiveIsTrue(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }

        ensureUserIsPartOfOrganisation(project);
        List<ResourceRequirement> entries = resourceRequirementRepository.findByNameAndProjectAndActiveIsTrue(name, project);
        if (entries == null || entries.isEmpty() == true) {
            newRequirement = new ResourceRequirement();
            newRequirement.setName(name);
            newRequirement.setOriginalAmount(amount);
            newRequirement.setAmount(amount);
            newRequirement.setDescription(description);
            newRequirement.setProject(project);
            newRequirement.setIsEssential(isEssential);
            newRequirement.setResourceTags(resourceTagService.getOrCreateTags(resourceTags));
            resourceRequirementRepository.save(newRequirement);
        } else {
            throw new ResourceNotFoundException(
                String.format("Requirement with description '%s' for the Project %d already exists",
                    description, project.getId()));
        }

        return newRequirement;
    }

    /**
     * Updates a Resource Requirement
     * @param id id of required resource
     * @param name name of required resource
     * @param amount amount of required resource
     * @param description description of required resource
     * @param projectId id of the project which contains the resource
     * @param isEssential true if resource is essential for the project, false otherwise
     * @param resourceTags tags of the resource
     * @return updated Resource requirement
     * @throws org.respondeco.respondeco.service.exception.ResourceNotFoundException if resource requirement can't be found
     * @throws OperationForbiddenException if operation is forbidden
     * @throws NoSuchProjectException if project of the resource requirement can't be found
     */
    public ResourceRequirement updateRequirement(Long id, String name, BigDecimal amount, String description,
                                                 Long projectId, Boolean isEssential, List<String> resourceTags)
        throws ResourceNotFoundException, OperationForbiddenException, NoSuchProjectException, IllegalValueException {
        ResourceRequirement requirement = this.resourceRequirementRepository.findByIdAndActiveIsTrue(id);

        Project project = projectRepository.findByIdAndActiveIsTrue(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        if (project.equals(requirement.getProject()) == false) {
            throw new OperationForbiddenException("cannot modify resource requirements of other projects");
        }
        if (requirement != null) {
            ensureUserIsPartOfOrganisation(requirement.getProject());
            BigDecimal matchSum = new BigDecimal(0);
            if(requirement.getResourceMatches() != null) {
                for (ResourceMatch match : requirement.getResourceMatches()) {
                    matchSum = matchSum.add(match.getAmount());
                }
            }
            log.debug("MATCH SUM = " + matchSum);
            requirement.setName(name);
            requirement.setDescription(description);
            requirement.setIsEssential(isEssential);
            requirement.setResourceTags(resourceTagService.getOrCreateTags(resourceTags));

            if(amount.subtract(matchSum).longValue() < 0L) {
                throw new IllegalValueException("resource.errors.update.negative",
                    "New amount is too low, a higher amount of resources was alredy donated");
            }
            requirement.setAmount(amount.subtract(matchSum));
            requirement.setOriginalAmount(amount);
            resourceRequirementRepository.save(requirement);

        } else {
            throw new ResourceNotFoundException(String.format("No resource requirement found for the id: %d", id));
        }

        return requirement;
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
    public List<ResourceRequirement> getAllRequirements() {
        return resourceRequirementRepository.findByActiveIsTrue();
    }

    /**
     * Get all ResourceRequirements for a specific project given by id
     * @param projectId
     * @return List of ResourceRequirements
     */
    public List<ResourceRequirement> getAllRequirements(Long projectId) {
        List<ResourceRequirement> entries = this.resourceRequirementRepository.findByProjectIdAndActiveIsTrue(projectId);

        return entries;
    }

    /**
     * Create a new ResourceOffer
     * @param name ResourceOffer name
     * @param amount ResourceOffer amount
     * @param description ResourceOffer description
     * @param organizationId organization id which created the ResourceOffer
     * @param isCommercial true if ResourceOffer is a commercial Resource, false otherwise
     * @param startDate available at startDate
     * @param endDate available until endDate
     * @param resourceTags Tags describing the ResourceOffer
     * @param logoId id of the resource logo
     * @param price
     * @return created ResourceOffer
     */
    public ResourceOffer createOffer(String name, BigDecimal amount, String description, Long organizationId,
                                     Boolean isCommercial, LocalDate startDate,
                                     LocalDate endDate, List<String> resourceTags, Long logoId, BigDecimal price)
        throws NoSuchOrganizationException, OrganizationNotVerifiedException {
        Organization organization = organizationRepository.findByIdAndActiveIsTrue(organizationId);
        if(organization == null) {
            throw new NoSuchOrganizationException(organizationId);
        }
        if(organization.getVerified() == false) {
            throw new OrganizationNotVerifiedException(organizationId);
        }
        ResourceOffer newOffer = new ResourceOffer();
        newOffer.setName(name);
        newOffer.setAmount(amount);
        newOffer.setOriginalAmount(amount);
        newOffer.setDescription(description);
        newOffer.setOrganization(organization);
        newOffer.setIsCommercial(isCommercial);
        newOffer.setPrice(price);
        newOffer.setStartDate(startDate);
        newOffer.setEndDate(endDate);
        if(logoId != null) {
            newOffer.setLogo(imageRepository.findOne(logoId));
        }

        newOffer.setResourceTags(resourceTagService.getOrCreateTags(resourceTags));
        this.resourceOfferRepository.save(newOffer);

        return newOffer;
    }

    /**
     *
     * @param offerId id of the resource offer
     * @param organisationId organization id of the resource offer
     * @param name name of the resource offer
     * @param amount amount of the resource
     * @param description description of the resource offer
     * @param isCommercial true if the resource is a commercial resource, false otherwise
     * @param startDate available from
     * @param endDate available until
     * @param resourceTags tags belonging to the resource
     * @param logoId id of the resource logo
     * @param price
     * @return updated Resource Offer
     * @throws org.respondeco.respondeco.service.exception.ResourceNotFoundException if resource offer with id can't be found
     * @throws ResourceTagException
     * @throws ResourceJoinTagException
     */
    public ResourceOffer updateOffer(Long offerId, Long organisationId, String name, BigDecimal amount,
                                     String description, Boolean isCommercial,
                                     LocalDate startDate, LocalDate endDate, List<String> resourceTags, Long logoId, BigDecimal price)
        throws IllegalValueException {
        ResourceOffer offer = this.resourceOfferRepository.findByIdAndActiveIsTrue(offerId);

        if (offer != null) {
            ensureUserIsPartOfOrganisation(organizationRepository.findByIdAndActiveIsTrue(organisationId));

            offer.setName(name);
            offer.setAmount(amount);
            offer.setDescription(description);
            offer.setIsCommercial(isCommercial);
            offer.setPrice(price);
            offer.setStartDate(startDate);
            offer.setEndDate(endDate);
            offer.setResourceTags(resourceTagService.getOrCreateTags(resourceTags));
            if(logoId != null) {
                offer.setLogo(imageRepository.findOne(logoId));
            }
            this.resourceOfferRepository.save(offer);
        }
        else{
            throw new ResourceNotFoundException(String.format("No resource offer found for the id: %d", offerId));
        }

        return offer;
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
     * @param restParameters Rest Parameters to be set
     * @return List of active ResourceOffers filtered by set parameters. (searchField, isCommercial)
     */
    public Page<ResourceOffer> getAllOffers(String searchField, Boolean isCommercial, RestParameters restParameters) {
        User user = userService.getUserWithAuthorities();

        PageRequest pageRequest = null;
        Page page;

        if(restParameters != null) {
            pageRequest = restParameters.buildPageRequest();
        }

        if(searchField.isEmpty() && isCommercial == null) {
            if (user.getOrganization() != null) {
                page = resourceOfferRepository.findByOrganizationNotAndActiveIsTrue(user.getOrganization(), null);
            } else {
                page = resourceOfferRepository.findByActiveIsTrue(pageRequest);
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

        return orderByProbability(page, pageRequest);
    }

    private <T extends MatchingEntity> Page<T> orderByProbability(Page<T> page, PageRequest pageRequest) {
        User user = userService.getUserWithAuthorities();
        List<T> entities;

        // check if a user is currently signed in
        if (user.getOrganization() != null) {
            Set<MatchingTag> matchingTags = new HashSet<>();
            Set<MatchingEntity> matchingEntities = new HashSet<>();

            Long orgId = user.getOrganization().getId();
            Page<Project> organizations = projectRepository.findByOrganization(orgId, new RestParameters(0, 100000).buildPageRequest());

            if (organizations != null) {
                List<Project> projects = organizations.getContent();

                // add all projects of the organization to the matchingEntities
                // only add projects which are currently active and haven't started yet (projects which aren't concrete or the start date is in the future)
                matchingEntities.addAll(projects.stream().filter(p -> !p.isConcrete() || p.getStartDate().isBefore(LocalDate.now()) == false).collect(Collectors.toList()));

                // add all resource requests of all projects of the organization
                matchingEntities.addAll(
                    projects
                        .stream()
                        .map(p -> getAllRequirements(p.getId()))
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

                int from = pageRequest.getPageNumber() * pageRequest.getPageSize();
                int to = from + pageRequest.getPageSize();

                if (to > entities.size()) {
                    to -= (to - entities.size());
                }

                List<T> pagedEntities = new ArrayList<>(entities.subList(from, to));

                page = new PageImpl<>(pagedEntities, pageRequest, entities.size());
            }
        }

        return page;
    }

    /**
     * Get all ResourceOffers for a specific organization given by id
     * @param organizationId Organization id
     * @return List of ResourceOffers
     */
    public List<ResourceOffer> getAllOffers(Long organizationId) {
        List<ResourceOffer> entries = this.resourceOfferRepository.findByOrganizationIdAndActiveIsTrue(organizationId);

        return entries;
    }

    /**
     * Get ResourceOffer by given id
     * @param id resourceOffer id
     * @return ResourceOffer
     */
    public ResourceOffer getOfferById(Long id) throws GeneralResourceException {
        ResourceOffer resourceOffer = resourceOfferRepository.getOne(id);
        if (resourceOffer.isActive() == false) {
            throw new GeneralResourceException("resource with given id is not active");
        }

        return resourceOffer;
    }

    /**
     * Creates a new ResourceMatch for claiming a ResourceOffer
     * @param resourceOfferId id of the claimed resourceoffer
     * @param resourceRequirementId id of the resourcerequirement, where the resourceoffer is used.
     * @return created ResourceMatch for the claimed ResourceOffer
     * @throws IllegalValueException
     * @throws MatchAlreadyExistsException
     */
    public ResourceMatch createClaimResourceRequest(Long resourceOfferId, Long resourceRequirementId)
        throws IllegalValueException, MatchAlreadyExistsException {

        ResourceMatch resourceMatch = new ResourceMatch();

        ResourceOffer resourceOffer = resourceOfferRepository.findByIdAndActiveIsTrue(resourceOfferId);
        if(resourceOffer == null) {
            throw new IllegalValueException("no resourceoffer with id {} found", resourceOfferId.toString());
        }

        Organization organization = resourceOffer.getOrganization();

        if(organization == null) {
            throw new IllegalValueException("no organization for resourceoffer {} found", resourceOffer.toString());
        }

        //check for authorization
        //checkAuthoritiesForResourceMatch(organization);

        ResourceRequirement resourceRequirement = resourceRequirementRepository.findByIdAndActiveIsTrue(resourceRequirementId);
        if(resourceRequirement == null) {
            throw new IllegalValueException("no resourceRequirement with id {} found", resourceRequirementId.toString());
        }

        Project project = resourceRequirement.getProject();
        if(project == null) {
            throw new IllegalValueException("no project for resourceRequirement {} found", resourceRequirement.toString());
        }

        if(project.getOrganization().getId() == organization.getId()) {
            throw new IllegalValueException("resource.claim.error.ownresource", "cannot claim own resourceoffer" + resourceOffer.toString());
        }

        List<ResourceMatch> result = resourceMatchRepository
            .findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(resourceOffer,
            resourceRequirement, organization, project);

        if(result.isEmpty() == true) {
            resourceMatch.setResourceOffer(resourceOffer);
            resourceMatch.setResourceRequirement(resourceRequirement);
            resourceMatch.setOrganization(organization);
            resourceMatch.setProject(project);
            resourceMatch.setMatchDirection(MatchDirection.ORGANIZATION_CLAIMED);

            resourceMatchRepository.save(resourceMatch);
        } else {
            throw new MatchAlreadyExistsException("resource.claim.error.matchexists", "match already exists");
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
        if(user.equals(organization.getOwner()) == false) {
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
            throw new NoSuchResourceMatchException(resourceMatchId);
        }

        Project project = resourceMatch.getProject();
        Organization organization = resourceMatch.getOrganization();

        if(project == null) {
            throw new NoSuchProjectException("can't find project for match with matchId " + resourceMatchId);
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

        if(accept == true) {
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
     * @param restParameters creates pageable from this variable
     * @return List of ResourceMatches representing open resource claims/applies
     */
    @Transactional(readOnly=true)
    public List<ResourceMatch> getResourcesForOrganization(Long organizationId, RestParameters restParameters) {
        PageRequest pageRequest = restParameters.buildPageRequest();

        /// first of all, we have here organization that offered a resource for projects.
        QResourceMatch resourceMatch = QResourceMatch.resourceMatch;
        Collection<Long> projects = projectRepository.findByOrganizationId(organizationId);
        BooleanExpression exp1 = resourceMatch.project.id.in(projects);
        BooleanExpression exp2 = resourceMatch.active.isTrue();
        BooleanExpression exp3 = resourceMatch.matchDirection.eq(MatchDirection.ORGANIZATION_OFFERED);

        Predicate where = ExpressionUtils.allOf(exp1, exp2, exp3);
        List<ResourceMatch> first = resourceMatchRepository.findAll(where, pageRequest).getContent();

        //and here we have a Project that claim the resource from organization
        exp1 = resourceMatch.organization.id.eq(organizationId);
        exp3 = resourceMatch.matchDirection.eq(MatchDirection.ORGANIZATION_CLAIMED);
        where = ExpressionUtils.allOf(exp1, exp2, exp3);
        //we need to join both list
        List<ResourceMatch> second = resourceMatchRepository.findAll(where, pageRequest).getContent();
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

        if(hasData.isEmpty() == false){
            throw new IllegalValueException("resourcematch.error.projectapply.offeralreadyexists", "Current offer has already been donated before");
        }

        if(organization == project.getOrganization()){
            throw new IllegalValueException("resourcematch.error.projectapply.ownproject", "Organization cannot offer resources to own project");
        }
        //,check if we need new apply
        if(resourceRequirement.getAmount().equals(BigDecimal.ZERO) == true){
            throw new IllegalValueException("resourcematch.error.projectapply.requestfulfilled", "Requirements are already fulfilled");
        }

        if(resourceOffer.getAmount().equals(BigDecimal.ZERO) == true){
            throw new IllegalValueException("resourcematch.error.projectapply.offerdepleted", "Current Offer already depleted");
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
     * @throws NoSuchOrganizationException if organization with id cannot be found
     */
    public Page<ResourceMatch> getDonatedResourcesForOrganization(Long orgId, RestParameters restParameters)
        throws NoSuchOrganizationException {

        PageRequest pageRequest = null;

        if (restParameters != null) {
            pageRequest = restParameters.buildPageRequest();
        }

        Organization organization = organizationRepository.findOne(orgId);
        if (organization == null) {
            throw new NoSuchOrganizationException("organization with id " + orgId + "can't be found");
        }

        Page<ResourceMatch> resourceMatches =
            resourceMatchRepository.findByOrganizationAndAcceptedIsTrueAndActiveIsTrue(organization, pageRequest);

        return resourceMatches;
    }

    public Page<ResourceMatch> getSupportedProjectsForOrganization(Long orgId, RestParameters restParameters)
        throws NoSuchOrganizationException {

        PageRequest pageRequest = null;

        if (restParameters != null) {
            pageRequest = restParameters.buildPageRequest();
        }

        Organization organization = organizationRepository.findOne(orgId);
        if (organization == null) {
            throw new NoSuchOrganizationException("organization with id " + orgId + "can't be found");
        }

        Page<ResourceMatch> resourceMatches =
            resourceMatchRepository.findByOrganizationAndAcceptedIsTrueAndActiveIsTrue(organization, pageRequest);

        return resourceMatches;

    }


}
