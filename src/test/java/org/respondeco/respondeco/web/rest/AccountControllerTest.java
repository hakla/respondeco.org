package org.respondeco.respondeco.web.rest;

import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.ControllerLayerTest;
import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the AccountController REST controller.
 *
 * @see UserService
 */
public class AccountControllerTest extends ControllerLayerTest {


    @Override
    public Object getController() {
        return accountController;
    }

    @Test
    public void testNonAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/app/rest/authenticate")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

    }

    @Test
    public void testAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/app/rest/authenticate")
                .with(request -> {
                    request.setRemoteUser("test");
                    return request;
                })
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("test"));
    }

//    @Test
//    public void testGetExistingAccount() throws Exception {
//        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultAdmin);
//
//        restUserMockMvc.perform(get("/app/rest/account")
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.login").value(defaultAdmin.getLogin()))
//                .andExpect(jsonPath("$.title").value(defaultAdmin.getTitle()))
//                .andExpect(jsonPath("$.gender").value(defaultAdmin.getGender().name()))
//                .andExpect(jsonPath("$.firstName").value(defaultAdmin.getFirstName()))
//                .andExpect(jsonPath("$.lastName").value(defaultAdmin.getLastName()))
//                .andExpect(jsonPath("$.email").value(defaultAdmin.getEmail()))
//                .andExpect(jsonPath("$.description").value(defaultAdmin.getDescription()))
//                .andExpect(jsonPath("$.roles").value(AuthoritiesConstants.ADMIN));
//    }
//
//    @Test
//    public void testRegisterNewAccount_shouldCreateUserAndOrganization() throws Exception {
//        when(userServiceMock.getUserWithAuthorities()).thenReturn(null);
//
//        restUserMockMvc.perform(get("/app/rest/account")
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError());
//    }

}
