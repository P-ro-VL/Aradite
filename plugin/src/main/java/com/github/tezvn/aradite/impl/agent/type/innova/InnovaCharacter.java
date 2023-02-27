package com.github.tezvn.aradite.impl.agent.type.innova;

import com.github.tezvn.aradite.api.agent.AgentIndicator;
import com.github.tezvn.aradite.api.agent.skill.SkillType;
import com.github.tezvn.aradite.api.agent.texture.TextureType;
import com.github.tezvn.aradite.api.agent.type.Innova;
import com.github.tezvn.aradite.impl.agent.AbstractAgent;
import com.github.tezvn.aradite.impl.agent.texture.TextureImpl;
import com.github.tezvn.aradite.impl.agent.type.innova.skill.InnovaC;
import com.github.tezvn.aradite.impl.agent.type.innova.skill.InnovaUltimate;
import com.github.tezvn.aradite.impl.agent.type.innova.skill.InnovaX;
import org.bukkit.Color;

@AgentIndicator(id="innova")
public class InnovaCharacter extends AbstractAgent implements Innova {

    public InnovaCharacter() {
        super("innova", "Innova");

        setSkill(SkillType.ACTIVE_X, new InnovaX());
        setSkill(SkillType.ACTIVE_C, new InnovaC());
        setSkill(SkillType.ULTIMATE, new InnovaUltimate());

        setThemeColor(Color.BLUE);

        setTexture(TextureType.SKULL, new TextureImpl("663f5a7d7128c73f9b949a582ba58b572d289734abc4121f45537073827110bb"));

    }

}
