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
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.activationKey = ?1")
    User getUserByActivationKey(String activationKey);

    @Query("select u from User u where u.activated = false and u.createdDate > ?1")
    List<User> findNotActivatedUsersByCreationDateBefore(DateTime dateTime);

    @Query("select u from User u where u.organization is null")
    List<User> findInvitableUsers();

    Page<User> findUsersByOrganizationId(Long orgId, Pageable pageable);

    User findByLogin(String userlogin);

    @Query("select u from User u where u.login like %:filter%")
    Page<User> findUsersByNameLike(@Param("filter") String filter, Pageable pageable);

    User findByIdAndActiveIsTrue(Long id);

}
