package org.respondeco.respondeco.web.rest.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;

/**
 * Created by Roman Kern on 18.11.14.
 */
@ApiModel(value = "Resource Tag", description = "allow to manage resource tags")
public class ResourceTagDTO {

    @ApiModelProperty(value = "ID of the given Resource Tag")
    private Long id;
    @ApiModelProperty(value = "Property Tag name", required = true)
    private String name;

    public void setId(Long id){ this.id = id; }

    public void setName(String name){ this.name = name; }

    public Long getId(){ return this.id; }

    public String getName(){ return this.name; }
}
