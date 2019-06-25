package com.ray3k.interstellarscum;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;

public class Core extends Game {
	public static Core core;
	public static AssetManager assetManager;
	public static Skin skin;
	public static TextureAtlas textureAtlas;
	public static SkeletonRenderer skeletonRenderer;
	public static float sfxVolume;
	public static float bgmVolume;
	public static Array<String> infectedCrew;
	public static Array<String> stasisCrew;
	public static Array<String> livingCrew;
	public static Array<String> hostCrew;
	public static Array<String> doctorCrew;
	public static Array<String> detectiveCrew;
	public static Array<String> liarCrew;
	public static Array<String> loyalistCrew;
	public static String player;
	public static Array<String> names;
	public static Array<String> accusations;
	public static Array<String> neutrals;
	public static ObjectMap<String, Array<String>> crewToVoteMap;
	
	@Override
	public void create () {
		//set variables
		core = this;
		assetManager = new AssetManager(new InternalFileHandleResolver());
		assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(assetManager.getFileHandleResolver()));
		skeletonRenderer = new SkeletonRenderer();
		skeletonRenderer.setPremultipliedAlpha(false);
		sfxVolume = 1.0f;
		bgmVolume = 1.0f;
		
		//load assets
		assetManager.load("skin/interstellar-scum.json", Skin.class);
		
		SkeletonDataLoader.SkeletonDataLoaderParameter parameter = new SkeletonDataLoader.SkeletonDataLoaderParameter("skin/interstellar-scum.atlas");
		assetManager.load("spine/intro.json", SkeletonData.class, parameter);
		assetManager.load("spine/person.json", SkeletonData.class, parameter);
		assetManager.load("spine/space.json", SkeletonData.class, parameter);
		assetManager.load("spine/splash.json", SkeletonData.class, parameter);
		
		assetManager.load("sfx/blast.mp3", Sound.class);
		assetManager.load("sfx/concede.mp3", Sound.class);
		assetManager.load("sfx/confirm.mp3", Sound.class);
		assetManager.load("sfx/fart.mp3", Sound.class);
		assetManager.load("sfx/hibernate.mp3", Sound.class);
		assetManager.load("sfx/vomit.mp3", Sound.class);
		assetManager.load("sfx/libgdx.mp3", Sound.class);
		assetManager.load("sfx/bubble.mp3", Sound.class);
		
		assetManager.finishLoading();
		
		skin = assetManager.get("skin/interstellar-scum.json");
		textureAtlas = assetManager.get("skin/interstellar-scum.atlas");
		
		String text = Gdx.files.internal("data/names.txt").readString();
		names = new Array<String>(text.split("\\n"));
		
		text = Gdx.files.internal("data/accusations.txt").readString();
		accusations = new Array<String>(text.split("\\n"));
		
		text = Gdx.files.internal("data/neutrals.txt").readString();
		neutrals = new Array<String>(text.split("\\n"));
		
		//set screen
		setScreen(new MenuScreen());
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}
	
	@Override
	public void dispose () {
		super.dispose();
		
		assetManager.dispose();
	}
	
	public Sound playSound(String name, float volume) {
		Sound sound = assetManager.get("sfx/" + name + ".mp3");
		sound.play(sfxVolume * volume);
		return sound;
	}
	
	public Music playMusic(String name, float volume) {
		Music music = assetManager.get("bgm/" + name + ".mp3");
		music.setVolume(bgmVolume * volume);
		music.play();
		return music;
	}
}
