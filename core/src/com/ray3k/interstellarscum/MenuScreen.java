package com.ray3k.interstellarscum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;

public class MenuScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private static Music music;
    
    @Override
    public void show() {
        if (music == null) {
            music = Core.assetManager.get("bgm/music.mp3");
            music.setLooping(true);
            music.setVolume(.5f);
            music.play();
        }
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        skin = Core.skin;
    
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    
        root.pad(20);
        Label label = new Label("INTERSTELLAR SCUM", skin,"title");
        root.add(label).expand().bottom();
        
        root.row();
        label = new Label("Type your name:", skin, "name");
        root.add(label).spaceTop(75).spaceBottom(30);
        
        root.row();
        final TextField textField = new TextField("",skin);
        textField.setName("textField");
        textField.setAlignment(Align.center);
        root.add(textField).spaceBottom(75).minWidth(300);
        stage.setKeyboardFocus(textField);
        textField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    nextScreen();
                }
                return super.keyDown(event, keycode);
            }
        });
        
        root.row();
        TextButton textButton = new TextButton("OK", skin);
        root.add(textButton).minWidth(300);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Core.core.playSound("confirm",1);
                nextScreen();
            }
        });
        
        root.row();
        label = new Label("A game by Raeleus... Copyright © Raymond Buckley 2019", skin);
        root.add(label).expand().bottom();
        
        stage.getRoot().setColor(1, 1, 1, 0);
        stage.addAction(Actions.fadeIn(1));
    }
    
    private void nextScreen() {
        Core.crew = new Array<Person>();
    
        TextField textField = stage.getRoot().findActor("textField");
        String name = textField.getText();
        if (name.equals("")) name = "You";
        Core.player = name;
    
        Array<String> temp = new Array<String>(Core.names);
        temp.shuffle();
        for (int i = 0; i < 15; i++) {
            Person person = new Person();
            person.name = temp.pop();
            person.type = Person.Type.NORMAL;
            person.mode = Person.Mode.ALIVE;
            Core.crew.add(person);
        }
        
        Array<Person> tempCrew = new Array<Person>(Core.crew);
        int count = MathUtils.random(2, 4);
        for (int i = 0; i < Math.min(tempCrew.size, count); i++) {
            tempCrew.shuffle();
            Person person = tempCrew.pop();
            person.type = Person.Type.HOST;
        }
    
        count = MathUtils.random(2);
        for (int i = 0; i < Math.min(tempCrew.size, count); i++) {
            tempCrew.shuffle();
            Person person = tempCrew.pop();
            person.type = Person.Type.DOCTOR;
        }
    
        count = MathUtils.random(2);
        for (int i = 0; i < Math.min(tempCrew.size, count); i++) {
            tempCrew.shuffle();
            Person person = tempCrew.pop();
            person.type = Person.Type.DETECTIVE;
        }
    
        count = MathUtils.random(2, 4);
        for (int i = 0; i < Math.min(tempCrew.size, count); i++) {
            tempCrew.shuffle();
            Person person = tempCrew.pop();
            person.type = Person.Type.LIAR;
        }
    
        count = MathUtils.random(1,3);
        for (int i = 0; i < Math.min(tempCrew.size, count); i++) {
            tempCrew.shuffle();
            Person person = tempCrew.pop();
            person.type = Person.Type.COPYCAT;
        }
    
        count = MathUtils.random(2, 4);
        for (int i = 0; i < Math.min(tempCrew.size, count); i++) {
            tempCrew.shuffle();
            Person person = tempCrew.pop();
            person.type = Person.Type.LOYALIST;
        }
    
        Gdx.input.setInputProcessor(null);
        stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.delay(1), new SingleAction() {
            @Override
            public void perform() {
                Core.core.setScreen(new IntroScreen());
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
