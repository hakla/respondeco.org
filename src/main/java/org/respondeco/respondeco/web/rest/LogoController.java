package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.Logo;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.repository.LogoRepository;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.service.LogoService;
import org.respondeco.respondeco.web.rest.dto.LogoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Logo.
 */
@RestController
@RequestMapping("/app")
public class LogoController {

    private final Logger log = LoggerFactory.getLogger(LogoController.class);

    @Inject
    private LogoRepository logoRepository;

    @Inject
    private LogoService logoService;

    @Inject
    private OrganizationRepository organizationRepository;

    /**
     * POST  /rest/logos -> Create a new logo.
     */
    @RequestMapping(value = "/rest/logos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody LogoDTO logo) throws UnsupportedEncodingException {
        log.debug("REST request to save Logo : {}", logo);
        logoService.createLogo(logo.getLabel(),logo.getData(),logo.getOrgId());
    }

    /**
     * GET  /rest/logos -> get all the logos.
     */
    @RequestMapping(value = "/rest/logos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Logo> getAll() {
        log.debug("REST request to get all Logos");
        return logoRepository.findAll();
    }

    /**
     * GET  /rest/logos/:id -> get the "id" logo.
     */
    @RequestMapping(value = "/rest/logos/{orgName}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Logo> get(@PathVariable String orgName) {
        log.debug("REST request to get Logo : {}", orgName);
        return Optional.ofNullable(logoService.findLogoByOrgName(orgName))
            .map(logo -> new ResponseEntity<>(
                logo,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/logos/:id -> delete the "id" logo.
     */
    @RequestMapping(value = "/rest/logos/{orgName}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable String orgName) {
        log.debug("REST request to delete Logo : {}", orgName);
        logoService.deleteLogo(orgName);
    }
}
