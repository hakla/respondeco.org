package org.respondeco.respondeco.web.rest.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;
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

    private DateTime startDate;

    private DateTime endDate;


    public void setIsCommercial(Boolean isCommercial){ this.isCommercial = isCommercial; }
    public void setIsRecurrent(Boolean isRecurrent){ this.isRecurrent = isRecurrent; }
    public void setStartDate(DateTime startDate){ this.startDate = startDate; }
    public void setEndDate(DateTime endDate){ this.endDate = endDate; }

    public Boolean getIsCommercial() { return this.isCommercial; }
    public Boolean getIsRecurrent() { return this.isRecurrent; }
    public DateTime getStartDate() { return this.startDate; }
    public DateTime getEndDate() { return this.endDate; }

    private String[] resourceTags;

    public ResourceOfferDTO(){ }
    public ResourceOfferDTO(ResourceOffer offer){
        this.setId(offer.getId());
        this.setAmount(offer.getAmount());
        this.setDescription(offer.getDescription());
        this.setOrganisationId(offer.getOrganisationId());
        this.setIsCommercial(offer.getIsCommercial());
        this.setIsRecurrent(offer.getIsRecurrent());
        this.setStartDate(offer.getStartDate());
        this.setEndDate(offer.getEndDate());
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
