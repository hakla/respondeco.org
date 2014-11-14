package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ProfilePicture;
        import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the ProfilePicture entity.
 */
public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Long> {

}
