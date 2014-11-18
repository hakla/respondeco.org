package org.respondeco.respondeco.web.rest.dto;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.respondeco.respondeco.domain.ResourceRequirement;

import java.math.BigDecimal;

/**
 * Created by Roman Kern on 18.11.14.
 */
@ApiModel(value = "Resource Requirement", description = "manage all resource requirements")
public class ResourceRequirementDTO {

    @ApiModelProperty(value = "ID of the given Resource requirement")
    private Long id;
    @ApiModelProperty(value = "amount of existing resource requirements")
    private BigDecimal amount;
    @ApiModelProperty(value = "Description of the requirement", required = true)
    private String description;
    @ApiModelProperty(value = "ID of the project that created this requirement", required = true)
    private Long projectId;
    @ApiModelProperty(value = "define if this resource is essential to start the project")
    private Boolean isEssential;

    private String[] resourceTags;

    public ResourceRequirementDTO(){ }
    public ResourceRequirementDTO(ResourceRequirement requirement){
        this.setId(requirement.getId());
        this.setAmount(requirement.getAmount());
        this.setDescription(requirement.getDescription());
        this.setProjectId(requirement.getProjectId());
        this.setIsEssential(requirement.getIsEssential());
    }


    public void setId(Long id){ this.id = id; }
    public void setAmount(BigDecimal amount){ this.amount = amount; }
    public void setDescription(String description){ this.description = description; }
    public void setProjectId(Long projectId){ this.projectId = projectId; }
    public void setIsEssential(Boolean isEssential){ this.isEssential = isEssential; }
    public void setResourceTags(String[] tags){ this.resourceTags = tags; }

    public Long getId(){ return this.id; }
    public BigDecimal getAmount() { return this.amount; }
    public String getDescription() { return this.description; }
    public Long getProjectId(){ return this.projectId; }
    public Boolean getIsEssential(){ return this.isEssential; }
    public String[] getResourceTags() { return this.resourceTags; }
}
