package com.g6.pavovival.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Sound;

import com.g6.pavovival.Main;
import com.g6.pavovival.actions.*;
import com.g6.pavovival.effects.Hud;
import com.g6.pavovival.entities.*;

import javax.print.AttributeException;

public class GameScreen implements Screen {

    private int lastLevelMusic = -1;
    private boolean bossMusicOn = false;

    private final Main game;

    private static final float WORLD_W = 960f;
    private static final float WORLD_H = 540f;

    private OrthographicCamera camera;
    private Viewport viewport;
    private final GlyphLayout layout = new GlyphLayout();

    // fx
    private float t = 0f;
    private float bgX = 0f;
    private float bgSpeed = 0.5f;       // parallax speed
    private float shakeTime = 0f;      // camera shake timer
    private float shakeStrength = 3f;

    // game objects
    private Pavo pavo;
    private Array<Enemy> enemies = new Array<>();
    private Array<Bullet> playerBullets = new Array<>();
    private Array<Bullet> enemyBullets  = new Array<>();

    private LevelController levels = new LevelController();
    private Spawn spawner;
    private CollisionController collisions = new CollisionController();
    private InputController input = new InputController();
    private Hud hud;
    private Boss boss = null;

    public enum State { PLAYING, PAUSED, WIN, GAME_OVER }
    private State state = State.PLAYING;

    public GameScreen(Main game) {
        this.game = game;
        this.pavo = new Pavo(game.assets.pavoTexture, 40, 0);
        this.spawner = new Spawn(game.assets, levels);
        this.hud = new Hud(game.assets);

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_W, WORLD_H, camera);
        camera.position.set(WORLD_W * 0.5f, WORLD_H * 0.5f, 0f);
        camera.update();
    }

    private void drawRetroGlow(String text, Color glow, float radius) {
        BitmapFont f = game.assets.retroFont;
        layout.setText(f, text);
        float cx = (WORLD_W - layout.width) / 2f;
        float cy = (WORLD_H + layout.height) / 2f;

        f.setColor(glow.r, glow.g, glow.b, 0.35f);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                f.draw(game.batch, text, cx + dx * radius, cy + dy * radius);
            }
        }
    }

    // left" and right" words with different colors, centered together
    private void drawCenteredRetroTwoTone(String left, Color leftColor,
                                          String right, Color rightColor,
                                          float baseScale, float wobbleY) {
        BitmapFont f = game.assets.retroFont;

        // total width
        layout.setText(f, left + right);
        float cx = (WORLD_W - layout.width) / 2f;
        float cy = (WORLD_H + layout.height) / 2f + wobbleY;

        f.getData().setScale(baseScale);

        // left part
        f.setColor(leftColor);
        f.draw(game.batch, left, cx, cy);

        // right part immediately after left
        GlyphLayout leftW = new GlyphLayout(f, left);
        float x2 = cx + leftW.width;
        f.setColor(rightColor);
        f.draw(game.batch, right, x2, cy);
    }

    @Override
    public void render(float delta) {
        t += delta;
        float dt = Math.min(delta, 1/30f);

        // inputs: pause, movement, jump
        input.handle(pavo);
        if (input.paused && state == State.PLAYING) state = State.PAUSED;
        else if (!input.paused && state == State.PAUSED) state = State.PLAYING;

        if (state == State.PLAYING) {
            // Player
            pavo.update(dt);

            // spawn minions
            spawner.update(dt, enemies, pavo);

            // advance level if cleared
            levels.advanceLevelIfCleared();
            int currentLevel = (levels.level < 1 ? 1 : levels.level);

            if (!bossMusicOn && currentLevel != lastLevelMusic) {
                game.assets.playLevelMusic(currentLevel);
                lastLevelMusic = currentLevel;
            }

            // spawn boss after level 3 cleared
            if (levels.shouldSpawnBoss() && boss == null) {
                boss = new Boss(game.assets.bossyTexture, 820, 0);
                levels.bossSpawned = true;
                enemies.add(boss);

                bossMusicOn = true;

                if (game.assets.sfxBossRoar != null) game.assets.sfxBossRoar.play(0.9f);

                game.assets.playBossMusic();
            }

            // Enemies update
            for (Enemy e : enemies)
                if (e.alive) {
                    if (e instanceof Boss) {
                        Boss b = (Boss) e;
                        b.pursue(pavo);
                        b.maybeShoot(enemyBullets, game.assets.bossBulletTexture, pavo);
                    }
                    e.update(dt);
                }

            // Pavo shoot (F)
            if (Gdx.input.isKeyJustPressed(Input.Keys.F) && pavo.canShoot()) {
                Bullet b = Bullet.playerBullet(
                    game.assets.bulletTexture,
                    pavo.x + (pavo.facing == 1 ? pavo.w : -8),
                    pavo.y + pavo.h * 0.6f,
                    pavo.facing
                );
                playerBullets.add(b);
                pavo.didShoot();

                if (game.assets.sfxShoot != null)
                    game.assets.sfxShoot.play(0.8f);
            }

            // Bullets
            for (int i = 0; i < playerBullets.size; i++) playerBullets.get(i).update(dt);
            for (int i = 0; i < enemyBullets.size; i++) enemyBullets.get(i).update(dt);

            // Collisions
            collisions.playerBulletsVsEnemies(playerBullets, enemies, levels, game.assets.sfxHit);
            collisions.enemyBulletsVsPavo(enemyBullets, pavo);

// Touch damage (existing)
            if (collisions.enemiesVsPavo(enemies, pavo)) {
                pavo.hp -= 1;
                pavo.x = Math.max(0, pavo.x - 50);
                shakeTime = 0.20f; // trigger screenshake
            }

// === ADDED: single place to decide death from ANY source (bullets or touch) ===
            if (pavo.hp <= 0) {
                triggerGameOver();  // handles music + state + clamp
            }

            // cleanup dead
            for (int i = playerBullets.size - 1; i >= 0; i--)
                if (!playerBullets.get(i).alive) playerBullets.removeIndex(i);
            for (int i = enemyBullets.size - 1; i >= 0; i--)
                if (!enemyBullets.get(i).alive) enemyBullets.removeIndex(i);
            for (int i = enemies.size - 1; i >= 0; i--) if (!enemies.get(i).alive) enemies.removeIndex(i);

            // parallax
            bgX -= bgSpeed * dt;
            if (bgX <= -WORLD_W) bgX += WORLD_W;

            // decay screenshake
            if (shakeTime > 0f) shakeTime -= dt;

            // WIN check
            if (boss != null && !boss.alive && state != State.WIN) {
                state = State.WIN;
                bossMusicOn = false;
                game.assets.playWinMusic();
            }
        }

        // apply screenshake to camera
        if (shakeTime > 0f) {
            camera.position.set(
                WORLD_W * 0.5f + MathUtils.random(-shakeStrength, shakeStrength),
                WORLD_H * 0.5f + MathUtils.random(-shakeStrength, shakeStrength),
                0f);
        } else {
            camera.position.set(WORLD_W * 0.5f, WORLD_H * 0.5f, 0f);
        }
        camera.update();

        // Render
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        drawBackground();
        pavo.render(game.batch);
        for (Enemy e : enemies) e.render(game.batch);
        for (int i = 0; i < playerBullets.size; i++) playerBullets.get(i).render(game.batch);
        for (int i = 0; i < enemyBullets.size;  i++) enemyBullets.get(i).render(game.batch);

        int bossHP = boss != null && boss.alive ? boss.hp : -1;
        hud.draw(game.batch, levels.level, levels.totalKills, pavo.hp, bossHP);

        // Retro banners
        if (state == State.GAME_OVER) {
            float wobbleY = 4f * (float)Math.sin(t * 6.0);
            drawRetroGlow("GAME OVER", new Color(1f, 0.5f, 0f, 1f), 2.5f);
            drawCenteredRetroTwoTone("GAME ", Color.ORANGE, "OVER", Color.YELLOW, 3.0f, wobbleY);
        } else if (state == State.WIN) {
            float wobbleY = 4f * (float)Math.sin(t * 6.0);
            drawRetroGlow("YOU WIN!", new Color(0.2f, 1f, 0.4f, 1f), 2.5f);
            drawCenteredRetroTwoTone("YOU ", Color.LIME, "WIN!", Color.GREEN, 3.0f, wobbleY);
        } else if (state == State.PAUSED) {
            float wobbleY = 4f * (float)Math.sin(t * 6.0);
            drawRetroGlow("PAUSED", new Color(0.2f, 0.9f, 1f, 1f), 2.0f);
            drawCenteredRetroTwoTone("PAUS", Color.CYAN, "ED", Color.WHITE, 2.5f, wobbleY);
        }


        game.batch.end();
    }

    private void drawBackground() {
        Texture bg = game.assets.lvOne;
        if (levels.level == 2) bg = game.assets.lvTwo;
        if (levels.level == 3) bg = game.assets.lvThree;

        game.batch.draw(bg, bgX, 0, WORLD_W, WORLD_H);
        game.batch.draw(bg, bgX + WORLD_W, 0, WORLD_W, WORLD_H);
    }

    // === ADDED: central Game Over handler ===
    private void triggerGameOver() {
        if (state == State.GAME_OVER) return;   // fire once
        state = State.GAME_OVER;
        bossMusicOn = false;

        // stop any level/boss music and play Game Over
        try {
            game.assets.stopAllMusic(); // if you have such helper; else stop individually
        } catch (Exception ignored) {}

        try {
            game.assets.playGameOverMusic();
        } catch (Exception ignored) {}

        // clamp HP (HUD won’t show negative)
        if (pavo.hp < 0) pavo.hp = 0;
    }

    @Override public void show() {
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        int startLevel = (levels.level < 1 ? 1 : levels.level);
        game.assets.playLevelMusic(startLevel);
        lastLevelMusic = startLevel;
        bossMusicOn = false;
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
