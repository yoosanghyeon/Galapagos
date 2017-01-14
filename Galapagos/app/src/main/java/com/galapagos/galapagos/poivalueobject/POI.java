package com.galapagos.galapagos.poivalueobject;


public class POI {
    public String id;
    public String name;
    public String telNo;
    public String frontLat;
    public String frontLon;
    public String noorLat;
    public String noorLon;

    public String upperAddrName;
    public String middleAddrName;
    public String lowerAddrName;
    public String firstNo;
    public String secondNo;




    @Override
    public String toString() {
        return name;
    }

    public String getAddress() {
        return upperAddrName + " " + middleAddrName + " " + lowerAddrName + " " + firstNo + "-" + secondNo;
    }

    double lat = -1;
    double lng = -1;

    public double getLatitude() {
        if (lat == -1) {
            lat = (Double.parseDouble(frontLat) + Double.parseDouble(noorLat)) / 2;
        }
        return lat;
    }

    public double getLongitude() {
        if (lng == -1) {
            lng = (Double.parseDouble(frontLon) + Double.parseDouble(noorLon)) / 2;
        }
        return lng;
    }


}
