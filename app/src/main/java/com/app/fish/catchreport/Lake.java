package com.app.fish.catchreport;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * @author Trevor Sherwood
 * @version 1.0
 */
public class Lake implements Serializable {

    private int id;
    private double lat, lon;
    private String name;
    private String county;
    private String abbreviation;

    /**
     * Lake object set with attributes
     *
     * @param id id in FishAndLakes.db table Lakes
     * @param name Name of lake
     * @param county County of lake
     * @param abbrev Abbreviation of County
     */
    public Lake(int id, String name, String county, String abbrev, double lat, double lon)
    {
        this.id = id;
        this.name = name;
        this.county = county;
        this.abbreviation = abbrev;
        this.lat = lat;
        this.lon = lon;

    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getCounty()
    {
        return county;
    }

    public String getAbbreviation(){return abbreviation;}

    public double getLat()
    {
        return lat;
    }

    public double getLong()
    {
        return lon;
    }

}
