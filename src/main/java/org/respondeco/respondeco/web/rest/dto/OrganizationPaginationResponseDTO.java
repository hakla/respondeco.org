package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.Organization;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by clemens on 19/01/15.
 */

@Data
public class OrganizationPaginationResponseDTO {

    private List<OrganizationResponseDTO> organizations;
    private Long totalItems;

    public static OrganizationPaginationResponseDTO createFromPage(Page<Organization> page, List<String> fieldNames) {
        OrganizationPaginationResponseDTO responseDTO = new OrganizationPaginationResponseDTO();
        responseDTO.setTotalItems(page.getTotalElements());
        responseDTO.setOrganizations(OrganizationResponseDTO.fromEntities(page.getContent(), fieldNames));
        return responseDTO;
    }

}
