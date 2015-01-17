package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.respondeco.respondeco.domain.Posting;
import org.respondeco.respondeco.domain.PostingFeed;
import org.respondeco.respondeco.domain.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class PostingDTO {

    public static final String[] DEFAULT_FIELDS = {"id", "information", "author", "postingfeed","createddate"};

    public static PostingDTO fromEntity(Posting posting, List<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = Arrays.asList(DEFAULT_FIELDS);
        }
        PostingDTO responseDTO = new PostingDTO();
        if (fieldNames.contains("id")) {
            responseDTO.setId(posting.getId());
        }
        if (fieldNames.contains("information")) {
            responseDTO.setInformation(posting.getInformation());
        }
        if (fieldNames.contains("author")) {
            responseDTO.setAuthor(UserDTO.fromEntity(posting.getAuthor(),Arrays.asList("id","login","organization")));
        }
        if (fieldNames.contains("postingfeed")) {
            responseDTO.setPostingFeed_id(posting.getPostingfeed().getId());
        }
        if (fieldNames.contains("createddate")) {
            responseDTO.setCreatedDate(posting.getCreatedDate().toDate());
        }

        return responseDTO;
    }

    public static List<PostingDTO> fromEntities(List<Posting> postings, List<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = Arrays.asList(DEFAULT_FIELDS);
        }
        List<PostingDTO> responseDTOs = new ArrayList<>();
        for(Posting posting : postings) {
            responseDTOs.add(PostingDTO.fromEntity(posting, fieldNames));
        }
        return responseDTOs;
    }

    private Long id;
    private String information;
    private UserDTO author;
    private Long postingFeed_id;
    private Date createdDate;


    public PostingDTO() {
    }

    public PostingDTO(Long id, String information, User author, PostingFeed postingFeed, Date createdDate) {
        this.id = id;
        this.information = information;
        this.author = UserDTO.fromEntity(author,Arrays.asList("id","login","organization"));
        this.postingFeed_id = postingFeed.getId();
        this.createdDate = createdDate;
    }

    public PostingDTO(Posting posting) {
        this.id = posting.getId();
        this.information = posting.getInformation();
        this.author = UserDTO.fromEntity(posting.getAuthor(),Arrays.asList("id","login","organization"));
        this.postingFeed_id = posting.getPostingfeed().getId();
        this.createdDate = posting.getCreatedDate().toDate();
    }
}
