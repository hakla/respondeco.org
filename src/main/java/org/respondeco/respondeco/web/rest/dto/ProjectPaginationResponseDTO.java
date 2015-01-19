package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.Project;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * ProjectPaginationResponseDTO
 * This DTO is used for returning projects with pagination. Therefor the DTO contains
 * a list of ProjectResponseDTOs plus additional information for pagination, namely
 * the total number of active projects found in the database.
 */
@Data
public class ProjectPaginationResponseDTO {

    private List<ProjectResponseDTO> projects;
    private Long totalItems;

    /**
     * Creates a new ProjectPaginationResponseDTO from a retrieved page element from the repository.
     * It contains the project-elements found in the database plus additional information for pagination
     * @param page Page containing the project elements and the pagination information
     * @param fieldNames defines which fields from the ProjectResponseDTOs are filled with information.
     * @return new ProjectPaginationResponse DTO
     */
    public static ProjectPaginationResponseDTO createFromPage(Page page, List<String> fieldNames) {
        ProjectPaginationResponseDTO dto = new ProjectPaginationResponseDTO();
        dto.setTotalItems(page.getTotalElements());

        List<Project> projects = page.getContent();
        dto.setProjects(ProjectResponseDTO.fromEntities(projects, fieldNames));

        return dto;
    }
}
