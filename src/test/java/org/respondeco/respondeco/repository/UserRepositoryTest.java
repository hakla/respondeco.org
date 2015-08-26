package org.respondeco.respondeco.repository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.respondeco.respondeco.DatabaseBackedTest;
import org.respondeco.respondeco.domain.*;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */
public class UserRepositoryTest extends DatabaseBackedTest {

    @Test
    public void testGetUserByActivationKey() {
        User savedUser = userRepository.getUserByActivationKey(model.USER1_OWNS_ORG1_MANAGES_P1.getActivationKey());
        assertEquals(model.USER1_OWNS_ORG1_MANAGES_P1, savedUser);
    }

    @Test
    public void testDelete_shouldSetActiveFalse() throws Exception {
        userRepository.delete(model.USER_SAVED_MINIMAL);
        assertFalse(userRepository.findOneIgnoreActive(model.USER_SAVED_MINIMAL.getId()).getActive());
    }

    @Test
    public void testFindOne_shouldReturnActiveUser() {
        User savedUser = userRepository.findOne(model.USER_SAVED_MINIMAL.getId());
        assertEquals(model.USER_SAVED_MINIMAL, savedUser);
    }

    @Test
    public void testFindOne_shouldNotReturnInactiveUser() {
        userRepository.delete(model.USER_SAVED_MINIMAL);
        assertNull(userRepository.findOne(model.USER_SAVED_MINIMAL.getId()));
    }

    @Test
    public void testFindNotActivatedUsersByCreationDateBefore() {
        model.USER_SAVED_MINIMAL.setActivated(false);
        userRepository.save(model.USER_SAVED_MINIMAL);
        DateTime dateTime = DateTime.now().plusDays(1);
        List<User> savedUsers = userRepository.findNotActivatedUsersByCreationDateBefore(dateTime);
        assertFalse(savedUsers.contains(model.USER1_OWNS_ORG1_MANAGES_P1));
        assertTrue(savedUsers.contains(model.USER_SAVED_MINIMAL));
    }

    @Test
    public void testFindByOrganizationId_shouldReturnCorrectUser() {
        Page<User> savedUsers = userRepository.findByOrganization(model.ORGANIZATION_SAVED_MINIMAL, null);
        assertFalse(savedUsers.getContent().contains(model.USER1_OWNS_ORG1_MANAGES_P1));
        assertTrue(savedUsers.getContent().contains(model.USER_SAVED_MINIMAL));
    }

    @Test
    public void testFindByOrganizationId_shouldNotReturnInactiveUser() {
        userRepository.delete(model.USER_SAVED_MINIMAL);
        Page<User> savedUsers = userRepository.findByOrganization(model.ORGANIZATION_SAVED_MINIMAL, null);
        assertFalse(savedUsers.getContent().contains(model.USER_SAVED_MINIMAL));
    }

    @Test
    public void testFindByLogin_shouldReturnActiveUser() {
        User savedUser = userRepository.findByLogin(model.USER1_OWNS_ORG1_MANAGES_P1.getLogin());
        assertEquals(model.USER1_OWNS_ORG1_MANAGES_P1, savedUser);
    }

    @Test
    public void testFindByLogin_shouldNotReturnInactiveUser() {
        userRepository.delete(model.USER1_OWNS_ORG1_MANAGES_P1);
        User savedUser = userRepository.findByLogin(model.USER1_OWNS_ORG1_MANAGES_P1.getLogin());
        assertNull(savedUser);
    }

    @Test
    public void testFindByLoginLike_shouldReturnActiveUsers() throws Exception {
        Page<User> users = userRepository.findByLoginLike("respondeco", null);
        assertTrue(users.getContent().size() >= 3);
    }

    @Test
    public void testFindByLoginLike_shouldNotReturnInactiveUsers() throws Exception {
        userRepository.delete(model.USER_SAVED_MINIMAL);
        Page<User> users = userRepository.findByLoginLike(model.USER_SAVED_MINIMAL.getLogin(), null);
        assertEquals(0, users.getContent().size());
    }
}
