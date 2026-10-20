package com.afs.dragbits.funcionalidades;

import com.afs.dragbits.autos.Auto;

public class CajaDeCambios {

    private int palancaX = 0;
    private int palancaY = 0;

    public void actualizar(Auto auto, EntradaJugador entrada) {
        boolean embrague = entrada.estaEmbragado();
        auto.setEmbraguePresionado(embrague);

        if (embrague) {
            if (palancaY == 0) {
                if (entrada.seMovioPalancaIzquierda()) {
                    if (palancaX > -1) palancaX--;
                } else if (entrada.seMovioPalancaDerecha()) {
                    if (palancaX < 1) palancaX++;
                }
            }

            if (entrada.seMovioPalancaArriba()) {
                if (palancaY < 1) palancaY++;
            } else if (entrada.seMovioPalancaAbajo()) {
                if (palancaY > -1) palancaY--;
            }

            int nuevaMarcha = 0;

            if (palancaX == -1 && palancaY == 1)      nuevaMarcha = 1;
            else if (palancaX == -1 && palancaY == -1) nuevaMarcha = 2;
            else if (palancaX == 0  && palancaY == 1)  nuevaMarcha = 3;
            else if (palancaX == 0  && palancaY == -1) nuevaMarcha = 4;
            else if (palancaX == 1  && palancaY == 1)  nuevaMarcha = 5;

            auto.setMarchaActual(nuevaMarcha);
        }
    }

    public int getPalancaX() { return palancaX; }
    public int getPalancaY() { return palancaY; }
}
