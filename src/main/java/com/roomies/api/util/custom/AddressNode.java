package com.roomies.api.util.custom;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddressNode extends SearchTrieNode{
    protected double latitude;
    protected double longitude;
    protected String locale;

    public AddressNode() {
        super();
        this.latitude = 0;
        this.longitude = 0;
    }
}
