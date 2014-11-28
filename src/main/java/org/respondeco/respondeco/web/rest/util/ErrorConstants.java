package org.respondeco.respondeco.web.rest.util;

import org.respondeco.respondeco.web.rest.dto.ErrorResponseDTO;

/**
 * Created by Clemens Puehringer on 28/11/14.
 */
public class ErrorConstants {

    public static ErrorResponseDTO PROJECTS_NO_SUCH_PROJECT =
            new ErrorResponseDTO("rest.error.project.nosuchproject", "The given project does not exist");

    public static ErrorResponseDTO PROJECTS_NO_SUCH_USER =
            new ErrorResponseDTO("rest.error.project.nosuchuser",
                    "Could not set manager, the given manager does not exist");

    public static ErrorResponseDTO PROJECTS_NOT_A_VALID_MANAGER =
            new ErrorResponseDTO("rest.error.project.notvalidmanager",
                    "Could not set manager, new manager does not belong to your organization");



}
