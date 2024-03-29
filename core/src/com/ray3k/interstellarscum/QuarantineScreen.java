package com.ray3k.interstellarscum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.Slot;
import com.esotericsoftware.spine.attachments.PointAttachment;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import regexodus.Matcher;
import regexodus.Pattern;

import java.util.Iterator;

public class QuarantineScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private boolean showDiscussion;
    private ParticleEffect particleEffect;
    
    @Override
    public void show() {
        Person.accusedList.clear();
        
        final Array<Person> livingCrew = new Array<Person>();
        for (Person person : Core.crew) {
            if (person.mode == Person.Mode.ALIVE) {
                livingCrew.add(person);
                person.accusation = null;
                person.vote = null;
            }
        }
        showDiscussion = livingCrew.size == 15;
        
        final Array<Person> uninfectedCrew = new Array<Person>(livingCrew);
        Iterator<Person> iter = uninfectedCrew.iterator();
        while (iter.hasNext()) {
            Person person = iter.next();
            if (person.type == Person.Type.HOST) {
                iter.remove();
            }
        }
        
        Person infected = uninfectedCrew.random();
        
        if (livingCrew.size != 15) for (Person person : livingCrew) {
            if (person.type == Person.Type.DOCTOR) {
                Array<Person> others = new Array<Person>(livingCrew);
                others.removeValue(person, false);
                
                if (infected.equals(others.random())) {
                    infected = null;
                    break;
                }
            }
        }
        
        if (infected != null) {
            infected.mode = Person.Mode.SICK;
            uninfectedCrew.removeValue(infected, false);
            Core.daysToComplete += Core.SINGLE_PERSON_WORK;
        }
        
        for (Person person : uninfectedCrew) {
            if (person.type == Person.Type.LIAR || person.type == Person.Type.DETECTIVE) {
                person.chooseAccusation();
            }
        }
    
        for (Person person : uninfectedCrew) {
            if (person.type != Person.Type.LIAR && person.type != Person.Type.DETECTIVE && person.type != Person.Type.NORMAL) {
                person.chooseAccusation();
            }
        }
    
        for (Person person : uninfectedCrew) {
            if (person.type == Person.Type.NORMAL) {
                person.chooseAccusation();
            }
        }
        
        particleEffect = Core.assetManager.get("particles/barf.p");
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        skin = Core.skin;
    
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    
        root.pad(20);
        String text;
        if (infected != null || livingCrew.size == 0) {
            text = "{EASE}" + (infected != null ? infected.name : Core.player) + " is sick and has been quarantined! The telltale signs of infection are abundant.{ENDEASE}";
        } else {
            text = "{EASE}Nothing of interest has happened since the last jump!{ENDEASE}";
        }
        final TypingLabel typingLabel = new TypingLabel(text, skin);
        root.add(typingLabel);
        typingLabel.pause();
        
        root.row();
        SpineDrawable.SpineDrawableTemplate template = new SpineDrawable.SpineDrawableTemplate();
        final SpineDrawable spineDrawable = new SpineDrawable(Core.assetManager.get("spine/person.json", SkeletonData.class), Core.skeletonRenderer, template);
        if (infected != null || livingCrew.size == 0) {
            spineDrawable.getAnimationState().setAnimation(0, "sick", true);
        } else {
            spineDrawable.getAnimationState().setAnimation(0, "stand", true);
        }
        Image image = new Image(spineDrawable);
        root.add(image).expand();
        spineDrawable.getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
                Pattern pattern = new Pattern(".*(?=\\.)");
                Matcher matcher = pattern.matcher(event.getData().getAudioPath());
                
                if (matcher.find()) {
                    Core.core.playSound(matcher.group(), 1);
                    
                    if (matcher.group().equals("vomit")) {
                        Slot slot = spineDrawable.getSkeleton().findSlot("barf");
                        PointAttachment attachment = (PointAttachment) slot.getAttachment();
                        Vector2 result = new Vector2();
                        attachment.computeWorldPosition(slot.getBone(), result);
    
                        particleEffect.start();
                        particleEffect.setPosition(result.x, result.y);
                    }
                }
            }
        });
        
        root.row();
        TextButton textButton = new TextButton("CONTINUE", skin, "small");
        root.add(textButton).minWidth(300f);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Core.core.playSound("confirm",1);
                Gdx.input.setInputProcessor(null);
                spineDrawable.getAnimationState().setAnimation(1, "hide", false);
                stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.delay(.25f), new SingleAction() {
                    @Override
                    public void perform() {
                        if (showDiscussion) {
                            Core.core.setScreen(new DiscussionScreen());
                        } else if (uninfectedCrew.size > 0){
                            Core.core.setScreen(new GameScreen());
                        } else {
                            Core.core.setScreen(new ResultScreen());
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
        
        stage.act(delta);
        stage.draw();
        
        stage.getBatch().begin();
        particleEffect.draw(stage.getBatch(), delta);
        stage.getBatch().end();
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
