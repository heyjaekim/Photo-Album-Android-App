package com.example.photolibraryapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;



/**
 * A class to represent an Album instance.
 * @author Lizbeth Cespedes
 * @author Jaeweon Kim
 *
 */
public class Album implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6023219195019466307L;

    /**
     * The {@link String} that represents the name of the album
     */
    private String albumName;

    /**
     * The {@link Set} that keeps track of all the photos in the album
     */
    private List<Photo> photos;

    /**
     * The {@link Calendar} that keeps track of the oldest date of a
     * the photos in the album
     */
    private Calendar oldestDate;

    /**
     * The {@link Calendar} that keeps track of the newest date of
     * a photo in the album
     */
    private Calendar newestDate;


    /**
     * Constructor of the album
     * @param albumName the name of the album
     */
    public Album(String albumName) {
        this.albumName = albumName;
        photos = new ArrayList<>();
        oldestDate = Calendar.getInstance();
        newestDate = Calendar.getInstance();
        calculateDateRange();
    }

    /**
     * Returns the name of the album
     * @return String The album name
     */
    public String getAlbum() {
        return this.albumName;
    }

    /**
     * add a new photo to the photos list
     * @param photo Photo we are trying to add
     */
    public void addPhoto(Photo photo) {

        if(this.photos.size() == 0) {
            this.newestDate = photo.getDateTime();
            this.oldestDate = photo.getDateTime();
        }

        this.photos.add(photo);
        calculateDateRange();
    }

    /**
     * remove a photo from the photos list
     * @param photo Photo we are trying to delete
     */
    public void deletePhoto(Photo photo) {
        this.photos.remove(photo);
        calculateDateRange();
    }

    /**
     * calculate the date time range of the photo lists
     */
    public void calculateDateRange() {

        for(int i = 1; i<this.photos.size(); i++) {

            if(this.photos.get(i).getDateTime().before(oldestDate)) {
                oldestDate = this.photos.get(i).getDateTime();
                continue;
            }

            if(this.photos.get(i).getDateTime().after(newestDate)) {
                newestDate = this.photos.get(i).getDateTime();
                continue;
            }
        }
    }

    /**
     * update the new photos list to the current photos list
     * @param photos Set of photos that we are trying to add
     */
    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    /**
     *
     * @param index - The index of the photo we are trying to edit
     * @param photo - The edited photo
     */
    public void editPhoto(int index, Photo photo) {
        this.photos.set(index, photo);
    }
    /**
     * Returns the set of photos in the album
     * @return Set the set of photos in the album
     */
    public List<Photo> getPhotos() {
        return this.photos;
    }

    /**
     * Returns the oldest date in the album
     * @return Calendar
     */
    public Calendar getOldestDate() {
        return this.oldestDate;
    }

    /**
     * Returns the newest date in the album
     * @return Calendar The newest date
     */
    public Calendar getNewestDate() {
        return this.newestDate;
    }

    /**
     * Returns a string representation of the date range
     * of the photos that are currently in the album
     * @return String - The date range of all the photos
     * in the album
     */
    public String getDateRange() {
        return this.oldestDate.getTime().toString() + "-\n" + this.newestDate.getTime().toString();
    }
    /**
     * get the numbers of the photos list
     * @return the number of photos in the list
     */
    public int getNumPhotos() {
        return photos.size();
    }

    /**
     * Returns the string representation of the object
     * @return String The string version of the object
     */
    public String toString() {
        return this.albumName;
    }

    /**
     * set a new name of the album
     * @param name The new name of the album
     */
    public void setName(String name) {
        this.albumName = name;
    }

    /**
     * Equality check for albums
     * @param o Object we are trying to compare to
     * @return boolean - Represents if the objects are the same of not
     */
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Album)) return false;

        Album album = (Album) o;

        if(album.getAlbum().equalsIgnoreCase(this.albumName) &&
                album.getOldestDate().equals(this.oldestDate) &&
                album.getNewestDate().equals(this.newestDate) &&
                album.getPhotos().equals(this.photos)) return true;

        return false;
    }
}
