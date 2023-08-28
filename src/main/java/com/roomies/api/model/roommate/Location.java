package com.roomies.api.model.roommate;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "location")
public class Location {
    @Id
    private String id;
    @DBRef
    private Roommate roommate;
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
