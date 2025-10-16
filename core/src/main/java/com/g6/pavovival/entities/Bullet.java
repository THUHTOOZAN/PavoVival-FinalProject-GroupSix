package com.g6.pavovival.entities;

import com.badlogic.gdx.graphics.Texture;

public class Bullet extends Entity {
    private float speed = 520f;
    private int damage = 1;
    public boolean fromEnemy = false;

    private Bullet(Texture tex, float x, float y, int facing, boolean fromEnemy) {
        super(tex, x, y, 20, 12);
        this.fromEnemy = fromEnemy;
        vx = speed * facing;
    }

    // pavo
    public static Bullet playerBullet(Texture tex, float x, float y, int facing) {
        return new Bullet(tex, x, y, facing, false);
    }

    // boss
    public static Bullet enemyBullet(Texture tex, float x, float y, int facing) {
        return new Bullet(tex, x, y, facing, true);
    }

    @Override
    public void update(float dt) {
        x += vx * dt;
        if (x < -100 || x > 2000) alive = false;
    }

    public int damage() { return damage; }
}
