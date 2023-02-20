package com.github.tezvn.aradite.api.agent;

public enum Agents {

    WINNIN("winnin"),

    MOROE("moroe"),

    INNOVA("innova");

    private String id;

    private Agents(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
