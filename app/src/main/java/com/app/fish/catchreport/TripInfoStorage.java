package com.app.fish.catchreport;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Trevor Sherwood
 * @version 1.0
 */
public class TripInfoStorage implements Serializable{
    private Date date;
    private Lake lake;
    private ArrayList<Fish> fish;

    /**
     * Creates blank TripInfoStorage
     */
    public TripInfoStorage()
    {
        date = null;
        lake = null;
        fish = new ArrayList<Fish>();
    }

    /**
     * Creates trip info storage with set values
     *
     * @param d Date of trip
     * @param s Lake
     */
    public TripInfoStorage(Date d, Lake s)
    {
        date = d;
        lake = s;
        fish = new ArrayList<Fish>();
    }


    public void setDate(Date d)
    {
        date = d;
    }

    public void setLake(Lake s)
    {
        lake = s;
    }

    public void setFish(ArrayList<Fish> f)
    {
        fish = f;
    }

    public Date getDate()
    {
        return date;
    }

    public Lake getLake()
    {
        return lake;
    }

    public Fish getFish(int i)
    {
        return fish.get(i);
    }

    public int numFish()
    {
        return fish.size();
    }

    public void removeFish(int i)
    {
        fish.remove(i);
    }

    public int indexOf(Fish f)
    {
        return fish.indexOf(f);
    }

    public void addFish(int i, Fish f)
    {
        fish.add(i, f);
    }

    public void addFish(Fish f)
    {
        fish.add(f);
    }

}