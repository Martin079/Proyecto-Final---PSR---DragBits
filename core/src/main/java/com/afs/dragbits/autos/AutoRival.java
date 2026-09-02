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
        // 1. Iniciar marcha cuando el semáforo pasa a VERDE
        if (estadoSemaforo == Semaforo.EstadoSemaforo.VERDE && marchaActual == 0) {
            marchaActual = 1;
        }

        // 2. Si la carrera ya arrancó (marcha > 0)
        if (marchaActual > 0) {
            // Manejo de la simulación de embrague/cambio
            if (embraguePresionado) {
                tiempoSiguienteCambio -= delta;
                if (tiempoSiguienteCambio <= 0) {
                    embraguePresionado = false;
                }
            } else {
                acelerar(delta);

                // Cambiar de marcha automáticamente cerca de la zona óptima de RPM
                if (rpm >= (rpmMaximas - 600f) && marchaActual < (relacionesTransmision.length - 1)) {
                    marchaActual++;
                    embraguePresionado = true;
                    tiempoSiguienteCambio = 0.18f; // Tiempo que tarda el bot en meter el cambio
                }
            }
        }

        // Actualizar posición física y tiempo de animación
        actualizar(delta);
    }

    public int getRecompensa() {
        return recompensa;
    }
}
