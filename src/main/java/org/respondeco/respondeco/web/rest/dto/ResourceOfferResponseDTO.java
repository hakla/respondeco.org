package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.web.rest.mapping.serializing.CustomLocalDateSerializer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * ResourceOfferResponseDTO
 * Data Transfer Object for a ResourceOffer-Response used to send
 * ResourceOffers from the backend to the frontend
 */
@Data
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class ResourceOfferResponseDTO {

    public static List<String> DEFAULT_FIELDS = Arrays.asList(
        "id", "name", "amount", "price", "description", "isCommercial",
        "resourceTags", "originalAmount", "startDate", "endDate", "organization", "logo", "price");

    /**
     * Converts a ResourceOffer into a ResourceOfferResponseDTO object, which gets the fields defined
     * in the fieldNames list set.
     * @param resourceOffer ResourceOffer to be converted into DTO
     * @param fieldNames Fields that are included into the ResourceOfferResponseDTO
     * @return ResourceOfferResponse DTO
     */
    public static ResourceOfferResponseDTO fromEntity(ResourceOffer resourceOffer, Collection<String> fieldNames) {
        ResourceOfferResponseDTO responseDTO = new ResourceOfferResponseDTO();

        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        if(fieldNames.contains("id")) {
            responseDTO.setId(resourceOffer.getId());
        }
        if(fieldNames.contains("name")) {
            responseDTO.setName(resourceOffer.getName());
        }
        if(fieldNames.contains("amount")) {
            responseDTO.setAmount(resourceOffer.getAmount());
        }
        if(fieldNames.contains("description")) {
            responseDTO.setDescription(resourceOffer.getDescription());
        }
        if(fieldNames.contains("startDate")) {
            responseDTO.setStartDate(resourceOffer.getStartDate());
        }
        if(fieldNames.contains("endDate")) {
            responseDTO.setEndDate(resourceOffer.getEndDate());
        }
        if(fieldNames.contains("isCommercial")) {
            responseDTO.setIsCommercial(resourceOffer.getIsCommercial());
        }
        if (fieldNames.contains("resourceTags")) {
            responseDTO.setResourceTags(ResourceTagResponseDTO
                .fromEntities(resourceOffer.getResourceTags(), null));
        }
        if (fieldNames.contains("originalAmount")) {
            responseDTO.setOriginalAmount(resourceOffer.getOriginalAmount());
        }
        if (fieldNames.contains("organization")) {
            responseDTO.setOrganization(OrganizationResponseDTO
                .fromEntity(resourceOffer.getOrganization(), null));
        }
        if (fieldNames.contains("logo") && resourceOffer.getLogo() != null) {
            responseDTO.setLogo(new ImageDTO(resourceOffer.getLogo()));
        }
        if (fieldNames.contains("price")) {
            responseDTO.setPrice(resourceOffer.getPrice());
        }

        return responseDTO;
    }


    /**
     * Converts a list of ResourceOffers into a list of ResourceOfferResponseDTOs
     * containing the fields given by fieldNames.
     * @param resourceOffers List of ResourceOffers to be converted
     * @param fieldNames list of relevant field names for conversion
     * @return list of ResourceOfferResponse DTO Objects
     */
    public static List<ResourceOfferResponseDTO> fromEntities(List<ResourceOffer> resourceOffers,
                                                                    List<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        List<ResourceOfferResponseDTO> responseDTOs = new ArrayList<>();
        for(ResourceOffer resourceOffer : resourceOffers) {
            responseDTOs.add(ResourceOfferResponseDTO.fromEntity(resourceOffer, fieldNames));
        }
        return responseDTOs;
    }

    private Long id;
    private String name;
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate startDate;
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate endDate;
    private BigDecimal amount;
    private BigDecimal originalAmount;
    private String description;
    private Boolean isCommercial;
    private List<ResourceTagResponseDTO> resourceTags;
    private BigDecimal price;
    private OrganizationResponseDTO organization;
    private ImageDTO logo;

}
