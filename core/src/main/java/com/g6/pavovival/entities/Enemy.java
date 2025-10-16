package com.g6.pavovival.entities;

import com.badlogic.gdx.graphics.Texture;

public abstract class Enemy extends Entity {
    public int hp = 1;
    public Enemy (Texture texture, float x, float y, float width, float height) {
        super (texture,x, y, width, height);
    }
    public void hit (int damage) {
        hp -= damage;
        if (hp <= 0)
            alive = false;
    }
}

