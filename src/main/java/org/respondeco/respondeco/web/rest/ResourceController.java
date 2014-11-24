package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.domain.ResourceRequirement;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.ResourcesService;
import org.respondeco.respondeco.service.exception.ResourceJoinTagException;
import org.respondeco.respondeco.service.exception.ResourceException;
import org.respondeco.respondeco.service.exception.ResourceTagException;
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
    @RequestMapping(value = "/rest/organisations/resourceOffers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceOfferDTO> getAllResourceOffer() {
        log.debug("REST request to get all resource offer");
        return this.resourcesService.getAllOffers();
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organisations/{organisationId}/resourceOffers/",
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
    @RequestMapping(value = "/rest/organisations/resourceOffer",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createResourceOffer(@RequestBody ResourceOfferDTO resourceOfferDTO) {
        ResponseEntity<ResourceOfferDTO> result = null;
        String message = null;
        try {
            ResourceOffer offer = this.resourcesService.createOffer(
                resourceOfferDTO.getAmount(),
                resourceOfferDTO.getDescription(),
                resourceOfferDTO.getOrganisationId(),
                resourceOfferDTO.getResourceTags()
            );
            resourceOfferDTO.setId(offer.getId());
            result = new ResponseEntity<ResourceOfferDTO>(resourceOfferDTO, HttpStatus.CREATED);

        } catch (ResourceTagException e) {
            message = e.getMessage();
        } catch (ResourceJoinTagException e) {
            message = e.getMessage();
        } catch (Exception e) {
            message = String.format("Unexpected error. Couldn't save Resource Offer with description '%s' and Organisation Id: %d",
                resourceOfferDTO.getDescription(), resourceOfferDTO.getOrganisationId());
        } finally {
            if (message != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("errorMessage", message);
                result = new ResponseEntity<ResourceOfferDTO>(headers, HttpStatus.BAD_REQUEST);
                log.debug(message);
            }
        }
        return result;
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organisations/resourceOffers",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> updateResourceOffer(@RequestBody ResourceOfferDTO resourceOfferDTO) {
        String message = null;
        ResponseEntity<?> result = null;
        try {
            this.resourcesService.updateOffer(
                resourceOfferDTO.getId(),
                resourceOfferDTO.getAmount(),
                resourceOfferDTO.getDescription(),
                resourceOfferDTO.getResourceTags()
            );
            result = new ResponseEntity<>(HttpStatus.OK);
        } catch (ResourceTagException e) {
            message = e.getMessage();
        } catch (ResourceJoinTagException e) {
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
    @RequestMapping(value = "/rest/organisations/resourceOffers/{resourceOfferId}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteResourceOffer(@PathVariable Long resourceOfferId) {
        log.debug("REST request to delete resourceOffer : {}", resourceOfferId);
        String message = null;
        ResponseEntity<?> result = null;
        try{
            this.resourcesService.deleteOffer(resourceOfferId);
            result = new ResponseEntity<>(HttpStatus.OK);
        }
        catch (ResourceException e){
            message = e.getMessage();
        }
        catch (Exception e){
            message = String.format("Unexpected error. Trying to delete Resource Reqirement for ID: %d failed", resourceOfferId);
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

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organisations/{organisationId}/resourceOffers/",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void deleteResourceOfferByOrganisationId(@PathVariable Long organisationId) {
        log.debug("REST request to delete resourceOffer : {}", organisationId);
        //this.resourcesService.deleteOffer(organisationId);
    }

    //endregion

    //region Requirements
    /*
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/resourceRequirements/",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceRequirementDTO> getAllResourceRequirement() {
        log.debug("REST request to get all resource requirements");
        return this.resourcesService.getAllRequirements();
    }
    */

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/projects/{projectId}/resourceRequirements",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceRequirementDTO> getAllResourceRequirement(@PathVariable Long projectId) {
        log.debug("REST request to get all resource requirements belongs to project id:{}", projectId);
        return this.resourcesService.getAllRequirements(projectId);
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/projects/resourceRequirements",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createResourceRequirement(@RequestBody ResourceRequirementDTO resourceRequirementDTO) {
        ResourceRequirement requirement = null;
        ResponseEntity<ResourceRequirementDTO> result = null;
        String message = null;
        try {
            requirement = this.resourcesService.createRequirement(
                resourceRequirementDTO.getAmount(),
                resourceRequirementDTO.getDescription(),
                resourceRequirementDTO.getProjectId(),
                resourceRequirementDTO.getIsEssential(),
                resourceRequirementDTO.getResourceTags()
            );
            resourceRequirementDTO.setId(requirement.getId());
            result = new ResponseEntity<ResourceRequirementDTO>(resourceRequirementDTO, HttpStatus.CREATED);

        } catch (ResourceTagException e) {
            message = e.getMessage();//String.format("Couldn't save Resource Tag for the resource requirement with description '%s' and Project id: %d", resourceRequirementDTO.getDescription(), resourceRequirementDTO.getProjectId());
        } catch (ResourceJoinTagException e) {
            message = e.getMessage();//String.format("Couldn't save link between Resource Tag and Requirement for the requirement with description '%s' and Project id: %d", resourceRequirementDTO.getDescription(), resourceRequirementDTO.getProjectId());
        } catch (Exception e) {
            message = String.format("Unexpected error. Couldn't save Resource Requirement with description '%s' and Project id: %d",
                resourceRequirementDTO.getDescription(), resourceRequirementDTO.getProjectId());
        } finally {
            if (message != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("errorMessage", message);
                result = new ResponseEntity<ResourceRequirementDTO>(headers, HttpStatus.BAD_REQUEST);
                log.debug(message);
            }
        }
        return result;
    }

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/projects/resourceRequirements",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> updateResourceRequirement(@RequestBody ResourceRequirementDTO resourceRequirementDTO) {
        String message = null;
        ResponseEntity<?> result = null;
        try {
            this.resourcesService.updateRequirement(
                resourceRequirementDTO.getId(),
                resourceRequirementDTO.getAmount(),
                resourceRequirementDTO.getDescription(),
                resourceRequirementDTO.getIsEssential(),
                resourceRequirementDTO.getResourceTags()
            );
            result = new ResponseEntity<>(HttpStatus.OK);
        } catch (ResourceTagException e) {
            message = e.getMessage();//String.format("Couldn't save Resource Tag for the resource requirement: %d", resourceRequirementDTO.getId());
        } catch (ResourceJoinTagException e) {
            message = e.getMessage();//String.format("Couldn't save link between Resource Tag and Requirement for the requirement %d", resourceRequirementDTO.getId());
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
    @RequestMapping(value = "/rest/projects/resourceRequirements/{resourceRequirementId}",
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
        catch (ResourceException e){
            message = e.getMessage();//String.format("Couldn't delete Resource Reqirement for ID: %d", resourceRequirementId);
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

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/projects/{projectId}/resourceRequirements",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void deleteAllResourceRequirement(@PathVariable Long projectId) {
        log.debug("REST request to delete resourceOffer : {}");
        //this.resourcesService.deleteRequirement(projectId);
    }
    //endregion
}
