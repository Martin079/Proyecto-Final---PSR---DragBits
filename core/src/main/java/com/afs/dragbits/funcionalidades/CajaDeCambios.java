package com.afs.dragbits.funcionalidades;

import com.afs.dragbits.autos.Auto;

public class CajaDeCambios {

    // posX: -1 (Izquierda), 0 (Centro), 1 (Derecha)
    // posY:  1 (Arriba),    0 (Neutral), -1 (Abajo)
    private int palancaX = 0;
    private int palancaY = 0;

    public void actualizar(Auto auto, EntradaJugador entrada) {
        boolean embrague = entrada.estaEmbragado();
        auto.setEmbraguePresionado(embrague);

        if (embrague) {
            // Movimiento horizontal (Izquierda / Derecha)
            if (palancaY == 0) {
                if (entrada.seMovioPalancaIzquierda()) {
                    if (palancaX > -1) palancaX--;
                } else if (entrada.seMovioPalancaDerecha()) {
                    if (palancaX < 1) palancaX++;
                }
            }

            // Movimiento vertical (Arriba / Abajo)
            if (entrada.seMovioPalancaArriba()) {
                if (palancaY < 1) palancaY++;
            } else if (entrada.seMovioPalancaAbajo()) {
                if (palancaY > -1) palancaY--;
            }

            // Determinar marcha según coordenadas (X, Y)
            int nuevaMarcha = 0; // Neutral

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
