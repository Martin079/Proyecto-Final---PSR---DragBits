package com.afs.dragbits.hud;

/*IMPORTANTE!!!!!! ahora reinicia el auto a la posicion original, cuando este hecho lo demas se pierde la carrera*/

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.afs.dragbits.autos.Auto;
import com.afs.dragbits.util.SpriteSheetLoader;

/**
 * Controla la secuencia del semaforo de largada, la deteccion de salida en falso
 * y el renderizado en la parte superior central de la pantalla.
 */
public class Semaforo extends ElementoHUD {

    public enum EstadoSemaforo {
        APAGADO,      // Frame 0
        LUZ_1,        // Frame 1
        LUZ_2,        // Frame 2
        LUZ_3,        // Frame 3
        VERDE,        // Frame 4 (Largada)
        SALIDA_FALSO, // Frame 5 (Infracción)
        FINALIZADO    // Desaparece de pantalla
    }

    private EstadoSemaforo estadoActual;
    private Texture spriteSheet;
    private TextureRegion[] frames;

    // Dimensiones
    private final float ancho = 120f;
    private final float alto = 360f;

    // Temporizadores
    private float tiempoParaSiguienteLuz;
    private float temporizador;
    private float tiempoVerdeEnPantalla = 1.0f; // en verde antes de desaparecer

    // Guarda la posición X exacta donde debe reiniciarse el auto
    private float posXInicialAuto;

    public Semaforo(float anchoPantalla, float altoPantalla, float posXInicialAuto) {
        super(anchoPantalla, altoPantalla);
        this.posXInicialAuto = posXInicialAuto;

        // Carga del sprite usando SpriteSheetLoader
        spriteSheet = SpriteSheetLoader.cargarTextura("sprites/HUD/Semaforo-sheet.png");

        // Recorte directo de los 6 frames (60x180 px)
        frames = SpriteSheetLoader.recortar(spriteSheet, 60, 180, 6);

        iniciarSecuencia();
    }

    /**
     * reinicia o inicia la secuencia del semáforo desde el estado APAGADO.
     */
    public void iniciarSecuencia() {
        estadoActual = EstadoSemaforo.APAGADO;
        temporizador = 0f;
        // Asigna un tiempo aleatorio impredecible entre 0.3s y 0.8s
        tiempoParaSiguienteLuz = MathUtils.random(0.3f, 0.8f);
    }

    /**
     * logica de luces y verifica la salida en falso del auto.
     */
    public void actualizar(Auto auto, float delta) {
        if (estadoActual == EstadoSemaforo.FINALIZADO) return;

        // SALIDA EN FALSO
        // Si el semaforo no esta en VERDE y el auto tiene movimiento físico (velocidad > 0)
        if (estadoActual != EstadoSemaforo.VERDE && estadoActual != EstadoSemaforo.SALIDA_FALSO) {
            if (auto.getVelocidad() > 0) {
                estadoActual = EstadoSemaforo.SALIDA_FALSO;
                // Resetear auto a la posición de salida configurada dinámicamente y velocidad 0
                resetearAuto(auto);
                return;
            }
        }

        // TIEMPOS DE LUCES
        temporizador += delta;

        if (estadoActual == EstadoSemaforo.VERDE) {
            // en verde se mantiene 1 segundo y luego desaparecer
            if (temporizador >= tiempoVerdeEnPantalla) {
                estadoActual = EstadoSemaforo.FINALIZADO;
            }
            return;
        }

        if (estadoActual == EstadoSemaforo.SALIDA_FALSO) {
            // si hubo salida en falso se reinicia
            if (temporizador >= 1.5f) {
                iniciarSecuencia();
            }
            return;
        }

        if (temporizador >= tiempoParaSiguienteLuz) {
            temporizador = 0f;
            tiempoParaSiguienteLuz = MathUtils.random(0.3f, 0.8f);

            switch (estadoActual) {
                case APAGADO:
                    estadoActual = EstadoSemaforo.LUZ_1;
                    break;
                case LUZ_1:
                    estadoActual = EstadoSemaforo.LUZ_2;
                    break;
                case LUZ_2:
                    estadoActual = EstadoSemaforo.LUZ_3;
                    break;
                case LUZ_3:
                    estadoActual = EstadoSemaforo.VERDE;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * dibujar semaforo arriba al centro
     */
    public void dibujar(SpriteBatch batch, float anchoPantalla, float altoPantalla) {
        if (estadoActual == EstadoSemaforo.FINALIZADO) return;

        aplicarProyeccion(batch);

        // posicion
        float posX = (anchoPantalla / 2f) - (ancho / 2f);
        float posY = altoPantalla - alto - 10f; // 10px de margen respecto al borde superior

        int frameIndex = 0;
        switch (estadoActual) {
            case APAGADO:      frameIndex = 0; break;
            case LUZ_1:        frameIndex = 1; break;
            case LUZ_2:        frameIndex = 2; break;
            case LUZ_3:        frameIndex = 3; break;
            case VERDE:        frameIndex = 4; break;
            case SALIDA_FALSO: frameIndex = 5; break;
            default: break;
        }

        batch.draw(frames[frameIndex], posX, posY, ancho, alto);
    }

    private void resetearAuto(Auto auto) {
        auto.setVelocidad(0f);
        auto.setPosX(posXInicialAuto); // reinicia la posicion calculada por Picodromo
        auto.setMarchaActual(0);
    }

    public void setPosXInicialAuto(float posX) {
        this.posXInicialAuto = posX;
    }

    @Override
    public void dispose() {
        if (spriteSheet != null) spriteSheet.dispose();
    }

    public EstadoSemaforo getEstadoActual() {
        return estadoActual;
    }
}
