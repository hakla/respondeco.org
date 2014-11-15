package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Project;
        import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the ProjectIdea entity.
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

}
