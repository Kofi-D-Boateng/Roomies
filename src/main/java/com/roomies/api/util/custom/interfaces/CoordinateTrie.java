package com.roomies.api.util.custom.interfaces;

import com.roomies.api.util.custom.AddressNode;

public interface CoordinateTrie extends Trie{
    void insert(String address, double lat, double lon,String locale);

    /**
     *
     * @param address The address to search for in the trie
     * @return A static array with two coordinates: latitude and longitude
     */
    double[] getCoords(String address);

    /**
     *
     * @param address The address to search for in the trie
     * @return The Node which represents the last matched character in the address.
     */
    AddressNode getNode(String address);

    /**
     *
     * @param address The address to search for in the trie.
     * @param lat The latitude of the address
     * @param lon The longitude of the address
     * @param locale The locale of the address
     */
    void updateNode(String address, double lat, double lon,String locale);
}
