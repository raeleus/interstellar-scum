package com.ray3k.interstellarscum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;

public class VoteScreen implements Screen {
    private Stage stage;
    private Skin skin;
    
    @Override
    public void show() {
        final Array<Person> livingCrew = new Array<Person>();
        for (Person person : Core.crew) {
            if (person.mode == Person.Mode.ALIVE) {
                livingCrew.add(person);
            }
        }
        
        ObjectIntMap<Person> votes = new ObjectIntMap<Person>();
        for (Person person : livingCrew) {
            if (person.vote != null) {
                votes.put(person.vote, votes.get(person.vote, 0) + 1);
            }
        }
        
        votes.put(Person.playerVote, votes.get(Person.playerVote, 0) + 1);
    
        Person hibernatePerson = null;
        Person runnerUp = null;
        int highest = 0;
        for (Person person : votes.keys()) {
            if (votes.get(person, 0) > highest) {
                runnerUp = hibernatePerson;
                hibernatePerson = person;
                highest = votes.get(person, 0);
            } else if (votes.get(person, 0) == highest) {
                runnerUp = person;
            }
        }
        
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        skin = Core.skin;
    
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    
        root.pad(20);
        Label label = new Label("THE VOTE", skin, "title");
        root.add(label);
        
        root.row();
        String text = "";
        for (Person person : votes.keys()) {
            text += person.name + " " + votes.get(person, 0) + "\n";
        }
        
        if (votes.get(hibernatePerson, 0) == (runnerUp != null ? votes.get(runnerUp, 0) : 0)) {
            text += "\nNO CONSENSUS!";
        } else {
            text += "\n" + hibernatePerson.name.toUpperCase() + " WILL BE PUT INTO STASIS!";
            hibernatePerson.mode = Person.Mode.HIBERNATED;
        }
        
        label = new Label(text, skin);
        label.setAlignment(Align.center);
        root.add(label).expandY();
        
        root.row();
        TextButton textButton = new TextButton("CONTINUE",skin, "small");
        root.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.delay(1), new SingleAction() {
                    @Override
                    public void perform() {
                        Core.core.setScreen(new SpaceScreen());
                    }
                }));
            }
        });
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(22f / 255f, 22f / 255f, 22f / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void pause() {
    
    }
    
    @Override
    public void resume() {
    
    }
    
    @Override
    public void hide() {
        stage.dispose();
    }
    
    @Override
    public void dispose() {
        stage.dispose();
    }
}
