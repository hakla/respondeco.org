package org.respondeco.respondeco.testutil;

import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.User;

/**
 * Created by Clemens Puehringer on 19/01/15.
 */
public class ModelProvider {

    public static DomainModel buildModel() {
        DomainModel model = new DomainModel();
        addUsers(model);
        addOrganizations(model);
        addProjects(model);
        return model;
    }

    private static void addUsers(DomainModel model) {
        for(int i=0;i<15;i++) {
            User user = DomainModel.defaultUser();
            user.setLogin(String.format("user%d", i));
            model.addUser(user);
        }
    }

    private static void addOrganizations(DomainModel model) {
        for(int i=0;i<5;i++) {
            Organization organization = DomainModel.defaultOrganization();
            organization.setName(String.format("organization%d", i));
            //set the owner
            User owner = model.getUserByLogin(String.format("user%d", i));
            owner.setOrganization(organization);
            organization.setOwner(owner);
            //add one member
            User member = model.getUserByLogin(String.format("user%d", i + 5));
            member.setOrganization(organization);
            organization.addMember(member);
            model.addOrganization(organization);
        }
    }

    private static void addProjects(DomainModel model) {
        for(int i=0;i<10;i++) {
            Project project = DomainModel.defaultProject();
            project.setName(String.format("project%d", i));
            Organization org = model.getOrganizationByName(String.format("organization%d", i%5));
            project.setOrganization(org);
            User manager = model.getUserByLogin(String.format("user%d", i));
            project.setManager(manager);
            model.addProject(project);
        }
    }

}
