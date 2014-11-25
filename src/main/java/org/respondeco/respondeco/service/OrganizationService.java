package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.AuthorityRepository;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.PersistentTokenRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.SecurityUtils;
import org.respondeco.respondeco.service.exception.AlreadyInOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.OrganizationAlreadyExistsException;
import org.respondeco.respondeco.service.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Inject
    public OrganizationService(OrganizationRepository organizationRepository, UserService userService, UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public Organization createOrganizationInformation(String name, String description, String email, Boolean isNpo) throws AlreadyInOrganizationException, OrganizationAlreadyExistsException {
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

        if (organizationRepository.findByOwner(currentUser) != null) {
            throw new AlreadyInOrganizationException(String.format("Current User is already owner of an organization"));
        }
        newOrganization.setOwner(currentUser);

        currentUser.setOrgId(organizationRepository.save(newOrganization).getId());
        userRepository.save(currentUser);
        log.debug("Created Information for Organization: {}", newOrganization);
        return newOrganization;
    }

    @Transactional(readOnly = true)
    public Organization getOrganizationByName(String orgName) throws NoSuchOrganizationException {
        log.debug("getOrganizationByName(orgName) called");

        Organization currentOrganization = organizationRepository.findByName(orgName);
        if(currentOrganization == null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist", orgName));
        }

        log.debug("Found Information for Organization: {}", currentOrganization);
        return currentOrganization;
    }

    @Transactional(readOnly = true)
    public Organization getOrganizationByOwner() throws NoSuchOrganizationException {
        log.debug("getOrganizationByOwner() called");

        User currentUser = userService.getUserWithAuthorities();

        Organization currentOrganization = organizationRepository.findByOwner(currentUser);
        if(currentOrganization == null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist", currentUser.getLogin()));
        }
        log.debug("Found Information for Organization: {}", currentOrganization);
        return currentOrganization;
    }

    public void updaterOrganizationInformation(String name, String description, String email, Boolean isNpo) throws NoSuchOrganizationException {
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

        organizationRepository.save(currentOrganization);
        log.debug("Changed Information for Organization: {}", currentOrganization);
    }

    public void deleteOrganizationInformation() throws NoSuchOrganizationException {
        User currentUser = userService.getUserWithAuthorities();
        Organization currentOrganization = organizationRepository.findByOwner(currentUser);
        if(currentOrganization==null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist", currentUser.getLogin()));
        }
        organizationRepository.delete(currentOrganization);
        log.debug("Deleted Information for Organization: {}", currentOrganization);
    }
}
