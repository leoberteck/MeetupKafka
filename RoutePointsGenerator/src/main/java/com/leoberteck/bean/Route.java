package com.leoberteck.bean;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private String type;
    private Metadata metadata;
    private List<Feature> features;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public static class Metadata {
        private String name;
        private Query query;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Query getQuery() {
            return query;
        }

        public void setQuery(Query query) {
            this.query = query;
        }
    }

    public static class Query {
        private List<Coordinate> coordinates;

        public List<Coordinate> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Coordinate> coordinates) {
            this.coordinates = coordinates;
        }
    }

    public static class Feature {
        private String type;
        private List<Double> bbox;
        private Geometry geometry;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Double> getBbox() {
            return bbox;
        }

        public void setBbox(List<Double> bbox) {
            this.bbox = bbox;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
    }

    public static class Geometry {
        private String type;
        private List<Coordinate> coordinates;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Coordinate> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Coordinate> coordinates) {
            this.coordinates = coordinates;
        }
    }

    public static class Coordinate extends ArrayList<Double> {
        public Double getX(){
            return this.get(0);
        }

        public Double getY(){
            return this.get(1);
        }

        public Double getZ(){
            return this.get(2);
        }
    }
}
