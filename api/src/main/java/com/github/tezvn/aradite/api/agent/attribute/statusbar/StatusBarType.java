package com.github.tezvn.aradite.api.agent.attribute.statusbar;

import com.github.tezvn.aradite.api.agent.attribute.statusbar.StatusBar;
import com.github.tezvn.aradite.impl.agent.attribute.statusbar.AgentHealthBar;

public enum StatusBarType {

    HEALTH_BAR(AgentHealthBar.class);

    private final Class<? extends StatusBar> wrapper;

    private StatusBarType(Class<? extends StatusBar> wrapperClass){
        this.wrapper = wrapperClass;
    }

    public Class<? extends StatusBar> getWrapper() {
        return wrapper;
    }
}
