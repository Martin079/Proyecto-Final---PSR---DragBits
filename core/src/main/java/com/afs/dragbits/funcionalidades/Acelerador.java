package com.afs.dragbits.funcionalidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.afs.dragbits.autos.Auto;

public class Acelerador {

    public void actualizar(Auto auto, float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            auto.acelerar(delta);
        } else {
            auto.desacelerar(delta);
        }
    }
}
