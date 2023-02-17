package com.github.tezvn.aradite.agent.type.moroe;

import com.github.tezvn.aradite.agent.AbstractAgent;
import com.github.tezvn.aradite.agent.skill.SkillType;
import com.github.tezvn.aradite.agent.texture.Texture;
import com.github.tezvn.aradite.agent.texture.TextureType;
import com.github.tezvn.aradite.agent.type.moroe.skill.MoroeC;
import com.github.tezvn.aradite.agent.type.moroe.skill.MoroeUltimate;
import com.github.tezvn.aradite.agent.type.moroe.skill.MoroeX;
import org.bukkit.Color;

public class Moroe extends AbstractAgent  {

    public Moroe() {
        super("moroe", "Moroe");

        setSkill(SkillType.ACTIVE_X, new MoroeX());
        setSkill(SkillType.ACTIVE_C, new MoroeC());
        setSkill(SkillType.ULTIMATE, new MoroeUltimate());

        setThemeColor(Color.LIME);

        setTexture(TextureType.SKULL, new Texture("f2f320528fa9ce760d4f5ee711df11ce37ad71f0cd9d4cc2a68a862aa659202b"));
    }

}
