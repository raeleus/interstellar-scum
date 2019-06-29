package com.ray3k.interstellarscum;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Person {
    public Type type;
    public Mode mode;
    public Person accusation;
    public Person vote;
    public String name;
    
    private Array<Person> detectiveChecked = new Array<Person>();
    private Array<Person> detectiveFound = new Array<Person>();
    
    public static Array<Person> accusedList = new Array<Person>();
    
    public static enum Type {
        LIAR, HOST, DETECTIVE, DOCTOR, LOYALIST, COPYCAT, NORMAL
    }
    
    public static enum Mode {
        HIBERNATED, SICK, ALIVE
    }
    
    public void chooseVote() {
    
    }
    
    public void chooseAccusation() {
        Array<Person> livingCrew = new Array<Person>();
        for (Person person : Core.crew) {
            if (person.mode == Person.Mode.ALIVE && !person.equals(this)) {
                livingCrew.add(person);
            }
        }
        
        switch (type) {
            case LIAR:
                if (MathUtils.randomBoolean(.75f)) {
                    Person accused = livingCrew.random();
                    accusation = accused;
                    accusedList.add(accused);
                }
                break;
            case COPYCAT:
                if (accusedList.size > 0) {
                    accusation = accusedList.random();
                }
                break;
            case DETECTIVE:
                Array<Person> suspects = new Array<Person>(livingCrew);
                for (Person person : detectiveChecked) {
                    suspects.removeValue(person, false);
                }
                
                Person suspect = suspects.random();
                detectiveChecked.add(suspect);
                if (suspect != null && suspect.type == Type.HOST) {
                    detectiveFound.add(suspect);
                }
                
                if (detectiveFound.size > 0) {
                    accusation = detectiveFound.random();
                }
                
                break;
            case HOST:
                if (accusedList.size > 0 && MathUtils.randomBoolean(.75f)) {
                    accusation = accusedList.random();
                } else if (MathUtils.randomBoolean(.75f)) {
                    Person accused = livingCrew.random();
                    accusation = accused;
                    accusedList.add(accused);
                }
                
                break;
        }
    }
}
