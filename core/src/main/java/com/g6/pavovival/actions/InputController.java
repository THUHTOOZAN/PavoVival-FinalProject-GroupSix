package com.g6.pavovival.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.g6.pavovival.entities.Pavo;

public class InputController {

    public boolean paused = false;

    public void handle (Pavo pavo) {
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (left && !right) pavo.moveLeft();
        else if (right && !left) pavo.moveRight();
        else pavo.stopX();

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.SPACE))
            pavo.jump();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P))
            paused = !paused;
    }
}
