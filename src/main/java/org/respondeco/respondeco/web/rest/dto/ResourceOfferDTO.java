package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.domain.ResourceTag;
import org.respondeco.respondeco.web.rest.mapping.serializing.CustomLocalDateDeserializer;
import org.respondeco.respondeco.web.rest.mapping.serializing.CustomLocalDateSerializer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roman Kern on 18.11.14.
 */
@ApiModel(value = "Resource Offer", description = "manage all resource offers")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceOfferDTO {

    @ApiModelProperty(value = "ID of the given Resource offer")
    private Long id;
    @ApiModelProperty(value = "name of the resource offer", required = true)
    private String name;
    @ApiModelProperty(value = "amount of existing resource offers", required = true)
    private BigDecimal amount;
    @ApiModelProperty(value = "Description of the offer", required = true)
    private String description;
    @ApiModelProperty(value = "ID of the organization that created this offer", required = true)
    private Long organizationId;

    @ApiModelProperty(value = "ID of the image to use as logo")
    private Long logoId;
    private Boolean isCommercial = false;

    private BigDecimal price;

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate startDate;

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate endDate;

    private List<String> resourceTags;

    public ResourceOfferDTO(){ }
    public ResourceOfferDTO(ResourceOffer offer){
        this.setName(offer.getName());
        this.setId(offer.getId());
        this.setAmount(offer.getAmount());
        this.setDescription(offer.getDescription());
        this.setOrganizationId(offer.getOrganization().getId());
        this.setIsCommercial(offer.getIsCommercial());
        this.setStartDate(offer.getStartDate());
        this.setEndDate(offer.getEndDate());

        resourceTags = new ArrayList<>();
        for(ResourceTag tag : offer.getResourceTags()) {
            resourceTags.add(tag.getName());
        }
    }
}
