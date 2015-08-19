package org.respondeco.respondeco.web.rest;

import org.respondeco.respondeco.aop.RESTWrapped;
import org.respondeco.respondeco.service.UtilityService;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Created by Clemens Puehringer on 17/08/15.
 */

/**
 * REST Controller for various small REST functionality
 * Only functionality that is not sufficiently extensive to warrant its own
 * Controller should go here
 */
@RestController
@RequestMapping("/app")
@Transactional
public class UtilityController  {

    private UtilityService utilityService;

    @Inject
    public UtilityController(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    @RequestMapping(value = "/rest/isocategories",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RESTWrapped
    public Object getISOCategories() {
        return utilityService.getISOCategories();
    }
}
