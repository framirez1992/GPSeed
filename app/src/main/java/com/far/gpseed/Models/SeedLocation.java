package com.far.gpseed.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by framirez on 12/27/2017.
 */

public class SeedLocation implements Serializable {
    public String Id;
    public String Description, Description2 ;
    public double Latitude;
    public double Longitude;
    public ArrayList<String> imageUrls;

    @Override
    public String toString() {
       return  Description;
    }
}
