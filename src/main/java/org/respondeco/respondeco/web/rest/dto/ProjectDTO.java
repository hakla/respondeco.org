package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.util.CustomLocalDateSerializer;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by clemens on 15/11/14.
 */

@Data
@ApiModel(value = "Project", description = "A project DTO")
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
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate startDate;

    @ApiModelProperty(value = "The end date of the project (only needed if concrete is set to true")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate endDate;

    @ApiModelProperty(value = "A logo for the project")
    private byte[] projectLogo;


}
