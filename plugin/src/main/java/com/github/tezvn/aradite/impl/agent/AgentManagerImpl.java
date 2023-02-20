package com.github.tezvn.aradite.impl.agent;

import com.github.tezvn.aradite.api.agent.Agent;
import com.github.tezvn.aradite.api.agent.AgentIndicator;
import com.github.tezvn.aradite.api.agent.AgentManager;
import com.github.tezvn.aradite.api.agent.Agents;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.Objects;

public class AgentManagerImpl implements AgentManager {

    private static final Map<String, Class<? extends Agent>> registeredAgents = Maps.newHashMap();


    @Override
    public Agent createNewInstance(String agentId) {
        Class<? extends Agent> agentClass = getAgentWrapperById(agentId);
        try {
            return agentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("Cannot create a new instance of agent whose id is '" + agentId + "'");
    }

    @Override
    public Agent createNewInstance(Agents agentEnum) {
        return createNewInstance(agentEnum.getId());
    }

    @Override
    public Class<? extends Agent> getAgentWrapperById(String id) {
        return Objects.requireNonNull(registeredAgents.get(id), "There is no agent whose id is '" + id + "'");
    }

    @Override
    public void registerAgentClass(Class<? extends Agent> clazz) {
        try {
            AgentIndicator indicator = clazz.getDeclaredAnnotation(AgentIndicator.class);
            String id = indicator.id();
            registeredAgents.put(id, clazz);
        }catch (Exception ex){
            Bukkit.getLogger().severe("Cannot register agent with wrapper class: " + clazz.getName());
        }
    }
}
