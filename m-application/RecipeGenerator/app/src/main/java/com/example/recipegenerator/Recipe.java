package com.example.recipegenerator;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

public class Recipe implements Parcelable {

    private String title;
    private JSONArray ingredients;
    private JSONArray instructions;
    private int recipeNo;

    public Recipe(String title, JSONArray ingredients, JSONArray instructions, int recipeNo) {
        this.title = title;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.recipeNo = recipeNo;
    }

    public Recipe() {
    }


    protected Recipe(Parcel in) {
        title = in.readString();
        recipeNo = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(recipeNo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public JSONArray getIngredients() {
        return ingredients;
    }

    public JSONArray getInstructions() {
        return instructions;
    }

    public int getRecipeNo() {
        return recipeNo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIngredients(JSONArray ingredients) {
        this.ingredients = ingredients;
    }

    public void setInstructions(JSONArray instructions) {
        this.instructions = instructions;
    }

    public void setRecipeNo(int recipeNo) {
        this.recipeNo = recipeNo;
    }

}
