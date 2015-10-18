package org.respondeco.respondeco;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.service.OrganizationService;
import org.respondeco.respondeco.service.ProjectService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.testutil.MockServices;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.testutil.domain.Authorities;
import org.respondeco.respondeco.web.rest.AccountController;
import org.respondeco.respondeco.web.rest.UserController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.doReturn;

/**
 * Created by clemens on 15/09/15.
 */
@MockServices
public class ControllerLayerTest extends ServiceLayerTest {

    @Rule public TestWatcher annotationWatcher = new MockServicesAnnotationWatcher();
    private ServiceMocker serviceMocker;
    private MockServices methodLevelMockServicesAnnotation;

    //named as ...Mock because underlying services are getting replaced by mocks
    @Inject public AccountController accountController;
    @Inject public UserController userController;

    @Mock public UserService userServiceMock;
    @Mock public OrganizationService organizationServiceMock;
    @Mock public ProjectService projectServiceMock;

    @Before
    public void setupServiceMocking() {
        MockitoAnnotations.initMocks(this);
        serviceMocker = new ServiceMocker(methodLevelMockServicesAnnotation);
        serviceMocker.tryMockServices();
    }

    @Override
    protected void loginAs(User user) {
        super.loginAs(user);
        doReturn(user).when(userServiceMock).getUserWithAuthorities();
    }

    public class MockServicesAnnotationWatcher extends TestWatcher {
        @Override
        public void starting(Description description) {
            methodLevelMockServicesAnnotation = description.getAnnotation(MockServices.class);
        }
    }

    private class ServiceMocker {

        private MockServices classAnnotation;
        private MockServices methodAnnotation;

        public ServiceMocker(MockServices methodAnnotation) {
            this.classAnnotation = TestUtil.getAnnotation(ControllerLayerTest.this.getClass(), MockServices.class);
            this.methodAnnotation = methodAnnotation;
        }

        public void tryMockServices() {
            if (shouldMock()) {
                try {
                    doMockServices();
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

        private void doMockServices() throws Exception {
            for(Object controller : getControllers()) {
                replaceServices(TestUtil.unwrap(controller));
            }
        }

        private List<Object> getControllers() throws IllegalAccessException {
            List<Object> services = new ArrayList<>();
            for(Field field : ControllerLayerTest.class.getDeclaredFields()) {
                if(field.getName().endsWith("Controller")) {
                    services.add(field.get(ControllerLayerTest.this));
                }
            }
            return services;
        }

        private void replaceServices(Object controller) throws Exception {
            for(Field field : controller.getClass().getDeclaredFields()) {
                if(field.getName().endsWith("Service")) {
                    String mockName = field.getName() + "Mock";
                    Field mockField = ControllerLayerTest.class.getDeclaredField(mockName);
                    Object serviceMock = mockField.get(ControllerLayerTest.this);
                    log.debug("replacing {} with {}", field.getName(), mockName);
                    TestUtil.inject(controller, field.getName(), serviceMock);
                }
            }
        }
    }

}
