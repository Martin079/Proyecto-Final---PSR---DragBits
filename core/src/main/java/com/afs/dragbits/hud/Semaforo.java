package com.afs.dragbits.hud;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.afs.dragbits.autos.Auto;
import com.afs.dragbits.util.SpriteSheetLoader;

public class Semaforo extends ElementoHUD {

    private EstadoSemaforo estadoActual;
    private final Texture SPRITE_SHEET;
    private final TextureRegion[] FRAMES;

    // Temporizadores
    private float tiempoParaSiguienteLuz;
    private float temporizador;


    private float posXInicialAuto;

    public Semaforo(float posXInicialAuto) {
        super();
        this.posXInicialAuto = posXInicialAuto;


        SPRITE_SHEET = SpriteSheetLoader.cargarTextura("sprites/HUD/Semaforo-sheet.png");

        // 6 frames de 60x180 px
        FRAMES = SpriteSheetLoader.recortar(SPRITE_SHEET, 60, 180, 6);

        iniciarSecuencia();
    }


    public void iniciarSecuencia() {
        estadoActual = EstadoSemaforo.APAGADO;
        temporizador = 0f;
        //tiempo aleatorio entre 0.3s y 0.8s
        tiempoParaSiguienteLuz = MathUtils.random(0.3f, 0.8f);
    }


    public void actualizar(Auto auto, float delta) {
        if (estadoActual == EstadoSemaforo.FINALIZADO) return;

        // SALIDA EN FALSO
        // Si el semaforo no esta en VERDE y el auto tiene movimiento
        if (estadoActual != EstadoSemaforo.VERDE && estadoActual != EstadoSemaforo.SALIDA_FALSO) {
            if (auto.getVelocidad() > 0) {
                estadoActual = EstadoSemaforo.SALIDA_FALSO;
                resetearAuto(auto);
                return;
            }
        }

        temporizador += delta;

        if (estadoActual == EstadoSemaforo.VERDE) {
            // en verde se mantiene 1 segundo y luego desaparecer
            // en verde antes de desaparecer
            float tiempoVerdeEnPantalla = 1.0f;
            if (temporizador >= tiempoVerdeEnPantalla) {
                estadoActual = EstadoSemaforo.FINALIZADO;
            }
            return;
        }

        if (estadoActual == EstadoSemaforo.SALIDA_FALSO) {
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


    public void dibujar(SpriteBatch batch, float anchoPantalla, float altoPantalla) {
        if (estadoActual == EstadoSemaforo.FINALIZADO) return;

        // Tamaño
        float ancho = 168f;
        float posX = (anchoPantalla / 2f) - (ancho / 2f);
        float alto = 504f;
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

        batch.draw(FRAMES[frameIndex], posX, posY, ancho, alto);
    }

    private void resetearAuto(Auto auto) {
        auto.setVelocidad(0f);
        auto.setPosX(posXInicialAuto);
        auto.setMarchaActual(0);
    }

    public void setPosXInicialAuto(float posX) {
        this.posXInicialAuto = posX;
    }

    @Override
    public void dispose() {
        if (SPRITE_SHEET != null) SPRITE_SHEET.dispose();
    }

    public EstadoSemaforo getEstadoActual() {
        return estadoActual;
    }
}
