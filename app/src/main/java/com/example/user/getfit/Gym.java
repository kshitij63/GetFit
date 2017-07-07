package com.example.user.getfit;

/**
 * Created by user on 7/7/2017.
 */

public class Gym {
    private String phot_ref;
    private String rating;
    private String name;
    private String vicinity;
    private Boolean status;

    Gym(String phot_ref,String rating,String name,String vicinity,Boolean status){
        this.phot_ref=phot_ref;
        this.name=name;
        this.rating=rating;
        this.vicinity=vicinity;
        this.status=status;
    }

    public String getName() {
        return name;
    }

    public String getPhot_ref() {
        return phot_ref;
    }

    public String getRating() {
        return rating;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getVicinity() {
        return vicinity;
    }
}
