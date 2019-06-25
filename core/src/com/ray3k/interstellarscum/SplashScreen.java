package com.ray3k.interstellarscum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import regexodus.Matcher;
import regexodus.Pattern;

public class SplashScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private Table root;
    
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        skin = Core.skin;
    
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        root.setBackground(skin.getDrawable("white"));
    
        SpineDrawable.SpineDrawableTemplate template = new SpineDrawable.SpineDrawableTemplate();
        final SpineDrawable spineDrawable = new SpineDrawable(Core.assetManager.get("spine/splash.json", SkeletonData.class), Core.skeletonRenderer, template);
        spineDrawable.getAnimationState().setAnimation(0, "animation", false);
        Image image = new Image(spineDrawable);
        root.add(image);
        spineDrawable.getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                stage.addAction(Actions.sequence(Actions.delay(1), new SingleAction() {
                    @Override
                    public void perform() {
                        nextScreen();
                    }
                }));
            }
    
            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
                Pattern pattern = new Pattern(".*(?=\\.)");
                Matcher matcher = pattern.matcher(event.getData().getAudioPath());
    
                if (matcher.find()) {
                    Core.core.playSound(matcher.group(), 1);
                }
            }
        });
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(22 / 255f, 22 / 255f, 22 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act();
        stage.draw();
        
        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) || Gdx.input.isTouched()) {
            nextScreen();
        }
    }
    
    private void nextScreen() {
        Gdx.input.setInputProcessor(null);
        root.addAction(Actions.sequence(Actions.color(new Color(22 / 255f, 22 / 255f, 22 / 255f, 1), 1), Actions.delay(1), new SingleAction() {
            @Override
            public void perform() {
                Core.core.setScreen(new MenuScreen());
            }
        }));
    }
    
    @Override
    public void resize(int width, int height) {
    
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
