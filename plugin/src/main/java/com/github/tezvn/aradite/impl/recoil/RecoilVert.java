package com.github.tezvn.aradite.impl.recoil;

public enum RecoilVert {
    UPWARD(-1.0f),
    DOWNWARD(1.0f);

    private final float recoilVertValue;

    private RecoilVert(float recoilVertValue){
        this.recoilVertValue = recoilVertValue;
    }

    /**
     * Return the recoil vert value.
     */
    public float getRecoilVertValue() {
        return recoilVertValue;
    }
}
