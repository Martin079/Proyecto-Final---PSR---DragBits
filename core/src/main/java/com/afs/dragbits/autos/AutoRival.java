package com.afs.dragbits.autos;

import com.badlogic.gdx.graphics.Color;
import com.afs.dragbits.funcionalidades.Semaforo;

public class AutoRival extends com.afs.dragbits.autos.Auto {

    private int recompensa;
    private float tiempoSiguienteCambio;

    public AutoRival(float posX, float posY, float velocidadMaxima, float aceleracion, float traccion, int recompensa, String rutaSpriteSheet) {
        super(posX, posY, velocidadMaxima, aceleracion, traccion, rutaSpriteSheet);
        this.recompensa = recompensa;
        this.capacidadNitro = 0f; // Sin Nitro
        this.nitroRestante = 0f;
    }

    public AutoRival(float posX, float posY, float velocidadMaxima, float aceleracion, float traccion, int recompensa, Color colorFallback) {
        super(posX, posY, velocidadMaxima, aceleracion, traccion, colorFallback);
        this.recompensa = recompensa;
        this.capacidadNitro = 0f; // Sin Nitro
        this.nitroRestante = 0f;
    }


    public void actualizarIA(float delta, Semaforo.EstadoSemaforo estadoSemaforo) {
        // 1. inicia la marcha cuando el semaforo pasa a VERDE
        if (estadoSemaforo == Semaforo.EstadoSemaforo.VERDE && marchaActual == 0) {
            marchaActual = 1;
        }

        // 2. si la carrera ya arranco
        if (marchaActual > 0) {
            //simulacion de embrague/cambio
            if (embraguePresionado) {
                tiempoSiguienteCambio -= delta;
                if (tiempoSiguienteCambio <= 0) {
                    embraguePresionado = false;
                }
            } else {
                acelerar(delta);

                // cambiar de marcha cerca de la zona óptima de RPM
                if (rpm >= (rpmMaximas - 600f) && marchaActual < (relacionesTransmision.length - 1)) {
                    marchaActual++;
                    embraguePresionado = true;
                    tiempoSiguienteCambio = 0.18f; // tiempo que tarda en meter el cambio
                }
            }
        }

        // actualizar posición física y tiempo de animacion
        actualizar(delta);
    }

    public int getRecompensa() {
        return recompensa;
    }
}
