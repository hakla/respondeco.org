package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ResourceMatch;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.domain.ResourceRequirement;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.ResourceService;
import org.respondeco.respondeco.service.exception.GeneralResourceException;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.MatchAlreadyExistsException;
import org.respondeco.respondeco.web.rest.dto.ResourceMatchRequestDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceOfferDTO;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.dto.*;
import org.respondeco.respondeco.web.rest.util.ErrorHelper;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceoffers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResourceOfferPaginationResponseDTO> getAllResourceOffers(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean commercial,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) String fields,
        @RequestParam(required = false) String order) {

        log.debug("REST request to get all resource offers: {}", name, commercial, page, pageSize, fields, order);

        if(name == null) name = "";

        RestParameters restParameters = new RestParameters(page, pageSize, order, fields);
        log.debug(restParameters.toString());

        Page<ResourceOffer> resultPage = resourceService.getAllOffers(name, commercial, restParameters);

        ResponseEntity<ResourceOfferPaginationResponseDTO> responseEntity =
            new ResponseEntity<>(ResourceOfferPaginationResponseDTO.createFromPage(resultPage, restParameters.getFields()), HttpStatus.OK);

        return responseEntity;
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
    public ResponseEntity<ResourceOfferResponseDTO> getResourceOffer(@PathVariable Long id) {
        log.debug("REST request to get resource with id " + id);
        ResponseEntity<ResourceOfferResponseDTO> responseEntity;

        try {
            ResourceOffer resourceOffer = resourceService.getOfferById(id);

            ResourceOfferResponseDTO resourceOfferDTO = ResourceOfferResponseDTO.fromEntity(resourceOffer, null);
            responseEntity = new ResponseEntity<>(resourceOfferDTO, HttpStatus.OK);

        } catch (GeneralResourceException e) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return responseEntity;
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
    public ResponseEntity<?> claimResourceOffer(@RequestBody ResourceMatchRequestDTO resourceMatchRequestDTO) {
        log.debug("REST request to claim ResourceOffer : " + resourceMatchRequestDTO);
        ResponseEntity<?> responseEntity;

        ResourceMatch resourceMatch = null;
        try {
            resourceMatch = resourceService.createClaimResourceRequest(
            resourceMatchRequestDTO.getResourceOfferId(),
            resourceMatchRequestDTO.getResourceRequirementId());

            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);

        } catch (MatchAlreadyExistsException e) {
            log.error("Could not claim resource offer : {}", resourceMatchRequestDTO, e);
            responseEntity = ErrorHelper.buildErrorResponse(e.getInternationalizationKey(), e.getMessage());
        }catch (OperationForbiddenException e) {
            log.error("Could not create match for claiming offer: User is not Authorized : {}", resourceMatchRequestDTO, e);
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (IllegalValueException e) {
            log.error("Could not create match for claiming offer : {}", resourceMatchRequestDTO, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
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
     * @param resourceOfferDTO contains necessary information for the creation of a new ResourceOffer
     * @return ResponseEntity with HTTPStatus
     * @throws Exception
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceoffers",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Valid
    public ResponseEntity<?> createResourceOffer(@RequestBody ResourceOfferDTO resourceOfferDTO) throws Exception{
        ResponseEntity<?> result = null;
        try {
            ResourceOffer offer = this.resourceService.createOffer(
                resourceOfferDTO.getName(),
                resourceOfferDTO.getAmount(),
                resourceOfferDTO.getDescription(),
                resourceOfferDTO.getOrganizationId(),
                resourceOfferDTO.getIsCommercial(),
                resourceOfferDTO.getStartDate(),
                resourceOfferDTO.getEndDate(),
                resourceOfferDTO.getResourceTags(),
                resourceOfferDTO.getLogoId(),
                resourceOfferDTO.getPrice()
            );
            resourceOfferDTO.setId(offer.getId());
            result = new ResponseEntity<>(resourceOfferDTO, HttpStatus.CREATED);
        } catch (IllegalValueException e) {
            result = ErrorHelper.buildErrorResponse(e);
            log.error("Fail to create new Resource offer: {}", e);
        }
        return result;
    }

    /**
     * Update a ResourceOffer
     * @param id id of the ResourceOffer which will be updated
     * @param resourceOfferDTO contains information which will be used for updating the ResourceOffer
     * @return ResponseEntity with HTTPStatus
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceoffers/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Valid
    public ResponseEntity<?> updateResourceOffer(@PathVariable Long id, @RequestBody ResourceOfferDTO resourceOfferDTO) {
        ResponseEntity<?> result = null;
        try {
            this.resourceService.updateOffer(
                resourceOfferDTO.getId(),
                resourceOfferDTO.getOrganizationId(),
                resourceOfferDTO.getName(),
                resourceOfferDTO.getAmount(),
                resourceOfferDTO.getDescription(),
                resourceOfferDTO.getIsCommercial(),
                resourceOfferDTO.getStartDate(),
                resourceOfferDTO.getEndDate(),
                resourceOfferDTO.getResourceTags(),
                resourceOfferDTO.getLogoId(),
                resourceOfferDTO.getPrice()
            );
            result = new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalValueException e){
            result = ErrorHelper.buildErrorResponse(e);
            log.error("Could not update resource offer : {}", e);
        }
        return result;
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
    public ResponseEntity<?> deleteResourceOffer(@PathVariable Long id) {
        log.debug("REST request to delete resource offer : {}", id);
        String message = null;
        ResponseEntity<?> result = null;
        try{
            this.resourceService.deleteOffer(id);
            result = new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalValueException e){
            result = ErrorHelper.buildErrorResponse(e);
            log.error("Could not delete resource offer : {}", e);
        }
        return result;
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
    public List<ResourceRequirementRequestDTO> getAllResourceRequirement() {
        log.debug("REST request to get all resource requirements");
        List<ResourceRequirementRequestDTO> response = new ArrayList<>();

        List<ResourceRequirement> resourceRequirements = resourceService.getAllRequirements();
        if(resourceRequirements.isEmpty() == false) {
            ResourceRequirementResponseDTO.fromEntities(resourceRequirements, null);
        }
        return response;
    }


    /**
     * Creates a new ResourceRequirement
     * @param resourceRequirementRequestDTO ResourceRequirement to be saved in the db. Wrapped into RequestDTO.
     * @return ResponseEntity with HttpStatus
     * @throws Exception
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourcerequirements",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createResourceRequirement(@RequestBody ResourceRequirementRequestDTO resourceRequirementRequestDTO) throws Exception {
        ResourceRequirement requirement = null;
        ResponseEntity<?> result = null;
        try {
            requirement = this.resourceService.createRequirement(
                resourceRequirementRequestDTO.getName(),
                resourceRequirementRequestDTO.getAmount(),
                resourceRequirementRequestDTO.getDescription(),
                resourceRequirementRequestDTO.getProjectId(),
                resourceRequirementRequestDTO.getIsEssential(),
                resourceRequirementRequestDTO.getResourceTags()
            );
            resourceRequirementRequestDTO.setId(requirement.getId());
            result = new ResponseEntity<>(resourceRequirementRequestDTO, HttpStatus.CREATED);

        } catch (IllegalValueException e){
            result = ErrorHelper.buildErrorResponse(e);
            log.error("Could not create resource requirement : {}", e);
        }
        return result;
    }

    /**
     * Updates a ResourceRequirement
     * @param resourceRequirementId id of the resource requirement
     * @param resourceRequirementRequestDTO update information for the resource requirement
     * @return ResponseEntity with HttpStatus
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourcerequirements/{resourceRequirementId}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> updateResourceRequirement(@PathVariable Long resourceRequirementId, @RequestBody ResourceRequirementRequestDTO resourceRequirementRequestDTO) {
        ResponseEntity<?> result = null;
        try {
            this.resourceService.updateRequirement(
                resourceRequirementRequestDTO.getId(),
                resourceRequirementRequestDTO.getName(),
                resourceRequirementRequestDTO.getAmount(),
                resourceRequirementRequestDTO.getDescription(),
                resourceRequirementRequestDTO.getProjectId(),
                resourceRequirementRequestDTO.getIsEssential(),
                resourceRequirementRequestDTO.getResourceTags()
            );
            result = new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalValueException e){
            result = ErrorHelper.buildErrorResponse(e);
            log.error("Could not update resource requirement : {}", e);
        }
        return result;
    }

    /**
     * Delete a Resource Requirement
     * @param resourceRequirementId id of the resource requirement
     * @return ResponseEnetity with HttpStatus
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourcerequirements/{resourceRequirementId}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteResourceRequirement(@PathVariable Long resourceRequirementId) {
        log.debug("REST request to delete resourceOffer : {}");
        ResponseEntity<?> result = null;
        try{
            this.resourceService.deleteRequirement(resourceRequirementId);
            result = new ResponseEntity<>(HttpStatus.OK);
        }
        catch (IllegalValueException e){
            result = ErrorHelper.buildErrorResponse(e);
            log.error("Could not delete resource requirement : {}", e);
        }
        return result;
    }
}
