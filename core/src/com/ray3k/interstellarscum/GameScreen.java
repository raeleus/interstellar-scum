package com.ray3k.interstellarscum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;

public class GameScreen implements Screen {
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
        root.defaults().space(5);
        Table table = new Table();
        table.setBackground(skin.getDrawable("people-container"));
        root.add(table);
        
        HorizontalGroup horizontalGroup = new HorizontalGroup();
        horizontalGroup.wrap();
        horizontalGroup.center();
        horizontalGroup.space(5);
        horizontalGroup.wrapSpace(5);
        table.add(horizontalGroup).grow();
        
        for (int i = 0; i < Core.infectedCrew.size; i++) {
            Image image = new Image(skin, "icon-person-sick");
            image.setScaling(Scaling.none);
            horizontalGroup.addActor(image);
        }
    
        for (int i = 0; i < Core.stasisCrew.size; i++) {
            Image image = new Image(skin, "icon-person-hibernation");
            image.setScaling(Scaling.none);
            horizontalGroup.addActor(image);
        }
    
        for (int i = 0; i < Core.livingCrew.size + 1; i++) {
            Image image = new Image(skin, "icon-person");
            image.setScaling(Scaling.none);
            horizontalGroup.addActor(image);
        }
        
        table = new Table();
        root.add(table).grow();
        
        ImageButton imageButton = new ImageButton(skin, "left");
        table.add(imageButton).growY();
        
        Table scrollTable = new Table();
        ScrollPane scrollPane = new ScrollPane(scrollTable);
        scrollPane.setTouchable(Touchable.disabled);
        table.add(scrollPane).grow();
        
        imageButton = new ImageButton(skin, "right");
        table.add(imageButton).growY();
        
        root.row();
        table = new Table();
        root.add(table);
    
        ProgressBar progressBar = new ProgressBar(0, 1,1,false, skin);
        table.add(progressBar);
        
        table.row();
        Label label = new Label("Days to arrival: 365", skin);
        table.add(label);
        
        TextButton textButton = new TextButton("VOTE", skin);
        root.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Core.core.setScreen(new VoteScreen());
            }
        });
        
//        SpineDrawable.SpineDrawableTemplate template = new SpineDrawable.SpineDrawableTemplate();
//        SpineDrawable spineDrawable = new SpineDrawable(Core.assetManager.get("spine/intro.json", SkeletonData.class), Core.skeletonRenderer, template);
//        Image image = new Image(spineDrawable);
//        root.add(image);
    
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
