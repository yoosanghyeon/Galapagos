package com.galapagos.galapagos.poivalueobject;

/**
 * Created by Arous on 2016-12-01.
 */

public class GalaGpsValue {
    private Double latitudeValue;
    private Double longtitudeValue;

    public GalaGpsValue(Double latitudeValue, Double longtitudeValue) {
        this.latitudeValue = latitudeValue;
        this.longtitudeValue = longtitudeValue;
    }

    public Double getLatitudeValue() {
        return latitudeValue;
    }

    public void setLatitudeValue(Double latitudeValue) {
        this.latitudeValue = latitudeValue;
    }

    public Double getLongtitudeValue() {
        return longtitudeValue;
    }

    public void setLongtitudeValue(Double longtitudeValue) {
        this.longtitudeValue = longtitudeValue;
    }
}
