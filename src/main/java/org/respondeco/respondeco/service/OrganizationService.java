package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.service.exception.ServiceException.ErrorPrefix;
import org.respondeco.respondeco.service.util.Assert;
import org.respondeco.respondeco.service.util.EntityAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

@Service
@Transactional
public class OrganizationService {

    private static final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    public static final ErrorPrefix ERROR_PREFIX                    = new ErrorPrefix("organization");
    public static final String ERROR_ID_NULL                        = "id_null";
    public static final String ERROR_NAME_CHANGE_AFTER_VALIDATION   = "name_change_after_validation";
    public static final String ERROR_NOT_ADMIN                      = "not_admin";
    public static final String ERROR_NOT_OWNER                      = "not_owner";
    public static final String ERROR_NOT_VERIFIED                   = "not_verified";
    public static final String ERROR_NPO_CHANGE_AFTER_VERIFICATION  = "npo_change_after_validation";
    public static final String ERROR_ORGANIZATION_EXISTS            = "exists";

    private ImageRepository imageRepository;
    private ISOCategoryRepository isoCategoryRepository;
    private OrganizationRepository organizationRepository;
    private PostingFeedRepository postingFeedRepository;
    private ProjectRepository projectRepository;
    private ProjectService projectService;
    private ResourceOfferRepository resourceOfferRepository;
    private UserRepository userRepository;
    private UserService userService;

    @Inject
    public OrganizationService(ImageRepository imageRepository,
                               ISOCategoryRepository isoCategoryRepository,
                               OrganizationRepository organizationRepository,
                               PostingFeedRepository postingFeedRepository,
                               ProjectRepository projectRepository,
                               ProjectService projectService,
                               ResourceOfferRepository resourceOfferRepository,
                               UserRepository userRepository,
                               UserService userService) {
        this.imageRepository = imageRepository;
        this.isoCategoryRepository = isoCategoryRepository;
        this.organizationRepository = organizationRepository;
        this.postingFeedRepository = postingFeedRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.resourceOfferRepository = resourceOfferRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * {@see OrganizationService#createOrganizationInformation(String, String, String, Boolean, ImageDTO}
     *
     * @param name
     * @param description
     * @param email
     * @param isNpo
     * @param logoId
     * @return
     * @throws AlreadyInOrganizationException
     * @throws OrganizationAlreadyExistsException
     */
    public Organization create(Organization organization) {
        EntityAssert.Organization.isNew(organization, ERROR_PREFIX);

        Organization existing = organizationRepository.findByName(organization.getName());
        if (existing != null) {
            throw new IllegalValueException(ERROR_PREFIX.join(ERROR_ORGANIZATION_EXISTS),
                "An organization with the name %1 already exists.",
                Arrays.asList("name"), Arrays.asList(organization.getName()));
        }

        PostingFeed postingFeed = new PostingFeed();
        postingFeedRepository.save(postingFeed);
        organization.setPostingFeed(postingFeed);
        organization.getOwner().setOrganization(organizationRepository.save(organization));
        userRepository.save(organization.getOwner());

//        try {
//            projectService.create("ip", "", false, null, null, null, null);
//        } catch (OperationForbiddenException e) {
//            e.printStackTrace();
//        } catch (ResourceNotFoundException e) {
//            e.printStackTrace();
//        } catch (IllegalValueException e) {
//            e.printStackTrace();
//        }

        log.debug("Created Organization: {}", organization);
        return organization;
    }

    /**
     * updates an organization's information with the given data
     */
    public Organization update(Organization updatedOrganization) throws NoSuchEntityException {
        Assert.notNull(updatedOrganization.getId(), ERROR_PREFIX.join(ERROR_ID_NULL),
            "Id must not be null when updating organization information {%1}",
            Arrays.asList("organization"), Arrays.asList(updatedOrganization));
        User currentUser = userService.getUserWithAuthorities();
        Organization currentOrganization = organizationRepository.findByOwner(currentUser);
        //check if the organization belongs to the current user
        if (currentOrganization == null || !currentOrganization.getId().equals(updatedOrganization.getId())) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NOT_OWNER),
                String.format("The current user (%1) is not the owner of organization %2"),
                Arrays.asList("user", "organization"),
                Arrays.asList(currentUser.getLogin(), updatedOrganization.getName()));
        }
        if (currentOrganization.getVerified()) {
            if (!currentOrganization.getName().equals(updatedOrganization.getName())) {
                throw new IllegalValueException(ERROR_PREFIX.join(ERROR_NAME_CHANGE_AFTER_VALIDATION),
                    "The organization name can not be changed after it was verified", null, null);
            }
            if (!currentOrganization.getName().equals(updatedOrganization.getName())) {
                throw new IllegalValueException(ERROR_PREFIX.join(ERROR_NPO_CHANGE_AFTER_VERIFICATION),
                    "The NPO status can not be changed after the organization was verified", null, null);
            }
        }

        currentOrganization.setName(updatedOrganization.getName());
        currentOrganization.setDescription(updatedOrganization.getDescription());
        currentOrganization.setEmail(updatedOrganization.getEmail());
        currentOrganization.setIsNpo(updatedOrganization.getIsNpo());
        currentOrganization.setWebsite(updatedOrganization.getWebsite());
        currentOrganization.setIsoCategories(updatedOrganization.getIsoCategories());
        currentOrganization.setLogo(updatedOrganization.getLogo());

        log.debug("Changing Information for Organization: {}", currentOrganization);
        return organizationRepository.save(currentOrganization);
    }

    /**
     * finds and returns an organization by its name
     *
     * @param orgName the name of the organization to find
     * @return the organization with the given name
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if no organization with that name exists
     */
    @Transactional(readOnly = true)
    public Organization getOrganizationByName(String orgName) throws NoSuchEntityException {
        log.debug("getOrganizationByName(organization) called");

        Organization currentOrganization = organizationRepository.findByName(orgName);
        if (currentOrganization == null) {
            throw new NoSuchEntityException(ERROR_PREFIX, orgName, Organization.class);
        }

        log.debug("Found Information for Organization: {}", currentOrganization);
        return currentOrganization;
    }

    /**
     * Get Organization by Id
     *
     * @param id organization id
     * @return organization
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if organization with id could not be found
     */
    public Organization getById(Long id) {
        log.debug("getOfferById() with id " + id + " called");
        Optional<Organization> nullableOrganization =
            Optional.ofNullable(organizationRepository.findOne(id));
        nullableOrganization.orElseThrow(() -> new NoSuchEntityException(ERROR_PREFIX, id, Organization.class));
        return nullableOrganization.get();
    }

    /**
     * Returns all Organizations
     *
     * @return a list of all active organizations
     */
    public Page<Organization> get(Pageable pageable) {
        log.debug("get() called");
        return organizationRepository.findAll(pageable);
    }

    /**
     * deletes the user's organization
     *
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if the user is not the owner of any organization
     */
    public void delete(Long id) throws NoSuchEntityException {
        Organization currentOrganization = organizationRepository.findOne(id);
        if (currentOrganization == null) {
            throw new NoSuchEntityException(ERROR_PREFIX, id, Organization.class);
        }
        if (currentOrganization.getMembers() != null) {
            for (User user : currentOrganization.getMembers()) {
                userRepository.delete(user);
            }
        }
        User owner = currentOrganization.getOwner();
        userRepository.delete(currentOrganization.getOwner());
        if (currentOrganization.getProjects() != null) {
            for (Project project : currentOrganization.getProjects()) {
                if (!project.getSuccessful()) {
                    projectRepository.delete(project);
                }
            }
        }
        if (currentOrganization.getResourceOffers() != null) {
            for (ResourceOffer resourceOffer : currentOrganization.getResourceOffers()) {
                if (resourceOffer.getResourceMatches() == null) {
                    resourceOfferRepository.delete(resourceOffer);
                }
            }
        }

        organizationRepository.delete(currentOrganization);
        log.debug("Deleted Information for Organization: {}", currentOrganization);
    }

    /**
     * removes a member from the organization
     *
     * @param userId the id of the user to remove
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if the given user does not belong to an organization
     * @throws OperationForbiddenException                                       if the current user is not the owner of the user's organization
     */
    public void deleteMember(Long userId) throws NoSuchEntityException {
        User currentUser = userService.getUserWithAuthorities();
        User member = userRepository.findOne(userId);

        if (member == null) {
            throw new NoSuchEntityException(UserService.ERROR_PREFIX, userId, User.class);
        }
        System.out.println(member);
        Organization organization = organizationRepository.findOne(member.getOrganization().getId());
        if (organization == null) {
            throw new NoSuchEntityException(ERROR_PREFIX, member.getOrganization().getId(), Organization.class);
        }
        if (!organization.getOwner().equals(currentUser)) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NOT_OWNER),
                "Current User (%1, %2) is not owner of Organization %3 (%4)",
                Arrays.asList("userId", "userLogin", "organizationId", "organizationName"),
                Arrays.asList(currentUser.getId(), currentUser.getLogin(), organization.getId(),
                    organization.getName()));
        }
        log.debug("Deleting member from organization", currentUser.getLogin(), organization.getName());
        member.setOrganization(null);
        userRepository.save(member);
    }

    /**
     * searches users by their organization
     *
     * @param orgId the id of the organization to search the users for
     * @return a list of users belonging to the given organization
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if the given organization does not exist
     */
    public Page<User> getUserByOrgId(Long orgId) throws NoSuchEntityException {
        Organization organization = organizationRepository.findOne(orgId);

        if (organization == null) {
            throw new NoSuchEntityException(ERROR_PREFIX, orgId, Organization.class);
        }

        log.debug("Finding members of organization", organization.getName());
        return userRepository.findByOrganization(organization, null);
    }

    /**
     * verify or un-verify an organization
     *
     * @param id    the id of the organization
     * @param value the value of the verified flag
     * @return the updated organization
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException if the organization with the given id could not be found
     * @throws OperationForbiddenException                                       if the current user is not administrator
     */
    public Organization verify(Long id, Boolean value) throws NoSuchEntityException, OperationForbiddenException {
        User currentUser = userService.getUserWithAuthorities();
        //if current user is not admin
        if (!userService.isAdmin(currentUser)) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NOT_ADMIN),
                "Current user (%1) does not have administration authorities",
                Arrays.asList("user"), Arrays.asList(currentUser.getLogin()));
        }
        Organization organization = organizationRepository.findOne(id);
        if (organization == null) {
            throw new NoSuchEntityException(ERROR_PREFIX, id, Organization.class);
        }
        organization.setVerified(value);
        organizationRepository.save(organization);
        return organization;
    }

    /**
     * Get the current Following State of the Organization, instead of loading all Users manually.
     * Returns TRUE: if user already follow the Organization
     *
     * @param organizationId of which we need the following state
     * @return Boolean
     */
    public Boolean followingState(Long organizationId) {

        User currentUser = userService.getUserWithAuthorities();

        return organizationRepository.findOrganizationIfUserFollows(currentUser, organizationId) != null;
    }

    /**
     * Allow user to mark the given Organization as followed. If this become true, all newsfeed from organization
     * will be displayed in users dashboard
     *
     * @param organizationId (organization) that user would like to follow (add to subscripotion)
     */
    public void follow(Long organizationId) {
        User currentUser = userService.getUserWithAuthorities();
        Organization selected = organizationRepository.findOne(organizationId);
        // check if organization exists and is active. "Removed" organization will cause some confusion for users, so throw
        // an exception if organization is deactivated
        if (selected == null) {
            throw new NoSuchEntityException(ERROR_PREFIX, organizationId, Organization.class);
        }
        // if the user does not already follow the organization, add the organization to the organizations which the
        // user follows
        List<Organization> followers = currentUser.getFollowOrganizations();
        if (!followers.contains(selected)) {
            followers.add(selected);
            userRepository.save(currentUser);
        }
    }

    /**
     * Remove user from follower List and stop propagate the news from sepcific organization
     *
     * @param organizationId (organization) to un-follow or remove newsfeed subscription
     */
    public void unfollow(Long organizationId) throws IllegalValueException {
        User currentUser = userService.getUserWithAuthorities();

        Organization selected = organizationRepository.findOrganizationIfUserFollows(currentUser, organizationId);

        // check if organization exists and is active. "Removed" organization will cause some confusion for users, so throw
        // an exception if organization is deactivated
        if (selected == null) {
            throw new NoSuchEntityException(ERROR_PREFIX, organizationId, Organization.class);
        }

        // add new follower
        List<Organization> followers = currentUser.getFollowOrganizations();
        followers.remove(selected);
        userRepository.save(currentUser);
    }

}
