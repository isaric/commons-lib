package com.path.variable.commons.slack;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Accessory {

    @JsonProperty("image_url")
    private final String imageUrl;

    @JsonProperty("alt_text")
    private final String altText;

    Accessory(String imageUrl, String altText) {
        this.imageUrl = imageUrl;
        this.altText = altText;
    }

    public String getType() {
        return "image";
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAltText() {
        return altText;
    }
}
