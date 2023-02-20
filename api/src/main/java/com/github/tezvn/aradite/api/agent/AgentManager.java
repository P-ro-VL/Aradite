package com.github.tezvn.aradite.api.agent;

public interface AgentManager {

    Agent createNewInstance(String agentId);

    Agent createNewInstance(Agents agentEnum);

    Class<? extends Agent> getAgentWrapperById(String id);

    void registerAgentClass(Class<? extends Agent> clazz);

}
