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
    public static Person playerVote;
    public static Person hostTarget;
    
    public static enum Type {
        LIAR, HOST, DETECTIVE, DOCTOR, LOYALIST, COPYCAT, NORMAL
    }
    
    public static enum Mode {
        HIBERNATED, SICK, ALIVE
    }
    
    public void chooseVote() {
        Array<Person> livingCrew = new Array<Person>();
        for (Person person : Core.crew) {
            if (person.mode == Person.Mode.ALIVE && !person.equals(this)) {
                livingCrew.add(person);
            }
        }
        
        switch (type) {
            case LIAR:
                vote = accusation;
                break;
            case DETECTIVE:
                vote = accusation;
                break;
            case COPYCAT:
                Array<Person> people = new Array<Person>(accusedList);
                people.removeValue(this, false);
                vote = people.random();
                break;
            case NORMAL:
                if (accusation != null) vote = accusation;
                else if (MathUtils.randomBoolean(.75f)) {
                    people = new Array<Person>(accusedList);
                    people.removeValue(this, false);
                    vote = people.random();
                }
                break;
            case LOYALIST:
                vote = playerVote;
                break;
            case HOST:
                vote = hostTarget;
                break;
        }
        
        if (vote != null && vote.equals(this)) vote = null;
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
                //randomly accuse someone
                if (MathUtils.randomBoolean(.75f)) {
                    Person accused = livingCrew.random();
                    accusation = accused;
                    accusedList.add(accused);
                }
                break;
            case COPYCAT:
                //copy the accusation of someone else
                if (accusedList.size > 0) {
                    Array<Person> persons = new Array<Person>(accusedList);
                    persons.removeValue(this, false);
                    accusation = persons.random();
                }
                break;
            case DETECTIVE:
                //detect if someone is a host
                Array<Person> suspects = new Array<Person>(livingCrew);
                for (Person person : detectiveChecked) {
                    suspects.removeValue(person, false);
                }
                
                Person suspect = suspects.random();
                detectiveChecked.add(suspect);
                if (suspect != null && suspect.type == Type.HOST) {
                    detectiveFound.add(suspect);
                }
                
                //accuse anyone found to be a host
                if (detectiveFound.size > 0) {
                    accusation = detectiveFound.random();
                }
                
                break;
            case HOST:
                if (accusedList.size > 0 && MathUtils.randomBoolean(.75f)) {
                    //copy the accusation of someone else
                    Array<Person> persons = new Array<Person>(accusedList);
                    persons.removeValue(this, false);
                    accusation = persons.random();
                } else if (MathUtils.randomBoolean(.75f)) {
                    //randomly accuse someone
                    Person accused = livingCrew.random();
                    accusation = accused;
                    accusedList.add(accused);
                }
                
                break;
            case NORMAL:
                //retaliatory accusation if being accused
                if (MathUtils.randomBoolean(.5f) && accusedList.contains(this, false)) {
                    livingCrew.shuffle();
                    for (Person person : livingCrew) {
                        if (person.accusation != null && person.accusation.equals(this)) {
                            accusation = person;
                            break;
                        }
                    }
                }
                break;
        }
    
        if (accusation != null && accusation.equals(this)) accusation = null;
        if (accusation != null && accusation.mode != Mode.ALIVE) accusation = null;
    }
}
