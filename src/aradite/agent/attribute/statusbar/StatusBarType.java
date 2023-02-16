package aradite.agent.attribute.statusbar;

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
