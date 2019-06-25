package com.ray3k.interstellarscum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;

public class MenuScreen implements Screen {
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
        Label label = new Label("INTERSTELLAR SCUM", skin,"title");
        root.add(label).expand().bottom();
        
        root.row();
        label = new Label("Type your name:", skin, "name");
        root.add(label).spaceTop(75).spaceBottom(30);
        
        root.row();
        final TextField textField = new TextField("",skin);
        textField.setAlignment(Align.center);
        root.add(textField).spaceBottom(75).minWidth(300);
        stage.setKeyboardFocus(textField);
        
        root.row();
        TextButton textButton = new TextButton("OK", skin);
        root.add(textButton).minWidth(300);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Core.infectedCrew = new Array<String>();
                Core.stasisCrew = new Array<String>();
                Core.normalCrew = new Array<String>();
                
                String name = textField.getText();
                if (name.equals("")) name = "You";
                Core.normalCrew.add(name);
                
                Array<String> temp = new Array<String>(Core.names);
                temp.shuffle();
                for (int i = 0; i < 15; i++) {
                    Core.normalCrew.add(temp.pop());
                }
                
                Gdx.input.setInputProcessor(null);
                Core.core.setScreen(new IntroScreen());
            }
        });
        
        root.row();
        label = new Label("A game by Raeleus... Copyright © Raymond Buckley 2019", skin);
        root.add(label).expand().bottom();
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(22f / 255f, 22f / 255f, 22f / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act();
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
