package com.ray3k.interstellarscum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;

public class GameScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private int scrollIndex;
    private final float SCROLL_WIDTH = 400;
    
    @Override
    public void show() {
        scrollIndex = 0;
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
        
        for (Person person : Core.crew) {
            Image image;
            switch (person.mode) {
                case SICK:
                    image = new Image(skin, "icon-person-sick");
                    break;
                case HIBERNATED:
                    image = new Image(skin, "icon-person-hibernation");
                    break;
                default:
                    image = new Image(skin, "icon-person");
                    break;
            }
            
            image.setScaling(Scaling.none);
            horizontalGroup.addActor(image);
        }
    
        Image image = new Image(skin, "icon-person");
        image.setScaling(Scaling.none);
        horizontalGroup.addActor(image);
        
        table = new Table();
        root.add(table).grow();
        
        ImageButton imageButton = new ImageButton(skin, "left");
        table.add(imageButton).growY();
        
        final Table scrollTable = new Table();
        final ScrollPane scrollPane = new ScrollPane(scrollTable);
//        scrollPane.setTouchable(Touchable.disabled);
        table.add(scrollPane).width(SCROLL_WIDTH).growY();
        
        for (Person person : Core.crew) {
            if (person.mode == Person.Mode.ALIVE) {
                Label label = new Label(person.name, skin, "name");
                label.setAlignment(Align.center);
                scrollTable.add(label).width(SCROLL_WIDTH);
            }
        }
        
        scrollTable.row();
        
        for (Person person : Core.crew) {
            if (person.mode == Person.Mode.ALIVE) {
                SpineDrawable.SpineDrawableTemplate template = new SpineDrawable.SpineDrawableTemplate();
                SpineDrawable spineDrawable = new SpineDrawable(Core.assetManager.get("spine/person.json", SkeletonData.class), Core.skeletonRenderer, template);
                spineDrawable.getAnimationState().setAnimation(0, "stand", true);
                image = new Image(spineDrawable);
                scrollTable.add(image);
            }
        }
        
        imageButton = new ImageButton(skin, "right");
        table.add(imageButton).growY();
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                scrollIndex++;
                scrollPane.setScrollX(scrollIndex * scrollPane.getWidth());
            }
        });
        
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
                for (Actor actor1 : scrollTable.getChildren()) {
                    if (actor1 instanceof Image) {
                        ((SpineDrawable) ((Image) actor1).getDrawable()).getAnimationState().setAnimation(1, "hide", false);
                    }
                }
                
                stage.addAction(Actions.sequence(Actions.fadeOut(1f), Actions.delay(.5f), new SingleAction() {
                    @Override
                    public void perform() {
                        Core.core.setScreen(new VoteScreen());
                    }
                }));
            }
        });
        
        stage.getRoot().setColor(1,1,1,0);
        stage.addAction(Actions.fadeIn(.5f));
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
