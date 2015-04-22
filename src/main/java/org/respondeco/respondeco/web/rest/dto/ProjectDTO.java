package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.web.rest.mapping.serializer.CustomLocalDateDeserializer;
import org.respondeco.respondeco.web.rest.mapping.serializer.CustomLocalDateSerializer;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by clemens on 15/11/14.
 */

@Data
@ApiModel(value = "Project", description = "A project DTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDTO {

    @ApiModelProperty(value = "The id of the project to modify")
    private Long id;

    @ApiModelProperty(value = "The name of the project", required = true)
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @ApiModelProperty(value = "The purpose of the project", required = true)
    @NotNull
    @Size(min = 1, max = 2048)
    private String purpose;

    @ApiModelProperty(value = "If the project has start and end date (is a concrete project)", required = true)
    @NotNull
    private Boolean concrete = false;

    @ApiModelProperty(value = "The start date of the project (only needed if concrete is set to true")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate startDate;

    @ApiModelProperty(value = "A list of strings used as property tags")
    private List<String> propertyTags;

    @ApiModelProperty(value = "The resource requirements for the project")
    private List<ResourceRequirementRequestDTO> resourceRequirements;

    @ApiModelProperty(value = "The file which is used a the logo for the project")
    private ImageDTO logo;

    private Long organizationId;
    private Long managerId;

    @ApiModelProperty(value = "Location of the Project")
    private ProjectLocationDTO projectLocation;

}
