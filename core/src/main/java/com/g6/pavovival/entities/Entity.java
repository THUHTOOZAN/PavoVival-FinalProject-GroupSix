package com.g6.pavovival.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Entity {

    public float x, y, w, h;
    public float vx, vy;
    public boolean alive = true;
    protected Texture tex;

    public Entity(Texture tex, float x, float y, float w, float h) {
        this.tex = tex; this.x = x; this.y = y; this.w = w; this.h = h;
    }

    public Rectangle bounds() {
        return new Rectangle(x, y, w, h);
    }

    public abstract void update(float dt);

    public void render(SpriteBatch batch) {
        if (alive && tex != null)
            batch.draw(tex, x, y, w, h);
    }
}
