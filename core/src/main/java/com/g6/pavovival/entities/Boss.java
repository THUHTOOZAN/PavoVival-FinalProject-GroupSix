package com.g6.pavovival.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class Boss extends Enemy {
    private float speed   = 60f;
    private float gravity = -900f;
    private float jumpVel = 400f;
    private float jumpTimer = 0f;

    // shooting
    private float shootTimer = 0f;
    private float shootInterval = 1.1f; // seconds

    public Boss(Texture tex, float x, float y) {
        super(tex, x, y, 248, 248); // bigger
        this.hp = 5;
    }

    @Override
    public void update(float dt) {
        vy += gravity * dt;
        y  += vy * dt;
        if (y <= 0) { y = 0; vy = 0; }
        x  += vx * dt;

        if (jumpTimer > 0)  jumpTimer  -= dt;
        if (shootTimer > 0) shootTimer -= dt;
    }

    public void pursue(Pavo p) {
        vx = (p.x > x) ? speed : -speed;
        if (jumpTimer <= 0 && Math.random() < 0.004) {
            if (y == 0) { vy = jumpVel; jumpTimer = 1.5f; }
        }
    }

    // shoot toward pavo
    public void maybeShoot(Array<Bullet> enemyBullets, Texture bossBulletTex, Pavo target) {
        if (shootTimer > 0) return;
        int dir = (target.x < this.x) ? -1 : 1; // shoot toward Pavo
        Bullet b = Bullet.enemyBullet(bossBulletTex,
            this.x + (dir == 1 ? this.w : -8),
            this.y + this.h * 0.55f,
            dir);

        enemyBullets.add(b);
        shootTimer = shootInterval;
    }
}
