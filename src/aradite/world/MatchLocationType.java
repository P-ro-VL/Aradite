package aradite.world;

public enum MatchLocationType {

    UNDEFINED_TEAM_BASE(true, 2),
    TEAM_ATTACK_BASE(false, 1),
    TEAM_DEFEND_BASE(false, 1),
    BOMB_CART_START(false, 1),
    BOMB_CART_END(false, 1),
    CAPTURE_POINT(true, 3);

    private final boolean isMultiloc;
    private final int maxSize;

    private MatchLocationType(boolean isMultiloc, int maxSize) {
        this.isMultiloc = isMultiloc;
        this.maxSize = maxSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public boolean isMultiloc() {
        return isMultiloc;
    }
}
