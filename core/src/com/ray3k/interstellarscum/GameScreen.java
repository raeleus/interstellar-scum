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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;

public class GameScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private int scrollIndex;
    private final float SCROLL_WIDTH = 700;
    
    @Override
    public void show() {
        final Array<Person> livingCrew = new Array<Person>();
        for (Person person : Core.crew) {
            if (person.mode == Person.Mode.ALIVE) {
                livingCrew.add(person);
            }
        }
        
        scrollIndex = 0;
        stage = new Stage(new ScreenViewport(), new TwoColorPolygonBatch());
        Gdx.input.setInputProcessor(stage);
        skin = Core.skin;
    
        final Table root = new Table();
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
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                scrollIndex--;
                if (scrollIndex < 0) scrollIndex = 0;
                ScrollPane scrollPane = root.findActor("scrollPane");
                scrollPane.setScrollX(scrollIndex * scrollPane.getWidth());
            }
        });
        
        final Table scrollTable = new Table();
        final ScrollPane scrollPane = new ScrollPane(scrollTable);
        scrollPane.setName("scrollPane");
        scrollPane.setTouchable(Touchable.disabled);
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
                Stack stack = new Stack();
                scrollTable.add(stack).width(SCROLL_WIDTH);
                
                SpineDrawable.SpineDrawableTemplate template = new SpineDrawable.SpineDrawableTemplate();
                SpineDrawable spineDrawable = new SpineDrawable(Core.assetManager.get("spine/person.json", SkeletonData.class), Core.skeletonRenderer, template);
                spineDrawable.getAnimationState().setAnimation(0, "stand", true);
                image = new Image(spineDrawable);
                image.setScaling(Scaling.none);
                image.setAlign(Align.center);
                stack.add(image);
                
                Table subTable = new Table();
                stack.add(subTable);
    
                String text;
                if (person.accusation != null) {
                    text = Core.accusations.random();
                    text = text.replace("<name>", person.accusation.name);
                } else {
                    text = Core.neutrals.random();
                }
                Label label = new Label(text, skin, "accusation");
                label.setWrap(true);
                label.setAlignment(Align.center);
                subTable.add(label).expandY().bottom().minHeight(49).width(SCROLL_WIDTH);
            }
        }
        
        imageButton = new ImageButton(skin, "right");
        table.add(imageButton).growY();
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                scrollIndex++;
                if (scrollIndex >= livingCrew.size) scrollIndex = livingCrew.size - 1;
                scrollPane.setScrollX(scrollIndex * scrollPane.getWidth());
            }
        });
        
        root.row();
        table = new Table();
        root.add(table);
    
        ProgressBar progressBar = new ProgressBar(0, Core.daysToComplete, 1, false, skin);
        progressBar.setValue(Core.daysToComplete / livingCrew.size);
        table.add(progressBar);
        
        table.row();
        Label label = new Label("Days to arrival: " + (Core.daysToComplete - Core.daysToComplete / livingCrew.size + 1), skin);
        table.add(label);
        
        TextButton textButton = new TextButton("VOTE", skin);
        root.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Person.playerVote = livingCrew.get(scrollIndex);
                if (!Person.accusedList.contains(Person.playerVote, false)) {
                    Person.accusedList.add(Person.playerVote);
                }
                Array<Person> targetList = new Array<Person>(Person.accusedList);
                for (Person person : livingCrew) {
                    if (person.type == Person.Type.HOST) {
                        targetList.removeValue(person, false);
                    }
                }
                Person.hostTarget = targetList.random();
                
                for (Person person : livingCrew) {
                    person.chooseVote();
                }
                
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
