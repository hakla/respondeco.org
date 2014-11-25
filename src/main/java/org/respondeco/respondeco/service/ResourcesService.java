package org.respondeco.respondeco.service;

import org.joda.time.DateTime;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.ResourceJoinTagException;
import org.respondeco.respondeco.service.exception.enumException.EnumResourceJoinTag;
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
public class ResourcesService {

    // region Private Variables
    private final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    private ResourceOfferRepository resourceOfferRepository;

    private ResourceRequirementRepository resourceRequirementRepository;

    private ResourceTagRepository resourceTagRepository;

    private ResourceOfferJoinResourceRequirementRepository resourceOfferJoinResourceRequirementRepository;

    private ResourceOfferJoinResourceTagRepository resourceOfferJoinResourceTagRepository;

    private ResourceRequirementJoinResourceTagRepository resourceRequirementJoinResourceTagRepository;

    // endregion

    // region Constructor
    @Inject
    public ResourcesService(ResourceOfferRepository resourceOfferRepository,
                            ResourceRequirementRepository resourceRequirementRepository,
                            ResourceTagRepository resourceTagRepository,
                            ResourceRequirementJoinResourceTagRepository resourceRequirementJoinResourceTagRepository,
                            ResourceOfferJoinResourceRequirementRepository resourceOfferJoinResourceRequirementRepository,
                            ResourceOfferJoinResourceTagRepository resourceOfferJoinResourceTagRepository) {
        this.resourceOfferRepository = resourceOfferRepository;
        this.resourceRequirementRepository = resourceRequirementRepository;
        this.resourceTagRepository = resourceTagRepository;
        this.resourceRequirementJoinResourceTagRepository = resourceRequirementJoinResourceTagRepository;
        this.resourceOfferJoinResourceRequirementRepository = resourceOfferJoinResourceRequirementRepository;
        this.resourceOfferJoinResourceTagRepository = resourceOfferJoinResourceTagRepository;
    }

    // endregion

    // region public methods for Resource Requirement Create/Update/Delete + Select all/by project ID

    public ResourceRequirement createRequirement(BigDecimal amount, String description, Long projectId, Boolean isEssential, String[] resourceTags) throws ResourceException, ResourceTagException, ResourceJoinTagException, Exception {
        ResourceRequirement newRequirement = null;
        if (this.resourceRequirementRepository.findByDescriptionAndProjectId(description, projectId) == null) {
            newRequirement.setAmount(amount);
            newRequirement.setDescription(description);
            newRequirement.setProjectId(projectId);
            newRequirement.setIsEssential(isEssential);
            this.resourceRequirementRepository.save(newRequirement);
            this.saveRequirementJoinTags(newRequirement, resourceTags);
        } else {
            throw new ResourceException(String.format("Requirement with description '%s' for the Project %d already exists", description, projectId), EnumResourceException.ALREADY_EXISTS);
        }

        return newRequirement;
    }

    public ResourceRequirement updateRequirement(Long id, BigDecimal amount, String description, Boolean isEssential, String[] resourceTags) throws ResourceException, ResourceTagException, ResourceJoinTagException, Exception {
        ResourceRequirement requirement = this.resourceRequirementRepository.findOne(id);
        if (requirement != null) {
            requirement.setAmount(amount);
            requirement.setDescription(description);
            requirement.setIsEssential(isEssential);
            this.resourceRequirementRepository.save(requirement);

            this.resourceRequirementJoinResourceTagRepository.deleteByRequirementId(id);

            this.saveRequirementJoinTags(requirement, resourceTags);
        } else {
            throw new ResourceException(String.format("No resource requirement found for the id: %d", id), EnumResourceException.NOT_FOUND);
        }

        return requirement;
    }

    public void deleteRequirement(Long id) throws ResourceException {
        if (this.resourceRequirementRepository.findOne(id) != null) {
            this.resourceRequirementJoinResourceTagRepository.deleteByRequirementId(id);
            this.resourceRequirementRepository.delete(id);
        } else {
            throw new ResourceException(String.format("No resource requirement found for the id: %d", id), EnumResourceException.NOT_FOUND);
        }
    }

    public List<ResourceRequirementDTO> getAllRequirements() {
        List<ResourceRequirementDTO> result = new ArrayList<ResourceRequirementDTO>();
        for (ResourceRequirement requirement : this.resourceRequirementRepository.findAll()) {
            result.add(new ResourceRequirementDTO(requirement));
        }
        return result;
    }

    public List<ResourceRequirementDTO> getAllRequirements(Long projectId) {
        List<ResourceRequirementDTO> result = new ArrayList<ResourceRequirementDTO>();
        for (ResourceRequirement requirement : this.resourceRequirementRepository.findByProjectId(projectId)) {
            result.add(new ResourceRequirementDTO(requirement));
        }
        return result;
    }

    // endregion

    // region public methods for Resource Offer Create/Update/Delete + Select all/by organisation ID

    public ResourceOffer createOffer(BigDecimal amount, String description, Long organisationId, Boolean isCommercial, Boolean isRecurrent, DateTime startDate, DateTime endDate, String[] resourceTags) throws ResourceException, ResourceTagException, ResourceJoinTagException{
        ResourceOffer newOffer = null;
        List<ResourceOffer> existingOffer = this.resourceOfferRepository.findByDescriptionAndOrganisationId(description, organisationId);
        if (existingOffer == null || existingOffer.isEmpty() == true) {
            newOffer = new ResourceOffer();
            newOffer.setAmount(amount);
            newOffer.setDescription(description);
            newOffer.setOrganisationId(organisationId);
            newOffer.setIsCommercial(isCommercial);
            newOffer.setIsRecurrent(isRecurrent);
            newOffer.setStartDate(startDate);
            newOffer.setEndDate(endDate);
            this.resourceOfferRepository.save(newOffer);
            this.saveOffersJoinTags(newOffer, resourceTags);
        } else {
            throw new ResourceException(String.format("Offer with same description already exists (Description: %s)", description), EnumResourceException.ALREADY_EXISTS);
        }

        return newOffer;
    }

    public void updateOffer(Long offerId, Long organisationId, BigDecimal amount, String description, Boolean isCommercial, Boolean isRecurrent, DateTime startDate, DateTime endDate, String[] resourceTags) throws ResourceException, ResourceTagException, ResourceJoinTagException {
        ResourceOffer offer = this.resourceOfferRepository.findOne(offerId);
        if (offer != null) {
            offer.setAmount(amount);
            offer.setDescription(description);
            offer.setIsCommercial(isCommercial);
            offer.setIsRecurrent(isRecurrent);
            offer.setStartDate(startDate);
            offer.setEndDate(endDate);
            this.resourceOfferRepository.save(offer);

            // delete all offer join tags entries, so we could have clean insert
            this.resourceOfferJoinResourceTagRepository.deleteByOfferId(offer.getId());

            // no entry exists, save all
            this.saveOffersJoinTags(offer, resourceTags);
        }
        else{
            throw new ResourceException(String.format("Offer with Id: %d do not exists", offerId), EnumResourceException.NOT_FOUND);
        }
    }

    public void deleteOffer(Long offerId) throws ResourceException{
        if (this.resourceOfferRepository.findOne(offerId) != null) {
            this.resourceOfferJoinResourceTagRepository.deleteByOfferId(offerId);
            this.resourceOfferRepository.delete(offerId);
        }
        else{
            throw new ResourceException(String.format("Offer with Id: %d not found", offerId), EnumResourceException.NOT_FOUND);
        }
    }

    public List<ResourceOfferDTO> getAllOffers() {
        List<ResourceOfferDTO> result = new ArrayList<ResourceOfferDTO>();
        List<ResourceOffer> entries = this.resourceOfferRepository.findAll();
        if(entries != null && entries.isEmpty() == false) {
            for (ResourceOffer offer :entries) {
                result.add(new ResourceOfferDTO(offer));
            }
        }
        return result;
    }

    public List<ResourceOfferDTO> getAllOffers(Long organisationId) {
        List<ResourceOfferDTO> result = new ArrayList<ResourceOfferDTO>();
        List<ResourceOffer> entries = this.resourceOfferRepository.findByOrganisationId(organisationId);
        if(entries != null && entries.isEmpty() == false) {
            for (ResourceOffer offer : entries) {
                result.add(new ResourceOfferDTO(offer));
            }
        }
        return result;
    }

    // endregion

    // region Private methods
    private void saveOffersJoinTags(ResourceOffer offer, String[] resourceTags) throws ResourceTagException, ResourceJoinTagException {
        for (String tagName : resourceTags) {
            //save tags and add it to list
            ResourceTag tag = this.saveResourceTag(tagName);
            //save offer to tag
            if (tag != null) {
                offer.addResourceTag(tag);
                this.saveOfferJoinTag(offer, tag);
            }
        }
    }

    private void saveRequirementJoinTags(ResourceRequirement requirement, String[] resourceTags) throws ResourceTagException, ResourceJoinTagException {
        for (String tagName : resourceTags) {
            //save tags and add it to list
            ResourceTag tag = this.saveResourceTag(tagName);
            //save requirement to tag
            if (tag != null) {
                requirement.addResourceTag(tag);
                this.saveRequirementJoinTag(requirement, tag);
            }
        }
    }

    private void saveRequirementJoinTag(ResourceRequirement requirement, ResourceTag tag) throws ResourceJoinTagException {
        String message = null;
        if (this.resourceRequirementJoinResourceTagRepository.countByResourceRequirementIdAndResourceTagId(requirement.getId(), tag.getId()) == 0) {
            try {
                ResourceRequirementJoinResourceTag result = new ResourceRequirementJoinResourceTag();
                result.setResourceRequirementId(requirement.getId());
                result.setResourceTagId(tag.getId());
                this.resourceRequirementJoinResourceTagRepository.save(result);
            } catch (Exception e) {
                message = String.format("Couldn't create connection entry between requirement and tag with requirement id:%d and tag id: %d", requirement.getId(), tag.getId());
                throw new ResourceJoinTagException(message, EnumResourceJoinTag.CREATE, e);
            }
        }
    }

    private void saveOfferJoinTag(ResourceOffer offer, ResourceTag tag) throws ResourceJoinTagException {
        String message = null;
        if (this.resourceOfferJoinResourceTagRepository.countByResourceOfferIdAndResourceTagId(offer.getId(), tag.getId()) == 0) {
            try {
                ResourceOfferJoinResourceTag resOfferToTag = new ResourceOfferJoinResourceTag();
                resOfferToTag.setResourceOfferId(offer.getId());
                resOfferToTag.setResourceTagId(tag.getId());
                this.resourceOfferJoinResourceTagRepository.save(resOfferToTag);
            } catch (Exception e) {
                message = String.format("Couldn't create connection entry between offer and tag with offer id:%d and tag id: %d", offer.getId(), tag.getId());
                throw new ResourceJoinTagException(message, EnumResourceJoinTag.CREATE, e);
            }
        }
    }

    private ResourceTag saveResourceTag(String tagName) throws ResourceTagException {
        ResourceTag tag = null;
        try {
            List<ResourceTag> list = this.resourceTagRepository.findByName(tagName);
            if (list.size() == 0) {
                tag = new ResourceTag();
                tag.setName(tagName);
                this.resourceTagRepository.save(tag);
                tag.setIsNewEntry(true);
            } else {
                tag = list.get(0);
                tag.setIsNewEntry(false);
            }
        } catch (Exception e) {
            String message = String.format("Error at trying to Save resource tag named: %s", tagName);
            throw new ResourceTagException(message, EnumResourceTagException.CREATE, e);
        }

        return tag;
    }
    //endregion
}
