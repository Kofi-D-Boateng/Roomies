package com.roomies.api.util.custom.interfaces;


import java.util.List;
import java.util.Set;

public interface Trie {
    boolean containsPrefix(String prefix);
    int size();
    void insert(String prefix);
    void insert(List<String> prefixes);
    void insert(String[] prefixes);
    void delete(String prefix);
    void delete(String[] prefixes);
    void delete(List<String> prefixes);
    Set<String> getMatches(String prefix);
}
