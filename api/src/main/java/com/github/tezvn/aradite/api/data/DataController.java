package com.github.tezvn.aradite.api.data;

import com.github.tezvn.aradite.impl.data.global.PlayerDataStorage;

import java.util.UUID;

public interface DataController {

    PlayerDataStorage getUserData(UUID uuid)

}
