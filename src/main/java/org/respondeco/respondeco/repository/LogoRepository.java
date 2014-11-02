package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Logo;
        import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Logo entity.
 */
public interface LogoRepository extends JpaRepository<Logo, String> {

}
