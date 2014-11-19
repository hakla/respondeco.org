package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrgJoinRequestRepository;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrgJoinRequestService {

    private final Logger log = LoggerFactory.getLogger(OrgJoinRequestService.class);

    private OrgJoinRequestRepository orgJoinRequestRepository;

    private UserService userService;

    private UserRepository userRepository;

    private OrganizationRepository organizationRepository;

    @Inject
    public OrgJoinRequestService(OrgJoinRequestRepository orgJoinRequestRepository, UserService userService, UserRepository userRepository, OrganizationRepository organizationRepository) {
        this.orgJoinRequestRepository=orgJoinRequestRepository;
        this.userService=userService;
        this.userRepository=userRepository;
        this.organizationRepository=organizationRepository;
    }
    public OrgJoinRequest createOrgJoinRequest(Long orgId, String userlogin) {
        User user = userRepository.findOne(userlogin);
        if(organizationRepository.findOne(orgId) != null && user != null) {
            OrgJoinRequest orgJoinRequest = new OrgJoinRequest();
            orgJoinRequest.setOrgId(orgId);
            orgJoinRequest.setUserLogin(userlogin);
            orgJoinRequestRepository.save(orgJoinRequest);
            log.debug("Created OrgJoinRequest: {}", orgJoinRequest);
            return orgJoinRequest;
        }
        else {
            log.debug("Couldn't Create OrgJoinRequest: {}", orgId);
            return null;
        }

    }

    public List<OrgJoinRequest> getOrgJoinRequestByOrgName(String orgName) {
        Organization organization = organizationRepository.findByName(orgName);

        if(organization != null) {
            log.debug("Found List of OrgJoinRequest by OrgName");
            return orgJoinRequestRepository.findByOrgId(organization.getId());
        }
        else {
            log.debug("Couldn't find List of OrgJoinRequest by OrgName");
            return null;
        }
    }

    public List<OrgJoinRequest> getRequestsByOwner() {
        User user = userService.getUserWithAuthorities();
        Organization organization = organizationRepository.findByOwner(user.getLogin());
        if(organization != null) {
            log.debug("Found List of OrgJoinRequest by Owner");
            return orgJoinRequestRepository.findByOrgId(organization.getId());

        }
        else {
            log.debug("Couldn't find List of OrgJoinRequest by Owner");
            return null;
        }
    }

    public void acceptRequest(Long requestId) {
        User user = userService.getUserWithAuthorities();
        OrgJoinRequest orgJoinRequest = orgJoinRequestRepository.findOne(requestId);
        if(orgJoinRequest != null) {
            Organization organization = organizationRepository.findOne(orgJoinRequest.getOrgId());
            if(organization.getOwner().equals(user.getLogin())) {
                if(organization != null) {
                    User member = userRepository.findOne(orgJoinRequest.getUserLogin());
                    if(member != null) {
                        member.setOrgId(organization.getId());
                        orgJoinRequestRepository.delete(requestId);
                        log.debug("Accepted User and Deleted OrgJoinRequest: {}", requestId);
                    }
                }
            }
        }
        else {
            log.debug("Couldn't Accept OrgJoinRequest: {}", requestId);
        }
    }

    public void declineRequest(Long requestId) {
        User user = userService.getUserWithAuthorities();
        OrgJoinRequest orgJoinRequest = orgJoinRequestRepository.findOne(requestId);
        if(orgJoinRequest != null) {
            Organization organization = organizationRepository.findOne(orgJoinRequest.getOrgId());
            if(organization.getOwner().equals(user.getLogin())) {
                orgJoinRequestRepository.delete(requestId);
                log.debug("Declined User and Deleted OrgJoinRequest: {}", requestId);
            }
        }
        else {
            log.debug("Couldn't Decline OrgJoinRequest: {}", requestId);
        }
    }


    public List<OrgJoinRequest> getRequestForUser() {
        User user = userService.getUserWithAuthorities();
        return orgJoinRequestRepository.findByUserLogin(user.getLogin());
    }
}
