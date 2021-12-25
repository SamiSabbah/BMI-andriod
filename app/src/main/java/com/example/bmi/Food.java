package com.example.bmi;

public class Food {
    private String foodName, foodId, foodCal, foodCat, userId, imageUri;
    private int categoryFoodId;

    public  Food() {};

    public Food(String foodName, String foodId, String foodCal, String foodCat, String userId, String imageUri, int categoryFoodId) {
        this.foodName = foodName;
        this.foodId = foodId;
        this.foodCal = foodCal;
        this.foodCat = foodCat;
        this.userId = userId;
        this.imageUri = imageUri;
        this.categoryFoodId = categoryFoodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodId() {
        return foodId;
    }

    public String getFoodCal() {
        return foodCal;
    }

    public void setFoodCal(String foodCal) {
        this.foodCal = foodCal;
    }

    public String getFoodCat() {
        return foodCat;
    }

    public void setFoodCat(String foodCat) {
        this.foodCat = foodCat;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getCategoryFoodId() {
        return categoryFoodId;
    }

    public void setCategoryFoodId(int categoryFoodId) {
        this.categoryFoodId = categoryFoodId;
    }
}




