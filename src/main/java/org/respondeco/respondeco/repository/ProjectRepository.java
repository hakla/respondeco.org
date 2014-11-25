package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Project;
        import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the ProjectIdea entity.
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

    public List<Project> findByActiveIsTrue();

    public Project findByIdAndActiveIsTrue(Long id);

}
