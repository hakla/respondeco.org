package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;
import org.respondeco.respondeco.web.rest.mapping.DefaultReturnValue;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by clemens on 23/06/15.
 */

@Entity
@Table(name = "T_ISO_CATEGORY")
@Getter
@Setter
public class ISOCategory extends AbstractAuditingEntity {

    @NotNull
    @Size(max = 100)
    @DefaultReturnValue
    private String key;

    @OneToMany(mappedBy = "superCategory")
    @DefaultReturnValue(maxDepth = 1)
    private List<ISOCategory> subCategories;

    @ManyToOne
    @JoinColumn(name = "super_category_id")
    private ISOCategory superCategory;

    @ManyToMany
    @JoinTable(
        name="T_ORGANIZATION_T_ISO_CATEGORY",
        joinColumns = {         @JoinColumn(name="ISO_CATEGORY_ID", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "ORGANIZATION_ID", referencedColumnName = "id")}
    )
    private List<Organization> organizations;

}
