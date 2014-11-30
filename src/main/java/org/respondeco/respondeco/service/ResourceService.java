package org.respondeco.respondeco.service;

import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.ResourceJoinTagException;
import org.respondeco.respondeco.service.exception.enumException.EnumResourceException;
import org.respondeco.respondeco.service.exception.enumException.EnumResourceTagException;
import org.respondeco.respondeco.service.exception.ResourceException;
import org.respondeco.respondeco.service.exception.ResourceTagException;
import org.respondeco.respondeco.web.rest.dto.ResourceOfferDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementDTO;
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

    private ResourceTagRepository resourceTagRepository;

    private OrganizationRepository organizationRepository;

    private ProjectRepository projectRepository;

    // endregion

    // region Constructor
    @Inject
    public ResourceService(ResourceOfferRepository resourceOfferRepository,
                           ResourceRequirementRepository resourceRequirementRepository,
                           ResourceTagRepository resourceTagRepository,
                           OrganizationRepository organizationRepository,
                           ProjectRepository projectRepository) {
        this.resourceOfferRepository = resourceOfferRepository;
        this.resourceRequirementRepository = resourceRequirementRepository;
        this.resourceTagRepository = resourceTagRepository;
        this.organizationRepository = organizationRepository;
        this.projectRepository = projectRepository;
    }

    // endregion

    // region public methods for Resource Requirement Create/Update/Delete + Select all/by project ID
    public ResourceRequirement createRequirement(String name, BigDecimal amount, String description, Long projectId, Boolean isEssential, String[] resourceTags) throws ResourceException, ResourceTagException, ResourceJoinTagException, Exception {
        ResourceRequirement newRequirement = null;
        List<ResourceRequirement> entries = this.resourceRequirementRepository.findByNameAndProjectId(name, projectId);
        if (entries == null || entries.isEmpty() == true) {
            newRequirement = new ResourceRequirement();
            newRequirement.setName(name);
            newRequirement.setAmount(amount);
            newRequirement.setDescription(description);
            newRequirement.setProject(projectRepository.findOne(projectId));
            newRequirement.setIsEssential(isEssential);
           // this.mapTags(newRequirement, resourceTags); TODO FIX
            this.resourceRequirementRepository.save(newRequirement);
        } else {
            throw new ResourceException(String.format("Requirement with description '%s' for the Project %d already exists", description, projectId), EnumResourceException.ALREADY_EXISTS);
        }

        return newRequirement;
    }

    public ResourceRequirement updateRequirement(Long id, String name, BigDecimal amount, String description, Boolean isEssential, String[] resourceTags) throws ResourceException, ResourceTagException, ResourceJoinTagException, Exception {
        ResourceRequirement requirement = this.resourceRequirementRepository.findOne(id);
        if (requirement != null) {
            requirement.setName(name);
            requirement.setAmount(amount);
            requirement.setDescription(description);
            requirement.setIsEssential(isEssential);
            //this.mapTags(requirement, resourceTags); TODO FIX
            this.resourceRequirementRepository.save(requirement);

        } else {
            throw new ResourceException(String.format("No resource requirement found for the id: %d", id), EnumResourceException.NOT_FOUND);
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

    public List<ResourceRequirementDTO> getAllRequirements() {
        List<ResourceRequirementDTO> result = new ArrayList<>();
        List<ResourceRequirement> entries = this.resourceRequirementRepository.findAll();
        if(entries != null && entries.isEmpty() == false) {
            for (ResourceRequirement requirement : entries) {
                result.add(new ResourceRequirementDTO(requirement));
            }
        }
        return result;
    }

    public List<ResourceRequirementDTO> getAllRequirements(Long projectId) {
        List<ResourceRequirementDTO> result = new ArrayList<>();
        List<ResourceRequirement> entries = this.resourceRequirementRepository.findByProjectId(projectId);
        for (ResourceRequirement requirement : entries) {
            result.add(new ResourceRequirementDTO(requirement));
        }
        return result;
    }
    // endregion

    // region public methods for Resource Offer Create/Update/Delete + Select all/by organisation ID
    public ResourceOffer createOffer(String name, BigDecimal amount, String description, Long organisationId, Boolean isCommercial, Boolean isRecurrent, LocalDate startDate, LocalDate endDate, String[] resourceTags) throws ResourceException, ResourceTagException, ResourceJoinTagException{
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
        this.mapTags(newOffer, resourceTags);
        this.resourceOfferRepository.save(newOffer);

        return newOffer;
    }

    public ResourceOffer updateOffer(Long offerId, Long organisationId, String name, BigDecimal amount, String description, Boolean isCommercial, Boolean isRecurrent, LocalDate startDate, LocalDate endDate, String[] resourceTags) throws ResourceException, ResourceTagException, ResourceJoinTagException {
        ResourceOffer offer = this.resourceOfferRepository.findOne(offerId);
        if (offer != null) {
            offer.setName(name);
            offer.setAmount(amount);
            offer.setDescription(description);
            offer.setIsCommercial(isCommercial);
            offer.setIsRecurrent(isRecurrent);
            offer.setStartDate(startDate);
            offer.setEndDate(endDate);
            this.mapTags(offer, resourceTags);
            this.resourceOfferRepository.save(offer);
        }
        else{
            throw new ResourceException(String.format("Offer with Id: %d do not exists", offerId), EnumResourceException.NOT_FOUND);
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
    public ResourceOfferDTO getOfferById(Long id) {
        return new ResourceOfferDTO(this.resourceOfferRepository.getOne(id));
    }


    // endregion

    // region Private methods
    private void mapTags(ResourceOffer resource, String[] resourceTags) throws ResourceTagException, ResourceJoinTagException {

        resource.setResourceTags(new ArrayList<ResourceTag>());

        for (String tagName : resourceTags) {
            //save tags and add it to list
            ResourceTag tag;

            try {
                List<ResourceTag> list = this.resourceTagRepository.findByName(tagName);
                if (list.isEmpty() == true) {
                    tag = new ResourceTag();
                    tag.setName(tagName);
                    this.resourceTagRepository.save(tag);
                    log.debug(String.format("new TAG created. ID: %d", tag.getId()));
                } else {
                    tag = list.get(0);
                    log.debug(String.format("TAG already exists take it. ID: %d", tag.getId()));
                }
            } catch (Exception e) {
                String message = String.format("Error at trying to Save resource tag named: %s", tagName);
                throw new ResourceTagException(message, EnumResourceTagException.CREATE, e);
            }

            //save resource to tag
            resource.addResourceTag(tag);
        }
    }
    //endregion
}
