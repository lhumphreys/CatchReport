package com.app.fish.catchreport;

import java.io.Serializable;

/**
 * @author Trevor Sherwood
 * @version 1.0
 */
public class Lake implements Serializable {

    private int id;
    private String name;
    private String county;
    private String abbreviation;
    private String restrictions;

    /**
     * Lake object set with attributes
     *
     * @param i id in FishAndLakes.db table Lakes
     * @param n Name of lake
     * @param c County of lake
     * @param a Abbreviation of County
     * @param r Restrictions
     */
    public Lake(int i, String n, String c, String a, String r)
    {
        id = i;
        name = n;
        county = c;
        abbreviation = a;
        restrictions = r;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
}
