package com.g6.pavovival.actions;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.g6.pavovival.entities.*;


public class CollisionController {

    private final Rectangle r1 = new Rectangle();
    private final Rectangle r2 = new Rectangle();

    /** Player bullets hit enemies; report kills to LevelController. */
    public void playerBulletsVsEnemies(
        Array<Bullet> playerBullets,
        Array<Enemy> enemies,
        LevelController levels,
        Sound hitSfx
    ) {
        if (playerBullets == null || enemies == null || levels == null) return;

        for (int i = 0; i < playerBullets.size; i++) {
            Bullet b = playerBullets.get(i);
            if (b == null || !b.alive) continue;

            r1.set(b.bounds());

            for (int j = 0; j < enemies.size; j++) {
                Enemy e = enemies.get(j);
                if (e == null || !e.alive) continue;

                r2.set(e.bounds());

                if (r1.overlaps(r2)) {
                    // Apply damage and consume bullet
                    e.hit(b.damage());
                    b.alive = false;

                    // Play hit sound (safe)
                    if (hitSfx != null) {
                        try {
                            hitSfx.play(0.9f);
                        } catch (Exception ignored) {
                            // never crash if sound missing
                        }
                    }

                    if (!e.alive && (e instanceof Minion || e instanceof Bird)) {
                        levels.onMinionKilled();
                    }

                    break; // stop checking this bullet
                }
            }
        }
    }

    /** Boss bullets hit Pavo. */
    /** Boss (enemy) bullets hit Pavo. */
    public void enemyBulletsVsPavo(Array<Bullet> enemyBullets, Pavo p) {
        if (enemyBullets == null || p == null || !p.alive) return;

        r1.set(p.bounds());

        for (int i = 0; i < enemyBullets.size; i++) {
            Bullet b = enemyBullets.get(i);
            if (b == null || !b.alive) continue;

            r2.set(b.bounds());
            if (r1.overlaps(r2)) {
                b.alive = false;

                // use bullet damage if available, else 1
                int dmg = (b.damage() > 0) ? b.damage() : 1;
                p.hp -= dmg;

                // kill + clamp so HP never goes negative
                if (p.hp <= 0) {
                    p.hp = 0;
                    p.alive = false;     // Show Game Over
                }
            }
        }
    }

    public boolean enemiesVsPavo(Array<Enemy> enemies, Pavo p) {
        if (enemies == null || p == null) return false;

        r1.set(p.bounds());
        for (int i = 0; i < enemies.size; i++) {
            Enemy e = enemies.get(i);
            if (e == null || !e.alive) continue;

            r2.set(e.bounds());
            if (r1.overlaps(r2)) return true;
        }
        return false;
    }
}
