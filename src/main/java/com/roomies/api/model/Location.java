package com.roomies.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "location")
public class Location {
    private String id;
    @JsonIgnore
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

}
