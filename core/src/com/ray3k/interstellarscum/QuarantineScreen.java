package com.ray3k.interstellarscum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import regexodus.Matcher;
import regexodus.Pattern;

public class QuarantineScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private boolean showDiscussion;
    
    @Override
    public void show() {
        showDiscussion = Core.livingCrew.size == 15;
        
        if (Core.livingCrew.size > 0) {
            String crew = Core.livingCrew.random();
            Core.livingCrew.removeValue(crew, false);
            Core.infectedCrew.add(crew);
        }
        
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        skin = Core.skin;
    
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    
        root.pad(20);
        final TypingLabel typingLabel = new TypingLabel("{EASE}" + Core.infectedCrew.peek() + " is sick and has been quarantined! The telltale signs of infection are abundant.{ENDEASE}", skin);
        root.add(typingLabel);
        typingLabel.pause();
        
        root.row();
        SpineDrawable.SpineDrawableTemplate template = new SpineDrawable.SpineDrawableTemplate();
        final SpineDrawable spineDrawable = new SpineDrawable(Core.assetManager.get("spine/person.json", SkeletonData.class), Core.skeletonRenderer, template);
        spineDrawable.getAnimationState().setAnimation(0, "sick", true);
        Image image = new Image(spineDrawable);
        root.add(image).expand();
        spineDrawable.getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
                Pattern pattern = new Pattern(".*(?=\\.)");
                Matcher matcher = pattern.matcher(event.getData().getAudioPath());
                
                if (matcher.find()) {
                    Core.core.playSound(matcher.group(), 1);
                }
            }
        });
        
        root.row();
        TextButton textButton = new TextButton("CONTINUE", skin, "small");
        root.add(textButton).minWidth(300f);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                spineDrawable.getAnimationState().setAnimation(1, "hide", false);
                stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.delay(.25f), new SingleAction() {
                    @Override
                    public void perform() {
                        if (showDiscussion) {
                            Core.core.setScreen(new DiscussionScreen());
                        } else {
                            Core.core.setScreen(new GameScreen());
                        }
                    }
                }));
            }
        });
        
        stage.getRoot().setColor(1,1,1,0);
        stage.addAction(Actions.sequence(Actions.fadeIn(1), new SingleAction() {
            @Override
            public void perform() {
                typingLabel.resume();
            }
        }));
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
