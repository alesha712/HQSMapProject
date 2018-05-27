package com.hqs.alx.hqsmapproject;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

/**
 * Created by Alex on 04/01/2018.
 */

public class MyPlaces extends SugarRecord implements Parcelable {

    String name;
    String adress;
    String phoneNumber;
    String UniquePlaceId;
    double lat;
    double lon;
    String webSiteString;
    float rating;


    public MyPlaces(){
    }

    public MyPlaces(String name, String adress, String phoneNumber, String uniquePlaceId, double lat, double lon, float rating) {
        this.name = name;
        this.adress = adress;
        this.phoneNumber = phoneNumber;
        UniquePlaceId = uniquePlaceId;
        this.lat = lat;
        this.lon = lon;
        this.webSiteString = webSiteString;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUniquePlaceId() {
        return UniquePlaceId;
    }

    public void setUniquePlaceId(String uniquePlaceId) {
        UniquePlaceId = uniquePlaceId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getWebSiteString() {
        return webSiteString;
    }

    public void setWebSiteString(String webSiteString) {
        this.webSiteString = webSiteString;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Place name: " + getName() + "\n"
                + "Place address: " + getAdress() + "\n"
                + "Place Phone: " + getPhoneNumber() + "\n"
                + "Place UniqueID: " + getUniquePlaceId() + "\n"
                + "Place Lat: " + getLat() + " Lon: " + getLon() + "\n"
                + "Place rating: " + getRating() + "\n"
                + "Place webSite: " + getWebSiteString();
    }

    protected MyPlaces(Parcel in) {
        name= in.readString();
        adress = in.readString();
        phoneNumber = in.readString();
        UniquePlaceId = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        webSiteString = in.readString();
        rating = in.readFloat();

    }
    public static final Creator<MyPlaces> CREATOR = new Creator<MyPlaces>() {
        @Override
        public MyPlaces createFromParcel(Parcel in) {
            return new MyPlaces(in);
        }

        @Override
        public MyPlaces[] newArray(int size) {
            return new MyPlaces[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(adress);
        dest.writeString(phoneNumber);
        dest.writeString(UniquePlaceId);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(webSiteString);
        dest.writeFloat(rating);

    }
}
