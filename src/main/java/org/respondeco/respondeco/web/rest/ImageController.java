package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.aop.RESTWrapped;
import org.respondeco.respondeco.domain.Image;
import org.respondeco.respondeco.repository.ImageRepository;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
import org.respondeco.respondeco.web.rest.dto.ImageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Image.
 */
@RestController
@RequestMapping("/app")
public class ImageController {

    private final Logger log = LoggerFactory.getLogger(ImageController.class);

    @Inject
    private ImageRepository imageRepository;

    /**
     * POST  /rest/images -> Create a new image.
     */
    @RequestMapping(value = "/rest/images",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object create(@RequestParam("file") MultipartFile file) throws IOException {
        Image image = new Image();

        image.setData(file.getBytes());
        image.setName(file.getOriginalFilename());

        log.debug("REST request to save Image : {}", image);
        return imageRepository.save(image).getId();
    }

    /**
     * GET  /rest/images -> get all the images.
     */
    @RequestMapping(value = "/rest/images",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Object getAll() {
        log.debug("REST request to get all Images");
        List<Long> imageIds = new ArrayList<>();

        imageRepository.findAll().forEach((Image image) -> {
            imageIds.add(image.getId());
        });

        return imageIds;
    }

    /**
     * GET  /rest/images/:id -> get the "id" image.
     */
    @RequestMapping(value = "/rest/images/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object get(@PathVariable Long id) {
        log.debug("REST request to get Image : {}", id);
        return Optional.ofNullable(imageRepository.findOne(id))
            .map(image -> image.getId())
            .orElseThrow(() -> new NoSuchEntityException("There is no image with id " + id));
    }

    /**
     * GET  /rest/images/:id -> get the "id" image.
     */
    @RequestMapping(value = "/rest/images/file/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public void getPhysicalImage(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id)
        throws IOException {

        log.debug("REST request to get Image : {}", id);
        Image image = imageRepository.findOne(id);

        if (null == image) {
            throw new NoSuchEntityException("There is no image with id " + id);
        }

        response.setHeader("Content-Disposition", "attachment: filename=\"" + image.getName() + "\"");
        response.getOutputStream().write(image.getData());
        response.getOutputStream().close();
    }

    /**
     * DELETE  /rest/images/:id -> delete the "id" image.
     */
    @RequestMapping(value = "/rest/images/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Image : {}", id);
        imageRepository.delete(id);
    }

}
