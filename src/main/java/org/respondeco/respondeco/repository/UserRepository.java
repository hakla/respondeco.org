package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends AbstractEntityRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.activationKey = :activationKey")
    User getUserByActivationKey(@Param("activationKey") String activationKey);

    @Query("SELECT u FROM User u WHERE u.activated = FALSE AND u.createdDate < :date")
    List<User> findNotActivatedUsersByCreationDateBefore(@Param("date") DateTime dateTime);

    @Query("SELECT u FROM User u WHERE u.organization = :organization AND u.active = TRUE")
    Page<User> findByOrganization(@Param("organization") Organization organization, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.login = :login AND u.active = TRUE")
    User findByLogin(@Param("login") String login);

    @Query("SELECT u FROM User u WHERE u.login LIKE %:filter% AND u.active = TRUE")
    Page<User> findByLoginLike(@Param("filter") String filter, Pageable pageable);

}
