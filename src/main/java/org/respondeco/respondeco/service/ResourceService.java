package org.respondeco.respondeco.service;

import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.service.exception.enumException.EnumResourceException;
import org.respondeco.respondeco.service.exception.enumException.EnumResourceTagException;
import org.respondeco.respondeco.web.rest.dto.ResourceOfferDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementRequestDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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

    private ResourceTagService resourceTagService;

    private OrganizationRepository organizationRepository;

    private ProjectRepository projectRepository;

    // endregion

    // region Constructor
    @Inject
    public ResourceService(ResourceOfferRepository resourceOfferRepository,
                           ResourceRequirementRepository resourceRequirementRepository,
                           ResourceTagService resourceTagService,
                           OrganizationRepository organizationRepository,
                           ProjectRepository projectRepository) {
        this.resourceOfferRepository = resourceOfferRepository;
        this.resourceRequirementRepository = resourceRequirementRepository;
        this.resourceTagService = resourceTagService;
        this.organizationRepository = organizationRepository;
        this.projectRepository = projectRepository;
    }

    // endregion

    // region public methods for Resource Requirement Create/Update/Delete + Select all/by project ID
    public ResourceRequirement createRequirement(String name, BigDecimal amount, String description,
                                                 Long projectId, Boolean isEssential, List<String> resourceTags)
        throws ResourceException, ResourceTagException, ResourceJoinTagException, Exception {
        Project project = projectRepository.findOne(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        return createRequirement(name, amount, description, project, isEssential, resourceTags);
    }

    public ResourceRequirement createRequirement(String name, BigDecimal amount, String description,
                                                 Project project, Boolean isEssential, List<String> resourceTags)
        throws ResourceException, ResourceTagException, ResourceJoinTagException, Exception {
        ResourceRequirement newRequirement = null;
        List<ResourceRequirement> entries = this.resourceRequirementRepository.findByNameAndProject(name, project);
        if (entries == null || entries.isEmpty() == true) {
            newRequirement = new ResourceRequirement();
            newRequirement.setName(name);
            newRequirement.setAmount(amount);
            newRequirement.setDescription(description);
            newRequirement.setProject(project);
            newRequirement.setIsEssential(isEssential);
            newRequirement.setResourceTags(resourceTagService.getOrCreateTags(resourceTags));
            this.resourceRequirementRepository.save(newRequirement);
        } else {
            throw new ResourceException(
                String.format("Requirement with description '%s' for the Project %d already exists",
                    description, project.getId()), EnumResourceException.ALREADY_EXISTS);
        }

        return newRequirement;
    }

    public ResourceRequirement updateRequirement(Long id, String name, BigDecimal amount, String description,
                                                 Long projectId, Boolean isEssential, List<String> resourceTags)
        throws ResourceException, ResourceTagException, ResourceJoinTagException, Exception {
        Project project = projectRepository.findOne(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        return updateRequirement(id, name, amount, description, project, isEssential, resourceTags);
    }

    public ResourceRequirement updateRequirement(Long id, String name, BigDecimal amount, String description,
                                                 Project project, Boolean isEssential, List<String> resourceTags)
        throws ResourceException, ResourceTagException, ResourceJoinTagException, Exception {
        ResourceRequirement requirement = this.resourceRequirementRepository.findOne(id);
        if(project.equals(requirement.getProject()) == false) {
            throw new OperationForbiddenException("cannot modify resource requirements of other projects");
        }
        if (requirement != null) {
            requirement.setName(name);
            requirement.setAmount(amount);
            requirement.setDescription(description);
            requirement.setIsEssential(isEssential);
            requirement.setResourceTags(resourceTagService.getOrCreateTags(resourceTags));
            this.resourceRequirementRepository.save(requirement);

        } else {
            throw new ResourceException(String.format("No resource requirement found for the id: %d", id),
                EnumResourceException.NOT_FOUND);
        }

        return requirement;
    }

    public void deleteRequirement(Long id) throws ResourceException {
        if (this.resourceRequirementRepository.findOne(id) != null) {
            this.resourceRequirementRepository.delete(id);
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
    public ResourceOffer createOffer(String name, BigDecimal amount, String description, Long organisationId,
                                     Boolean isCommercial, Boolean isRecurrent, LocalDate startDate,
                                     LocalDate endDate, List<String> resourceTags) throws ResourceException, ResourceTagException, ResourceJoinTagException{
        ResourceOffer newOffer = new ResourceOffer();
        newOffer.setName(name);
        newOffer.setAmount(amount);
        newOffer.setDescription(description);
        newOffer.setOrganisation(organizationRepository.findOne(organisationId));
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

    public List<ResourceOfferDTO> getAllOffers() {
        List<ResourceOfferDTO> result = new ArrayList<ResourceOfferDTO>();
        List<ResourceOffer> entries = this.resourceOfferRepository.findAll();
        if(entries.isEmpty() == false) {
            for (ResourceOffer offer :entries) {
                result.add(new ResourceOfferDTO(offer));
            }
        }
        return result;
    }

    public List<ResourceOfferDTO> getAllOffers(Long organizationId) {
        List<ResourceOfferDTO> result = new ArrayList<ResourceOfferDTO>();
        List<ResourceOffer> entries = this.resourceOfferRepository.findByOrganisationId(organizationId);

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


    // endregion
}
