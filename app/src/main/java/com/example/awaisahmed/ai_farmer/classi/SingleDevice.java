package com.example.awaisahmed.ai_farmer.classi;

import android.os.Parcel;
import android.os.Parcelable;

public class SingleDevice implements Parcelable{

    private int id;
    private String nameDevice;
    private String dateAdded;
    private String pinDevice;
    private String tokenDevice;
    private double latitudeDevice;
    private double longitudeDevice;
    private boolean activationDevice;
    private String owners;

    public SingleDevice() {

    }

    public SingleDevice(Parcel parcel) {
        this.id = parcel.readInt();
        this.nameDevice = parcel.readString();
        this.dateAdded = parcel.readString();
        this.pinDevice = parcel.readString();
        this.tokenDevice = parcel.readString();
        this.latitudeDevice = parcel.readDouble();
        this.longitudeDevice = parcel.readDouble();
        this.activationDevice = parcel.readByte() != 0;
        this.owners = parcel.readString();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameDevice() {
        return nameDevice;
    }

    public void setNameDevice(String nameDevice) {
        this.nameDevice = nameDevice;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getPinDevice() {
        return pinDevice;
    }

    public void setPinDevice(String pinDevice) {
        this.pinDevice = pinDevice;
    }

    public String getTokenDevice() {
        return tokenDevice;
    }

    public void setTokenDevice(String tokenDevice) {
        this.tokenDevice = tokenDevice;
    }

    public double getLatitudeDevice() {
        return latitudeDevice;
    }

    public void setLatitudeDevice(double latitudeDevice) {
        this.latitudeDevice = latitudeDevice;
    }

    public double getLongitudeDevice() {
        return longitudeDevice;
    }

    public void setLongitudeDevice(double longitudeDevice) {
        this.longitudeDevice = longitudeDevice;
    }

    public boolean isActivationDevice() {
        return activationDevice;
    }

    public void setActivationDevice(boolean activationDevice) {
        this.activationDevice = activationDevice;
    }

    public String getOwners() {
        return owners;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nameDevice);
        dest.writeString(dateAdded);
        dest.writeString(pinDevice);
        dest.writeString(tokenDevice);
        dest.writeDouble(latitudeDevice);
        dest.writeDouble(longitudeDevice);
        dest.writeByte((byte)(activationDevice ? 1 : 0));
        dest.writeString(owners);
    }

    public final static Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public SingleDevice createFromParcel(Parcel source) {
            return new SingleDevice(source);
        }

        @Override
        public SingleDevice[] newArray(int size) {
            return new SingleDevice[size];
        }
    };

    public String toString() {
        return nameDevice;
    }
}
