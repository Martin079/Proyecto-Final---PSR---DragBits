package com.afs.dragbits.autos;

import com.afs.dragbits.Main;
import com.afs.dragbits.hud.EstadoSemaforo;
import com.afs.dragbits.network.Input;
import com.afs.dragbits.network.PhysicsEngine;
import com.afs.dragbits.rivales.RivalConfig;

public class AutoRival extends Auto {

    private int recompensa;
    private float tiempoSiguienteCambio;

    private static final int MARCHA_ACTIVACION_NITRO = 3;

    public AutoRival(float posX, float posY, float velocidadMaxima, float aceleracion, float traccion, int recompensa, String rutaSpriteSheet, Main mainGame) {
        super(posX, posY, velocidadMaxima, aceleracion, traccion, rutaSpriteSheet, mainGame);
        this.recompensa = recompensa;
        this.capacidadNitro = 0f;
        this.nitroRestante = 0f;
        this.physicsState.nitroCapacity = 0f;
        this.physicsState.nitroRemaining = 0f;
    }

    public AutoRival(float posX, float posY, RivalConfig config, int recompensa, Main mainGame) {
        super(posX, posY, config.getVelocidadMaxima(), config.getAceleracion(), config.getTraccion(), config.getRutaSprite(), mainGame);
        this.recompensa = recompensa;
        this.capacidadNitro = config.getNitroSegundos();
        this.nitroRestante = config.getNitroSegundos();
        this.physicsState.nitroCapacity = config.getNitroSegundos();
        this.physicsState.nitroRemaining = config.getNitroSegundos();
    }


    @Override
    public void acelerar(float delta) {
        Input input = new Input(true, embraguePresionado, 0, 0, false);
        PhysicsEngine.step(physicsState, input, delta);
        sincronizarDesdePhysics();
        this.estaAcelerando = true;
    }

    public void actualizarIA(float delta, EstadoSemaforo estadoSemaforo) {
        if (estadoSemaforo == EstadoSemaforo.VERDE && marchaActual == 0) {
            marchaActual = 1;
            physicsState.gear = 1;
        }

        if (marchaActual > 0) {
            boolean usarNitro = !nitroActivo && nitroRestante > 0 && marchaActual >= MARCHA_ACTIVACION_NITRO;
            int leverX = 0, leverY = 0;
            boolean embrague = false;

            if (embraguePresionado) {
                tiempoSiguienteCambio -= delta;
                if (tiempoSiguienteCambio <= 0) {
                    embraguePresionado = false;
                    physicsState.clutchPressed = false;
                }
                embrague = true;
            } else {
                if (rpm >= (rpmMaximas - 600f) && marchaActual < (relacionesTransmision.length - 1)) {
                    marchaActual++;
                    physicsState.gear = marchaActual;
                    embraguePresionado = true;
                    physicsState.clutchPressed = true;
                    tiempoSiguienteCambio = 0.15f;
                    embrague = true;
                }
            }

            Input input = new Input(true, embrague, leverX, leverY, usarNitro);
            if (usarNitro) {
                physicsState.nitroActive = true;
            }
            PhysicsEngine.step(physicsState, input, delta);
            sincronizarDesdePhysics();
            this.estaAcelerando = true;
        } else {
            this.estaAcelerando = false;
        }

        actualizar(delta);
    }

    public int getRecompensa() {
        return recompensa;
    }
}
