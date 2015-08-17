package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;
import org.respondeco.respondeco.web.rest.mapping.DefaultReturnValue;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A Organization.
 */

@Entity
@Table(name = "T_ORGANIZATION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@ToString(exclude = {"owner", "members", "logo", "projects", "FollowingUsers", "isoCategories"})
@JsonIgnoreProperties
public class Organization extends AbstractAuditingEntity implements Serializable {

    @NotNull
    @Size(min = 3, max = 255)
    @Column(length = 255)
    @DefaultReturnValue
    private String name;

    @Column(length = 2048)
    private String description;

    @Email
    @Size(min = 0, max = 100)
    @Column(length = 100)
    @DefaultReturnValue
    private String email;

    @Column(name = "is_npo")
    private Boolean isNpo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;

    @OneToMany(mappedBy = "organization")
    private List<User> members;

    @Column(name = "spokesperson")
    private Long spokesPerson;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image logo;

    @OneToMany(mappedBy = "organization")
    private List<ResourceOffer> resourceOffers;

    @OneToMany(mappedBy = "organization")
    private List<ResourceMatch> resourceMatches;

    @OneToMany(mappedBy = "organization")
    private List<Project> projects;

    @NotNull
    @OneToOne
    @JoinColumn(name = "postingfeed_id")
    private PostingFeed postingFeed;

    @Column(name = "verified")
    private Boolean verified = false;

    @Column(name = "website")
    private String website;

    @ManyToMany
    @JoinTable(
        name="T_ORGANIZATION_T_ISO_CATEGORY",
        joinColumns = {         @JoinColumn(name="ORGANIZATION_ID", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "ISO_CATEGORY_ID", referencedColumnName = "id")}
    )
    private List<ISOCategory> isoCategories;

    @ManyToMany
    @JoinTable(
        name="T_USER_FOLLOW_ORGANIZATION",
        joinColumns = {@JoinColumn(name = "ORGANIZATION_ID", referencedColumnName = "id")},
        inverseJoinColumns = { @JoinColumn(name = "USER_ID", referencedColumnName = "id")}
    )
    private List<User> FollowingUsers;

    public void addMember(User user) {
        members.add(user);
    }

    /**
     * arrange categories based on their super category
     * @return a map whose keys represent super categories and whose values are all the sub categories
     * that this organization has associated with itself
     */
    @DefaultReturnValue(useName = "isoCategories")
    public List<ISOCategory> getOrderedIsoCategories() {
        List<ISOCategory> categoryList = new ArrayList<>();
        Map<ISOCategory,List<ISOCategory>> superCategoryMap = new HashMap<>();
        if(isoCategories == null) {
            return categoryList;
        }
        for(ISOCategory category : isoCategories) {
            if(category.getSuperCategory() == null) {
                if(!superCategoryMap.containsKey(category)) {
                    superCategoryMap.put(category, new ArrayList<>());
                }
            } else {
                if(!superCategoryMap.containsKey(category.getSuperCategory())) {
                    superCategoryMap.put(category.getSuperCategory(), new ArrayList<>());
                }
                superCategoryMap.get(category.getSuperCategory()).add(category);
            }
        }
        for(Map.Entry<ISOCategory, List<ISOCategory>> entry : superCategoryMap.entrySet()) {
            entry.getKey().setSubCategories(entry.getValue());
            categoryList.add(entry.getKey());
        }
        return categoryList;
    }
}
