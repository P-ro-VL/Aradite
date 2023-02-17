package com.github.tezvn.aradite.api.agent;

import pdx.mantlecore.java.IntRange;

public enum AgentMasteryRank {

    SSS(9500), SS(9000), S(8000), A(7000), B(6000), C(5000), D(4000), UNRANKED(0);

    private final int masteryBreakthrough;

    private AgentMasteryRank(int masteryBreakthrough){
        this.masteryBreakthrough = masteryBreakthrough;
    }

    /**
     * Return the breakthrough at which the mastery will be relative rank.
     */
    public int getMasteryBreakthrough() {
        return masteryBreakthrough;
    }

    /**
     * Return the mastery rank at the current mastery point.
     * @param masteryPoint The mastery point
     * @return The rank
     */
    public static final AgentMasteryRank getRank(int masteryPoint){
        IntRange range = IntRange.of(0, masteryPoint);
        for(AgentMasteryRank rank : values()){
            if(range.isInRange(rank.getMasteryBreakthrough())) return rank;
        }
        return UNRANKED;
    }
}
