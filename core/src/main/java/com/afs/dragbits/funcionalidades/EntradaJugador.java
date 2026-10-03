package com.afs.dragbits.funcionalidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EntradaJugador extends InputAdapter {


    private int teclaAcelerador = Input.Keys.W;
    private int teclaEmbrague = Input.Keys.SPACE;
    private int teclaPalancaArriba = Input.Keys.UP;
    private int teclaPalancaAbajo = Input.Keys.DOWN;
    private int teclaPalancaIzquierda = Input.Keys.LEFT;
    private int teclaPalancaDerecha = Input.Keys.RIGHT;


    private final Vector3 TEMP_CORDS = new Vector3();
    private final Vector2 TOQUE_VIRTUAL = new Vector2();
    private boolean huboToque = false;


    public boolean estaAcelerando() {
        return Gdx.input.isKeyPressed(teclaAcelerador);
    }

    public boolean estaEmbragado() {
        return Gdx.input.isKeyPressed(teclaEmbrague);
    }

    public boolean seMovioPalancaArriba() {
        return Gdx.input.isKeyJustPressed(teclaPalancaArriba);
    }

    public boolean seMovioPalancaAbajo() {
        return Gdx.input.isKeyJustPressed(teclaPalancaAbajo);
    }

    public boolean seMovioPalancaIzquierda() {
        return Gdx.input.isKeyJustPressed(teclaPalancaIzquierda);
    }

    public boolean seMovioPalancaDerecha() {
        return Gdx.input.isKeyJustPressed(teclaPalancaDerecha);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            huboToque = true;
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    public boolean consumoToque() {
        if (huboToque) {
            huboToque = false;
            return true;
        }
        return false;
    }

    public Vector2 getCoordenadasToque(Viewport viewport) {
        TEMP_CORDS.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(TEMP_CORDS);
        TOQUE_VIRTUAL.set(TEMP_CORDS.x, TEMP_CORDS.y);
        return TOQUE_VIRTUAL;
    }

    public Vector2 getCoordenadasMouseActuales(Viewport viewport) {
        TEMP_CORDS.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(TEMP_CORDS);
        TOQUE_VIRTUAL.set(TEMP_CORDS.x, TEMP_CORDS.y);
        return TOQUE_VIRTUAL;
    }
}
