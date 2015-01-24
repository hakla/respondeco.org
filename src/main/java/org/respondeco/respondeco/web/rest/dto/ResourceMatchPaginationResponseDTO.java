package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.ResourceMatch;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * ResourceMatchPaginationResponseDTO
 * This Data Transfer Object is used to return a list of ResourceMatchResponseDTOs
 * plus additional information for pagination, namely the total number of items.
 */
@Data
public class ResourceMatchPaginationResponseDTO {

    private List<ResourceMatchResponseDTO> resourceMatches;
    private int totalItems;

    /**
     * Creates a new ResourceMatchPaginationResponseDTO from a given Page element which contains
     * the ResourceMatch elements from the database plus additional information for pagination.
     * @param page Page containing the ResourceMatches and pagination information
     * @param fieldNames defines the returned fields from the ResourceOfferResponseDTO objects
     * @return new ResourceMatchPaginationResponseDTO object
     */
    public static ResourceMatchPaginationResponseDTO createFromPage(Page page, List<String> fieldNames) {
        ResourceMatchPaginationResponseDTO dto = new ResourceMatchPaginationResponseDTO();

        List<ResourceMatch> resourceMatches = page.getContent();
        List<ResourceMatchResponseDTO> resourceMatchResponseDTOs =
            ResourceMatchResponseDTO.fromEntities(resourceMatches, fieldNames);

        dto.setResourceMatches(resourceMatchResponseDTOs);
        dto.setTotalItems((int)page.getTotalElements());

        return dto;
    }

}
