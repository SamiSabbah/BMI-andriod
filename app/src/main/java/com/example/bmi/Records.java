package com.example.bmi;

public class Records implements Comparable<Records> {
    private String date, time, userId, BMI_Categories;
    private Double BMI, weight, length;

    public Records() {
    }

    public Records(String BMI_Categories, String date, String time, String userId, Double BMI, Double weight, Double length) {
        this.BMI_Categories = BMI_Categories;
        this.date = date;
        this.time = time;
        this.userId = userId;
        this.BMI = BMI;
        this.weight = weight;
        this.length = length;
    }

    public String getBMI_Categories() {
        return BMI_Categories;
    }

    public void setBMI_Categories(String BMI_Categories) {
        this.BMI_Categories = BMI_Categories;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getBMI() {
        return BMI;
    }

    public void setBMI(Double BMI) {
        this.BMI = BMI;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    @Override
    public int compareTo(Records o) {
        if (getDate() == null || o.getDate() == null) {
            return 0;
        }
        return getDate().compareTo(o.getDate());
    }
}
