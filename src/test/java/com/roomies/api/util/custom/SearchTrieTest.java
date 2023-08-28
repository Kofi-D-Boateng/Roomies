package com.roomies.api.util.custom;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SearchTrieTest {

    List<String> words = List.of("Attend","At","Attention","Attitude","Bull","Bullish","Bunt","Belt","Buck");

    private static final SearchTrie searchTrie = new SearchTrie();
    @BeforeEach
    public void setUp(){
        words.forEach(searchTrie::insert);
    }

    @Test
    void containsPrefix() {
        for(String word:words){
            assertTrue(searchTrie.containsPrefix(word));
        }

        int index = 5;
        searchTrie.delete(words.get(index));

        assertFalse(searchTrie.containsPrefix(words.get(index)));
    }

    @Test
    void size() {
        assertEquals(searchTrie.size(), words.size());
    }

    @Test
    void insert() {
        searchTrie.insert("Cat");
        assertTrue(searchTrie.containsPrefix("Cat"));
    }

    @Test
    void delete() {
        searchTrie.delete("Cat");
        assertFalse(searchTrie.containsPrefix("Cat"));
    }

    @Test
    void getMatches() {
        System.out.println("searchTrie = " + searchTrie.size());
        Set<String> matches = searchTrie.getMatches("Att");
        assertNotNull(matches);
        assertEquals(3, matches.size());
    }
}