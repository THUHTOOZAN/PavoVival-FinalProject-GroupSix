package com.g6.pavovival;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.g6.pavovival.actions.AssetController;
import com.g6.pavovival.screens.GameScreen;

public class Main extends Game {
    public SpriteBatch batch;
    public AssetController assets;


    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new AssetController();
        assets.load();
        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
    }
}
