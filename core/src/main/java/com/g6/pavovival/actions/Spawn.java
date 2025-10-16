package com.g6.pavovival.actions;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.g6.pavovival.entities.Enemy;
import com.g6.pavovival.entities.Minion;
import com.g6.pavovival.entities.Pavo;
import com.g6.pavovival.entities.Bird;

/**
 * Spawns ground minions and (from L2) flying birds.
 */
public class Spawn {

    private float spawnTimer = 0f;
    private float spawnInterval = 1.1f;   // seconds between spawns

    private final AssetController assets;
    private final LevelController levels;

    public Spawn(AssetController assets, LevelController levels) {
        this.assets = assets;
        this.levels = levels;
    }

    // limit active enemies based on level for difficulty curve
    private int maxActiveForLevel(int level) {
        switch (level) {
            case 2: return 4;
            case 3: return 5;
            default: return 3;
        }
    }

    public void update(float dt, Array<Enemy> enemies, Pavo pavo) {
        // === 1. Stop spawning if it's boss time ===
        if (levels.shouldSpawnBoss()) return;

        // === 2. Count current active minions + birds ===
        int active = 0;
        for (Enemy e : enemies)
            if (e.alive && (e instanceof Minion || e instanceof Bird)) active++;

        spawnTimer -= dt;
        final int cap = maxActiveForLevel(levels.level);

        boolean canSpawn = levels.canSpawnMoreThisWave()
            && active < cap
            && spawnTimer <= 0f;

        // === 3. Spawn logic ===
        if (canSpawn) {
            boolean spawnBird = (levels.level >= 2) && MathUtils.randomBoolean(0.45f); // ~45% birds for L2/L3

            if (spawnBird && assets.birdTexture != null) {
                // spawn bird in higher lane so Pavo must jump-shoot
                float startX = 980f; // right edge
                float laneY = MathUtils.random(140f, 220f);
                enemies.add(new Bird(assets.birdTexture, startX, laneY));
            } else {
                // spawn ground minion
                enemies.add(new Minion(assets.minionTexture, 900, 0));
            }

            levels.onMinionSpawned();
            spawnTimer = spawnInterval;
        }

        // === 4. Update enemy steering ===
        for (Enemy e : enemies) {
            if (e instanceof Minion && e.alive)
                ((Minion) e).steerToward(pavo);
        }
    }
}
