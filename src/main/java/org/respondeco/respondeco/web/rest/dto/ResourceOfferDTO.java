package org.respondeco.respondeco.web.rest.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.respondeco.respondeco.domain.ResourceOffer;

import java.math.BigDecimal;

/**
 * Created by Roman Kern on 18.11.14.
 */
@ApiModel(value = "Resource Offer", description = "manage all resource offers")
public class ResourceOfferDTO {

    @ApiModelProperty(value = "ID of the given Resource offer")
    private Long id;
    @ApiModelProperty(value = "amount of existing resource offers")
    private BigDecimal amount;
    @ApiModelProperty(value = "Description of the offer", required = true)
    private String description;
    @ApiModelProperty(value = "ID of the organisation that created this offer", required = true)
    private Long organisationId;

    private Boolean isCommercial = false;

    private Boolean isRecurrent = false;

    private String startDate;

    private String endDate;


    public void setIsCommercial(Boolean isCommercial){ this.isCommercial = isCommercial; }
    public void setIsRecurrent(Boolean isRecurrent){ this.isRecurrent = isRecurrent; }
    public void setStartDate(String startDate){ this.startDate = startDate; }
    public void setEndDate(String endDate){ this.endDate = endDate; }

    public Boolean getIsCommercial() { return this.isCommercial; }
    public Boolean getIsRecurrent() { return this.isRecurrent; }
    public String getStartDate() { return this.startDate; }
    public String getEndDate() { return this.endDate; }

    private String[] resourceTags;

    public ResourceOfferDTO(){ }
    public ResourceOfferDTO(ResourceOffer offer){
        this.setId(offer.getId());
        this.setAmount(offer.getAmount());
        this.setDescription(offer.getDescription());
        this.setOrganisationId(offer.getOrganisationId());
        this.setIsCommercial(offer.getIsCommercial());
        this.setIsRecurrent(offer.getIsRecurrent());
        this.setStartDate(dateTimeToString(offer.getStartDate()));
        this.setEndDate(dateTimeToString(offer.getEndDate()));
    }

    public ResourceOffer getOffer(){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        DateTime dt = formatter.parseDateTime(this.getStartDate());
        ResourceOffer offer = new ResourceOffer();
        offer.setId(this.getId());
        offer.setAmount(this.getAmount());
        offer.setDescription(this.getDescription());
        offer.setOrganisationId(this.getOrganisationId());
        offer.setIsCommercial(this.getIsCommercial());
        offer.setIsRecurrent(this.getIsRecurrent());
        offer.setStartDate(formatter.parseDateTime(this.getStartDate()));
        offer.setEndDate(formatter.parseDateTime(this.getEndDate()));
        return offer;
    }

    private String dateTimeToString(DateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        String result = dateTime == null ? null : dateTime.toString(formatter);
        //return null;
        return result;
    }


    public void setId(Long id){ this.id = id; }
    public void setAmount(BigDecimal amount){ this.amount = amount; }
    public void setDescription(String description){ this.description = description; }
    public void setOrganisationId(Long organisationId){ this.organisationId = organisationId; }
    public void setResourceTags(String[] tags){ this.resourceTags = tags; }

    public Long getId(){ return this.id; }
    public BigDecimal getAmount() { return this.amount; }
    public String getDescription() { return this.description; }
    public Long getOrganisationId(){ return this.organisationId; }
    public String[] getResourceTags() { return this.resourceTags; }
}
