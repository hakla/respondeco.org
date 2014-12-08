package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by clemens on 08/12/14.
 */

@Getter
@Setter
public class RatingPermissions {

    private Long projectId;
    private List<Long> organizationIds;

    public void addOrganization(Long orgId) {
        organizationIds.add(orgId);
    }

}
