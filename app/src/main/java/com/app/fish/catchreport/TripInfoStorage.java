package com.app.fish.catchreport;

import java.util.Date;

/**
 * @author Trevor Sherwood
 * @version 1.0
 */
public class TripInfoStorage {
    private Date date;
    private Lake lake;

    /**
     * Creates blank TripInfoStorage
     */
    public TripInfoStorage()
    {
        date = null;
        lake = null;
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
    }

    public void setDate(Date d)
    {
        date = d;
    }

    public void setLake(Lake s)
    {
        lake = s;
    }

    public Date getDate()
    {
        return date;
    }

    public Lake getLake()
    {
        return lake;
    }
}