package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Clemens Puehringer on 22/12/14.
 */

@Data
public class PostingPaginationResponseDTO {

    private Long totalElements;
    private List<PostingDTO> postings;

}
