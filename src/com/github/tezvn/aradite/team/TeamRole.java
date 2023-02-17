package com.github.tezvn.aradite.team;

import com.github.tezvn.aradite.Aradite;
import com.github.tezvn.aradite.language.Language;

public enum TeamRole {

    UNDEFINED, ATTACK, DEFEND, OBSERVER;

    public String getDisplay() {
        Language lang = Aradite.getInstance().getLanguage();
        switch (this) {
            case DEFEND:
                return lang.getString("tean.defender");
            case ATTACK:
                return lang.getString("team.attacker");
            case OBSERVER:
                return lang.getString("team.observer");
            default:
                return "";
        }
    }

    public TeamRole not() {
        switch (this) {
            case DEFEND:
                return ATTACK;
            case ATTACK:
                return DEFEND;
            default:
                return this;
        }
    }

}
