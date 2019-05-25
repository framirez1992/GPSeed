package com.far.gpseed.Models;

import java.io.Serializable;

/**
 * Created by mdsoft on 12/27/2017.
 */

public class SeedLocation implements Serializable {
    public String Id;
    public String Description;
    public double Latitude;
    public double Longitude;
    public String imageUrl;

    @Override
    public String toString() {
       return  Description;
    }
}
