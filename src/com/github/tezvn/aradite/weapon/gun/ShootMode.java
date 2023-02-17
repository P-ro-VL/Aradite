package com.github.tezvn.aradite.weapon.gun;

import com.github.tezvn.aradite.weapon.gun.animation.RifleAndSMGAnimation;
import com.github.tezvn.aradite.weapon.gun.animation.ShootAnimation;
import com.github.tezvn.aradite.weapon.gun.animation.ShortgunAnimation;
import com.github.tezvn.aradite.weapon.gun.animation.SniperAnimation;

public enum ShootMode {

    SNIPER(new SniperAnimation()),

    RIFLE_AND_SMG(new RifleAndSMGAnimation()),

    RIFLE_SNIPER(new SniperAnimation()),

    SHORTGUN(new ShortgunAnimation());

    private final ShootAnimation animation;

    private ShootMode(ShootAnimation animation){
        this.animation = animation;
    }

    public ShootAnimation getAnimation() {
        return animation;
    }
}
