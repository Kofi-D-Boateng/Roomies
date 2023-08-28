package com.roomies.api.util.custom;

import com.roomies.api.util.custom.interfaces.Trie;

import java.util.List;
import java.util.Set;

public class SearchTrie implements Trie {
    protected SearchTrieNode root;
    protected int size;

    public SearchTrie(){
        root = new SearchTrieNode();
        size = 0;
    }
    @Override
    public boolean containsPrefix(String prefix) {
        if(root == null) return false;
        SearchTrieNode node = root;
        for(Character ch: prefix.toCharArray()){
            if(node.getValues().containsKey(ch)) node = (SearchTrieNode) node.getValues().get(ch);
            else return false;
        }
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void insert(String wordToInsert) {
        if(wordToInsert.trim().length() == 0) return;
        SearchTrieNode node = root;
        for(Character ch:wordToInsert.toCharArray()){
            node.getFinishedWords().add(wordToInsert);
            if(!node.getValues().containsKey(ch)){
                node.getValues().putIfAbsent(ch,new SearchTrieNode());
            }
            node = (SearchTrieNode) node.getValues().get(ch);
//            node.getFinishedWords().add(wordToInsert);
        }
        node.setEnd(true);
        size++;
    }

    @Override
    public void insert(List<String> prefixes) {
        if(prefixes == null || prefixes.size() == 0) return;
        for(String prefix: prefixes){
            SearchTrieNode node = root;
            for(Character ch:prefix.toCharArray()){
                node.getFinishedWords().add(prefix);
                if(!node.getValues().containsKey(ch)){
                    node.getValues().putIfAbsent(ch,new SearchTrieNode());
                }
                node = (SearchTrieNode) node.getValues().get(ch);
//            node.getFinishedWords().add(wordToInsert);
            }
            node.setEnd(true);
            size++;
        }
    }

    @Override
    public void insert(String[] prefixes) {
        if(prefixes == null) return;
        for(String prefix: prefixes){
            SearchTrieNode node = root;
            for(Character ch:prefix.toCharArray()){
                node.getFinishedWords().add(prefix);
                if(!node.getValues().containsKey(ch)){
                    node.getValues().putIfAbsent(ch,new SearchTrieNode());
                }
                node = (SearchTrieNode) node.getValues().get(ch);
//            node.getFinishedWords().add(wordToInsert);
            }
            node.setEnd(true);
            size++;
        }
    }

    @Override
    public void delete(String prefix) {
        if(prefix.trim().length() == 0) return;
        SearchTrieNode node = root;
        for(Character ch:prefix.toCharArray()){
            if(!node.getValues().containsKey(ch)){
                return;
            }
            node = (SearchTrieNode) node.getValues().get(ch);
        }
        node.setEnd(false);
        size--;
    }

    @Override
    public void delete(String[] prefixes) {
        if(prefixes == null) return;
        for(String prefix: prefixes){
            SearchTrieNode node = root;
            for(Character ch:prefix.toCharArray()){
                if(!node.getValues().containsKey(ch)){
                    return;
                }
                node = (SearchTrieNode) node.getValues().get(ch);
            }
            node.setEnd(false);
            size--;
        }
    }

    @Override
    public void delete(List<String> prefixes) {
        if(prefixes == null || prefixes.size() == 0) return;
        for(String prefix: prefixes){
            SearchTrieNode node = root;
            for(Character ch:prefix.toCharArray()){
                if(!node.getValues().containsKey(ch)){
                    return;
                }
                node = (SearchTrieNode) node.getValues().get(ch);
            }
            node.setEnd(false);
            size--;
        }

    }

    @Override
    public Set<String> getMatches(String prefix) {
        SearchTrieNode node = root;
        char[] prefixArr = prefix.toCharArray();
        for(Character character: prefixArr){
            if(!node.getValues().containsKey(character)) return null;
            node = (SearchTrieNode) node.getValues().get(character);
        }
        return node.getFinishedWords();
    }
}
