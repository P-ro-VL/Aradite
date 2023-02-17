package com.github.tezvn.aradite.api.team;

import com.github.tezvn.aradite.api.AraditeAPIProvider;
import com.github.tezvn.aradite.api.language.Language;

public enum TeamRole {

    UNDEFINED, ATTACK, DEFEND, OBSERVER;

    public String getDisplay() {
        Language lang = AraditeAPIProvider.get().getLanguage();
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

    public TeamRole getOpposite() {
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
