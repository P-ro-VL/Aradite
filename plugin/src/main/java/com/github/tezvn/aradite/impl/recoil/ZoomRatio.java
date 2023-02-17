package com.github.tezvn.aradite.impl.recoil;

public enum ZoomRatio {

    SMALLEST(0),

    SMALL(1),

    NORMAL(3),

    LARGE(4),

    HUGE(6);

    private final int zoomRatio;

    private ZoomRatio(int ratio) {
        this.zoomRatio = ratio;
    }

    /**
     * Return the room ratio.
     */
    public int getZoomRatio() {
        return zoomRatio;
    }
}
