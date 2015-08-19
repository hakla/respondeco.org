package org.respondeco.respondeco.service.util;

import org.respondeco.respondeco.domain.Organization;

/**
 * Created by Clemens Puehringer on 18/08/15.
 */
public class EntityAssert {

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
