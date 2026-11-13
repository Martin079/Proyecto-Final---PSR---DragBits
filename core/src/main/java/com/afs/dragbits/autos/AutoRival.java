package com.afs.dragbits.autos;

import com.afs.dragbits.Main;
import com.afs.dragbits.hud.EstadoSemaforo;
import com.afs.dragbits.rivales.RivalConfig;
import com.badlogic.gdx.math.MathUtils;

public class AutoRival extends Auto {

    private int recompensa;
    private float tiempoSiguienteCambio;

    private static final int MARCHA_ACTIVACION_NITRO = 3;

    public AutoRival(float posX, float posY, float velocidadMaxima, float aceleracion, float traccion, int recompensa, String rutaSpriteSheet, Main mainGame) {
        super(posX, posY, velocidadMaxima, aceleracion, traccion, rutaSpriteSheet, mainGame);
        this.recompensa = recompensa;
        this.capacidadNitro = 0f;
        this.nitroRestante = 0f;
    }

    public AutoRival(float posX, float posY, RivalConfig config, int recompensa, Main mainGame) {
        super(posX, posY, config.getVelocidadMaxima(), config.getAceleracion(), config.getTraccion(), config.getRutaSprite(), mainGame);
        this.recompensa = recompensa;
        this.capacidadNitro = config.getNitroSegundos();
        this.nitroRestante = config.getNitroSegundos();
    }


    @Override
    public void acelerar(float delta) {
        this.estaAcelerando = true;
        if (!embraguePresionado && marchaActual > 0) {
            int marchaValida = Math.min(marchaActual, relacionesTransmision.length - 1);
            float velMaxMarcha = velocidadMaxima * relacionesTransmision[marchaValida];

            // IA usa aceleración BASE sin multiplicador de potencia por marcha
            float aceleracionEfectiva = aceleracion * calcularFactorTraccion(marchaValida);

            if (velocidad < UMBRAL_ARRANQUE) {
                aceleracionEfectiva *= FACTOR_ARRANQUE;
            }

            if (velMaxMarcha > 0f) {
                float margen = MathUtils.clamp((velMaxMarcha - velocidad) / velMaxMarcha, PISO_CURVA_ACELERACION, 1f);
                aceleracionEfectiva *= margen;
            }

            if (nitroActivo && nitroRestante > 0) {
                aceleracionEfectiva *= potenciaNitro;
                velMaxMarcha *= 1.15f;
                nitroRestante -= delta;
                if (nitroRestante <= 0) {
                    nitroRestante = 0;
                    nitroActivo = false;
                }
            }

            if (velocidad < velMaxMarcha) {
                velocidad += aceleracionEfectiva * delta;
                if (velocidad > velMaxMarcha) velocidad = velMaxMarcha;
            }
        }

        if (embraguePresionado || marchaActual == 0) {
            rpm += 5500f * delta;
            if (rpm > rpmMaximas) rpm = rpmMaximas;
        } else {
            int marchaValida = Math.min(marchaActual, relacionesTransmision.length - 1);
            float velMaxMarcha = velocidadMaxima * relacionesTransmision[marchaValida];
            if (velMaxMarcha > 0) {
                rpm = 800f + (velocidad / velMaxMarcha) * (rpmMaximas - 800f);
            }
            if (rpm > rpmMaximas) rpm = rpmMaximas;
        }
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
                if (!nitroActivo && nitroRestante > 0 && marchaActual >= MARCHA_ACTIVACION_NITRO) {
                    nitroActivo = true;
                }
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
