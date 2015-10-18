package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.aop.RESTWrapped;
import org.respondeco.respondeco.domain.ResourceMatch;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.domain.ResourceRequirement;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.ResourceService;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.web.rest.dto.ResourceMatchRequestDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Arrays;

/**
 * ResourceController
 *
 * This REST-Controller handles all requests for resourceoffers and resourcerequirements
 */
@RestController
@Transactional
@RequestMapping("/app")
public class ResourceController {
    private final Logger log = LoggerFactory.getLogger(ResourceController.class);

    private ResourceService resourceService;

    @Inject
    public ResourceController(ResourceService resourceService){
        this.resourceService = resourceService;
    }


    /**
     * Return all ResourceOffers
     * @param name filter for name, organization and tags
     * @param commercial if true then only commercial resources will be returned
     *                   if false then only non-commercial resources will be returned
     *                   if null both - commercial and non-commercial resources - will be returned
     * @param page number of the page to be returned
     * @param pageSize size of the returned page
     * @param fields defines which fields will be returned
     * @param order defines the order of the returned fields
     * @return ResponseEntity containing a ResourceOfferPaginationResponseDTO object
     */
    @PermitAll
    @RequestMapping(value = "/rest/resourceoffers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getAllResourceOffers(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean commercial,
        @RequestParam(required = true) Integer page,
        @RequestParam(required = true) Integer pageSize,
        @RequestParam(required = false) String fields,
        @RequestParam(required = false) String order) {

        log.debug("REST request to get all resource offers: {}", name, commercial, page, pageSize, fields, order);

        if(name == null) name = "";

        RestParameters restParameters = new RestParameters(page, pageSize, order, fields);
        log.debug(restParameters.toString());

        return resourceService.getOffers(name, commercial, restParameters.buildPageRequest());
    }

    /**
     * Returns ResourceOffer with given id, wrapped in a ResourceOfferResponse DTO
     * @param id resourceoffer id
     * @return ResourceOffer wrapped in ResourceOfferResponseDTO
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceoffers/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getResourceOffer(@PathVariable Long id) {
        log.debug("REST request to get resource with id " + id);
        return resourceService.getOfferById(id);
    }


    /**
     * Create a Request to claim a Resourceoffer
     * @param resourceMatchRequestDTO information of the requested ResourceOffer
     * @return ResourceOffer
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourcerequests",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object claimResourceOffer(@RequestBody ResourceMatchRequestDTO resourceMatchRequestDTO) {
        log.debug("REST request to claim ResourceOffer : " + resourceMatchRequestDTO);

        ResourceMatch resourceMatch = null;
        return resourceService.createClaimResourceRequest(
            resourceMatchRequestDTO.getResourceOfferId(),
            resourceMatchRequestDTO.getResourceRequirementId());
    }


    /**
     * Answer the request for the claimed resourceoffer
     * @param id id of the resourcematch containing the request
     * @param resourceMatchRequestDTO ResourceMatch containing the resource request
     * @return ResponseEntity with HTTPStatus
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourcerequests/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> answerResourceRequest(
        @PathVariable Long id,
        @RequestBody ResourceMatchRequestDTO resourceMatchRequestDTO) {
        log.debug("REST request to accept or decline resource request. accept = " + resourceMatchRequestDTO.isAccepted());
        ResponseEntity<?> responseEntity;

        try{
            resourceService.answerResourceRequest(id, resourceMatchRequestDTO.isAccepted() );
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        }catch (OperationForbiddenException e) {
            log.error("Operation forbidden: ", e);
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch(IllegalValueException ex) {
            log.error("Could not set isAccepted for resourceMatch with id " + id);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
    }


    /**
     * Create a new ResourceOffer
     * @return ResponseEntity with HTTPStatus
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceoffers",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object createResourceOffer(@RequestBody ResourceOffer resourceOffer) throws Exception{
        return resourceService.createOffer(resourceOffer);
    }

    /**
     * Update a ResourceOffer
     * @param id id of the ResourceOffer which will be updated
     * @return ResponseEntity with HTTPStatus
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceoffers/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object updateResourceOffer(@PathVariable Long id, @RequestBody ResourceOffer resourceOffer) {
        return resourceService.updateOffer(resourceOffer);
    }

    /**
     * Delete ResourceOffer -> Sets ResourceOffer isActive to false
     * @param id id of ResourceOffer which will be deleted
     * @return ResponseEntity with HTTPStatus
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceoffers/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public void deleteResourceOffer(@PathVariable Long id) {
        log.debug("REST request to delete resource offer : {}", id);
        this.resourceService.deleteOffer(id);
    }

    /**
     * Returns all ResourceRequirements wrapped in a ResourceRequirementRequestDTO
     * @return List of ResourceRequirements represented by DTO
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourcerequirements",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getAllResourceRequirement() {
        log.debug("REST request to get all resource requirements");
        return resourceService.getRequirements();
    }


    /**
     * Creates a new ResourceRequirement
     * @return ResponseEntity with HttpStatus
     * @throws Exception
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourcerequirements",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object createResourceRequirement(@RequestBody ResourceRequirement resourceRequirement)
        throws Exception {
        //TODO: check if this method serves a purpose and fix service call
        return resourceService.getUpdatedRequirements(resourceRequirement.getProject(), Arrays.asList(resourceRequirement));
    }

    /**
     * Updates a ResourceRequirement
     * @param resourceRequirementId id of the resource requirement
     * @return ResponseEntity with HttpStatus
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourcerequirements/{resourceRequirementId}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Object updateResourceRequirement(@PathVariable Long resourceRequirementId,
                                            @RequestBody ResourceRequirement resourceRequirement) {
        //TODO: check if this method serves a purpose and fix service call
        return resourceService.getUpdatedRequirements(resourceRequirement.getProject(), Arrays.asList(resourceRequirement));
    }

    /**
     * Delete a Resource Requirement
     * @param resourceRequirementId id of the resource requirement
     * @return ResponseEntity with HttpStatus
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourcerequirements/{resourceRequirementId}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public void deleteResourceRequirement(@PathVariable Long resourceRequirementId) {
        log.debug("REST request to delete resourceOffer : {}");
        this.resourceService.deleteRequirement(resourceRequirementId);
    }
}
