package org.respondeco.respondeco.web.rest.util;

import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.web.rest.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by clemens on 28/11/14.
 */
public class ErrorHelper {

    public static ResponseEntity<ErrorResponseDTO> buildErrorResponse(String key, String message) {
        ErrorResponseDTO responseDTO = new ErrorResponseDTO(key, message);
        return new ResponseEntity<ErrorResponseDTO>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ErrorResponseDTO> buildErrorResponse(ErrorResponseDTO responseDTO) {
        return new ResponseEntity<ErrorResponseDTO>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ErrorResponseDTO> buildErrorResponse(IllegalValueException exception) {
        ErrorResponseDTO responseDTO = new ErrorResponseDTO(exception.getInternationalizationKey(), exception.getMessage());
        return new ResponseEntity<ErrorResponseDTO>(responseDTO, HttpStatus.BAD_REQUEST);
    }

}
