package org.respondeco.respondeco.web.rest.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.respondeco.respondeco.domain.ResourceRequirement;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Roman Kern on 18.11.14.
 */
@ApiModel(value = "Resource Requirement", description = "manage all resource requirements")
@Getter
@Setter
public class ResourceRequirementRequestDTO {

    @ApiModelProperty(value = "ID of the given Resource requirement")
    private Long id;
    @ApiModelProperty(value = "Name of the resource requirement", required = true)
    private String name;
    @ApiModelProperty(value = "amount of existing resource requirements",required =  true)
    private BigDecimal amount;
    @ApiModelProperty(value = "Description of the requirement", required = true)
    private String description;
    @ApiModelProperty(value = "ID of the project that created this requirement", required = true)
    private Long projectId;
    @ApiModelProperty(value = "define if this resource is essential to start the project")
    private Boolean isEssential;

    @ApiModelProperty(value = "definition of current Resource Tags", required = true)
    private List<String> resourceTags;

    public ResourceRequirementRequestDTO(){ }
    public ResourceRequirementRequestDTO(ResourceRequirement requirement){
        this.setId(requirement.getId());
        this.setName(requirement.getName());
        this.setAmount(requirement.getAmount());
        this.setDescription(requirement.getDescription());
        this.setProjectId(requirement.getProject().getId());
        this.setIsEssential(requirement.getIsEssential());
    }

}
