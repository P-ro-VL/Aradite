package aradite.weapon.gun;

import aradite.weapon.gun.animation.RifleAndSMGAnimation;
import aradite.weapon.gun.animation.ShootAnimation;
import aradite.weapon.gun.animation.ShortgunAnimation;
import aradite.weapon.gun.animation.SniperAnimation;

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
