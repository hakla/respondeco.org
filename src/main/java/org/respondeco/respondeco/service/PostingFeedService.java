package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Service
public class PostingFeedService {

    private PostingFeedRepository postingFeedRepository;

    private ProjectRepository projectRepository;

    private OrganizationRepository organizationRepository;

    private UserService userService;

    private PostingRepository postingRepository;

    @Inject
    public PostingFeedService(PostingFeedRepository postingFeedRepository,
                              ProjectRepository projectRepository,
                              OrganizationRepository organizationRepository,
                              UserService userService,
                              PostingRepository postingRepository) {
        this.postingFeedRepository = postingFeedRepository;
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.userService = userService;
        this.postingRepository = postingRepository;

    }

    /**
     * method to add a posting to the postingfeed of the given project
     * @param projectId the id of the project for which the posting should be added
     * @param information information of the posting
     * @return returns the added posting
     * @throws PostingFeedException is thrown if user is not manager, information of posting is empty or
     * project doesn't have a postingfeed
     */
    public Posting addPostingForProjects(Long projectId, String information) throws
        PostingFeedException, NoSuchEntityException {
        User currentUser = userService.getUserWithAuthorities();
        Project project = projectRepository.findByIdAndActiveIsTrue(projectId);
        if(project == null) {
            throw new NoSuchEntityException(projectId);
        }
        if(currentUser.equals(project.getManager()) == false) {
            throw new PostingFeedException(".notmanagerofproject", String.format("User is not manager of project"));
        }
        if(information.length() == 0) {
            throw new PostingFeedException(".emptyinformation",String.format("Information must not be empty"));
        }
        PostingFeed postingFeed = project.getPostingFeed();
        if(postingFeed == null) {
            throw new PostingFeedException(".nopostingfeed",
                    String.format("Project %s does not have a postingfeed", projectId));
        }
        Posting posting = new Posting();
        posting.setInformation(information);
        posting.setAuthor(currentUser);
        posting.setPostingfeed(postingFeed);

        return postingRepository.save(posting);
    }

    /**
     * method to add a posting to the postingfeed of the given organization
     * @param organizationId the id of the organization for which the posting should be added
     * @param information information of the posting
     * @return returns the added posting
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException is thrown if organization doesn't exist
     * @throws PostingFeedException is thrown if user is not owner, information of posting is empty or
     * organization doesn't have a postingfeed
     */
    public Posting createPostingForOrganization(Long organizationId, String information) throws
        NoSuchEntityException,
            PostingFeedException {
        User currentUser = userService.getUserWithAuthorities();
        Organization organization = organizationRepository.findByIdAndActiveIsTrue(organizationId);
        if(organization == null) {
            throw new NoSuchEntityException(organizationId);
        }
        if(currentUser.equals(organization.getOwner()) == false) {
            throw new PostingFeedException(".notowneroforganization", String.format("User is not owner of organization"));
        }
        if(information.length() == 0) {
            throw new PostingFeedException(".emptyinformation",String.format("Information must not be empty"));
        }
        PostingFeed postingFeed = organization.getPostingFeed();
        if(postingFeed == null) {
            throw new PostingFeedException(".nopostingfeed",
                    String.format("Organization %s does not have a postingfeed", organizationId));
        }
        Posting posting = new Posting();
        posting.setInformation(information);
        posting.setAuthor(currentUser);
        posting.setPostingfeed(postingFeed);

        return postingRepository.save(posting);
    }

    /**
     * method to return a page of postings for the given organization; page settings are defined by the restParameters
     * @param organizationId the given id of the organization for which the postings are searched
     * @param pageable pagination options for retrieving values from the database
     * @return returns a page of postings for the given organization
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException is thrown if the organization doesn't exist
     */
    public Page<Posting> getPostingsForOrganization(Long organizationId, Pageable pageable) throws
        NoSuchEntityException {
        Organization organization = organizationRepository.findByIdAndActiveIsTrue(organizationId);
        if(organization == null) {
            throw new NoSuchEntityException(organizationId);
        }
        return postingFeedRepository.getPostingsForOrganization(organizationId, pageable);
    }

    /**
     * method to return a page of postings for the given project; page settings are defined by the restParameters
     * @param projectId the given id of the project for which the postings are searched
     * @param pageable paging information to be used when retrieving values from the database
     * @return returns a page of postings for the given project
     * @throws NoSuchEntityException is thrown if the project doesn't exist
     */
    public Page<Posting> getPostingsForProject(Long projectId, Pageable pageable) throws
            NoSuchEntityException {
        Project project = projectRepository.findByIdAndActiveIsTrue(projectId);
        if(project == null) {
            throw new NoSuchEntityException(projectId);
        }
        return postingFeedRepository.getPostingsForProject(projectId, pageable);
    }

    /**
     * method to delete (setting active flag false) a posting with the given id
     * @param postingId the given id of the posting to be deleted
     * @throws PostingException is thrown if the posting doesn't exist or the user is not the author of the posting
     */
    public void deletePosting(Long postingId) throws PostingException {
        Posting posting = postingRepository.findByIdAndActiveIsTrue(postingId);
        User currentUser = userService.getUserWithAuthorities();
        if(posting == null) {
            throw new PostingException(".noSuchPosting", String.format("There is no such posting"));
        }
        if(posting.getAuthor().equals(currentUser) == false) {
            throw new PostingException(".userIsNotAuthorOfPosting", String.format("You are not the author of this posting"));
        }
        posting.setActive(false);
        postingRepository.save(posting);
    }
}
