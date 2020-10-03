package com.path.variable.commons.slack;

public enum BlockType {

    DIVIDER,
    TEXT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
