package com.github.tezvn.aradite.api.agent;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AgentIndicator {

    public String id();

}
