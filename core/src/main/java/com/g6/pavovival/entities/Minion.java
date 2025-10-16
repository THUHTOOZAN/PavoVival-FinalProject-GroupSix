package com.g6.pavovival.entities;

import com.badlogic.gdx.graphics.Texture;

public class Minion extends Enemy {
    private float speed = 75f;          // slower
    private float gravity = -900f;

    public Minion(Texture tex, float x, float y) {
        super(tex, x, y, 110, 110);       // bigger
        this.hp = 1;
    }

    @Override
    public void update(float dt) {
        vy += gravity * dt;
        y += vy * dt;
        if (y <= 0) { y = 0; vy = 0; }
        x += vx * dt;
    }

    public void steerToward(Pavo p) {
        vx = (p.x > x) ? speed : -speed;
    }
}
