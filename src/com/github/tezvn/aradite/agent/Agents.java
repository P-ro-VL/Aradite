package com.github.tezvn.aradite.agent;

import com.github.tezvn.aradite.agent.type.innova.Innova;
import com.github.tezvn.aradite.agent.type.moroe.Moroe;
import com.github.tezvn.aradite.agent.type.winnin.Winnin;
import org.bukkit.Bukkit;

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
