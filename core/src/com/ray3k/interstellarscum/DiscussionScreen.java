package com.ray3k.interstellarscum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.rafaskoberg.gdx.typinglabel.TypingAdapter;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class DiscussionScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private int labelIndex;
    
    @Override
    public void show() {
        labelIndex = 0;
        
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        skin = Core.skin;
    
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    
        final Array<TypingLabel> labels = new Array<TypingLabel>();
    
        TypingAdapter typingAdapter = new TypingAdapter() {
            @Override
            public void end() {
                labels.get(++labelIndex).resume();
            }
        };
        
        Person infected = Core.crew.first();
        for (Person person : Core.crew) {
            if (person.mode == Person.Mode.SICK) {
                infected = person;
                break;
            }
        }
        
        root.pad(20);
        root.defaults().growX().expandY();
        TypingLabel typingLabel = new TypingLabel("{FAST}{EASE}{COLOR=RED}My god! " + infected.name + " managed to pass screening with the Scum!\nAre we exposed? ARE WE EXPOSED?", skin);
        typingLabel.setWrap(true);
        root.add(typingLabel);
        labels.add(typingLabel);
        typingLabel.pause();
        typingLabel.setTypingListener(typingAdapter);
        
        root.row();
        typingLabel = new TypingLabel("{FAST}{EASE}I don’t know, signs should have shown early on during quarantine.", skin);
        typingLabel.setWrap(true);
        root.add(typingLabel);
        labels.add(typingLabel);
        typingLabel.pause();
        typingLabel.setTypingListener(typingAdapter);
    
        root.row();
        typingLabel = new TypingLabel("{FAST}{EASE}{COLOR=RED}Do you think… no… maybe one of us is a carrier?\nSomeone who has the infection, but isn’t affected like everybody else?", skin);
        typingLabel.setWrap(true);
        root.add(typingLabel);
        labels.add(typingLabel);
        typingLabel.pause();
        typingLabel.setTypingListener(typingAdapter);
    
        root.row();
        typingLabel = new TypingLabel("{FAST}{EASE}That’s it, I’m putting everyone into hibernation.", skin);
        typingLabel.setWrap(true);
        root.add(typingLabel);
        labels.add(typingLabel);
        typingLabel.pause();
        typingLabel.setTypingListener(typingAdapter);
    
        root.row();
        typingLabel = new TypingLabel("{FAST}{EASE}{COLOR=RED}No wait! We need a crew to keep the ship running.\nEach person we put into hibernation delays our arrival exponentially!", skin);
        typingLabel.setWrap(true);
        root.add(typingLabel);
        labels.add(typingLabel);
        typingLabel.pause();
        typingLabel.setTypingListener(typingAdapter);
        
        root.row();
        typingLabel = new TypingLabel("{FAST}{EASE}I propose that we vote.\nWe select one crew member to put into hibernation for stasis until we figure this out.\nNow, has anyone been acting strangely?", skin);
        typingLabel.setWrap(true);
        root.add(typingLabel);
        labels.add(typingLabel);
        typingLabel.pause();
        
        root.defaults().reset();
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
                        Core.core.setScreen(new GameScreen());
                    }
                }));
            }
        });
        
        stage.getRoot().setColor(1,1,1,0);
        stage.addAction(Actions.sequence(Actions.fadeIn(1),Actions.delay(.25f), new SingleAction() {
            @Override
            public void perform() {
                labels.get(labelIndex).resume();
            }
        }));
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
