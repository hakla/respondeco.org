package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.PropertyTag;
import org.respondeco.respondeco.domain.ResourceRequirement;
import org.respondeco.respondeco.domain.User;

import java.util.Arrays;
import java.util.List;

/**
 * Created by clemens on 25/11/14.
 */

@Data
public class ProjectResponseDTO {

    public static List<String> DEFAULT_FIELDS = Arrays.asList(
            "id", "name", "purpose", "concrete", "start_date", "end_date",
            "organization_id", "manager_id", "property_tags");

    private Long id;
    private String name;
    private String purpose;
    private boolean concrete;
    private LocalDate startDate;
    private LocalDate endDate;
    private Organization organization;
    private Long organizationId;
    private User manager;
    private Long managerId;
    private List<PropertyTag> propertyTags;
    private List<ResourceRequirement> resourceRequirements;

}
