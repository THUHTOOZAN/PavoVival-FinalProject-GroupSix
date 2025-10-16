package com.g6.pavovival.actions;

//  Handles wave logic, level progression, and boss conditions.

public class LevelController {

    public int level = 1;               // 1 → 2 → 3
    public int spawnedInWave = 0;       // spawned in current wave
    public int killedInWave  = 0;       // killed in current wave
    public int totalKills    = 0;
    public boolean bossSpawned = false;

    // === Dynamic wave size ===
    public int waveSize() {
        switch (level) {
            case 2: return 10;  // +5 over L1
            case 3: return 15;  // +10 over L1
            default: return 5;  // level 1
        }
    }

    // === Called when an enemy is spawned ===
    public void onMinionSpawned() {
        spawnedInWave++;
    }

    // === Called when an enemy (Minion or Bird) dies ===
    public void onMinionKilled() {
        killedInWave++;
        totalKills++;
    }

    // === Check if we can spawn more enemies ===
    public boolean canSpawnMoreThisWave() {
        return spawnedInWave < waveSize();
    }

    // === Check if all spawned enemies are dead ===
    public boolean isWaveCleared() {
        return killedInWave >= waveSize();
    }

    // === Move to next level when cleared ===
    public void advanceLevelIfCleared() {
        if (isWaveCleared() && level < 3) {
            level++;
            resetWave();
        }
    }

    // === After L3 cleared, trigger boss ===
    public boolean shouldSpawnBoss() {
        return (level == 3) && isWaveCleared() && !bossSpawned;
    }

    // === Reset counters for next wave/level ===
    public void resetWave() {
        spawnedInWave = 0;
        killedInWave = 0;
    }
}
