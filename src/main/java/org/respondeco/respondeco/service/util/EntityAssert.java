package org.respondeco.respondeco.service.util;

import org.joda.time.LocalDate;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.ServiceException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Clemens Puehringer on 18/08/15.
 */
public class EntityAssert {

    private static final String ERROR_KEY_FIELD_SET = "field_set";
    private static final String ERROR_KEY_FIELD_NOT_SET = "field_not_set";

    private static final String ERROR_TEMPLATE_FIELD_SET = "%s can not be set.";
    private static final String ERROR_TEMPLATE_FIELD_NOT_SET = "%s must be set.";

    private static final List<String> FIELD_ERROR_DETAILS_KEYS = Arrays.asList("field");

    private static void isValid(String string, String fieldName, ServiceException.ErrorPrefix prefix) {
        Assert.isValid(string, prefix.join(ERROR_KEY_FIELD_NOT_SET),
            ERROR_TEMPLATE_FIELD_NOT_SET, FIELD_ERROR_DETAILS_KEYS, Arrays.asList(fieldName));
    }

    private static void notNull(Object object, String fieldName, ServiceException.ErrorPrefix prefix) {
        Assert.notNull(object, prefix.join(ERROR_KEY_FIELD_NOT_SET),
            ERROR_TEMPLATE_FIELD_NOT_SET, FIELD_ERROR_DETAILS_KEYS, Arrays.asList(fieldName));
    }

    private static void isNull(Object object, String fieldName, ServiceException.ErrorPrefix prefix) {
        Assert.isNull(object, prefix.join(ERROR_KEY_FIELD_SET),
            ERROR_TEMPLATE_FIELD_SET, FIELD_ERROR_DETAILS_KEYS, Arrays.asList(fieldName));
    }

    private static void isFalse(Boolean bool, String fieldName, ServiceException.ErrorPrefix prefix) {
        Assert.isFalse(bool, prefix.join(ERROR_KEY_FIELD_SET),
            ERROR_TEMPLATE_FIELD_SET, FIELD_ERROR_DETAILS_KEYS, Arrays.asList(fieldName));
    }

    public static class Organization {

        public static void isNew(org.respondeco.respondeco.domain.Organization organization,
                                 ServiceException.ErrorPrefix prefix) {
            isValid(organization.getName(), "name", prefix);
            notNull(organization.getOwner(), "owner", prefix);

            isNull(organization.getProjects(), "projects", prefix);
            isFalse(organization.getVerified(), "verified", prefix);
            isNull(organization.getMembers(), "members", prefix);
            isNull(organization.getIsoCategories(), "ISOCategories", prefix);
            isNull(organization.getPostingFeed(), "postingFeed", prefix);
            isNull(organization.getFollowingUsers(), "followingUsers", prefix);
            isNull(organization.getResourceOffers(), "resourceOffers", prefix);
            isNull(organization.getResourceMatches(), "resourceMatches", prefix);
        }
    }

    public static class Project {

        private static final String ERROR_KEY_STARTDATE_NULL = "startdate.null";
        private static final String ERROR_KEY_STARTDATE_BEFORE_NOW = "startdate.before_now";

        public static void isNew(org.respondeco.respondeco.domain.Project project,
                                 ServiceException.ErrorPrefix prefix) {
            isValid(project.getName(), "name", prefix);
            isValid(project.getPurpose(), "purpose", prefix);
            isNull(project.getResourceMatches(), "resourceMatches", prefix);
            isNull(project.getFollowingUsers(), "followingUsers", prefix);
            isNull(project.getOrganization(), "organization", prefix);
            isDateValid(project, prefix);
        }

        public static void isUpdateable(org.respondeco.respondeco.domain.Project project,
                                        ServiceException.ErrorPrefix prefix) {
            notNull(project.getId(), "id", prefix);
            isValid(project.getName(), "name", prefix);
            isValid(project.getPurpose(), "purpose", prefix);
            isDateValid(project, prefix);
        }

        public static void isDateValid(org.respondeco.respondeco.domain.Project project,
                                       ServiceException.ErrorPrefix prefix) {
            if(project.getConcrete()) {
                if(project.getStartDate() == null) {
                    throw new IllegalValueException(prefix.join(ERROR_KEY_STARTDATE_NULL),
                        "start date cannot be null if project is concrete", null, null);
                }
                if(project.getStartDate().isBefore(LocalDate.now())) {
                    throw new IllegalValueException(prefix.join(ERROR_KEY_STARTDATE_BEFORE_NOW),
                        "start date cannot be before before now: %1",
                        Arrays.asList("startDate"), Arrays.asList(project.getStartDate()));
                }
            }
        }
    }

}
