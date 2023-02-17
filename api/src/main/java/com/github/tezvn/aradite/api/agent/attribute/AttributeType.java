package com.github.tezvn.aradite.api.agent.attribute;

import com.github.tezvn.aradite.api.Aradite;
import com.github.tezvn.aradite.api.AraditeAPIProvider;

public enum AttributeType {

	DAMAGE_REDUCE_IN_PERCENT,

	CURRENT_HEALTH,

	MAX_HEALTH,

	ARMOR,

	CRITICAL_CHANCE,

	CRITICAL_MULTIPLY,

	MOVEMENT_SPEED,

	ARMOR_PENETRATION,

	ABILITY_HASTE;

	private boolean isInGameAttribute;
	private String display;

	private AttributeType(boolean isInGameAttr){
		this.isInGameAttribute = isInGameAttr;
	}

	private AttributeType() {
		this.isInGameAttribute = false;
	}

	public String getDisplay() {
		return AraditeAPIProvider.get().getLanguage().getString("attribute.display." + toString());
	}

	public boolean isInGameAttribute() {
		return isInGameAttribute;
	}
}
