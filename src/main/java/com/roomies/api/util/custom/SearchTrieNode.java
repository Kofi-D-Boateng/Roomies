package com.roomies.api.util.custom;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class SearchTrieNode {
    protected Map<Character,Object> values;
    protected Set<String> finishedWords;
    protected boolean end;

    public SearchTrieNode() {
        this.values = new HashMap<>();
        this.finishedWords = new HashSet<>();
        this.end = false;
    }
}
