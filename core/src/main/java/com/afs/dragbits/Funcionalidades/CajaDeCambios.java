package com.afs.dragbits.funcionalidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.afs.dragbits.autos.Auto;

/**
 * Controla la lógica de embrague (Espacio) y la palanca en H (Flechas).
 */
public class CajaDeCambios {

    // Posición interna de la palanca:
    // posX: -1 (Izquierda), 0 (Centro), 1 (Derecha)
    // posY:  1 (Arriba),    0 (Neutral), -1 (Abajo)
    private int palancaX = 0;
    private int palancaY = 0;

    public void actualizar(Auto auto, float delta) {
        // 1. Detección de Embrague (ESPACIO)
        boolean embrague = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        auto.setEmbraguePresionado(embrague);

        // SOLO se puede mover la palanca si el embrague está presionado
        if (embrague) {
            // Movimiento Horizontal (Flecha Izquierda / Derecha)
            // Solo permite cambiar de carril si la palanca está en la línea media (posY == 0)
            if (palancaY == 0) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                    if (palancaX > -1) palancaX--;
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                    if (palancaX < 1) palancaX++;
                }
            }

            // Movimiento Vertical (Flecha Arriba / Abajo)
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                if (palancaY < 1) palancaY++;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                if (palancaY > -1) palancaY--;
            }

            // Evaluar qué marcha engancha la palanca según sus coordenadas (X, Y)
            int nuevaMarcha = 0; // Neutral por defecto

            if (palancaX == -1 && palancaY == 1)      nuevaMarcha = 1; // Izquierda - Arriba
            else if (palancaX == -1 && palancaY == -1) nuevaMarcha = 2; // Izquierda - Abajo
            else if (palancaX == 0  && palancaY == 1)  nuevaMarcha = 3; // Centro - Arriba
            else if (palancaX == 0  && palancaY == -1) nuevaMarcha = 4; // Centro - Abajo
            else if (palancaX == 1  && palancaY == 1)  nuevaMarcha = 5; // Derecha - Arriba

            auto.setMarchaActual(nuevaMarcha);
        }
    }

    public int getPalancaX() { return palancaX; }
    public int getPalancaY() { return palancaY; }
}
