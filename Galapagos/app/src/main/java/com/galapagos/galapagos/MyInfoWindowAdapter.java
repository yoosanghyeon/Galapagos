package com.galapagos.galapagos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.galapagos.galapagos.common.GalaPoiCategory;
import com.galapagos.galapagos.poivalueobject.GalaPOI;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;


public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    HashMap<Marker, GalaPOI> poiResolver;
    View contentView;
    TextView contentInfo;
    FrameLayout frameCategory;

    public MyInfoWindowAdapter(Context context, HashMap<Marker, GalaPOI> poiResolver) {
        contentView = LayoutInflater.from(context).inflate(R.layout.view_info_window, null);
        contentInfo = (TextView) contentView.findViewById(R.id.gala_info_marker);
        frameCategory = (FrameLayout) contentView.findViewById(R.id.frame_category_poi);
        this.poiResolver = poiResolver;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        GalaPOI poi = poiResolver.get(marker);
        contentInfo.setText(poi.boardContent);

        switch (poi.boardCategory) {
            case GalaPoiCategory.GalaPoiCategoryOthers:
                frameCategory.setBackgroundResource(R.drawable.gala_poi_category_others);
                break;
            case GalaPoiCategory.GalaPoiCategoryFood:
                frameCategory.setBackgroundResource(R.drawable.gala_poi_category_food);
                break;
            case GalaPoiCategory.GalaPoiCategoryPlay:
                frameCategory.setBackgroundResource(R.drawable.gala_poi_category_play);
                break;
            case GalaPoiCategory.GalaPoiCategoryOutLocation:
                frameCategory.setBackgroundResource(R.drawable.gala_poi_category_ourlocation);
                break;
            case GalaPoiCategory.GalaPoiCategoryDeal:
                frameCategory.setBackgroundResource(R.drawable.gala_poi_category_deal);
                break;
            case GalaPoiCategory.GalaPoiCategoryMetting:
                frameCategory.setBackgroundResource(R.drawable.gala_poi_category_metting);
                break;
            case GalaPoiCategory.GalaPoiCategorySuggestion:
                frameCategory.setBackgroundResource(R.drawable.gala_poi_category_suggestion);
                break;
            case GalaPoiCategory.GalaPoiCategoryTmap:
                frameCategory.setBackgroundResource(R.drawable.gala_poi_category_tmap);
                break;
        }
        return contentView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        GalaPOI poi = poiResolver.get(marker);
        contentInfo.setText(poi.boardContent);
        return contentView;
    }
}
