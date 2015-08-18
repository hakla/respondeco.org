package org.respondeco.respondeco.service.util;

import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.service.exception.IllegalValueException;

/**
 * Created by Clemens Puehringer on 18/08/15.
 */
public class Assert {

    public static void isNull(Object object, String key, String message) {
        if(object != null) {
            throw new IllegalValueException(key, message);
        }
    }

    public static void notNull(Object object, String key, String message) {
        if(object == null) {
            throw new IllegalValueException(key, message);
        }
    }

    public static void isValid(String string, String key, String message) {
        if(string == null || string.length() == 0) {
            throw new IllegalValueException(key, message);
        }
    }

    public static void isValid(String string, Integer min, Integer max, String key, String message) {
        if(string == null || string.length() == 0 || string.length() < min || string.length() > max) {
            throw new IllegalValueException(key, message);
        }
    }

    public static void isFalse(Boolean bool, String key, String message) {
        if(bool) {
            throw new IllegalValueException(key, message);
        }
    }

    public static void isNew(Organization organization) {
        String template = "%s must not be set in a new organization";
        Assert.isNull(organization.getProjects(), "", String.format(template, "projects"));
        Assert.isFalse(organization.getVerified(), "", String.format(template, "verified"));
        Assert.isNull(organization.getMembers(), "", String.format(template, "members"));
        Assert.isNull(organization.getIsoCategories(), "", String.format(template, "ISO categories"));
        Assert.isNull(organization.getPostingFeed(), "", String.format(template, "posting feed"));
        Assert.isNull(organization.getFollowingUsers(), "", String.format(template, "following users"));
        Assert.isNull(organization.getResourceOffers(), "", String.format(template, "resource offers"));
        Assert.isNull(organization.getResourceMatches(), "", String.format(template, "resource matches"));
    }

}
