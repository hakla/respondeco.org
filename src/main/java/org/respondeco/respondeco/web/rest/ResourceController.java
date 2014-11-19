package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.domain.ResourceRequirement;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.ResourcesService;
import org.respondeco.respondeco.web.rest.dto.ResourceOfferDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by Roman Kern on 18.11.14.
 */
@RestController
@RequestMapping("/app")
public class ResourceController {
    //region Private variables
    private final Logger log = LoggerFactory.getLogger(ResourceController.class);

    private ResourcesService resourcesService;
    //endregion

    //region Constructor

    @Inject
    public ResourceController(ResourcesService resourcesService){
        this.resourcesService = resourcesService;
    }

    //endregion

    //region Offer Controller

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceOffers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceOfferDTO> getAllResourceOffer() {
        log.debug("REST request to get all resource offer");
        return this.resourcesService.getAllOffers();
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceOffers/{organisationId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceOfferDTO> getAllResourceOffer(@PathVariable Long organisationId) {
        log.debug("REST request to get all resource offer belongs to Organization id: {}", organisationId);
        return this.resourcesService.getAllOffers(organisationId);
    }

    /*
    Create new Resource offer!
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceOffer",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResourceOfferDTO createResourceOffer(@RequestBody ResourceOfferDTO resourceOfferDTO){
        ResourceOffer offer = this.resourcesService.createOffer(
            resourceOfferDTO.getAmount(),
            resourceOfferDTO.getDescription(),
            resourceOfferDTO.getOrganisationId(),
            resourceOfferDTO.getResourceTags()
        );

        resourceOfferDTO.setId(offer.getId());
        return resourceOfferDTO;
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceOffer/update",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void updateResourceOffer(@RequestBody ResourceOfferDTO resourceOfferDTO){
        this.resourcesService.updateOffer(
            resourceOfferDTO.getId(),
            resourceOfferDTO.getAmount(),
            resourceOfferDTO.getDescription(),
            resourceOfferDTO.getResourceTags()
        );
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceOffer/delete/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void deleteResourceOffer(@PathVariable Long id) {
        log.debug("REST request to delete resourceOffer : {}");
        this.resourcesService.deleteOffer(id);
    }

    //endregion

    //region Requirements

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceRequirements",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceRequirementDTO> getAllResourceRequirement() {
        log.debug("REST request to get all resource requirements");
        return this.resourcesService.getAllRequirements();
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceRequirements/{projectId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceRequirementDTO> getAllResourceRequirement(@PathVariable Long projectId) {
        log.debug("REST request to get all resource requirements belongs to project id:{}", projectId);
        return this.resourcesService.getAllRequirements(projectId);
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceRequirement",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResourceRequirementDTO createResourceRequirement(@RequestBody ResourceRequirementDTO resourceRequirementDTO){
        ResourceRequirement requirement = this.resourcesService.createRequirement(
            resourceRequirementDTO.getAmount(),
            resourceRequirementDTO.getDescription(),
            resourceRequirementDTO.getProjectId(),
            resourceRequirementDTO.getIsEssential(),
            resourceRequirementDTO.getResourceTags()
        );
        resourceRequirementDTO.setId(requirement.getId());
        return resourceRequirementDTO;
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceRequirement/update",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void updateResourceRequirement(@RequestBody ResourceRequirementDTO resourceRequirementDTO){
        this.resourcesService.updateRequirement(
            resourceRequirementDTO.getId(),
            resourceRequirementDTO.getAmount(),
            resourceRequirementDTO.getDescription(),
            resourceRequirementDTO.getIsEssential(),
            resourceRequirementDTO.getResourceTags()
        );
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceRequirement/delete/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void deleteResourceRequirement(@PathVariable Long id) {
        log.debug("REST request to delete resourceOffer : {}");
        this.resourcesService.deleteRequirement(id);
    }
    //endregion
}
