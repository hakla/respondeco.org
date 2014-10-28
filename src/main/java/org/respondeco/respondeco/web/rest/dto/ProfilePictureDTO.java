package org.respondeco.respondeco.web.rest.dto;

import java.util.Arrays;

/**
 * Created by clemens on 28/10/14.
 */
public class ProfilePictureDTO {

    private String label;

    private byte[] data;

    public ProfilePictureDTO() {
    }

    public ProfilePictureDTO(String label, byte[] data) {
        this.label = label;
        this.data = data;
    }

    public String getLabel() {
        return label;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ProfilePictureDTO{" +
                "label='" + label + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
