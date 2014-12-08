package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ResourceMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by clemens on 08/12/14.
 */

public interface ResourceMatchRepository extends JpaRepository<ResourceMatch, Long> {

    public List<ResourceMatch> findByProjectAndOrganization(Project project, Organization organization);

}
