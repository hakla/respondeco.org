package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by clemens on 08/12/14.
 */

public interface ResourceMatchRepository extends JpaRepository<ResourceMatch, Long>, QueryDslPredicateExecutor {

    public List<ResourceMatch> findByProjectAndOrganization(Project project, Organization organization);

    public List<ResourceMatch> findByProjectIdAndAcceptedIsTrueAndActiveIsTrue(Long id);

    public List<ResourceMatch> findByResourceOfferAndResourceRequirementAndOrganizationAndProject(
        ResourceOffer resourceOffer, ResourceRequirement resourceRequirement, Organization organization, Project project);
}
