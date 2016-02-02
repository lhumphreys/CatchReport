package com.app.fish.catchreport;

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
     * @param i id in FishAndLakes.db table Lakes
     * @param n Name of lake
     * @param c County of lake
     * @param a Abbreviation of County
     */
    public Lake(int i, String n, String c, String a, double la, double lo)
    {
        id = i;
        name = n;
        county = c;
        abbreviation = a;
        lat = la;
        lon = lo;
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
