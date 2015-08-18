package org.respondeco.respondeco.testutil.domain;

import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by clemens on 18/08/15.
 */
public class OrganizationFactory extends EntityFactory<Organization> {

    private static final User templateOwner = new User() {{
        setId(1000L);
        setEmail("template@owner.com");
        setLogin("template@owner.com");
        setPassword("templateowner123");
        setActive(true);
    }};

    private static final Organization template = new Organization() {{
        setId(1000L);
        setName("template organization");
        setEmail("template@template.com");
        setVerified(false);
        setIsNpo(false);
        setActive(true);
        setOwner(templateOwner);
        templateOwner.setOrganization(this);
    }};

    public Organization getWithNullValues(String ... fieldNames) {
        try {
            return setNull(template, fieldNames);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
