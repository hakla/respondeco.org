package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ResourceMatch;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.domain.ResourceRequirement;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.ResourceService;
import org.respondeco.respondeco.service.exception.GeneralResourceException;
import org.respondeco.respondeco.service.exception.ResourceException;
import org.respondeco.respondeco.web.rest.dto.ProjectApplyDTO;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.MatchAlreadyExistsException;
import org.respondeco.respondeco.web.rest.dto.ResourceMatchRequestDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceOfferDTO;
import org.respondeco.respondeco.web.rest.util.ErrorHelper;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Created by Roman Kern on 18.11.14.
 */
@RestController
@Transactional
@RequestMapping("/app")
public class ResourceController {
    //region Private variables
    private final Logger log = LoggerFactory.getLogger(ResourceController.class);

    private ResourceService resourceService;
    //endregion

    //region Constructor

    @Inject
    public ResourceController(ResourceService resourceService){
        this.resourceService = resourceService;
    }

    //endregion

    //region Offer Controller

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceOffers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceOfferDTO> getAllResourceOffer(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String organization,
        @RequestParam(required = false) String tags,
        @RequestParam(required = false) Boolean available,
        @RequestParam(required = false) Boolean commercial,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) String fields,
        @RequestParam(required = false) String order) {

        log.debug("REST request to get all resource offer");

        if(name == null) name = "";
        if(organization == null) organization = "";
        if(tags == null) tags = "";
        if(available == null) available = false;


        RestParameters restParameters = new RestParameters(page, pageSize, order, fields);
        List<ResourceOffer> entries = resourceService.getAllOffers(name, organization, tags, available, commercial, restParameters);

        List<ResourceOfferDTO> result = new ArrayList<ResourceOfferDTO>();
        if(entries.isEmpty() == false) {
            for (ResourceOffer offer :entries) {
                result.add(new ResourceOfferDTO(offer));
            }
        }

        return result;
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceOffers/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResourceOfferDTO> getResourceOffer(@PathVariable Long id) {
        log.debug("REST request to get resource with id " + id);
        ResponseEntity<ResourceOfferDTO> responseEntity;

        try {
            ResourceOfferDTO resourceOfferDTO = new ResourceOfferDTO(this.resourceService.getOfferById(id));
            responseEntity = new ResponseEntity<>(resourceOfferDTO, HttpStatus.OK);

        } catch (GeneralResourceException e) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }


    /**
     * Create Claim ResourceOffer Request
     * @param resourceMatchRequestDTO
     * @return
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
        } catch (IllegalValueException e) {
            log.error("Could not create match for claiming offer : {}", resourceMatchRequestDTO, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
    }

    /**
     * Create offer ResourceOffer Request
     * @param ProjectApplyDTO
     * @return
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceoffers",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> offerResourceOffer(@RequestBody ProjectApplyDTO projectApplyDTO) {
        log.debug("REST request to offer ResourceOffer : " + projectApplyDTO);
        ResponseEntity<?> responseEntity;
        try {
            ResourceMatch resourceMatch = resourceService.createOfferResourceOffer(
                projectApplyDTO.getResourceOfferId(),
                projectApplyDTO.getResourceRequirementId(),
                projectApplyDTO.getOrganizationId(),
                projectApplyDTO.getProjectId()
            );

            log.debug("ResourceOffer: " + resourceMatch.getResourceOffer().getId());

            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        } catch (ResourceException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("errorMessage", e.getMessage());
            responseEntity = new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
    }


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

        try {
            ResourceMatch resourceMatch = resourceService.answerResourceRequest(id, resourceMatchRequestDTO.isAccepted());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        }
        catch (ResourceException e){
            HttpHeaders headers = new HttpHeaders();
            headers.add("errorMessage", e.getMessage());
            responseEntity = new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
    }


    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceOffers",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Valid
    public ResponseEntity<?> createResourceOffer(@RequestBody ResourceOfferDTO resourceOfferDTO) throws Exception{
        ResponseEntity<ResourceOfferDTO> result = null;
        String message = null;
        try {
            ResourceOffer offer = this.resourceService.createOffer(
                resourceOfferDTO.getName(),
                resourceOfferDTO.getAmount(),
                resourceOfferDTO.getDescription(),
                resourceOfferDTO.getOrganizationId(),
                resourceOfferDTO.getIsCommercial(),
                resourceOfferDTO.getIsRecurrent(),
                resourceOfferDTO.getStartDate(),
                resourceOfferDTO.getEndDate(),
                resourceOfferDTO.getResourceTags()
            );
            resourceOfferDTO.setId(offer.getId());
            result = new ResponseEntity<>(resourceOfferDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            message = String.format("Unexpected error. Couldn't save Resource Offer with description '%s' and Organisation Id: %d.",
                resourceOfferDTO.getDescription(), resourceOfferDTO.getOrganizationId());
        } finally {
            if (message != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("errorMessage", message);
                result = new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
                log.debug(message);
            }
        }
        return result;
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceOffers/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Valid
    public ResponseEntity<?> updateResourceOffer(@PathVariable Long id, @RequestBody ResourceOfferDTO resourceOfferDTO) {
        String message = null;
        ResponseEntity<?> result = null;
        try {
            this.resourceService.updateOffer(
                resourceOfferDTO.getId(),
                resourceOfferDTO.getOrganizationId(),
                resourceOfferDTO.getName(),
                resourceOfferDTO.getAmount(),
                resourceOfferDTO.getDescription(),
                resourceOfferDTO.getIsCommercial(),
                resourceOfferDTO.getIsRecurrent(),
                resourceOfferDTO.getStartDate(),
                resourceOfferDTO.getEndDate(),
                resourceOfferDTO.getResourceTags()
            );
            result = new ResponseEntity<>(HttpStatus.OK);
        } catch (GeneralResourceException e){
            message = e.getMessage();
        } catch (Exception e) {
            message = String.format("Unexpected error. Couldn't update Resource Offer with %d", resourceOfferDTO.getId());
        } finally {
            if (message != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("errorMessage", message);
                result = new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
                log.debug(message);
            }
        }
        return result;
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceOffers/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteResourceOffer(@PathVariable Long id) {
        log.debug("REST request to delete resourceOffer : {}", id);
        String message = null;
        ResponseEntity<?> result = null;
        try{
            this.resourceService.deleteOffer(id);
            result = new ResponseEntity<>(HttpStatus.OK);
        } catch (GeneralResourceException e){
            message = e.getMessage();
        }
        catch (Exception e){
            message = String.format("Unexpected error. Trying to delete Resource Reqirement for ID: %d failed", id);
        }
        finally {
            if (message != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("errorMessage", message);
                result = new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
                log.debug(message);
            }
        }
        return result;
    }

    //endregion

    //region Requirements

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceRequirements",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceRequirementRequestDTO> getAllResourceRequirement() {
        log.debug("REST request to get all resource requirements");
        List<ResourceRequirementRequestDTO> response = new ArrayList<>();
        for(ResourceRequirement resourceRequirement : resourceService.getAllRequirements()) {
            response.add(new ResourceRequirementRequestDTO(resourceRequirement));
        }
        return response;
    }


    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceRequirements",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createResourceRequirement(@RequestBody ResourceRequirementRequestDTO resourceRequirementRequestDTO) throws Exception {
        ResourceRequirement requirement = null;
        ResponseEntity<ResourceRequirementRequestDTO> result = null;
        String message = null;
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

        } catch (GeneralResourceException e){
            message = e.getMessage();
        } catch (Exception e) {
            message = String.format("Unexpected error. Couldn't save Resource Requirement with description '%s' and Project id: %d",
                resourceRequirementRequestDTO.getDescription(), resourceRequirementRequestDTO.getProjectId());
        } finally {
            if (message != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("errorMessage", message);
                result = new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
                log.debug(message);
            }
        }
        return result;
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceRequirements/{resourceRequirementId}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> updateResourceRequirement(@PathVariable Long resourceRequirementId, @RequestBody ResourceRequirementRequestDTO resourceRequirementRequestDTO) {
        String message = null;
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
        } catch (GeneralResourceException e){
            message = e.getMessage();
        } catch (Exception e) {
            message = String.format("Unexpected error. Couldn't update Resource Requirement with %d", resourceRequirementRequestDTO.getId());
        } finally {
            if (message != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("errorMessage", message);
                result = new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
                log.debug(message);
            }
        }
        return result;
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceRequirements/{resourceRequirementId}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteResourceRequirement(@PathVariable Long resourceRequirementId) {
        log.debug("REST request to delete resourceOffer : {}");
        String message = null;
        ResponseEntity<?> result = null;
        try{
            this.resourceService.deleteRequirement(resourceRequirementId);
            result = new ResponseEntity<>(HttpStatus.OK);
        }
        catch (GeneralResourceException e){
            message = e.getMessage();
        }
        catch (Exception e){
            message = String.format("Unexpected error. Trying to delete Resource Reqirement for ID: %d failed", resourceRequirementId);
        }
        finally {
            if (message != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("errorMessage", message);
                result = new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
                log.debug(message);
            }
        }
        return result;
    }
    //endregion
}
