package com.github.tezvn.aradite.language;

public class Placeholder {

    private final String identifier;
    private final Object replacement;

    public static Placeholder of(String identifier, Object replacement){
        return new Placeholder(identifier, replacement);
    }
    
    private Placeholder(String placeholderIdentifier, Object replacement){
        this.identifier = placeholderIdentifier;
        this.replacement = replacement;
    }

    public Object getReplacement() {
        return replacement;
    }

    public String getIdentifier() {
        return identifier;
    }

}
