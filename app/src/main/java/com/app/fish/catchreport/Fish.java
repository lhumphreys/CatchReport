package com.app.fish.catchreport;

import java.io.Serializable;

/**
 * Created by Laura on 12/2/2015.
 */
public class Fish implements Serializable{
    private String species;
    private double weight;
    private double length;
    private boolean released;
    private boolean tagged;
    private int quantity;

    public Fish(String species, double weight, double length, boolean released, boolean tagged,int quantity)
    {
        this.species = species;
        this.weight = weight;
        this.length = length;
        this.released = released;
        this.tagged = tagged;
        this.quantity = quantity;
    }

    public Fish()
    {
        this("Lake Trout", 0, 0, false, false,1);
    }

    public int getQuantity(){return this.quantity;}

    public void setQuantity(int quantity){this.quantity = quantity;}

    public void setSpecies(String species){
        this.species = species;
    }

    public String getSpecies(){
        return this.species;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    public double getWeight(){
        return this.weight;
    }

    public void setLength(double length){
        this.length = length;
    }

    public double getLength(){
        return this.length;
    }

    public void setReleased(boolean released){
        this.released = released;
    }

    public boolean isReleased(){
        return this.released;
    }

    public void setTagged(boolean tagged){
        this.tagged = tagged;
    }

    public boolean isTagged(){
        return this.tagged;
    }

    public String displayWeight()
    {
        return weight + " lbs";
    }

    public String displayLength()
    {
        return length + " in";
    }

    public void clone(Fish fish)
    {
        this.species = fish.species;
        this.weight = fish.weight;
        this.length = fish.length;
        this.released = fish.released;
        this.tagged = fish.tagged;
        this.quantity = fish.quantity;
    }

}
