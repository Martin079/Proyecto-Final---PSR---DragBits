package com.afs.dragbits.Funcionalidades;

/*IMPORTANTE!!!!!! ahora reinicia el auto a la posicion original, cuando este hecho lo demas se pierde la carrera*/

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.afs.dragbits.autos.Auto;

/**controla la secuencia del semaforo de largada, la deteccion de salida en falso
 * y el renderizado en la parte superior central de la pantalla.
 */
public class Semaforo {

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
    private final OrthographicCamera camaraHUD;
    private Texture spriteSheet;
    private TextureRegion[] frames;

    // Dimensiones visuales en pantalla
    private final float ancho = 60f;
    private final float alto = 180f;

    // Temporizadores
    private float tiempoParaSiguienteLuz;
    private float temporizador;
    private float tiempoVerdeEnPantalla = 1.0f; // en verde antes de desaparecer

    public Semaforo(float anchoPantalla, float altoPantalla) {
        camaraHUD = new OrthographicCamera();
        camaraHUD.setToOrtho(false, anchoPantalla, altoPantalla);

        // Carga del sprite sheet
        spriteSheet = new Texture(Gdx.files.internal("sprites/HUD/Semaforo-sheet.png"));
        spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Recorte de los 6 frames (60x180 px cada uno)
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 60, 180);
        frames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            frames[i] = tmp[0][i];
        }

        iniciarSecuencia();
    }

    /**reinicia o inicia la secuencia del semáforo desde el estado APAGADO.
     */
    public void iniciarSecuencia() {
        estadoActual = EstadoSemaforo.APAGADO;
        temporizador = 0f;
        // Asigna un tiempo aleatorio impredecible entre 0.3s y 0.8s
        tiempoParaSiguienteLuz = MathUtils.random(0.3f, 0.8f);
    }

    /**logica de luces y verifica la salida en falso del auto.
     */
    public void actualizar(Auto auto, float delta) {
        if (estadoActual == EstadoSemaforo.FINALIZADO) return;

        // --- DETECCIÓN DE SALIDA EN FALSO ---
        // Si el semaforo no esta en VERDE y el auto tiene movimiento físico (velocidad > 0)
        if (estadoActual != EstadoSemaforo.VERDE && estadoActual != EstadoSemaforo.SALIDA_FALSO) {
            if (auto.getVelocidad() > 0) {
                estadoActual = EstadoSemaforo.SALIDA_FALSO;
                // Resetear auto a la posición de salida original (X = 50) y velocidad 0
                resetearAuto(auto);
                return;
            }
        }

        // --- SECUENCIA DE TIEMPOS DE LUCES ---
        temporizador += delta;

        if (estadoActual == EstadoSemaforo.VERDE) {
            // en verde se mantiene 1 segundo y luego desaparece
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

    /**dibujar semaforo arriba al centro
     */
    public void dibujar(SpriteBatch batch, float anchoPantalla, float altoPantalla) {
        if (estadoActual == EstadoSemaforo.FINALIZADO) return;

        batch.setProjectionMatrix(camaraHUD.combined);

        // Posición: Arriba en el centro
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
        auto.setPosX(50f); // Posición inicial de salida
        auto.setMarchaActual(0);
    }

    public void resize(float ancho, float alto) {
        camaraHUD.setToOrtho(false, ancho, alto);
    }

    public void dispose() {
        if (spriteSheet != null) spriteSheet.dispose();
    }

    public EstadoSemaforo getEstadoActual() {
        return estadoActual;
    }
}
