package com.roomies.api.util.external.ip2location;

import lombok.NonNull;

import java.io.IOException;

@FunctionalInterface
public interface IPLocator {
    IPAddressInfo reverseLookup(@NonNull String ip) throws IOException;
}
