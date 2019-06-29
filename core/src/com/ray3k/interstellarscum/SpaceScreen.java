package com.ray3k.interstellarscum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;

public class SpaceScreen implements Screen {
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
        
        SpineDrawable.SpineDrawableTemplate template = new SpineDrawable.SpineDrawableTemplate();
        final SpineDrawable spineDrawable = new SpineDrawable(Core.assetManager.get("spine/space.json", SkeletonData.class), Core.skeletonRenderer, template);
        spineDrawable.getAnimationState().setAnimation(0, "animation", true);
        spineDrawable.getAnimationState().setAnimation(1, "show", false);
        Image image = new Image(spineDrawable);
        root.add(image).grow();
        spineDrawable.getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation().getName().equals("hide")) {
                    Core.core.setScreen(new QuarantineScreen());
                }
            }
        });
        
        stage.addAction(Actions.sequence(Actions.delay(5), new SingleAction() {
            @Override
            public void perform() {
                spineDrawable.getAnimationState().setAnimation(1, "hide", false);
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
