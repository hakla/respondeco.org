package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Clemens Puehringer on 18/08/15.
 */
@Getter
@Setter
@Embeddable
public class Address implements Serializable {

    private String country;
    private String zipCode;
    private String city;
    private String street;
    private Integer streetNumber;
    private String extra;

    private Double latitude;
    private Double longitude;

}
