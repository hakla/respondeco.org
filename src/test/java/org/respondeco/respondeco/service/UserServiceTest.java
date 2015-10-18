package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.Test;
import org.respondeco.respondeco.ServiceLayerTest;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.testutil.ArgumentCaptor;
import org.respondeco.respondeco.testutil.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for the UserService.
 *
 * @see UserService
 */
public class UserServiceTest extends ServiceLayerTest {

    public ArgumentCaptor<User> userArgumentCaptor;

    @Before
    public void setupUserTest() {
        userArgumentCaptor = ArgumentCaptor.forType(User.class, 0, false);
    }

    @Test
    public void testActivateRegistration_shouldSaveRegisteredUser() throws Exception {
        String activationKey = "someactivationkey";
        doReturn(model.USER_SAVED_MINIMAL).when(userRepositoryMock).getUserByActivationKey(activationKey);
        doAnswer(userArgumentCaptor).when(userRepositoryMock).save(any(User.class));
        userService.activateRegistration(activationKey);
        assertEquals(true, userArgumentCaptor.getValue().isActivated());
        verify(userRepositoryMock, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUserInformation_shouldUpdateValidValues() throws Exception {
        loginAs(model.USER_SAVED_MINIMAL);
        doAnswer(userArgumentCaptor).when(userRepositoryMock).save(any(User.class));
        String newDescription = "new description";
        String newFirstName = "newfirstname";
        User updatedUser = TestUtil.clone(model.USER_SAVED_MINIMAL);
        updatedUser.setDescription(newDescription);
        updatedUser.setFirstName(newFirstName);
        userService.update(updatedUser);
        User savedUser = userArgumentCaptor.getValue();
        assertEquals(newDescription, savedUser.getDescription());
        assertEquals(newFirstName, savedUser.getFirstName());
    }
}
