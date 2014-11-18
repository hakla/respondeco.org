package org.respondeco.respondeco.service;

import org.hibernate.id.BulkInsertionCapableIdentifierGenerator;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
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

    private final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    private ResourceOfferRepository resourceOfferRepository;

    private ResourceRequirementRepository resourceRequirementRepository;

    private ResourceTagRepository resourceTagRepository;

    private ResourceOfferJoinResourceRequirementRepository resourceOfferJoinResourceRequirementRepository;

    private ResourceOfferJoinResourceTagRepository resourceOfferJoinResourceTagRepository;

    private ResourceRequirementJoinResourceTagRepository resourceRequirementJoinResourceTagRepository;



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

    public ResourceRequirement createRequirement(BigDecimal amount, String description, Long projectId, Boolean isEssential, String[] resourceTags){
        ResourceRequirement newRequirement = null;
        if(this.resourceRequirementRepository.findByDescriptionAndProjectId(description, projectId) == null){
            newRequirement.setAmount(amount);
            newRequirement.setDescription(description);
            newRequirement.setProjectId(projectId);
            newRequirement.setIsEssential(isEssential);

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

    public ResourceRequirement updateRequirement(Long id, BigDecimal amount, String description, Long projectId, Boolean isEssential, String[] resourceTags){
        ResourceRequirement requirement = this.resourceRequirementRepository.findOne(id);
        if(requirement != null){
            requirement.setAmount(amount);
            requirement.setDescription(description);
            requirement.setProjectId(projectId);
            requirement.setIsEssential(isEssential);

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

    public ResourceOffer createOffer(BigDecimal amount, String description, Long organisationId, String[] resourceTags){
        ResourceOffer newOffer = null;
        if(this.resourceOfferRepository.findByDescriptionAndOrganisationId(description, organisationId) == null) {
            newOffer = new ResourceOffer();
            newOffer.setAmount(amount);
            newOffer.setDescription(description);
            newOffer.setOrganisationId(organisationId);

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

    private void saveOffersJoinTags(ResourceOffer offer, String[] resourceTags){
        for(String tagName: resourceTags){
            //save tags and add it to list
            ResourceTag tag = this.saveResourceTag(tagName);
            offer.addResourceTag(tag);
            //save offer to tag
            if(tag != null){
                this.saveOfferJoinTag(offer, tag);
            }
        }
    }

    private void saveRequirementJoinTags(ResourceRequirement requirement, String[] resourceTags){
        for(String tagName: resourceTags){
            //save tags and add it to list
            ResourceTag tag = this.saveResourceTag(tagName);
            requirement.addResourceTag(tag);
            //save requirement to tag
            if(tag != null){
                this.saveRequirementJoinTag(requirement, tag);
            }
        }
    }

    private void saveRequirementJoinTag(ResourceRequirement requirement, ResourceTag tag){
        if(this.resourceRequirementJoinResourceTagRepository.findByResourceRequirementIdAndResourceTagId(requirement.getId(), tag.getId()) == null){
            ResourceRequirementJoinResourceTag result = new ResourceRequirementJoinResourceTag();
            result.setResourceRequirementId(requirement.getId());
            result.setResourceTagId(tag.getId());
            this.resourceRequirementJoinResourceTagRepository.save(result);
        }
    }

    private void saveOfferJoinTag(ResourceOffer offer, ResourceTag tag){
        if(this.resourceOfferJoinResourceTagRepository.findByResourceOfferIdAndResourceTagId(offer.getId(), tag.getId()) == null){
            ResourceOfferJoinResourceTag resOfferToTag = new ResourceOfferJoinResourceTag();
            resOfferToTag.setResourceOfferId(offer.getId());
            resOfferToTag.setResourceTagId(tag.getId());
            this.resourceOfferJoinResourceTagRepository.save(resOfferToTag);
        }
    }

    private ResourceTag saveResourceTag(String tagName){
        ResourceTag tag = null;
        List<ResourceTag> list = this.resourceTagRepository.findByName(tagName);
        if(list == null){
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
}
