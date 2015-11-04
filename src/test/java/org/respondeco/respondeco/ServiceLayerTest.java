package org.respondeco.respondeco;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.testutil.MockRepositories;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.testutil.domain.Authorities;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

/**
 * Created by Clemens Puehringer on 25/08/15.
 */
@TransactionConfiguration(defaultRollback = true)
@Transactional
@MockRepositories
public abstract class ServiceLayerTest extends RepositoryLayerTest {

    @Rule public TestWatcher annotationWatcher = new MockRepositoriesAnnotationWatcher();
    private RepositoryMocker repositoryMocker;
    private MockRepositories methodLevelMockRepositoriesAnnotation;

    @Inject public UserService userService;

    @Mock public AuthorityRepository authorityRepositoryMock;
    @Mock public ImageRepository imageRepositoryMock;
    @Mock public OrganizationRepository organizationRepositoryMock;
    @Mock public PersistentTokenRepository persistentTokenRepositoryMock;
    @Mock public ProjectRepository projectRepositoryMock;
    @Mock public UserRepository userRepositoryMock;

    protected SecurityContext securityContext;

    @Before
    public void setupRepositoryMocking() {
        MockitoAnnotations.initMocks(this);
        repositoryMocker = new RepositoryMocker(methodLevelMockRepositoriesAnnotation);
        repositoryMocker.tryMockRepositories();
    }

    @Before
    public void setupSecurity() {
        securityContext = SecurityContextHolder.getContext();
        loginAsUser();
    }

    protected void loginAsUser() {
        securityContext.setAuthentication(new DummyAuthentication(null, Authorities.userAuthorities()));
    }

    protected void loginAsAdmin() {
        securityContext.setAuthentication(new DummyAuthentication(null, Authorities.adminAuthorities()));
    }

    protected void loginAs(User user) {
        securityContext.setAuthentication(new DummyAuthentication(user.getLogin(), user.getAuthorities()));
        doReturn(user).when(userRepositoryMock).findByLogin(user.getLogin());
    }

    protected void logout() {
        SecurityContextHolder.clearContext();
        securityContext = SecurityContextHolder.getContext();
    }

    private class DummyAuthentication implements Authentication {

        private String userLogin = null;
        private Collection<Authority> authorities = null;

        public DummyAuthentication(String userLogin, Collection<Authority> authorities) {
            this.userLogin = userLogin;
            this.authorities = authorities;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
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
            return userLogin;
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

    public class MockRepositoriesAnnotationWatcher extends TestWatcher {
        @Override
        public void starting(Description description) {
            methodLevelMockRepositoriesAnnotation = description.getAnnotation(MockRepositories.class);
        }

    }

    private class RepositoryMocker {

        private MockRepositories classAnnotation;
        private MockRepositories methodAnnotation;

        public RepositoryMocker(MockRepositories methodAnnotation) {
            this.classAnnotation = TestUtil.getAnnotation(ServiceLayerTest.this.getClass(), MockRepositories.class);
            this.methodAnnotation = methodAnnotation;
        }

        public void tryMockRepositories() {
            if (shouldMock()) {
                try {
                    doMockRepositories();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private Boolean shouldMock() {
            if(methodAnnotation != null) {
                return methodAnnotation.value();
            }
            if(classAnnotation != null) {
                return classAnnotation.value();
            }
            return false;
        }

        private void doMockRepositories() throws Exception {
            for(Object service : getServices()) {
                replaceRepositories(TestUtil.unwrap(service));
            }
        }

        private List<Object> getServices() throws IllegalAccessException {
            List<Object> services = new ArrayList<>();
            for(Field field : ServiceLayerTest.class.getDeclaredFields()) {
                if(field.getName().endsWith("Service")) {
                    services.add(field.get(ServiceLayerTest.this));
                }
            }
            return services;
        }

        private void replaceRepositories(Object service) throws Exception {
            for(Field field : service.getClass().getDeclaredFields()) {
                if(field.getName().endsWith("Repository")) {
                    String mockName = field.getName() + "Mock";
                    Field mockField = ServiceLayerTest.class.getDeclaredField(mockName);
                    Object repositoryMock = mockField.get(ServiceLayerTest.this);
                    log.debug("replacing {} with {}", field.getName(), mockName);
                    TestUtil.inject(service, field.getName(), repositoryMock);
                }
            }
        }
    }

}
