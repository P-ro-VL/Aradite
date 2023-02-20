package com.github.tezvn.aradite.impl;

import com.github.tezvn.aradite.api.Aradite;
import com.github.tezvn.aradite.api.AraditeAPI;
import com.github.tezvn.aradite.api.language.Language;

public class DefaultAraditeAPI implements AraditeAPI {

    public Aradite plugin;

    public DefaultAraditeAPI(Aradite plugin) {
        this.plugin = plugin;
    }

    @Override
    public Language getLanguage() {
        return ((AraditeImpl) plugin).getLanguage();
    }
}
