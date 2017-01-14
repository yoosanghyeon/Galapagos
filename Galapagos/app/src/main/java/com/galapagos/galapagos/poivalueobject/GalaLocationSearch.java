package com.galapagos.galapagos.poivalueobject;

/**
 * Created by Arous on 2016-11-24.
 */

public class GalaLocationSearch {

    public GalaLocationSearch() {
    }

    public GalaLocationSearch(String name, Double latitude, Double longtitude) {
        this.name = name;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    // 갈라파고스 지명과 위도
    public String name;
    public Double latitude;
    public Double longtitude;
}
