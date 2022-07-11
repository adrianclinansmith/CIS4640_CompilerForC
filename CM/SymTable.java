/* 
    A Symbol table for storing variable and function declarations along with
    their respective scope levels.
    By: Adrian Clinansmith
    March 2022
*/

import java.util.ArrayList;
import java.util.HashMap;
import absyn.*;

public class SymTable {

    // ************************************************************************
    // Inner Node Class
    // ************************************************************************

    /* 
        SymNode: Each entry in the hash table stores a SymNode (or null), and
        each SymNode is linked to the next SymNode until null is reached. 
    */
    private class SymNode {

        private Dec dec;
        private int level;
        private SymNode next;

        SymNode(Dec dec, int level) {
            this.dec = dec;
            this.level = level;
            this.next = null;
        }

        public boolean at(int level) {
            return this.level == level;
        }

        public boolean sameName(Dec dec) {
            return this.dec.name.equals(dec.name);
        }

        @Override
        public String toString() {
            return this.dec + " <" + this.level + ">";
        }
    }

    // ************************************************************************
    // Class
    // ************************************************************************

    // Private Properties

    private HashMap<String, SymNode> hashMap;

    // Constructor 

    SymTable() {
        this.hashMap = new HashMap<>();
    } 
    
    // Public Methods

    // ************************************************************************
    // Semantic Analyzer methods
    // ************************************************************************

    /*
        Delete each node at or greater than the given level and return a list
        of the removed declarations. 
    */
    public ArrayList<Dec> delete(int level) {
        ArrayList<Dec> removedDecs = new ArrayList<>();
        this.hashMap.forEach((key, node) -> {
            while (node != null) {
                if (node.level < level) {
                    break;
                }
                removedDecs.add(node.dec);
                node = node.next;
                this.hashMap.put(key, node);
            }
        });
        return removedDecs;
    }

    /* 
        Insert a new entry into the front of the table. 
        If an entry with the same name and level is already in the table,
        then the new entry is not added and false is returned. Otherwise the 
        entry is added and true is returned. 
    */
    public boolean insert(Dec dec, int level) {
        SymNode node = this.hashMap.get(dec.name);
        if (node != null && node.sameName(dec) && node.at(level)) {
            return false;
        }
        SymNode newNode = new SymNode(dec, level);
        newNode.next = node;
        this.hashMap.put(dec.name, newNode);
        return true;
    }

    /*
        Return the declaration for the given name. If the name is not found
        then null is returned.
    */
    public Dec lookup(String name) {
        SymNode node = this.hashMap.get(name);
        while (node != null) {
            if (name.equals(node.dec.name)) {
                break;
            }
            node = node.next;
        }
        return node != null ? node.dec : null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.hashMap.forEach((key, node) -> {
            boolean onFirst = true;
            while (node != null) {
                if (!onFirst) {
                    sb.append(" -> ");
                }
                onFirst = false;
                sb.append(node);
                node = node.next;
            }
            sb.append("\n");
        });
        return sb.toString();
    }
}
