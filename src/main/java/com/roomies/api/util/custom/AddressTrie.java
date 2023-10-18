package com.roomies.api.util.custom;

import com.roomies.api.util.custom.interfaces.CoordinateTrie;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Data
@Slf4j
public class AddressTrie implements CoordinateTrie {
    private AddressNode root;
    private int size;

    public AddressTrie() {
        this.root = new AddressNode();
        this.size = 0;
    }

    @Override
    public void insert(String address, double lat, double lon, String locale) {
        log.info("Attempting to insert the following attributes into the tree:\nAddress: {}\nLatitude: {}\nLongitude: {}\nLocale: {}",address,lat,lon,locale);
        if(address.trim().length() == 0) return;
        AddressNode node = root;
        for(Character ch:address.toCharArray()){
            if(Character.isWhitespace(ch))continue;
            node.getFinishedWords().add(address);
            if(!node.getValues().containsKey(ch) && !node.getValues().containsKey(Character.toUpperCase(ch))){
                node.getValues().putIfAbsent(ch,new AddressNode());
            }
            node =  node.getValues().containsKey(ch) ? (AddressNode) node.getValues().get(ch) : (AddressNode) node.getValues().get(Character.toUpperCase(ch));
            log.info("CURRENT CHAR: {}",ch);
        }
        node.setEnd(true);
        node.setLatitude(lat);
        node.setLongitude(lon);
        node.setLocale(locale);
        size++;
        log.info("Successfully inserted {} into the tree.\nSize is now {}",address,size);
    }

    @Override
    public boolean containsPrefix(String prefix) {
        log.info("Attempting search for {}",prefix);
        if(root == null) return false;
        AddressNode node = root;
        for(Character ch: prefix.toCharArray()){
            if(Character.isWhitespace(ch))continue;
            if(node.getValues().containsKey(ch)|| node.getValues().containsKey(Character.toUpperCase(ch))) node = (AddressNode) node.getValues().get(ch);
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
        log.info("Attempting tempting to insert {}",wordToInsert);
        if(wordToInsert.trim().length() == 0) return;
        AddressNode node = root;
        for(Character ch:wordToInsert.toCharArray()){
            if(Character.isWhitespace(ch))continue;
            node.getFinishedWords().add(wordToInsert);
            if(!node.getValues().containsKey(ch) || !node.getValues().containsKey(Character.toUpperCase(ch))){
                node.getValues().putIfAbsent(ch,new AddressNode());
            }
            node =  node.getValues().containsKey(ch) ? (AddressNode) node.getValues().get(ch) : (AddressNode) node.getValues().get(Character.toUpperCase(ch));
//            node.getFinishedWords().add(wordToInsert);
        }
        node.setEnd(true);
        size++;
        log.info("Successfully inserted {} into the tree.\nSize is now {}",wordToInsert,size);
    }

    @Override
    public void insert(List<String> prefixes) {
        log.info("Attempting tempting to insert {}",prefixes);
        if(prefixes == null || prefixes.size() == 0) return;
        for(String prefix: prefixes){
            AddressNode node = root;
            for(Character ch:prefix.toCharArray()){
                if(Character.isWhitespace(ch))continue;
                node.getFinishedWords().add(prefix);
                if(!node.getValues().containsKey(ch) || !node.getValues().containsKey(Character.toUpperCase(ch))){
                    node.getValues().putIfAbsent(ch,new AddressNode());
                }
                node =  node.getValues().containsKey(ch) ? (AddressNode) node.getValues().get(ch) : (AddressNode) node.getValues().get(Character.toUpperCase(ch));
            }
            node.setEnd(true);
            size++;
        }
        log.info("Successfully inserted {} into the tree.\nSize is now {}",prefixes,size);
    }

    @Override
    public void insert(String[] prefixes) {
        log.info("Attempting tempting to insert {}", Arrays.toString(prefixes));
        if(prefixes == null) return;
        for(String prefix: prefixes){
            SearchTrieNode node = root;
            for(Character ch:prefix.toCharArray()){
                if(Character.isWhitespace(ch))continue;
                node.getFinishedWords().add(prefix);
                if(!node.getValues().containsKey(ch) || !node.getValues().containsKey(Character.toUpperCase(ch))){
                    node.getValues().putIfAbsent(ch,new AddressNode());
                }
                node =  node.getValues().containsKey(ch) ? (AddressNode) node.getValues().get(ch) : (AddressNode) node.getValues().get(Character.toUpperCase(ch));
            }
            node.setEnd(true);
            size++;
        }
        log.info("Successfully inserted {} into the tree.\nSize is now {}",Arrays.toString(prefixes),size);
    }

    @Override
    public void delete(String prefix) {
        log.info("Attempting tempting to delete {}. Size is currently {}",prefix,size);
        if(prefix.trim().length() == 0) return;
        AddressNode node = root;
        for(Character ch:prefix.toCharArray()){
            if(Character.isWhitespace(ch))continue;
            if(!node.getValues().containsKey(ch) || !node.getValues().containsKey(Character.toUpperCase(ch))){
                return;
            }
            node =  node.getValues().containsKey(ch) ? (AddressNode) node.getValues().get(ch) : (AddressNode) node.getValues().get(Character.toUpperCase(ch));
        }
        node.setEnd(false);
        size--;
        log.info("Successfully deleted {} into the tree.\nSize is now {}",prefix,size);
    }

    @Override
    public void delete(String[] prefixes) {
        log.info("Attempting tempting to delete {}. Size is currently {}",Arrays.toString(prefixes),size);
        if(prefixes == null) return;
        for(String prefix: prefixes){
            AddressNode node = root;
            for(Character ch:prefix.toCharArray()){
                if(Character.isWhitespace(ch))continue;
                if(!node.getValues().containsKey(ch) || !node.getValues().containsKey(Character.toUpperCase(ch))){
                    return;
                }
                node =  node.getValues().containsKey(ch) ? (AddressNode) node.getValues().get(ch) : (AddressNode) node.getValues().get(Character.toUpperCase(ch));
            }
            node.setEnd(false);
            size--;
        }
        log.info("Successfully deleted {} into the tree.\nSize is now {}",Arrays.toString(prefixes),size);
    }

    @Override
    public void delete(List<String> prefixes) {
        log.info("Attempting tempting to delete {}. Size is currently {}",prefixes,size);
        if(prefixes == null || prefixes.size() == 0) return;
        for(String prefix: prefixes){
            AddressNode node = root;
            for(Character ch:prefix.toCharArray()){
                if(Character.isWhitespace(ch))continue;
                if(!node.getValues().containsKey(ch) || !node.getValues().containsKey(Character.toUpperCase(ch))){
                    return;
                }
                node =  node.getValues().containsKey(ch) ? (AddressNode) node.getValues().get(ch) : (AddressNode) node.getValues().get(Character.toUpperCase(ch));
            }
            node.setEnd(false);
            size--;
        }
        log.info("Successfully inserted {} into the tree.\nSize is now {}",prefixes,size);
    }

    @Override
    public Set<String> getMatches(String prefix) {
        AddressNode node = root;
        char[] prefixArr = prefix.toCharArray();
        log.info("Current char array: {}",Arrays.toString(prefixArr));
        for(Character character: prefixArr){
            if(Character.isWhitespace(character)) continue;
            if(!node.getValues().containsKey(character) && !node.getValues().containsKey(Character.toUpperCase(character))){
                log.warn("Could not find the last character in the current node: {} with current character: {}",node,character);
                if(node.finishedWords != null && node.finishedWords.size() > 0) break;
                else return null;
            };
            node =  node.getValues().containsKey(character) ? (AddressNode) node.getValues().get(character) : (AddressNode) node.getValues().get(Character.toUpperCase(character));
        }
        log.info("Last found Node's Finished Words: {}",node.getFinishedWords());
        return node.getFinishedWords();
    }

    @Override
    public double[] getCoords(String address) {
        AddressNode node = root;
        char[] prefixArr = address.toCharArray();
        for(Character character: prefixArr){
            if(Character.isWhitespace(character))continue;
            if(!node.getValues().containsKey(character) && !node.getValues().containsKey(Character.toUpperCase(character))){
                log.warn("Could not find the last character in the current node: {} with current character: {}",node.getValues(),character);
                if(node.finishedWords != null && node.finishedWords.size() > 0) break;
                else return null;
            };
            node =  node.getValues().containsKey(character) ? (AddressNode) node.getValues().get(character) : (AddressNode) node.getValues().get(Character.toUpperCase(character));
        }
        return new double[]{node.latitude,node.longitude};
    }

    @Override
    public AddressNode getNode(String address) {
        AddressNode node = root;
        char[] prefixArr = address.toCharArray();
        for(Character character: prefixArr){
            if(Character.isWhitespace(character))continue;
            log.info("Character Searching for: {}",character);
            if(!node.getValues().containsKey(character) && !node.getValues().containsKey(Character.toUpperCase(character))){
                log.warn("Could not find the last character in the current node: {} with current character: {}",node.getValues(),character);
                if(node.finishedWords != null && node.finishedWords.size() > 0) break;
                else return null;
            };
            node =  node.getValues().containsKey(character) ? (AddressNode) node.getValues().get(character) : (AddressNode) node.getValues().get(Character.toUpperCase(character));
        }
        return node;
    }

    @Override
    public void updateNode(String address, double lat, double lon, String locale) {
        log.info("Attempting tempting to update address '{}'",address);
        if(address == null) return;
        AddressNode node = root;
        for(Character ch:address.toCharArray()){
            if(Character.isWhitespace(ch))continue;
            if(!node.getValues().containsKey(ch) || !node.getValues().containsKey(Character.toUpperCase(ch))){
                return;
            }
            node =  node.getValues().containsKey(ch) ? (AddressNode) node.getValues().get(ch) : (AddressNode) node.getValues().get(Character.toUpperCase(ch));
        }
        node.setLatitude(lat);
        node.setLongitude(lon);
        node.setLocale(locale);
        log.info("Successfully updated node for address {} into the tree.\nUpdated Node: {}",address,node);
    }
}
