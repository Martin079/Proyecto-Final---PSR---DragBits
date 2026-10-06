package com.afs.dragbits.autos;

import com.afs.dragbits.hud.EstadoSemaforo;
import com.badlogic.gdx.graphics.Color;

public class AutoRival extends Auto {

    private int recompensa;
    private float tiempoSiguienteCambio;

    public AutoRival(float posX, float posY, float velocidadMaxima, float aceleracion, float traccion, int recompensa, String rutaSpriteSheet) {
        super(posX, posY, velocidadMaxima, aceleracion, traccion, rutaSpriteSheet);
        this.recompensa = recompensa;
        this.capacidadNitro = 0f;
        this.nitroRestante = 0f;
    }

    public AutoRival(float posX, float posY, float velocidadMaxima, float aceleracion, float traccion, int recompensa, Color colorFallback) {
        super(posX, posY, velocidadMaxima, aceleracion, traccion, colorFallback);
        this.recompensa = recompensa;
        this.capacidadNitro = 0f;
        this.nitroRestante = 0f;
    }


    public void actualizarIA(float delta, EstadoSemaforo estadoSemaforo) {
        if (estadoSemaforo == EstadoSemaforo.VERDE && marchaActual == 0) {
            marchaActual = 1;
        }

        if (marchaActual > 0) {
            if (embraguePresionado) {
                tiempoSiguienteCambio -= delta;
                if (tiempoSiguienteCambio <= 0) {
                    embraguePresionado = false;
                }
            } else {
                acelerar(delta);

                if (rpm >= (rpmMaximas - 600f) && marchaActual < (relacionesTransmision.length - 1)) {
                    marchaActual++;
                    embraguePresionado = true;
                    tiempoSiguienteCambio = 0.15f;
                }
            }
        }

        actualizar(delta);
    }

    public int getRecompensa() {
        return recompensa;
    }
}
