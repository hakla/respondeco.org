package org.respondeco.respondeco.web.rest;

import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.MVCTest;
import org.junit.Before;
import org.junit.Test;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserController REST controller.
 *
 * @see UserController
 */

public class UserControllerTest extends MVCTest {

    private MockMvc userMockMvc;

    @Before
    public void setup() {
        this.userMockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetExistingUser() throws Exception {
        setAuthorities(model.USER_ADMIN.getAuthorities());
        doReturn(model.USER_SAVED_MINIMAL).when(userServiceMock).getUser(model.USER_SAVED_MINIMAL.getId());
        userMockMvc.perform(get("/app/rest/users/" + model.USER_SAVED_MINIMAL.getId())
            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.login").value(model.USER_SAVED_MINIMAL.getLogin()));
    }


    @Test
    public void testGetUnknownUser() throws Exception {
        setAuthorities(model.USER_ADMIN.getAuthorities());
        Long unknownUserId = 99999999L;
        doThrow(NoSuchEntityException.class).when(userServiceMock).getUser(unknownUserId);
        userMockMvc.perform(get("/app/rest/users/" + unknownUserId)
            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
