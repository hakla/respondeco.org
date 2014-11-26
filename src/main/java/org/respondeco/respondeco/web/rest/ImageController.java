package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.Image;
import org.respondeco.respondeco.repository.ImageRepository;
import org.respondeco.respondeco.web.rest.dto.ImageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
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
    public ResponseEntity<ImageDTO> create(@RequestParam("file") MultipartFile file) {
        Image image = new Image();

        try {
            image.setData(file.getBytes());
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.debug("REST request to save Image : {}", image);
        image = imageRepository.save(image);

        return new ResponseEntity(new ImageDTO(image.getId()), HttpStatus.CREATED);
    }

    /**
     * GET  /rest/images -> get all the images.
     */
    @RequestMapping(value = "/rest/images",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Image> getAll() {
        log.debug("REST request to get all Images");
        return imageRepository.findAll();
    }

    /**
     * GET  /rest/images/:id -> get the "id" image.
     */
    @RequestMapping(value = "/rest/images/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Image> get(@PathVariable Long id) {
        log.debug("REST request to get Image : {}", id);
        return Optional.ofNullable(imageRepository.findOne(id))
            .map(image -> new ResponseEntity<>(
                image,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/images/:id -> delete the "id" image.
     */
    @RequestMapping(value = "/rest/images/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Image : {}", id);
        imageRepository.delete(id);
    }
}
