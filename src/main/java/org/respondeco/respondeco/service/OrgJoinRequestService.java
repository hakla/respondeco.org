package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrgJoinRequestRepository;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.dto.OrganizationResponseDTO;
import org.respondeco.respondeco.web.rest.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class OrgJoinRequestService {

    private final Logger log = LoggerFactory.getLogger(OrgJoinRequestService.class);

    public static final ServiceException.ErrorPrefix ERROR_PREFIX = new ServiceException.ErrorPrefix("orgjoinrequest");
    public static final String ERROR_INVITATION_PENDING = "invitation_pending";
    public static final String ERROR_NO_AUTHORITY       = "no_authority";

    private OrganizationRepository organizationRepository;
    private OrgJoinRequestRepository orgJoinRequestRepository;

    private UserRepository userRepository;
    private UserService userService;

    @Inject
    public OrgJoinRequestService(OrganizationRepository organizationRepository,
                                 OrgJoinRequestRepository orgJoinRequestRepository,
                                 UserRepository userRepository,
                                 UserService userService) {
        this.organizationRepository=organizationRepository;
        this.orgJoinRequestRepository=orgJoinRequestRepository;
        this.userRepository=userRepository;
        this.userService=userService;
    }

    /**
     * creates a new orgjoinrequest, therefore organization and user is needed
     * @param organizationDTO dto object for the organization for which the user should be invited
     * @param userDTO dto object for the user to be invited
     * @return the successfull created orgjoinrequest
     * @throws NoSuchEntityException is thrown if organization or user doesn't exist
     * @throws OperationForbiddenException is thrown if the user is already invited in the organization
     * @throws IllegalValueException is thrown if the user is not owner of the organization or or he has an open invitation to join the organization
     */
    public OrgJoinRequest createOrgJoinRequest(OrganizationResponseDTO organizationDTO, UserDTO userDTO)
        throws IllegalValueException {
        User currentUser = userService.getUserWithAuthorities();
        User user = userRepository.findOne(userDTO.getId());
        Organization organization = organizationRepository.findOne(organizationDTO.getId());
        if(user == null) {
            throw new NoSuchEntityException(UserService.ERROR_PREFIX, userDTO.getId(), User.class);
        }
        if(organization == null) {
            throw new NoSuchEntityException(OrganizationService.ERROR_PREFIX, organizationDTO.getId(),
                Organization.class);
        }
        if(!organization.getVerified()) {
            throw new OperationForbiddenException(
                OrganizationService.ERROR_PREFIX.join(OrganizationService.ERROR_NOT_VERIFIED),
                "Cannot create organization join request, organization %1 (%2) is not yet verified",
                Arrays.asList("organizationId", "organizationName"),
                Arrays.asList(organization.getId(), organization.getName()));
        }
        if(!organization.getOwner().equals(currentUser)) {
            throw new OperationForbiddenException(
                OrganizationService.ERROR_PREFIX.join(OrganizationService.ERROR_NOT_OWNER),
                "Current User (%1, %2) is not owner of Organization %3 (%4)",
                Arrays.asList("userId", "userLogin", "organizationId", "organizationName"),
                Arrays.asList(currentUser.getId(), currentUser.getLogin(), organization.getId(),
                    organization.getName()));
        }

        OrgJoinRequest orgJoinRequest = orgJoinRequestRepository.findByUserAndOrganizationAndActiveIsTrue(user, organization);

        if (orgJoinRequest != null) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_INVITATION_PENDING),
                "An invitation from organization %1 (%2) to user %3 (%4) is already pending.",
                Arrays.asList("organizationId", "organizationName", "userId", "userLogin"),
                Arrays.asList(organization.getId(), organization.getName(), user.getId(), user.getLogin()));
        }

        orgJoinRequest = new OrgJoinRequest();
        orgJoinRequest.setOrganization(organization);
        orgJoinRequest.setUser(user);
        orgJoinRequestRepository.save(orgJoinRequest);
        log.debug("Created OrgJoinRequest: {}", orgJoinRequest);
        return orgJoinRequest;
    }

    /**
     * gets a list of active orgjoinrequests for the given organization
     * @param id given id of the organization
     * @return a list of orgjoinrequests for the organization
     * @throws NoSuchEntityException organization doesn't exist
     */
    public List<OrgJoinRequest> getOrgJoinRequestByOrganization(Long id) throws NoSuchEntityException {
        Organization organization = organizationRepository.findOne(id);

        if(organization == null) {
            throw new NoSuchEntityException(OrganizationService.ERROR_PREFIX, id, Organization.class);
        }
        log.debug("Found List of OrgJoinRequest by OrgName");

        return orgJoinRequestRepository.findByOrganizationAndActiveIsTrue(organization);
    }

    /**
     * gets a list of orgjoinrequests (invitations) for the current user
     * @return all orgjoinrequest for the current user
     */
    public List<OrgJoinRequest> getOrgJoinRequestByCurrentUser() {
        log.debug("Get List of OrgJoinRequest by Current User");

        return getOrgJoinRequestByUser(userService.getUserWithAuthorities());
    }

    /**
     * gets a list of orgjoinrequests for a given user
     * @param user user for which the orgjoinrequests should be found
     * @return all active orgjoinrequests (invitations) for the given user
     */
    public List<OrgJoinRequest> getOrgJoinRequestByUser(User user) {
        log.debug("Get List of OrgJoinRequest by User");
        return orgJoinRequestRepository.findByUserAndActiveIsTrue(user);
    }

    /**
     * gets all orgjoinrequests (should be only possible for the admin; restriction in controller)
     * @return all orgjoinrequests
     */
    public List<OrgJoinRequest> getAll() {
        return orgJoinRequestRepository.findAll();
    }

/*
    public List<OrgJoinRequestWithActiveFlagDTO> getOrgJoinRequestsByOwner() throws NoSuchOrganizationException {
        User user = userService.getUserWithAuthorities();
        log.debug("Get List of OrgJoinRequest by Owner");
        Organization organization = organizationRepository.findByOwner(user);
        if(organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist"));
        }
        List<OrgJoinRequestWithActiveFlagDTO> orgJoinRequestWithActiveFlagDTOList = new ArrayList<OrgJoinRequestWithActiveFlagDTO>();
        OrgJoinRequestWithActiveFlagDTO orgJoinRequestWithActiveFlagDTO;
        for (OrgJoinRequest orgJoinRequest:orgJoinRequestRepository.findByOrganization(organization)) {
            orgJoinRequestWithActiveFlagDTO = new OrgJoinRequestWithActiveFlagDTO();
            orgJoinRequestWithActiveFlagDTO.setId(orgJoinRequest.getId());
            orgJoinRequestWithActiveFlagDTO.setOrgName((orgJoinRequest.getOfferById()).getName());
            orgJoinRequestWithActiveFlagDTO.setUserLogin((orgJoinRequest.getUser()).getLogin());
            orgJoinRequestWithActiveFlagDTOList.add(orgJoinRequestWithActiveFlagDTO);
        }

        return orgJoinRequestWithActiveFlagDTOList;
    }*/

    /**
     * method to accept an orgjoinrequest; given user accepts the request given by requestId
     * @param requestId given id for the orgjoinrequest to be accepted
     * @param user given user to accept orgjoinrequest
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException is thrown if the organization doesn't exist
     */
    public void acceptRequest(Long requestId, User user) throws NoSuchEntityException {
        OrgJoinRequest orgJoinRequest = orgJoinRequestRepository.findByIdAndActiveIsTrue(requestId);
        if(null == orgJoinRequest) {
            throw new NoSuchEntityException(ERROR_PREFIX, requestId, OrgJoinRequest.class);
        }
        if(!user.equals(orgJoinRequest.getUser())) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_AUTHORITY),
                "User %1 (%2) has no authority to accept request %3",
                Arrays.asList("userId", "userLogin", "orgJoinRequestId"),
                Arrays.asList(user.getId(), user.getLogin(), requestId));
        }
        Organization organization = organizationRepository.findOne(orgJoinRequest.getOrganization().getId());
        if(organization == null) {
            throw new NoSuchEntityException(OrganizationService.ERROR_PREFIX, orgJoinRequest.getOrganization().getId(),
                Organization.class);
        }

        user.setOrganization(organization);
        orgJoinRequest.setActive(false);
        userRepository.save(user);
        orgJoinRequestRepository.save(orgJoinRequest);
        log.debug("Accepted Request and Deleted OrgJoinRequest: {}", requestId);
    }

    /**
     * @see #acceptRequest(Long requestId , User user)
     */
    public void acceptRequest(Long requestId) throws NoSuchEntityException {
        acceptRequest(requestId, userService.getUserWithAuthorities());
    }

    /**
     * method to decline an orgjoinrequest; given user declines the request given by requestId and sets active false
     * @param requestId given id for the orgjoinrequest to be declined
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException is thrown if the organization doesn't exist
     */
    public void declineRequest(Long requestId) throws NoSuchEntityException {
        User user = userService.getUserWithAuthorities();
        OrgJoinRequest orgJoinRequest = orgJoinRequestRepository.findByIdAndActiveIsTrue(requestId);
        if(orgJoinRequest==null) {
            throw new NoSuchEntityException(ERROR_PREFIX, requestId, OrgJoinRequest.class);
        }
        if(!user.equals(orgJoinRequest.getUser())) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_AUTHORITY),
                "User %1 (%2) has no authority to accept request %3",
                Arrays.asList("userId", "userLogin", "orgJoinRequestId"),
                Arrays.asList(user.getId(), user.getLogin(), requestId));
        }
        orgJoinRequest.setActive(false);
        orgJoinRequestRepository.save(orgJoinRequest);
        log.debug("Declined Request and Deleted OrgJoinRequest: {}", requestId);
    }

    /**
     * method to delete an orgjoinrequest; real deletion of orgjoinrequest in the repository
     * @param id given id of the orgjoinrequest to be deleted
     * @throws NoSuchEntityException is thrown if the organization doesn't exist
     * @throws OperationForbiddenException is thrown if user is not owner of organization
     */
    public void delete(Long id) throws NoSuchEntityException {
        User currentUser = userService.getUserWithAuthorities();
        OrgJoinRequest orgJoinRequest = orgJoinRequestRepository.findOne(id);
        if (orgJoinRequest == null) {
            throw new NoSuchEntityException(ERROR_PREFIX, id, OrgJoinRequest.class);
        }
        if(currentUser.getOrganization() == null) {
            throw new OperationForbiddenException(
                OrganizationService.ERROR_PREFIX.join(OrganizationService.ERROR_NOT_OWNER),
                "Current User (%1, %2) is not owner of an Organization",
                Arrays.asList("userId", "userLogin"),
                Arrays.asList(currentUser.getId(), currentUser.getLogin()));
        }
        if(!currentUser.equals(currentUser.getOrganization().getOwner())) {
            throw new OperationForbiddenException(
                OrganizationService.ERROR_PREFIX.join(OrganizationService.ERROR_NOT_OWNER),
                "Current User (%1, %2) is not owner of Organization %3 (%4)",
                Arrays.asList("userId", "userLogin", "organizationId", "organizationName"),
                Arrays.asList(currentUser.getId(), currentUser.getLogin(), currentUser.getOrganization().getId(),
                    currentUser.getOrganization().getName()));
        }
        if (orgJoinRequest.getOrganization() != currentUser.getOrganization()) {
            throw new OperationForbiddenException(
                OrganizationService.ERROR_PREFIX.join(OrganizationService.ERROR_NOT_OWNER),
                "Current User (%1, %2) is not owner of Organization %3 (%4)",
                Arrays.asList("userId", "userLogin", "organizationId", "organizationName"),
                Arrays.asList(currentUser.getId(), currentUser.getLogin(), orgJoinRequest.getOrganization().getId(),
                    orgJoinRequest.getOrganization().getName()));
        }
        orgJoinRequestRepository.delete(id);
        log.debug("Deleted OrgJoinRequest: {}", id);
    }
}
