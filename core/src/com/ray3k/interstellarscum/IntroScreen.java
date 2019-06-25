package com.ray3k.interstellarscum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.rafaskoberg.gdx.typinglabel.TypingAdapter;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

public class IntroScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private static final float TOAST_PADDING = 60f;
    private static final float TOAST_HEIGHT = 125f;
    private int textState;
    
    @Override
    public void show() {
        textState = 0;
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        skin = Core.skin;
    
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        SpineDrawable.SpineDrawableTemplate template = new SpineDrawable.SpineDrawableTemplate();
        final SpineDrawable spineDrawable = new SpineDrawable(Core.assetManager.get("spine/intro.json", SkeletonData.class), Core.skeletonRenderer, template);
        spineDrawable.getAnimationState().setAnimation(0, "show", false);
        Image image = new Image(spineDrawable);
        root.add(image);
        
        final Table toast = new Table();
        toast.setBackground(skin.getDrawable("intro-toast"));
        toast.setWidth(stage.getWidth() - TOAST_PADDING * 2);
        toast.setHeight(TOAST_HEIGHT);
        toast.setPosition(TOAST_PADDING,  -TOAST_HEIGHT);
        stage.addActor(toast);
    
        final TypingLabel typingLabel = new TypingLabel("",skin);
        typingLabel.setWrap(true);
        toast.add(typingLabel).grow();
        
        spineDrawable.getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation().getName().equals("show")) {
                    toast.addAction(Actions.sequence(Actions.delay(.25f), Actions.moveTo(toast.getX(), 0, .5f), new SingleAction() {
                        @Override
                        public void perform() {
                            spineDrawable.getAnimationState().setAnimation(0, "turn green", false);
                            typingLabel.setText("{FASTER}{FADE=#16161600;#161616FF;1}Mission Statement: Humanity of Earth has been overwhelmed by a parasitic infection of unknown origin. Symptoms include hallucinations, vomiting, convulsions, coughing fits, diarrhea. The interstellar spacecraft Deonida transports the last survivors to sanctuary: wherever they may find it...{ENDFADE}");
                            typingLabel.restart();
                        }
                    }));
    
                    typingLabel.setTypingListener(new TypingAdapter() {
                        @Override
                        public void end() {
                            stage.addAction(Actions.sequence(Actions.delay(5), new SingleAction() {
                                @Override
                                public void perform() {
                                    if (textState == 0) {
                                        typingLabel.setText("{FASTER}{FADE=#16161600;#161616FF;1}Status: 16 crew - status well\n" +
                                                "5223 passengers - status hybernation\n" +
                                                "Days to destination: 365 (estimate)\n" +
                                                "Personnel Records are Confidential{ENDFADE}");
                                        typingLabel.restart();
                                        textState = 1;
                                    } else if (textState == 1){
                                        textState = 2;
                                        toast.addAction(Actions.sequence(Actions.moveTo(toast.getX(),-TOAST_HEIGHT, .5f),Actions.delay(.5f), new SingleAction() {
                                            @Override
                                            public void perform() {
                                                spineDrawable.getAnimationState().setAnimation(0, "escape", false);
                                                spineDrawable.getAnimationState().addAnimation(0, "hide", false, 0);
                                            }
                                        }));
                                    }
                                }
                            }));
                        }
                    });
                } else if (entry.getAnimation().getName().equals("hide")) {
                    Gdx.input.setInputProcessor(null);
                    stage.addAction(Actions.sequence(Actions.delay(3), new SingleAction() {
                        @Override
                        public void perform() {
                            Core.core.setScreen(new SpaceScreen());
                        }
                    }));
                }
            }
        });
    
        stage.addListener(new InputListener() {
            boolean listening = true;
        
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                nextScreen();
                return super.touchDown(event, x, y, pointer, button);
            }
        
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                nextScreen();
                return super.keyDown(event, keycode);
            }
        
            public void nextScreen() {
                if (listening) {
                    listening = false;
                    Gdx.input.setInputProcessor(null);
                    stage.addAction(Actions.sequence(Actions.delay(1), new SingleAction() {
                        @Override
                        public void perform() {
                            Core.core.setScreen(new QuarantineScreen());
                        }
                    }));
                }
            }
        });
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
