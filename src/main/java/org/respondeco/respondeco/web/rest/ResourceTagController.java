package org.respondeco.respondeco.web.rest;

/**
 * Created by clemens on 21/01/15.
 */

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.aop.RESTWrapped;
import org.respondeco.respondeco.domain.ResourceTag;
import org.respondeco.respondeco.service.PropertyTagService;
import org.respondeco.respondeco.service.ResourceTagService;
import org.respondeco.respondeco.web.rest.dto.PropertyTagResponseDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceTagResponseDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * REST controller for managing ResourceTag.
 */
@RestController
@RequestMapping("/app")
public class ResourceTagController {

    private final Logger log = LoggerFactory.getLogger(ResourceTagController.class);

    private ResourceTagService resourceTagService;

    @Inject
    public ResourceTagController(ResourceTagService resourceTagService) {
        this.resourceTagService = resourceTagService;
    }

    /**
     * GET  /rest/names/resourcetags -> get resourcetags.
     */
    @RequestMapping(value = "/rest/resourcetags",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getTagsMatching(
        @RequestParam(required = false) String filter,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) String order,
        @RequestParam(required = false) String fields) {
        log.debug("REST request to get ResourceTags");
        RestParameters params = new RestParameters(page, pageSize, order, fields);
        return resourceTagService.getResourceTags(filter, params);
    }

}
