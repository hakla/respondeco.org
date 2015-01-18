package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.SocialMediaConnection;
import org.respondeco.respondeco.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for Social Media Connections
 */
public interface SocialMediaRepository extends JpaRepository<SocialMediaConnection, Long> {

    public SocialMediaConnection findByUserAndProvider(User user, String provider);

    public List<SocialMediaConnection> findByUserAndActiveIsTrue(User user);

    public SocialMediaConnection findByUserAndProviderAndActiveIsTrue(User user, String provider);

    public SocialMediaConnection findByUserAndProviderAndActiveIsFalse(User user, String provider);
}
