package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.repository.ResourceOfferRepository;
import org.respondeco.respondeco.repository.ResourceRequirementRepository;
import org.respondeco.respondeco.repository.ResourceTagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;

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



    @Inject
    public ResourcesService(ResourceOfferRepository resourceOfferRepository,
                            ResourceRequirementRepository resourceRequirementRepository,
                            ResourceTagRepository resourceTagRepository) {
        this.resourceOfferRepository = resourceOfferRepository;
        this.resourceRequirementRepository = resourceRequirementRepository;
        this.resourceTagRepository = resourceTagRepository;
    }

    public ResourceOffer CreateOffer(BigDecimal amount, String description, Long organisationId, Long[] resourceTags){
        ResourceOffer newOffer = null;
        if(this.resourceOfferRepository.findByDescriptionAndOrganisationId(description, organisationId) == null) {
            newOffer = new ResourceOffer();
            newOffer.setAmount(amount);
            newOffer.setDescription(description);
            newOffer.setOrganisationId(organisationId);

            //TODO: save resource Tags
        }
        else{
            log.debug("Offer with same description already exists (ID: {}, Description:{})");
        }

        return newOffer;
    }
}
