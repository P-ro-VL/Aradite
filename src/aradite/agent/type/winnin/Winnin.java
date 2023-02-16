package aradite.agent.type.winnin;

import aradite.agent.AbstractAgent;
import aradite.agent.attribute.AttributeType;
import aradite.agent.attribute.BaseAttribute;
import aradite.agent.skill.SkillType;
import aradite.agent.texture.Texture;
import aradite.agent.texture.TextureType;
import aradite.agent.type.winnin.skill.WinninActivateC;
import aradite.agent.type.winnin.skill.WinninActivateX;
import aradite.agent.type.winnin.skill.WinninUltimate;
import org.bukkit.Color;

public final class Winnin extends AbstractAgent {

	public Winnin() {
		super("winnin", // id
				"Winnin"); // display name

		setSkill(SkillType.ACTIVE_X, new WinninActivateX());
		setSkill(SkillType.ACTIVE_C, new WinninActivateC());
		setSkill(SkillType.ULTIMATE, new WinninUltimate());

		setThemeColor(Color.YELLOW);

		setTexture(TextureType.SKULL, new Texture("7f2c921873e67dbd48c6fce317e3ce7fe6a9dbe28f71a0da1fa54d2a1542143f"));
	}
	

}
