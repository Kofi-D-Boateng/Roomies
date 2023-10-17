package com.roomies.api.model.roommate;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "location")
public class Location implements Serializable {
    private static final long serializableId = 65466425627L;
    @Id
    @JsonIgnore
    private String id;
    @DBRef
    @JsonIgnore
    private transient Roommate roommate;
    private String country;
    private String area;
    @Field("area_code")
    private String areaCode;
    @Field("house_number")
    private Integer houseNumber;
    private String street;
    @Field("apartment_suffix")
    private Object apartmentSuffix;
    private Double latitude;

    private Double longitude;

    @Override
    public String toString() {
        return "Location{" +
                "id='" + id + '\'' +
                ", roommate=" + (roommate != null ? roommate.getId():null) +
                ", country='" + country + '\'' +
                ", area='" + area + '\'' +
                '}';

//        return "Location{" +
//                "id='" + id + '\'' +
//                ", roommate=" + (roommate != null ? roommate.getId():null) +
//                ", country='" + country + '\'' +
//                ", area='" + area + '\'' +
//                ", areaCode='" + areaCode + '\'' +
//                ", houseNumber=" + houseNumber +
//                ", street='" + street + '\'' +
//                ", apartmentSuffix=" + apartmentSuffix +
//                '}';
    }
}
