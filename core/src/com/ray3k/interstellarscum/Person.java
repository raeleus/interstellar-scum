package com.ray3k.interstellarscum;

public class Person {
    public Type type;
    public Mode mode;
    public Person accusation;
    public Person vote;
    public String name;
    
    public static enum Type {
        LIAR, HOST, DETECTIVE, DOCTOR, LOYALIST, NORMAL
    }
    
    public static enum Mode {
        HIBERNATED, SICK, ALIVE
    }
    
    private void chooseVote() {
    
    }
    
    private void chooseAccusation() {
    
    }
}
