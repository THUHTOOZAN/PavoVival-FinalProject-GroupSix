package com.g6.pavovival.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.g6.pavovival.actions.AssetController;

public class Hud {
    private final GlyphLayout layout = new GlyphLayout();
    private final AssetController assets;

    public Hud(AssetController assets) {
        this.assets = assets;
        if (assets.hudFont != null) {
            assets.hudFont.getData().markupEnabled = true;
            assets.hudFont.setUseIntegerPositions(true);
        }
    }

    public void draw(SpriteBatch batch, int level, int minions, int hp, int bossHP) {
        if (assets.hudFont == null) return;

        final float pad = 16f;

        // scale HUD font for screen width (snap to reduce blur)
        float screenScale = Gdx.graphics.getWidth() / 1280f;
        float s = 2.6f * screenScale;           // tweak this to taste
        s = Math.round(s * 2f) / 2f;            // snap to 0.5 steps
        assets.hudFont.getData().setScale(s);

        float topY  = Math.round(Gdx.graphics.getHeight() - pad);
        float line  = assets.hudFont.getLineHeight();
        float leftX = Math.round(pad);

        // LEFT column
        assets.hudFont.setColor(Color.WHITE);
        assets.hudFont.draw(batch, "STAGE : " + level, leftX, topY);

        float minionsY = Math.round(topY - line);
        assets.hudFont.draw(batch, "MINIONS : " + minions, leftX, minionsY);

        // RIGHT column (HP right-aligned)
        String hpText = "HP : " + hp;
        layout.setText(assets.hudFont, hpText);
        float hpX = Math.round(Gdx.graphics.getWidth() - pad - layout.width);
        Color hpColor = hp <= 2 ? Color.RED : (hp <= 5 ? Color.ORANGE : Color.LIME);
        assets.hudFont.setColor(hpColor);
        assets.hudFont.draw(batch, hpText, hpX, topY);

        // Boss HP (second right line)
        if (bossHP > 0) {
            String bossText = "FINAL BOSS HP : " + bossHP;
            layout.setText(assets.hudFont, bossText);
            float bossX = Math.round(Gdx.graphics.getWidth() - pad - layout.width);
            assets.hudFont.setColor(Color.WHITE);
            assets.hudFont.draw(batch, bossText, bossX, minionsY);
        }
    }
}
