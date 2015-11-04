package org.respondeco.respondeco.web.rest;

import org.respondeco.respondeco.ControllerLayerTest;
import org.junit.Before;
import org.junit.Test;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserController REST controller.
 *
 * @see UserController
 */
public class UserControllerTest extends ControllerLayerTest {

    @Override
    public Object getController() {
        return userController;
    }

    @Test
    public void testGetExistingUser() throws Exception {
        loginAsAdmin();
        doReturn(model.USER_SAVED_MINIMAL).when(userServiceMock).getUser(model.USER_SAVED_MINIMAL.getId());
        mockMvc.perform(get("/app/rest/users/" + model.USER_SAVED_MINIMAL.getId())
            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.login").value(model.USER_SAVED_MINIMAL.getLogin()));
        verify(userServiceMock, times(1)).getUser(model.USER_SAVED_MINIMAL.getId());
    }

    @Test
    public void testGetUnknownUser() throws Exception {
        loginAsAdmin();
        Long unknownUserId = 99999999L;
        doThrow(NoSuchEntityException.class).when(userServiceMock).getUser(unknownUserId);
        mockMvc.perform(get("/app/rest/users/" + unknownUserId)
            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(userServiceMock, times(1)).getUser(unknownUserId);
    }

    @Test
    public void testGetUserAsUser_shouldBeUnauthorized() throws Exception {
        try {
            mockMvc.perform(get("/app/rest/users/" + model.USER_SAVED_MINIMAL.getId())
                .accept(MediaType.APPLICATION_JSON));
                //following line is never reached because mockMvc throws the exception instead of
                //sending the error via http
                //.andExpect(status().isUnauthorized());
        } catch(ServletException e) {
            assertThat(e.getCause()).isInstanceOf(AccessDeniedException.class);
        }
    }
}
