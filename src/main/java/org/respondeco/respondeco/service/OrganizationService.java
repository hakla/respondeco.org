package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.dto.ImageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

@Service
@Transactional
public class OrganizationService {

    private final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    private OrganizationRepository organizationRepository;

    private UserService userService;

    private UserRepository userRepository;

    private ImageRepository imageRepository;

    @Inject
    public OrganizationService(OrganizationRepository organizationRepository, UserService userService, UserRepository userRepository, ImageRepository imageRepository) {
        this.organizationRepository = organizationRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    public Organization createOrganizationInformation(String name, String description, String email, Boolean isNpo, ImageDTO logo) throws AlreadyInOrganizationException, OrganizationAlreadyExistsException {
        Long logoId = null;

        if (logo != null) {
            logoId = logo.getId();
        }

        return createOrganizationInformation(name, description, email, isNpo, logoId);
    }

    public Organization createOrganizationInformation(String name, String description, String email, Boolean isNpo, Long logoId) throws AlreadyInOrganizationException, OrganizationAlreadyExistsException {
        if(organizationRepository.findByName(name)!=null) {
            throw new OrganizationAlreadyExistsException(String.format("Organization %s already exists", name));
        }
        if(name=="" || name==null){
            throw new IllegalArgumentException(String.format("Organization name must not be empty"));
        }
        User currentUser = userService.getUserWithAuthorities();
        Organization newOrganization = new Organization();

        newOrganization.setName(name);
        newOrganization.setDescription(description);
        newOrganization.setEmail(email);
        newOrganization.setIsNpo(isNpo);
        if(logoId != null) {
            newOrganization.setLogo(imageRepository.findOne(logoId));
        }

        if (organizationRepository.findByOwner(currentUser) != null) {
            throw new AlreadyInOrganizationException(String.format("Current User is already owner of an organization"));
        }
        newOrganization.setOwner(currentUser);

        currentUser.setOrganization(organizationRepository.save(newOrganization));
        userRepository.save(currentUser);
        log.debug("Created Information for Organization: {}", newOrganization);
        return newOrganization;
    }

    @Transactional(readOnly = true)
    public Organization getOrganizationByName(String orgName) throws NoSuchOrganizationException {
        log.debug("getOrganizationByName(organization) called");

        Organization currentOrganization = organizationRepository.findByName(orgName);
        if(currentOrganization == null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist", orgName));
        }

        log.debug("Found Information for Organization: {}", currentOrganization);
        return currentOrganization;
    }

    /**
     * Get Organization by Id
     * @param id organization id
     * @return organization
     * @throws NoSuchOrganizationException if organization with id could not be found
     */
    @Transactional(readOnly=true)
    public Organization getOrganization(Long id) {
        log.debug("getOrganization() with id " + id + " called");
        Organization org = organizationRepository.findOne(id);

        return org;
    }

    /**
     * Returns all Organizations
     * @return
     */
    @Transactional(readOnly = true)
    public List<Organization> getOrganizations() {
        log.debug("getOrganizations() called");

        List<Organization> organizations = organizationRepository.findByActiveIsTrue();

        return organizations;
    }

    public void updaterOrganizationInformation(String name, String description, String email, Boolean isNpo, ImageDTO logo) throws NoSuchOrganizationException {
        Long logoId = null;

        if (logo != null) {
            logoId = logo.getId();
        }

        updaterOrganizationInformation(name, description, email, isNpo, logoId);
    }

    public void updaterOrganizationInformation(String name, String description, String email, Boolean isNpo, Long logoId) throws NoSuchOrganizationException {
        User currentUser = userService.getUserWithAuthorities();
        Organization currentOrganization = organizationRepository.findByOwner(currentUser);
        if(currentOrganization==null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist for %s", currentUser.getLogin()));
        }
        if(name=="") {
            throw new IllegalArgumentException(String.format("Name must not be an empty string"));
        }

        currentOrganization.setName(name);
        currentOrganization.setDescription(description);
        currentOrganization.setEmail(email);
        currentOrganization.setIsNpo(isNpo);
        currentOrganization.setOwner(currentUser);
        if(logoId != null) {
            currentOrganization.setLogo(imageRepository.findOne(logoId));
        }

        organizationRepository.save(currentOrganization);
        log.debug("Changed Information for Organization: {}", currentOrganization);
    }

    public void deleteOrganizationInformation() throws NoSuchOrganizationException {
        User currentUser = userService.getUserWithAuthorities();
        Organization currentOrganization = organizationRepository.findByOwner(currentUser);
        if(currentOrganization==null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist", currentUser.getLogin()));
        }
        currentUser.setOrganization(null);
        userRepository.save(currentUser);
        organizationRepository.delete(currentOrganization);
        log.debug("Deleted Information for Organization: {}", currentOrganization);
    }

    public void deleteMember(Long userId) throws NoSuchUserException, NoSuchOrganizationException, NotOwnerOfOrganizationException {
        User user = userService.getUserWithAuthorities();
        User member = userRepository.findOne(userId);

        if(member == null) {
            throw new NoSuchUserException(String.format("User %s does not exist", userId));
        }
        System.out.println(member);
        Organization organization = organizationRepository.findOne(member.getOrganization().getId());
        if(organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization %s does not exist", member.getOrganization().getId()));
        }
        if(organization.getOwner().equals(user) == false) {
            throw new NotOwnerOfOrganizationException(String.format("Current User is not owner of Organization %s ", organization.getOwner()));
        }
        log.debug("Deleting member from organization", user.getLogin(), organization.getName());
        member.setOrganization(null);
        userRepository.save(member);
    }

    public List<User> getUserByOrgId(Long orgId) throws NoSuchOrganizationException {
        Organization organization = organizationRepository.findOne(orgId);
        User user = userService.getUserWithAuthorities();

        if(organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization %s does not exist", orgId));
        }

        log.debug("Finding members of organization", organization.getName());
        return userRepository.findUsersByOrganizationId(orgId);
    }

    public List<User> findInvitableUsersByOrgId(Long orgId) {
        Organization organization = organizationRepository.getOne(orgId);

        // if there is no organization than all users should be returned
        if(organization != null) {
            List<User> users = userRepository.findInvitableUsers();
            User owner = null;

            // find the owner and remove him from the list
            // @TODO set the orgId of the owner when set as owner
            for (User user: users) {
                if (organization.getOwner().equals(user)) {
                    owner = user;
                    break;
                }
            }

            if (owner != null) {
                users.remove(owner);
            }

            return users;
        }
        return userRepository.findAll();
    }
}
