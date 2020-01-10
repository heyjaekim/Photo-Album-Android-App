package com.example.photolibraryapp;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;



/**
 * A class to represent a Photo instance
 * @author Lizbeth Cespedes
 * @author Jaeweon Kim
 *
 */
public class Photo implements Serializable {

    /**
     * Serial ID for serializing purposes
     */
    private static final long serialVersionUID = -112727204185193728L;

    /**
     * A {@link String} that represents the location on the user's
     * machine that a photo is located
     */
    private String location;

    /**
     * A {@link Calendar} that represents the date and time that
     * a photo was taken
     */
    private Calendar dateTime;

    /**
     * A {@link Set} that represents the tags associated with the photo
     */
    private List<Tag> tags;

    /**
     * The {@link String} to store the name of the photo
     */
    private String name;

    /**
     * The {@link String} to store the caption of the photo
     */
    private String caption;

    private String fileName;

    /**
     * A constructor of Photo with only three parameters
     * @param location The location of the photo on the user's computer
     * @param dateTime The time the photo was last modified
     * @param caption The caption of the photo
     */
    public Photo(String location, Calendar dateTime, String caption) {
        this.location = location;
        this.fileName = new File(location).getName();
        this.dateTime = dateTime;
        this.dateTime.set(Calendar.MILLISECOND, 0);
        this.tags = new ArrayList<Tag>();
        this.name = location;
        this.caption = caption;
    }

    public Photo(String location, Calendar dateTime, String caption, String fileName) {
        this.location = location;
        this.fileName = fileName;
        this.dateTime = dateTime;
        this.dateTime.set(Calendar.MILLISECOND, 0);
        this.tags = new ArrayList<Tag>();
        this.name = location;
        this.caption = caption;
    }

    /**
     * A constructor of Photo with two parameters
     * @param location Location of the photo on the user's computer
     * @param dateTime The date the photo was last modified
     */
    public Photo(String location, Calendar dateTime) {
        this.location = location;
        this.dateTime = dateTime;
        this.fileName = new File(location).getName();
        this.dateTime.set(Calendar.MILLISECOND, 0);
        this.tags = new ArrayList<Tag>();
        this.name = location;
        this.caption = "";

    }

    public Photo(String location, String fileName, Calendar dateTime) {
        this.location = location;
        this.fileName = fileName;
        this.dateTime = dateTime;
        this.dateTime.set(Calendar.MILLISECOND, 0);
        this.tags = new ArrayList<Tag>();
        this.name = location;
        this.caption = "";
    }

    /**
     * A constructor of Photo with one parameter
     * @param photo sets the photo's current location, caption, dateTime, and tags
     */
    public Photo(Photo photo) {
        this.location = new String(photo.getLocation());
        this.caption = new String(photo.getCaption());
        this.dateTime = photo.getDateTime();
        this.tags = new ArrayList<Tag>();

        for(Tag tag: photo.getTags()) {
            tags.add(new Tag(new String(tag.getTagName()), tag.getTagValue()));
        }
    }
    /**
     * Returns the location of the photo in the user's machine
     * @return String represents the location of the photo
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Returns the date that the picture was taken
     * @return Calendar the date and time of a photo
     */
    public Calendar getDateTime() {
        return this.dateTime;
    }

    /**
     * Returns all of the tags of a photo
     * @return Set representing all the tags of a photo
     */
    public List<Tag> getTags(){
        return this.tags;
    }

    /**
     * replace the tag name value according to the index number from the tags list
     * @param index choose the photo's tag from the list
     * @param s is the editing Tag that user wants to change
     */
    public void setTag(int index, String s) {
        String tagName = s.substring(0, s.indexOf("="));
        String tagValue = s.substring(s.indexOf('=')+1);
        this.tags.get(index).setTagName(tagName);
        this.tags.get(index).setTagValue(tagValue);
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public String getFileName(){
        return this.fileName;
    }

    /**
     * Sets the value of name
     * @param name The name we are trying to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the photo
     * @return String - The string representation of the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Recaption of a photo
     * @param caption the caption of the photo
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

//    /**
//     * Gets the image that is located at the location on the user's computer
//     * @return Image - The image located at the location
//     * @throws FileNotFoundException In case file is not found
//     */
//    public Image getImage() throws FileNotFoundException {
//
//        FileInputStream inputstream = new FileInputStream(this.location);
//        Image image = new Image(inputstream);
//        return image;
//    }

    /**
     * Sets the tags of the photo
     * @param tags The list of tags to change
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Adds a tag to the photo
     * @param tag The new tag to add
     */
    public void addTag(Tag tag) {
        this.tags.add(tag);
    }


    /**
     * Deletes a tag at the index
     * @param index The index of the tag
     */
    public void delTag(int index) {
        List<Tag> temp = new ArrayList<Tag>();
        for(int i=0; i!= this.tags.size(); i++) {

            if(i != index) {
                temp.add(this.tags.get(i));
            }
        }
        this.tags.clear();
        this.tags.addAll(temp);

    }
//    /**
//     * Equality check for Photo
//     * @param o - The object to compare
//     * @return boolean - Represents if the objects are equals
//     */
//    public boolean equals(Object o) {
//
//        if(!(o instanceof Photo)) return false;
//
//        Photo photo = (Photo) o;
//
//        if(photo.getDateTime().equals(this.getDateTime()) &&
//                photo.getTags().equals(this.tags) &&
//                photo.getLocation().equals(this.location)) return true;
//
//        return false;
//    }

    /**
     * Gets a non paragraph version of the string
     * @return String - the caption of the photo
     */
    public String getCaptionNoParagraph() {
        return this.caption;
    }
    /**
     * Returns a paragraph version of the photo
     * @return String The caption of the photo
     */
    public String getCaption() {
        //TODO replace s with this.caption

        if(this.caption == null || this.caption.isEmpty()) {
            return "[No caption available]";
        }

        StringBuilder sb = new StringBuilder();

        int j = 0;
        for(int i = 0; i<this.caption.length(); i++) {
            if(j == 40) {
                if(this.caption.charAt(i) != ' ' &&
                        this.caption.charAt(i-1) != ' ') {
                    sb.append("-");
                    sb.append("\n");
                    sb.append(this.caption.charAt(i));
                }else {
                    sb.append("\n");
                    sb.append(this.caption.charAt(i));
                }
                j = 0;
            }else {
                sb.append(this.caption.charAt(i));
                j++;
            }
        }

        return sb.toString();
    }

    public List<String> getTagNames(){

        List<String> res = new ArrayList<>();
        for(Tag t: this.tags){
            res.add(t.getTagName());
        }

        return res;
    }

    public List<String> getTagValues(){

        List<String> res = new ArrayList<>();
        for(Tag t: this.tags){
            res.addAll(t.getTagValue());
        }

        return res;
    }

    /**
     * Returns the string representation of the photo
     * @return String - The string representation of the picture
     */
    public String toString() {
        int month = this.dateTime.get(Calendar.MONTH) + 1;
        int day = this.dateTime.get(Calendar.DAY_OF_MONTH);
        int year = this.dateTime.get(Calendar.YEAR);
        String tempFileName = (this.fileName.length() > 20) ? this.fileName.substring(0, 10) + "..." : this.fileName;
        String tempCaption = (this.caption.length() >20) ? this.caption.substring(0,15) + "..." : this.caption;
        return "Filename: " + tempFileName + "\n" + tempCaption + "\nUploaded on: " + month + "/" + day + "/" + year;
    }

    /**
     * Compares tags
     * @param name the name of the tag
     * @return boolean - in case names are the same
     */
    public boolean compareTags(String name) {
        for(Tag t: this.tags) {
            if(t.getTagName().equals(name)) {
                return false;
            }
        }
        return true;
    }
}