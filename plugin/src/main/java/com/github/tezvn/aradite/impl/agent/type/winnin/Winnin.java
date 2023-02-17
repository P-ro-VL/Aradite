package com.github.tezvn.aradite.impl.agent.type.winnin;

import com.github.tezvn.aradite.api.agent.skill.SkillType;
import com.github.tezvn.aradite.api.agent.texture.TextureType;
import com.github.tezvn.aradite.impl.agent.AbstractAgent;
import com.github.tezvn.aradite.impl.agent.texture.TextureImpl;
import com.github.tezvn.aradite.impl.agent.type.winnin.skill.WinninActivateC;
import com.github.tezvn.aradite.impl.agent.type.winnin.skill.WinninActivateX;
import com.github.tezvn.aradite.impl.agent.type.winnin.skill.WinninUltimate;
import org.bukkit.Color;

public final class Winnin extends AbstractAgent {

	public Winnin() {
		super("winnin", // id
				"Winnin"); // display name

		setSkill(SkillType.ACTIVE_X, new WinninActivateX());
		setSkill(SkillType.ACTIVE_C, new WinninActivateC());
		setSkill(SkillType.ULTIMATE, new WinninUltimate());

		setThemeColor(Color.YELLOW);

		setTexture(TextureType.SKULL, new TextureImpl("7f2c921873e67dbd48c6fce317e3ce7fe6a9dbe28f71a0da1fa54d2a1542143f"));
	}
	

}
