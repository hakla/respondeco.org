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

    @Inject
    public OrganizationService(OrganizationRepository organizationRepository, UserService userService) {
        this.organizationRepository = organizationRepository;
        this.userService = userService;
    }

    public Organization createOrganizationInformation(String name, String description, String email, Boolean isNpo, String owner) {
        if(organizationRepository.findByName(name)==null) {
            Organization newOrganization = new Organization();

            newOrganization.setName(name);
            newOrganization.setDescription(description);
            newOrganization.setEmail(email);
            newOrganization.setIsNpo(isNpo);

            if(organizationRepository.findByOwner(owner)==null) {
                newOrganization.setOwner(owner);
            }
            else {
                log.debug("Couldn't Create Information for Organization: {}", newOrganization);
                //TODO ADD Exception
                return null;
            }
            organizationRepository.save(newOrganization);
            log.debug("Created Information for Organization: {}", newOrganization);
            return newOrganization;
        }
        else {
            log.debug("Couldn't Create Information for Organization because it already exists");
            //TODO ADD Exception
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Organization getOrganizationByName(String orgName) {
        log.debug("getOrganizationByName(orgName) called");

        Organization currentOrganization = organizationRepository.findByName(orgName);
        if(currentOrganization != null) {
            log.debug("Found Information for Organization: {}", currentOrganization);
            return currentOrganization;
        }
        else {
            log.debug("Couldn't find Organization by Name");
            return null;
        }

    }

    @Transactional(readOnly = true)
    public Organization getOrganizationByOwner() {
        log.debug("getOrganizationByOwner() called");

        User currentUser = userService.getUserWithAuthorities();

        Organization currentOrganization = organizationRepository.findByOwner(currentUser.getLogin());
        if(currentOrganization != null) {
            log.debug("Found Information for Organization: {}", currentOrganization);
            return currentOrganization;
        }
        else {
            log.debug("Couldn't find Organization by Owner");
            return null;
        }

    }

    public void updaterOrganizationInformation(String name, String description, String email, Boolean isNpo, String owner) {
        User currentUser = userService.getUserWithAuthorities();
        Organization currentOrganization = organizationRepository.findByOwner(currentUser.getLogin());
        if(currentOrganization!=null) {
            currentOrganization.setName(name);
            currentOrganization.setDescription(description);
            currentOrganization.setEmail(email);
            currentOrganization.setIsNpo(isNpo);
            currentOrganization.setOwner(owner);
            organizationRepository.save(currentOrganization);
            log.debug("Changed Information for Organization: {}", currentOrganization);
        }
        else {
            log.debug("Couldn't Change Information for Organization: {}", name);
            //TODO ADD Exception
        }
    }

    public void deleteOrganizationInformation() {
        User currentUser = userService.getUserWithAuthorities();
        Organization currentOrganization = organizationRepository.findByOwner(currentUser.getLogin());
        if(currentOrganization!=null) {
            organizationRepository.delete(currentOrganization);
            log.debug("Deleted Information for Organization: {}", currentOrganization);
        }
        else {
            log.debug("Couldn't Delete Information for Organization: {}");
            //TODO ADD Exception
        }
    }


}
