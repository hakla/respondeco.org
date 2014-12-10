package org.respondeco.respondeco.service;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.*;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.template.BooleanTemplate;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.domain.QResourceOffer;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.service.exception.enumException.EnumResourceException;
import org.respondeco.respondeco.web.rest.dto.ResourceOfferDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.respondeco.respondeco.web.rest.util.RestUtil;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import java.beans.Expression;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roman Kern on 15.11.14.
 * Definition for
 * Create/Update/Delete Resources Offer
 * Create/Update/Delete Resources Requirement
 * Manage new Tags
 */

@Service
@Transactional
public class ResourceService {

    // region Private Variables
    private final Logger log = LoggerFactory.getLogger(ResourceService.class);
    private ResourceOfferRepository resourceOfferRepository;
    private ResourceRequirementRepository resourceRequirementRepository;
    private ResourceTagRepository resourceTagRepository;
    private ResourceTagService resourceTagService;
    private OrganizationRepository organizationRepository;
    private ProjectRepository projectRepository;
    private ResourceMatchRepository resourceMatchRepository;
    private ImageRepository imageRepository;

    private UserService userService;

    private RestUtil restUtil;

    // endregion

    // region Constructor
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

    private void ensureUserIsPartOfOrganisation(Project project) throws ResourceException {
        User user = userService.getUserWithAuthorities();
        if (project.getOrganization().getOwner() != user){
            throw new ResourceException(String.format("Current user %s is not a part of Organisation or do not have enough rights for the operation", user.getLogin()), EnumResourceException.USER_NOT_AUTHORIZED);
        }
    }

    private void ensureUserIsPartOfOrganisation(Organization organization) throws ResourceException {
        User user = userService.getUserWithAuthorities();
        if (organization.getOwner() != user){
            throw new ResourceException(String.format("Current user %s is not a part of Organisation or do not have enough rights for the operation", user.getLogin()), EnumResourceException.USER_NOT_AUTHORIZED);
        }
    }
    // endregion

    // region public methods for Resource Requirement Create/Update/Delete + Select all/by project ID
    public ResourceRequirement createRequirement(String name, BigDecimal amount, String description,
                                                 Long projectId, Boolean isEssential, List<String> resourceTags)
        throws ResourceException {
        Project project = projectRepository.findOne(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        return createRequirement(name, amount, description, project, isEssential, resourceTags);
    }

    public ResourceRequirement createRequirement(String name, BigDecimal amount, String description,
                                                 Project project, Boolean isEssential, List<String> resourceTags)
        throws ResourceException {
        ResourceRequirement newRequirement = null;
        ensureUserIsPartOfOrganisation(project);
        List<ResourceRequirement> entries = resourceRequirementRepository.findByNameAndProject(name, project);
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
            throw new ResourceException(
                String.format("Requirement with description '%s' for the Project %d already exists",
                    description, project.getId()), EnumResourceException.ALREADY_EXISTS);
        }

        return newRequirement;
    }

    public ResourceRequirement updateRequirement(Long id, String name, BigDecimal amount, String description,
                                                 Long projectId, Boolean isEssential, List<String> resourceTags)
        throws ResourceException, OperationForbiddenException {
        Project project = projectRepository.findOne(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        return updateRequirement(id, name, amount, description, project, isEssential, resourceTags);
    }

    public ResourceRequirement updateRequirement(Long id, String name, BigDecimal amount, String description,
                                                 Project project, Boolean isEssential, List<String> resourceTags)
        throws ResourceException, OperationForbiddenException {
        ResourceRequirement requirement = this.resourceRequirementRepository.findOne(id);
        if(project.equals(requirement.getProject()) == false) {
            throw new OperationForbiddenException("cannot modify resource requirements of other projects");
        }
        if (requirement != null) {
            ensureUserIsPartOfOrganisation(requirement.getProject());
            requirement.setName(name);
            requirement.setAmount(amount);
            requirement.setDescription(description);
            requirement.setIsEssential(isEssential);
            requirement.setResourceTags(resourceTagService.getOrCreateTags(resourceTags));
            resourceRequirementRepository.save(requirement);

        } else {
            throw new ResourceException(String.format("No resource requirement found for the id: %d", id),
                EnumResourceException.NOT_FOUND);
        }

        return requirement;
    }

    public void deleteRequirement(Long id) throws Exception, ResourceException {
        ResourceRequirement requirement = this.resourceRequirementRepository.findOne(id);
        if (requirement != null) {
            ensureUserIsPartOfOrganisation(requirement.getProject());
            resourceRequirementRepository.delete(id);
        } else {
            throw new ResourceException(String.format("No resource requirement found for the id: %d", id), EnumResourceException.NOT_FOUND);
        }
    }

    public List<ResourceRequirement> getAllRequirements() {
        return resourceRequirementRepository.findAll();
    }

    public List<ResourceRequirementRequestDTO> getAllRequirements(Long projectId) {
        List<ResourceRequirementRequestDTO> result = new ArrayList<>();
        List<ResourceRequirement> entries = this.resourceRequirementRepository.findByProjectId(projectId);
        for (ResourceRequirement requirement : entries) {
            result.add(new ResourceRequirementRequestDTO(requirement));
        }
        return result;
    }
    // endregion

    // region public methods for Resource Offer Create/Update/Delete + Select all/by organisation ID
    public ResourceOffer createOffer(String name, BigDecimal amount, String description, Long organizationId,
                                     Boolean isCommercial, Boolean isRecurrent, LocalDate startDate,
                                     LocalDate endDate, List<String> resourceTags) {
        ResourceOffer newOffer = new ResourceOffer();
        newOffer.setName(name);
        newOffer.setAmount(amount);
        newOffer.setOriginalAmount(amount);
        newOffer.setDescription(description);
        newOffer.setOrganization(organizationRepository.findOne(organizationId));
        newOffer.setIsCommercial(isCommercial);
        newOffer.setIsRecurrent(isRecurrent);
        newOffer.setStartDate(startDate);
        newOffer.setEndDate(endDate);

        log.debug("OFFER: " + newOffer.toString());
        newOffer.setResourceTags(resourceTagService.getOrCreateTags(resourceTags));
        this.resourceOfferRepository.save(newOffer);

        return newOffer;
    }

    public ResourceOffer updateOffer(Long offerId, Long organisationId, String name, BigDecimal amount,
                                     String description, Boolean isCommercial, Boolean isRecurrent,
                                     LocalDate startDate, LocalDate endDate, List<String> resourceTags)
        throws ResourceException, ResourceTagException, ResourceJoinTagException {
        ResourceOffer offer = this.resourceOfferRepository.findOne(offerId);

        if (offer != null) {
            ensureUserIsPartOfOrganisation(organizationRepository.findOne(organisationId));

            offer.setName(name);
            offer.setAmount(amount);
            offer.setDescription(description);
            offer.setIsCommercial(isCommercial);
            offer.setIsRecurrent(isRecurrent);
            offer.setStartDate(startDate);
            offer.setEndDate(endDate);
            offer.setResourceTags(resourceTagService.getOrCreateTags(resourceTags));
            this.resourceOfferRepository.save(offer);
        }
        else{
            throw new ResourceException(String.format("Offer with Id: %d do not exists", offerId),
                EnumResourceException.NOT_FOUND);
        }

        return offer;
    }

    public void deleteOffer(Long offerId) throws ResourceException{
        if (this.resourceOfferRepository.findOne(offerId) != null) {
            this.resourceOfferRepository.delete(offerId);
        }
        else{
            throw new ResourceException(String.format("Offer with Id: %d not found", offerId), EnumResourceException.NOT_FOUND);
        }
    }

    public List<ResourceOffer> getAllOffers(String name, String organization, String tags, Boolean available, Boolean isCommercial, RestParameters restParameters) {

        PageRequest pageRequest = null;
        if(restParameters != null) {
            pageRequest = restParameters.buildPageRequest();
        }

        List<ResourceOfferDTO> result = new ArrayList<ResourceOfferDTO>();
        List<ResourceOffer> entries;

        if(name.isEmpty() && organization.isEmpty() && tags.isEmpty() && available == false && isCommercial == null) {
            entries = resourceOfferRepository.findByActiveIsTrue();
        } else {
            //create dynamic query with help of querydsl
            BooleanExpression resourceOfferNameLike = null;
            BooleanExpression resourceOfferOrganizationLike = null;
            BooleanExpression resourceOfferTagLike = null;
            BooleanExpression resourceOfferAvailable = null;
            BooleanExpression resourceCommercial = null;
            BooleanExpression isActive = null;

            QResourceOffer resourceOffer = QResourceOffer.resourceOffer;

            isActive = resourceOffer.active.isTrue();

            if(name.isEmpty() == false) {
                resourceOfferNameLike = resourceOffer.name.toLowerCase().contains(name.toLowerCase());
            }

            if(organization.isEmpty() == false) {
                resourceOfferOrganizationLike = resourceOffer.organization.name.toLowerCase().contains(organization.toLowerCase());
            }

            if(tags.isEmpty() == false) {
                List<String> tagList = restUtil.splitCommaSeparated(tags);

                resourceOfferTagLike = resourceOffer.resourceTags.any().name.in(tagList);
            }

            if(available == true) {
                resourceOfferAvailable = resourceOffer.startDate.before(LocalDate.now()).
                    and(resourceOffer.endDate.after(LocalDate.now()));

            }

            if(isCommercial!=null && isCommercial == true) {
                resourceCommercial = resourceOffer.isCommercial.eq(true);
            } else if(isCommercial!=null && isCommercial == false) {
                resourceCommercial = resourceOffer.isCommercial.eq(false);
            }

            Predicate where = ExpressionUtils.allOf(resourceOfferNameLike, resourceOfferOrganizationLike,
                resourceOfferAvailable, resourceCommercial, resourceOfferTagLike, isActive);

            entries = resourceOfferRepository.findAll(where, pageRequest).getContent();
        }

        return entries;
    }

    public List<ResourceOfferDTO> getAllOffers(Long organizationId) {
        List<ResourceOfferDTO> result = new ArrayList<ResourceOfferDTO>();
        List<ResourceOffer> entries = this.resourceOfferRepository.findByOrganizationId(organizationId);

        log.debug(entries.toString());
        if(entries.isEmpty() == false) {
            for (ResourceOffer offer : entries) {
                result.add(new ResourceOfferDTO(offer));
            }
        } else {
            log.debug("entries are empty");
        }
        return result;
    }

    /**
     * Get ResourceOffer by given id
     * @param id resourceOffer id
     * @return ResourceOfferDTO
     */
    public ResourceOffer getOfferById(Long id) {
        return resourceOfferRepository.getOne(id);
    }


    /**
     * Request a Resource Offer
     * @param resourceOfferId
     * @param resourceRequirementId
     * @param organizationId
     * @param projectId
     * @return ResourceMatch representing the resource request
     */
    public ResourceMatch createClaimResourceRequest(Long resourceOfferId, Long resourceRequirementId, Long organizationId, Long projectId)
        throws IllegalValueException, MatchAlreadyExistsException {

        ResourceMatch resourceMatch = new ResourceMatch();

        ResourceOffer resourceOffer = resourceOfferRepository.findOne(resourceOfferId);
        if(resourceOffer == null) {
            throw new IllegalValueException("no resourceoffer with id {} found", resourceOfferId.toString());
        }
        ResourceRequirement resourceRequirement = resourceRequirementRepository.findOne(resourceRequirementId);
        if(resourceRequirement == null) {
            throw new IllegalValueException("no resourceRequirement with id {} found", resourceRequirement.toString());
        }
        Organization organization = organizationRepository.findOne(organizationId);
        if(organization == null) {
            throw new IllegalValueException("no organization with id {} found", organization.toString());
        }
        Project project = projectRepository.findOne(projectId);
        if(project == null) {
            throw new IllegalValueException("no project with id {} found", project.toString());
        }

        List<ResourceMatch> result = resourceMatchRepository.findByResourceOfferAndResourceRequirementAndOrganizationAndProject(resourceOffer,
            resourceRequirement, organization, project);

        if(result.isEmpty() == true) {
            resourceMatch.setResourceOffer(resourceOffer);
            resourceMatch.setResourceRequirement(resourceRequirement);
            resourceMatch.setOrganization(organization);
            resourceMatch.setProject(project);
            resourceMatch.setMatchDirection(MatchDirection.ORGANIZATION_CLAIMED);

            resourceMatchRepository.save(resourceMatch);
        } else {
            throw new MatchAlreadyExistsException("claim.error.matchexists", "match already exists");
        }

        return resourceMatch;
    }

    /**
     * Accept or decline the resource request
     * @param resourceMatchId resourceMatch id
     * @param accept true if accepted, false if declined
     * @return accepted or declined ResourceMatch
     */
    public ResourceMatch answerResourceRequest(Long resourceMatchId, boolean accept) {

        //TODO: add authority check
        ResourceMatch resourceMatch = resourceMatchRepository.findOne(resourceMatchId);

        if(resourceMatchId == null) {
            throw new IllegalValueException("resourcematch.error.idnull", "Resourcematch id must not be null");
        }
        if(resourceMatch == null) {
            throw new NoSuchResourceMatchException(resourceMatchId);
        }

        resourceMatch.setAccepted(accept);

        //check if resourceOffer amount is completely consumed by resourceRequirement
        ResourceOffer offer = resourceMatch.getResourceOffer();
        ResourceRequirement req = resourceMatch.getResourceRequirement();

        //offer greater req amount -> keep offer active and lower amount
        if(offer.getAmount().compareTo(req.getAmount()) == 1 ) {

        } else {
            //requirement consumes offer -> offer is no longer active
            offer.setActive(false);
        }

        resourceOfferRepository.save(offer);

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
     * Get Resource Requests for specific Organization with given id. (Claim Resource)
     * @param organizationId Organization id
     * @return List of ResourceMatches representing open resource requests
     */
    @Transactional(readOnly=true)
    public List<ResourceMatch> getResourceRequestsForOrganization(Long organizationId, RestParameters restParameters) {
        PageRequest pageRequest = restParameters.buildPageRequest();

        QResourceMatch resourceMatch = QResourceMatch.resourceMatch;
        BooleanExpression resourceMatchOrganization = resourceMatch.organization.id.eq(organizationId);
        BooleanExpression resourceMatchAccepted = resourceMatch.accepted.isNull();

        Predicate where = ExpressionUtils.allOf(resourceMatchAccepted, resourceMatchOrganization);

        List<ResourceMatch> requests = resourceMatchRepository.findAll(where, pageRequest).getContent();

        return requests;
    }




    // endregion
}
