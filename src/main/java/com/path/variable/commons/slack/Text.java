package com.path.variable.commons.slack;

public class Text {

    private final String type = "mrkdwn";

    private String text;

    private Accessory accessory;

    Text() {}

    public Accessory setImage(String imageUrl, String altText) {
        accessory = new Accessory(imageUrl, altText);
        return accessory;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Accessory getAccessory() {
        return accessory;
    }
}
