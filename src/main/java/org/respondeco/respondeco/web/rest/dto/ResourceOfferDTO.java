package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.web.rest.dto.util.CustomLocalDateDeserializer;
import org.respondeco.respondeco.web.rest.dto.util.CustomLocalDateSerializer;

import java.math.BigDecimal;

/**
 * Created by Roman Kern on 18.11.14.
 */
@ApiModel(value = "Resource Offer", description = "manage all resource offers")
@Getter
@Setter
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

    private Boolean isCommercial = false;

    private Boolean isRecurrent = false;

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate startDate;

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate endDate;

    public Boolean getIsCommercial() { return this.isCommercial; }
    public Boolean getIsRecurrent() { return this.isRecurrent; }
    public LocalDate getStartDate() { return this.startDate; }
    public LocalDate getEndDate() { return this.endDate; }

    private String[] resourceTags;

    public ResourceOfferDTO(){ }
    public ResourceOfferDTO(ResourceOffer offer){
        this.setName(offer.getName());
        this.setId(offer.getId());
        this.setAmount(offer.getAmount());
        this.setDescription(offer.getDescription());
        this.setOrganizationId(offer.getOrganisation().getId());
        this.setIsCommercial(offer.getIsCommercial());
        this.setIsRecurrent(offer.getIsRecurrent());
        this.setStartDate(offer.getStartDate());
        this.setEndDate(offer.getEndDate());
    }

    private String dateTimeToString(DateTime dateTime){
        //DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        //String result = dateTime == null ? null : dateTime.toString(formatter);
        return null;
        //return result;
    }
}
