package com.example.user.getfit;

/**
 * Created by user on 7/7/2017.
 */

public class BodyPart {
    String body_part_name;
    Integer image;

    BodyPart(String body_part_name,Integer image){
        this.body_part_name=body_part_name;
        this.image=image;

    }

    public Integer getImage() {
        return image;
    }

    public String getBody_part_name() {
        return body_part_name;
    }
}
