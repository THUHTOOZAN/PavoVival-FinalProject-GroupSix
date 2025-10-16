package com.g6.pavovival.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class Bird extends Enemy {
    private float vx = -110f;
    private float t  = 0f;
    private final float baseY;

    // set the on-screen height you want (match your minion height)
    private static final float TARGET_HEIGHT = 60f;

    public Bird(Texture texture, float x, float y) {
        super(texture, x, y, texture.getWidth(), texture.getHeight());
        this.hp = 3;
        this.baseY = y;

        // force render size regardless of source pixels
        this.h = TARGET_HEIGHT;
        this.w = TARGET_HEIGHT * (texture.getWidth() / (float) texture.getHeight());
    }

    @Override
    public void update(float dt) {
        t += dt;
        x += vx * dt;
        y = baseY + 20f * MathUtils.sin(6f * t);
        if (x + w < -16) alive = false;
    }
}
