package com.ray3k.interstellarscum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;

public class ResultScreen implements Screen {
    private Stage stage;
    private Skin skin;
    
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        skin = Core.skin;
    
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    
        root.pad(20);
        root.defaults().space(30);
        Label label = new Label("THE RESULT", skin, "title");
        root.add(label);
        
        root.row();
        Table table = new Table();
        root.add(table).expandY();
        
        table.defaults().spaceRight(30).spaceBottom(5);
        label = new Label("INFECTED", skin, "heading");
        table.add(label);
    
        label = new Label("STASIS", skin, "heading");
        table.add(label);
    
        label = new Label("SURVIVORS", skin, "heading");
        table.add(label);
    
        boolean foundHost = false;
    
        for (Person person : Core.crew) {
            if (person.type == Person.Type.HOST && person.mode == Person.Mode.ALIVE) {
                foundHost = true;
                break;
            }
        }
        
        table.row();
        String text = "";
        for (Person person : Core.crew) {
            if (person.mode == Person.Mode.SICK) {
                text += person.name + " (" + person.type + ")\n";
            }
        }
        if (foundHost) {
            text += Core.player + " (CAPTAIN)\n";
        }
        label = new Label(text, skin);
        ScrollPane scrollPane = new ScrollPane(label, skin);
        scrollPane.setFadeScrollBars(false);
        table.add(scrollPane).fill().uniform();
    
        text = "";
        for (Person person : Core.crew) {
            if (person.mode == Person.Mode.HIBERNATED) {
                text += person.name + " (" + person.type + ")\n";
            }
        }
        label = new Label(text, skin);
        scrollPane = new ScrollPane(label, skin);
        scrollPane.setFadeScrollBars(false);
        table.add(scrollPane).fill().uniform();
    
        text = "";
        for (Person person : Core.crew) {
            if (person.mode == Person.Mode.ALIVE) {
                text += person.name + " (" + person.type + ")\n";
            }
        }
        if (!foundHost) {
            text += Core.player + " (CAPTAIN)\n";
        }
        label = new Label(text, skin);
        scrollPane = new ScrollPane(label, skin);
        scrollPane.setFadeScrollBars(false);
        table.add(scrollPane).fill().uniform();
        
        root.row();
        if (foundHost) {
            text = "MISSION: FAILURE";
        } else {
            text = "MISSION: SUCCESS";
        }
        label = new Label(text, skin, "subtitle");
        root.add(label);
        
        root.row();
        label = new Label("All crew infected by day 13", skin, "heading");
        root.add(label);
        
        root.row();
        TextButton textButton = new TextButton("TRY AGAIN",skin, "small");
        root.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.delay(1), new SingleAction() {
                    @Override
                    public void perform() {
                        Core.core.setScreen(new MenuScreen());
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
