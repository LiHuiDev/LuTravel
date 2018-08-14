package com.example.lihui.lutravel.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by lihui on 2018/3/4.
 */

public class Marker extends DataSupport {

    private Double latitude;//纬度
    private Double longitude;//精度
    private String city;//城市
    private String title;//标题
    private String note;//笔记
    private String date;//日期

    public Marker(Double latitude, Double longitude, String city, String title, String note, String date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.title = title;
        this.note = note;
        this.date = date;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Marker{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", city='" + city + '\'' +
                ", title='" + title + '\'' +
                ", note='" + note + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
