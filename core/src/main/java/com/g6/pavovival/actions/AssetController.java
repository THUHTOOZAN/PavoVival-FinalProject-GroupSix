package com.g6.pavovival.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AssetController {
    private Music currentMusic = null;
    // --- Textures ---
    public Texture lvOne, lvTwo, lvThree;
    public Texture pavoTexture, minionTexture, bossyTexture;
    public Texture bulletTexture, bossBulletTexture;

    public Texture birdTexture;

    // --- Fonts ---
    public BitmapFont font;        // fallback / debug
    public BitmapFont retroFont;   // big banner font (GAME OVER / YOU WIN / PAUSED)
    public BitmapFont hudFont;     // crisp HUD font (STAGE / MINIONS / HP)

    public Music musicLv1, musicLv2, musicLv3, musicBoss, musicGameOver, musicWin;
    public Sound sfxShoot, sfxHit, sfxBossRoar;

    public void load() {
        // Backgrounds
        lvOne = new Texture(Gdx.files.internal("lvOne.png"));
        lvTwo = new Texture(Gdx.files.internal("lvTwo.png"));
        lvThree = new Texture(Gdx.files.internal("lvThree.png"));

        // Characters
        pavoTexture   = new Texture(Gdx.files.internal("pavo.png"));
        minionTexture = new Texture(Gdx.files.internal("minion.png"));
        bossyTexture  = new Texture(Gdx.files.internal("bossy.png"));

        // Bullets
        bulletTexture     = new Texture(Gdx.files.internal("pavo_bullet.png"));
        bossBulletTexture = new Texture(Gdx.files.internal("boss_bullet.png"));

        // bird
        birdTexture = new Texture("bird.png");

        // Default font (fallback)
        font = new BitmapFont();
        font.getData().markupEnabled = true;

        // === Retro banner font ===
        final String retroPath = "Pixeloid.ttf";   // make sure this is in assets/
        if (Gdx.files.internal(retroPath).exists()) {
            FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(retroPath));
            FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
            p.size = 64; // big and readable
            p.color = Color.ORANGE;
            p.borderWidth = 3f;
            p.borderColor = Color.BLACK;
            p.shadowOffsetX = 3;
            p.shadowOffsetY = 3;
            p.shadowColor   = new Color(0, 0, 0, 0.8f);
            p.minFilter = Texture.TextureFilter.Nearest;
            p.magFilter = Texture.TextureFilter.Nearest;
            retroFont = gen.generateFont(p);
            retroFont.getData().markupEnabled = true;   // safe even if we don’t use tags
            retroFont.setUseIntegerPositions(true);
            gen.dispose();
        } else {
            retroFont = new BitmapFont();
            Gdx.app.log("Assets", "Retro TTF missing at " + retroPath + " — using default BitmapFont.");
        }

        // === HUD font ===
        final String hudPath = "Pixeloid.ttf";
        if (Gdx.files.internal(hudPath).exists()) {
            FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(hudPath));
            FreeTypeFontGenerator.FreeTypeFontParameter hud = new FreeTypeFontGenerator.FreeTypeFontParameter();
            hud.size = 40;                      // bump to taste (32–48)
            hud.color = Color.WHITE;
            hud.borderWidth = 2f;
            hud.borderColor = new Color(0, 0, 0, 0.9f);
            hud.shadowOffsetX = 1;
            hud.shadowOffsetY = 1;
            hud.shadowColor   = new Color(0, 0, 0, 0.5f);
            hud.minFilter = Texture.TextureFilter.Nearest;
            hud.magFilter = Texture.TextureFilter.Nearest;
            hudFont = gen.generateFont(hud);
            hudFont.getData().markupEnabled = true;
            hudFont.setUseIntegerPositions(true);
            gen.dispose();
        } else {
            hudFont = new BitmapFont();
            Gdx.app.log("Assets", "HUD TTF missing at " + hudPath + " — using default BitmapFont.");
        }

        musicLv1     = Gdx.audio.newMusic(Gdx.files.internal("audio/level1.wav"));
        musicLv2     = Gdx.audio.newMusic(Gdx.files.internal("audio/level2.mp3"));
        musicLv3     = Gdx.audio.newMusic(Gdx.files.internal("audio/level3.mp3"));
        musicBoss    = Gdx.audio.newMusic(Gdx.files.internal("audio/boss.wav"));
        musicGameOver= Gdx.audio.newMusic(Gdx.files.internal("audio/gameover.wav"));
        musicWin     = Gdx.audio.newMusic(Gdx.files.internal("audio/win.wav"));

        for (Music m : new Music[]{musicLv1, musicLv2, musicLv3, musicBoss}) {
            m.setLooping(true);
            m.setVolume(0.8f); // 0.0–1.0
        }
        musicGameOver.setLooping(false);
        musicWin.setLooping(false);
        musicGameOver.setVolume(0.9f);
        musicWin.setVolume(0.9f);

// SFX
        sfxShoot    = Gdx.audio.newSound(Gdx.files.internal("audio/shoot.wav"));
        sfxHit      = Gdx.audio.newSound(Gdx.files.internal("audio/hit.wav"));
        sfxBossRoar = Gdx.audio.newSound(Gdx.files.internal("audio/roar.wav")); // optional
    }

    public void stopAllMusic() {
        if (musicLv1 != null) musicLv1.stop();
        if (musicLv2 != null) musicLv2.stop();
        if (musicLv3 != null) musicLv3.stop();
        if (musicBoss != null) musicBoss.stop();
        if (musicGameOver != null) musicGameOver.stop();
        if (musicWin != null) musicWin.stop();
    }

    public void playLevelMusic(int level) {
        // sanitize: treat 0 or out-of-range as level 1
        int lvl = (level < 1 || level > 3) ? 1 : level;
        Gdx.app.log("MUSIC", "Level " + lvl);

        stopAllMusic();
        if (lvl == 1 && musicLv1 != null)      musicLv1.play();
        else if (lvl == 2 && musicLv2 != null) musicLv2.play();
        else if (lvl == 3 && musicLv3 != null) musicLv3.play();
    }

    public void playBossMusic()     { stopAllMusic(); if (musicBoss != null)    musicBoss.play(); }
    public void playGameOverMusic() { stopAllMusic(); if (musicGameOver != null) musicGameOver.play(); }
    public void playWinMusic()      { stopAllMusic(); if (musicWin != null)      musicWin.play(); }


    public void dispose() {
        if (lvOne != null) lvOne.dispose();
        if (lvTwo != null) lvTwo.dispose();
        if (lvThree != null) lvThree.dispose();
        if (pavoTexture != null) pavoTexture.dispose();
        if (minionTexture != null) minionTexture.dispose();
        if (bossyTexture != null) bossyTexture.dispose();
        if (bulletTexture != null) bulletTexture.dispose();
        if (bossBulletTexture != null) bossBulletTexture.dispose();

        if (font != null) font.dispose();
        if (retroFont != null) retroFont.dispose();
        if (hudFont != null) hudFont.dispose();
        if (musicLv1 != null) musicLv1.dispose();
        if (musicLv2 != null) musicLv2.dispose();
        if (musicLv3 != null) musicLv3.dispose();
        if (musicBoss != null) musicBoss.dispose();
        if (musicGameOver != null) musicGameOver.dispose();
        if (musicWin != null) musicWin.dispose();

        if (sfxShoot != null) sfxShoot.dispose();
        if (sfxHit != null) sfxHit.dispose();
        if (sfxBossRoar != null) sfxBossRoar.dispose();
    }
}
