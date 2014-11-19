package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
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

    public ResourceRequirement createRequirement(BigDecimal amount, String description, Long projectId, Boolean isEssential, String[] resourceTags){
        ResourceRequirement newRequirement = null;
        if(this.resourceRequirementRepository.findByDescriptionAndProjectId(description, projectId) == null){
            newRequirement.setAmount(amount);
            newRequirement.setDescription(description);
            newRequirement.setProjectId(projectId);
            newRequirement.setIsEssential(isEssential);
            this.resourceRequirementRepository.save(newRequirement);

            for(String tagName: resourceTags){
                //save tags and add it to list
                ResourceTag tag = this.saveResourceTag(tagName);
                newRequirement.addResourceTag(tag);
                //save offer to tag
                if(tag != null){
                    ResourceRequirementJoinResourceTag reqTag = new ResourceRequirementJoinResourceTag();
                    reqTag.setResourceRequirementId(newRequirement.getId());
                    reqTag.setResourceTagId(tag.getId());
                    this.resourceRequirementJoinResourceTagRepository.save(reqTag);
                }
            }
        }
        else{
            log.debug("Requirement with same description already exists (Description:{})", description);
        }

        return newRequirement;
    }

    public ResourceRequirement updateRequirement(Long id, BigDecimal amount, String description, Boolean isEssential, String[] resourceTags){
        ResourceRequirement requirement = this.resourceRequirementRepository.findOne(id);
        if(requirement != null){
            requirement.setAmount(amount);
            requirement.setDescription(description);
            requirement.setIsEssential(isEssential);
            this.resourceRequirementRepository.save(requirement);

            this.resourceRequirementJoinResourceTagRepository.deleteByRequirementId(id);

            this.saveRequirementJoinTags(requirement, resourceTags);
        }
        else{
            log.debug("Requirement with same description already exists (Description:{})", description);
        }

        return requirement;
    }

    public void deleteRequirement(Long id){
        if(this.resourceRequirementRepository.findOne(id) != null){
            this.resourceRequirementJoinResourceTagRepository.deleteByRequirementId(id);
            this.resourceRequirementRepository.delete(id);
        }
    }

    public List<ResourceRequirementDTO> getAllRequirements(){
        List<ResourceRequirementDTO> result = new ArrayList<ResourceRequirementDTO>();
        for(ResourceRequirement requirement: this.resourceRequirementRepository.findAll()){
            result.add(new ResourceRequirementDTO(requirement));
        }
        return result;
    }

    public List<ResourceRequirementDTO> getAllRequirements(Long projectId){
        List<ResourceRequirementDTO> result = new ArrayList<ResourceRequirementDTO>();
        for(ResourceRequirement requirement: this.resourceRequirementRepository.findByProjectId(projectId)){
            result.add(new ResourceRequirementDTO(requirement));
        }
        return result;
    }

    // endregion

    // region public methods for Resource Offer Create/Update/Delete + Select all/by organisation ID

    public ResourceOffer createOffer(BigDecimal amount, String description, Long organisationId, String[] resourceTags){
        ResourceOffer newOffer = null;
        if(this.resourceOfferRepository.findByDescriptionAndOrganisationId(description, organisationId) == null) {
            newOffer = new ResourceOffer();
            newOffer.setAmount(amount);
            newOffer.setDescription(description);
            newOffer.setOrganisationId(organisationId);
            this.resourceOfferRepository.save(newOffer);

            this.saveOffersJoinTags(newOffer, resourceTags);
        }
        else{
            log.debug("Offer with same description already exists (Description:{})", description);
        }

        return newOffer;
    }

    public void updateOffer(Long offerId, BigDecimal amount, String description, String[] resourceTags){
        ResourceOffer offer = this.resourceOfferRepository.findOne(offerId);
        if(offer != null){
            offer.setAmount(amount);
            offer.setDescription(description);
            this.resourceOfferRepository.save(offer);

            // delete all offer join tags entries, so we could have clean insert
            this.resourceOfferJoinResourceTagRepository.deleteByOfferId(offer.getId());

            // no entry exists, save all
            this.saveOffersJoinTags(offer, resourceTags);
        }
    }

    public void deleteOffer(Long offerId){
        if(this.resourceOfferRepository.findOne(offerId) != null){
            this.resourceOfferJoinResourceTagRepository.deleteByOfferId(offerId);
            this.resourceOfferRepository.delete(offerId);
        }
    }

    public List<ResourceOfferDTO> getAllOffers(){
        List<ResourceOfferDTO> result = new ArrayList<ResourceOfferDTO>();
        for(ResourceOffer offer: this.resourceOfferRepository.findAll()){
            result.add(new ResourceOfferDTO(offer));
        }
        return result;
    }

    public List<ResourceOfferDTO> getAllOffers(Long organisationId){
        List<ResourceOfferDTO> result = new ArrayList<ResourceOfferDTO>();
        for(ResourceOffer offer: this.resourceOfferRepository.findByOrganisationId(organisationId)){
            result.add(new ResourceOfferDTO(offer));
        }
        return result;
    }

    // endregion

    // region Private methods
    private void saveOffersJoinTags(ResourceOffer offer, String[] resourceTags){
        for(String tagName: resourceTags){
            //save tags and add it to list
            ResourceTag tag = this.saveResourceTag(tagName);
            //save offer to tag
            if(tag != null){
                offer.addResourceTag(tag);
                this.saveOfferJoinTag(offer, tag);
            }
        }
    }

    private void saveRequirementJoinTags(ResourceRequirement requirement, String[] resourceTags){
        for(String tagName: resourceTags){
            //save tags and add it to list
            ResourceTag tag = this.saveResourceTag(tagName);
            //save requirement to tag
            if(tag != null){
                requirement.addResourceTag(tag);
                this.saveRequirementJoinTag(requirement, tag);
            }
        }
    }

    private void saveRequirementJoinTag(ResourceRequirement requirement, ResourceTag tag){
        if(this.resourceRequirementJoinResourceTagRepository.countByResourceRequirementIdAndResourceTagIdCount(requirement.getId(), tag.getId()) == 0){
            ResourceRequirementJoinResourceTag result = new ResourceRequirementJoinResourceTag();
            result.setResourceRequirementId(requirement.getId());
            result.setResourceTagId(tag.getId());
            this.resourceRequirementJoinResourceTagRepository.save(result);
        }
    }

    private void saveOfferJoinTag(ResourceOffer offer, ResourceTag tag){
        if(this.resourceOfferJoinResourceTagRepository.countByResourceOfferIdAndResourceTagIdCount(offer.getId(), tag.getId()) == 0){
            ResourceOfferJoinResourceTag resOfferToTag = new ResourceOfferJoinResourceTag();
            resOfferToTag.setResourceOfferId(offer.getId());
            resOfferToTag.setResourceTagId(tag.getId());
            this.resourceOfferJoinResourceTagRepository.save(resOfferToTag);
        }
    }

    private ResourceTag saveResourceTag(String tagName){
        ResourceTag tag = null;
        List<ResourceTag> list = this.resourceTagRepository.findByName(tagName);
        if(list.size() == 0){
            tag = new ResourceTag();
            tag.setName(tagName);
            this.resourceTagRepository.save(tag);
            tag.setIsNewEntry(true);
        }
        else{
            tag = list.get(0);
            tag.setIsNewEntry(false);
        }

        return tag;
    }
    //endregion
}
