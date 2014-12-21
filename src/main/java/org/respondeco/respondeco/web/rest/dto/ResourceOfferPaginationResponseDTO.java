package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * ResourceOfferPaginationResponseDTO
 * This Data Transfer Object is used to return a list of ResourceOfferResponseDTOs
 * plus additional information for pagination, namely the total number of items.
 */
@Data
public class ResourceOfferPaginationResponseDTO {

    private List<ResourceOfferResponseDTO> resourceOffers;
    private int totalItems;

    /**
     * Creates a new ResourceOfferPaginationResponseDTO from a given Page element which contains
     * the ResourceOffer elements from the database plus additional information for pagination.
     * @param page Page containing the ResourceOffers and pagination information
     * @param fieldNames defines the returned fields from the ResourceOfferResponseDTO objects
     * @return new ResponseOfferPaginationResponseDTO object
     */
    public static ResourceOfferPaginationResponseDTO createFromPage(Page page, List<String> fieldNames) {
        ResourceOfferPaginationResponseDTO dto = new ResourceOfferPaginationResponseDTO();

        List<ResourceOffer> resourceOffers = page.getContent();
        List<ResourceOfferResponseDTO> resourceOfferResponseDTOs = ResourceOfferResponseDTO.fromEntities(resourceOffers, fieldNames);

        dto.setResourceOffers(resourceOfferResponseDTOs);
        dto.setTotalItems((int)page.getTotalElements());

        return dto;
    }
}
