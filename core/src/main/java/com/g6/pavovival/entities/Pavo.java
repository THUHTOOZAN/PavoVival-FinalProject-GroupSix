package com.g6.pavovival.entities;

import com.badlogic.gdx.graphics.Texture;

public class Pavo extends Entity {
    public int hp = 3;
    public boolean onGround = true;
    private float moveSpeed = 220f;
    private float jumpVel   = 450f;
    private float gravity   = -900f;
    private float shootCooldown = 0.15f;
    private float shootTimer = 0f;
    public int facing = 1;

    public boolean isDead(){
        return !alive || hp <= 0;
    }
    public Pavo(Texture tex, float x, float y) {
        super(tex, x, y, 144, 144); // bigger
    }

    @Override
    public void update(float dt) {
        vy += gravity * dt;
        x  += vx * dt;
        y  += vy * dt;
        if (y <= 0) { y = 0; vy = 0; onGround = true; }
        if (shootTimer > 0) shootTimer -= dt;
    }

    public void moveLeft()  {
        vx = -moveSpeed;
        facing = -1;
    }
    public void moveRight() {
        vx =  moveSpeed;
        facing =  1;
    }
    public void stopX()     {
        vx = 0;
    }
    public void jump()      {
        if (onGround) {
            vy = jumpVel; onGround = false;
        }
    }

    public boolean canShoot() {
        return shootTimer <= 0;
    }
    public void didShoot()  {
        shootTimer = shootCooldown;
    }
}
