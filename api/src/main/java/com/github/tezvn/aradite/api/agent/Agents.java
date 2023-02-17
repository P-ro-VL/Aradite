package com.github.tezvn.aradite.api.agent;

import com.github.tezvn.aradite.impl.agent.type.innova.Innova;
import com.github.tezvn.aradite.impl.agent.type.moroe.Moroe;
import com.github.tezvn.aradite.impl.agent.type.winnin.Winnin;

public enum Agents {

    WINNIN(Winnin.class),

    MOROE(Moroe.class),

    INNOVA(Innova.class);

    private Class<? extends Agent> wrapper;

    private Agents(Class<? extends Agent> wrapper) {
        this.wrapper = wrapper;
    }

    public Class<? extends Agent> getWrapper() {
        return wrapper;
    }

}
