package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.web.rest.dto.util.CustomLocalDateSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by clemens on 25/11/14.
 */

@Data
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class OrganizationResponseDTO {

    private static final Logger log = LoggerFactory.getLogger(OrganizationResponseDTO.class);

    public static List<String> DEFAULT_FIELDS = Arrays.asList(
            "id", "name", "description", "email", "isNpo", "owner", "spokesperson", "logo", "projects");

    public static OrganizationResponseDTO fromEntity(Organization organization, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        OrganizationResponseDTO responseDTO = new OrganizationResponseDTO();
        if (fieldNames.contains("id")) {
            responseDTO.setId(organization.getId());
        }
        if (fieldNames.contains("name")) {
            responseDTO.setName(organization.getName());
        }
        if (fieldNames.contains("description")) {
            responseDTO.setDescription(organization.getDescription());
        }if (fieldNames.contains("email")) {
            responseDTO.setEmail(organization.getEmail());
        }
        if (fieldNames.contains("isNpo")) {
            responseDTO.setIsNpo(organization.getIsNpo());
        }
        if (fieldNames.contains("owner")) {
            responseDTO.setOwner(new UserDTO(organization.getOwner()));
        }
        if (fieldNames.contains("ownerId")) {
            responseDTO.setOwnerId(organization.getOwner().getId());
        }
        if (fieldNames.contains("logo")) {
            if (organization.getLogo() != null) {
                responseDTO.setLogo(new ImageDTO(organization.getLogo()));
            }
        }
        if (fieldNames.contains("members")) {
            List<UserDTO> membersDTO = new ArrayList<UserDTO>();
            for (User member : organization.getMembers()) {
                membersDTO.add(new UserDTO(member));
            }
            responseDTO.setMembers(membersDTO);
        }
        if (fieldNames.contains("spokesperson")) {
            responseDTO.setSpokesperson(organization.getSpokesPerson());
        }
        if(fieldNames.contains("projects")) {
            responseDTO.setProjects(ProjectResponseDTO
                    .fromEntities(organization.getProjects(),null));
        }
        return responseDTO;
    }

    public static List<OrganizationResponseDTO> fromEntities(Collection<Organization> organizations, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        List<OrganizationResponseDTO> responseDTOs = new ArrayList<>();
        for(Organization organization : organizations) {
            responseDTOs.add(OrganizationResponseDTO.fromEntity(organization, fieldNames));
        }
        return responseDTOs;
    }

    private Long id;
    private String name;
    private String description;
    private String email;
    private Boolean isNpo;
    private UserDTO owner;
    private Long ownerId;
    private List<ProjectResponseDTO> projects;
    private ImageDTO logo;
    private List<UserDTO> members;
    private Long spokesperson;

}
