package com.roomies.api.model.geolocation;

import com.google.gson.annotations.SerializedName;
import com.roomies.api.model.Roommate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "geolocation")
public class IPAddressInfo {

    @Id
    private String id;
    @DBRef
    private Roommate roommate;
    @SerializedName("ip")
    private String ip;

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("country_name")
    private String countryName;

    @SerializedName("region_name")
    private String regionName;

    @SerializedName("city_name")
    private String cityName;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("zip_code")
    private String zipCode;

    @SerializedName("time_zone")
    private String timeZone;

    @SerializedName("asn")
    private String asn;

    @SerializedName("as")
    private String as;

    @SerializedName("is_proxy")
    private boolean isProxy;
    private List<String> userAgents = new ArrayList<>();
    private Long blockedDate;
    private String reason;
}
