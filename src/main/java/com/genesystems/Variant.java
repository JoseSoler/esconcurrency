package com.genesystems;


import java.util.ArrayList;
import java.util.List;

public class Variant {
    private String id;
    private List<Sample> samples = new ArrayList<Sample>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    public void addSample(Sample sample) {
        samples.add(sample);
    }
}
