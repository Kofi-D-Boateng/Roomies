package com.roomies.api.util.external.ip2location;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomies.api.model.roommate.Roommate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "geolocation")
public class IPAddressInfo implements Serializable {

    private static final long serialId = -5344092562L ;

    @Id
    private String id;
    @DBRef
    private Roommate roommate;
    @JsonProperty("ip")
    private String ip;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("region_name")
    private String regionName;

    @JsonProperty("city_name")
    private String cityName;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("zip_code")
    private String zipCode;

    @JsonProperty("time_zone")
    private String timeZone;

    @JsonProperty("asn")
    private String asn;

    @JsonProperty("as")
    private String as;

    @JsonProperty("is_proxy")
    private boolean isProxy;
    private Set<String> userAgents = new HashSet<>();
    private Long blockedDate;
    private String reason;
}
