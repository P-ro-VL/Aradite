package com.github.tezvn.aradite.api;

import com.google.common.base.Preconditions;

public class AraditeAPIProvider {

    private static AraditeAPI instance;

    public static AraditeAPI get() {
        Preconditions.checkNotNull(instance, "api is not loaded!");
        return instance;
    }

    public static void register(AraditeAPI api) {
        instance = api;
    }

    public static void unregister() {
        instance = null;
    }

}
