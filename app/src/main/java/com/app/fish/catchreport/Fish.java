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

    public Fish(String species, double weight, double length, boolean released, boolean tagged)
    {
        this.species = species;
        this.weight = weight;
        this.length = length;
        this.released = released;
        this.tagged = tagged;
    }

    public Fish()
    {
        this("Lake Trout", 0, 0, false, false);
    }

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

}
