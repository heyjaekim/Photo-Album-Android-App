package com.example.photolibraryapp;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A class representation for Tag
 * @author Lizbeth Cespedes
 * @author Jaeweon Kim
 *
 */
public class Tag implements Serializable {
    /**
     * Serial ID for serialization purposes
     */
    private static final long serialVersionUID = 5678650670873947634L;

    /**
     * The {@link Set} that stores the tag name
     */
    private String tagName;

    /**
     * The {@link Set} have all the tag values of the tag
     */
    private Set<String> tagValue;

    /**
     * Constructor for a tag
     * @param tagName the name of the tag
     * @param tagValue The set of values for the given tag
     */
    public Tag(String tagName, Set<String> tagValue) {
        this.tagName = tagName;
        this.tagValue = new HashSet<String>(tagValue);
    }

    /**
     * Contructor for the tag
     * @param tagName the name of the tag
     * @param tagValue A single value for the tag
     */
    public Tag(String tagName, String tagValue) {
        this.tagName = tagName;
        this.tagValue = new HashSet<String>();
        this.tagValue.add(tagValue);
    }

    /**
     * Contructor for a tag
     * @param tagName The name of the tag
     */
    public Tag(String tagName) {
        this.tagName = tagName;
        this.tagValue = new HashSet<String>();
    }

    /**
     * Returns the tag name
     * @return String - The name of the tag
     */
    public String getTagName() {
        return this.tagName;
    }

    /**
     * Returns all values associated with the tag name
     * @return Set with all the values associated with the tag
     */
    public Set<String> getTagValue(){
        return this.tagValue;
    }


    /**
     * Changes/sets the name of the tag
     * @param tagName The new name of the tag
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Sets the tagValue for a name
     * @param tagValue the new value of the tag
     */
    public void setTagValue(String tagValue) {
        this.tagValue.clear();
        addTagValue(tagValue);
    }

    /**
     * Adds a new tag value for a given tagName
     * @param tagValue the new value of tag
     */
    public void addTagValue(String tagValue) {
        this.tagValue.add(tagValue);
    }

    /**
     * Checks for inequality
     * @param o the object to be compared
     * @return boolean represents if the objects are the same
     */
    @Override
    public boolean equals(Object o) {

        if(!(o instanceof Tag)) return false;

        Tag tag = (Tag) o;
        if(!tag.getTagName().equals(this.tagName)) return false;

        for(String s : tag.getTagValue()) {
            if(this.tagValue.contains(s)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a string representation of the tag
     * @return String - The string representation of the tag
     */
    public String toString() {
        String res = "";

        for(String s: tagValue) {
            res += this.tagName + "=" + s + "\n";
        }
        return res;
    }
}
