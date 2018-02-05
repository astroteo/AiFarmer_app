package com.example.awaisahmed.ai_farmer.classi;

/**
 * Created by Awais Ahmed on 25/01/2018.
 */
public class HumidityReading implements Reading {
    private String dateHumidity;
    private int id;
    private int sensorHumidity;
    private float humidityRead;

    @Override
    public String getDate() {
        return dateHumidity;
    }

    public void setDateHumidity(String dateHumidity) {
        this.dateHumidity = dateHumidity;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getSensor() {
        return sensorHumidity;
    }

    public void setSensorHumidity(int sensorHumidity) {
        this.sensorHumidity = sensorHumidity;
    }

    @Override
    public float getReading() {
        return humidityRead;
    }

    public void setHumidityRead(float humidityRead) {
        this.humidityRead = humidityRead;
    }
}