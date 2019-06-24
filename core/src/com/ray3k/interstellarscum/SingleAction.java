package com.ray3k.interstellarscum;

import com.badlogic.gdx.scenes.scene2d.Action;

public abstract class SingleAction extends Action {
    @Override
    public boolean act(float delta) {
        perform();
        return true;
    }
    
    public abstract void perform();
}
