package com.example.awaisahmed.ai_farmer.classi;

/**
 * Created by Awais Ahmed on 25/01/2018.
 */
/**
 * Created by Awais Ahmed on 25/01/2018.
 */
public class TemperatureReading implements Reading{
    private String dateTemperature;
    private int id;
    private int sensorTemperature;
    private float temperatureRead;


    public String getDate() {
        return dateTemperature;
    }

    public void setDateTemperature(String dateTemperature) {
        this.dateTemperature = dateTemperature;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSensor() {
        return sensorTemperature;
    }

    public void setSensorTemperature(int sensorTemperature) {
        this.sensorTemperature = sensorTemperature;
    }

    public float getReading() {
        return temperatureRead;
    }

    public void setTemperatureRead(float temperatureRead) {
        this.temperatureRead = temperatureRead;
    }
}