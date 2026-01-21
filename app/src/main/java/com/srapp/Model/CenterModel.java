package com.srapp.Model;

import java.util.List;

public class CenterModel {
    public String id;
    public String name;
    public String mapType;
    public String radiusMeter;
    public List<CoordinateModel> coordinates;

    public CenterModel(String id, String name, String mapType, String radiusMeter, List<CoordinateModel> coordinates) {
        this.id = id;
        this.name = name;
        this.mapType = mapType;
        this.radiusMeter = radiusMeter;
        this.coordinates = coordinates;
    }
}
