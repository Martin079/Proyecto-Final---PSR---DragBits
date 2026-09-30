package com.afs.dragbits.funcionalidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;


public class EntradaJugador {

    private int teclaAcelerador = Input.Keys.W;
    private int teclaEmbrague = Input.Keys.SPACE;
    private int teclaPalancaArriba = Input.Keys.UP;
    private int teclaPalancaAbajo = Input.Keys.DOWN;
    private int teclaPalancaIzquierda = Input.Keys.LEFT;
    private int teclaPalancaDerecha = Input.Keys.RIGHT;


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


}
