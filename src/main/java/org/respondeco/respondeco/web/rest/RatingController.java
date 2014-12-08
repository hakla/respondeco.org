package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.ApiOperation;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.RatingService;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.web.rest.dto.ProjectRequestDTO;
import org.respondeco.respondeco.web.rest.util.ErrorHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;

/**
 * Created by clemens on 08/12/14.
 */

@RestController
@RequestMapping("/app")
@Transactional
public class RatingController {

    private RatingService ratingService;

    @Inject
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * POST  /rest/ratings/permissions -> Get viable rating targets for the current user and the selected project
     */
    @ApiOperation(value = "Create a project",
        notes = "Get viable rating targets for the current user and the selected project")
    @RequestMapping(value = "/rest/ratings/permissions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> create(@RequestParam Long projectid) {
        ResponseEntity<?> responseEntity;
        responseEntity = new ResponseEntity<Object>(
            ratingService.getRatingPermissionsForProject(projectid), HttpStatus.OK);
        return responseEntity;
    }

}
