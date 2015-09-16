package org.respondeco.respondeco;

import net.sf.cglib.proxy.Enhancer;
import org.junit.Before;
import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.UserController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by clemens on 15/09/15.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MVCTest extends DatabaseBackedTest {

    @Inject protected UserController userController;

    protected Collection<Authority> currentAuthorities;

    @Before
    public void setupSecurity() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        currentAuthorities = Arrays.asList(new Authority("USER"));
        securityContext.setAuthentication(new DummyAuthentication());
    }

    @Before
    public void setupControllers() throws Exception {
        log.debug("setting userService to {}", userServiceMock);
        TestUtil.inject(userController, "userService", userServiceMock);
    }

    protected void setAuthorities(Collection<Authority> authorities) {
        currentAuthorities = authorities;
    }

    private class DummyAuthentication implements Authentication {
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return currentAuthorities;
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public void setAuthenticated(boolean b) throws IllegalArgumentException {
        }

        @Override
        public String getName() {
            return null;
        }
    }


}
