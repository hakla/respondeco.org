package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.domain.ResourceRequirement;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.ResourcesService;
import org.respondeco.respondeco.service.exception.GeneralResourceException;
import org.respondeco.respondeco.web.rest.dto.ResourceOfferDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
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

    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RequestMapping(value = "/rest/resourceOffers",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Valid
    public ResponseEntity<?> createResourceOffer(@RequestBody ResourceOfferDTO resourceOfferDTO) throws Exception{
        ResponseEntity<ResourceOfferDTO> result = null;
        String message = null;
        try {
            ResourceOffer offer = this.resourcesService.createOffer(
                resourceOfferDTO.getName(),
                resourceOfferDTO.getAmount(),
                resourceOfferDTO.getDescription(),
                resourceOfferDTO.getOrganizationId(),
                resourceOfferDTO.getIsCommercial(),
                resourceOfferDTO.getIsRecurrent(),
                resourceOfferDTO.getStartDateAsDateTime(),
                resourceOfferDTO.getEndDateAsDateTime(),
                resourceOfferDTO.getResourceTags()
            );
            resourceOfferDTO.setId(offer.getId());
            result = new ResponseEntity<>(resourceOfferDTO, HttpStatus.CREATED);

        } catch (GeneralResourceException e){
            message = e.getMessage();
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

    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RequestMapping(value = "/rest/resourceOffers/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Valid
    public ResponseEntity<?> updateResourceOffer(@PathVariable Long id, @RequestBody ResourceOfferDTO resourceOfferDTO) {
        String message = null;
        ResponseEntity<?> result = null;
        try {
            this.resourcesService.updateOffer(
                resourceOfferDTO.getId(),
                resourceOfferDTO.getOrganizationId(),
                resourceOfferDTO.getName(),
                resourceOfferDTO.getAmount(),
                resourceOfferDTO.getDescription(),
                resourceOfferDTO.getIsCommercial(),
                resourceOfferDTO.getIsRecurrent(),
                resourceOfferDTO.getStartDateAsDateTime(),
                resourceOfferDTO.getEndDateAsDateTime(),
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

    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RequestMapping(value = "/rest/resourceOffers/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteResourceOffer(@PathVariable Long id) {
        log.debug("REST request to delete resourceOffer : {}", id);
        String message = null;
        ResponseEntity<?> result = null;
        try{
            this.resourcesService.deleteOffer(id);
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
    public List<ResourceRequirementDTO> getAllResourceRequirement() {
        log.debug("REST request to get all resource requirements");
        return this.resourcesService.getAllRequirements();
    }


    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RequestMapping(value = "/rest/resourceRequirements",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createResourceRequirement(@RequestBody ResourceRequirementDTO resourceRequirementDTO) throws Exception {
        ResourceRequirement requirement = null;
        ResponseEntity<ResourceRequirementDTO> result = null;
        String message = null;
        try {
            requirement = this.resourcesService.createRequirement(
                resourceRequirementDTO.getName(),
                resourceRequirementDTO.getAmount(),
                resourceRequirementDTO.getDescription(),
                resourceRequirementDTO.getProjectId(),
                resourceRequirementDTO.getIsEssential(),
                resourceRequirementDTO.getResourceTags()
            );
            resourceRequirementDTO.setId(requirement.getId());
            result = new ResponseEntity<>(resourceRequirementDTO, HttpStatus.CREATED);

        } catch (GeneralResourceException e){
            message = e.getMessage();
        } catch (Exception e) {
            message = String.format("Unexpected error. Couldn't save Resource Requirement with description '%s' and Project id: %d",
                resourceRequirementDTO.getDescription(), resourceRequirementDTO.getProjectId());
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

    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RequestMapping(value = "/rest/resourceRequirements/{resourceRequirementId}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> updateResourceRequirement(@PathVariable Long resourceRequirementId, @RequestBody ResourceRequirementDTO resourceRequirementDTO) {
        String message = null;
        ResponseEntity<?> result = null;
        try {
            this.resourcesService.updateRequirement(
                resourceRequirementDTO.getId(),
                resourceRequirementDTO.getName(),
                resourceRequirementDTO.getAmount(),
                resourceRequirementDTO.getDescription(),
                resourceRequirementDTO.getIsEssential(),
                resourceRequirementDTO.getResourceTags()
            );
            result = new ResponseEntity<>(HttpStatus.OK);
        } catch (GeneralResourceException e){
            message = e.getMessage();
        } catch (Exception e) {
            message = String.format("Unexpected error. Couldn't update Resource Requirement with %d", resourceRequirementDTO.getId());
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
            this.resourcesService.deleteRequirement(resourceRequirementId);
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
