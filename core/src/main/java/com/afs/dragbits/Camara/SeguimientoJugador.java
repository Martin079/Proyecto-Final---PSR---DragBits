package com.afs.dragbits.camara;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.afs.dragbits.autos.Auto;

/**
 * Controla la cámara 2D del juego. Sigue la posición X del auto del jugador
 * para dar la sensación de avance y velocidad.
 */
public class SeguimientoJugador {

    private final OrthographicCamera camara;

    public SeguimientoJugador(float anchoPantalla, float altoPantalla) {
        camara = new OrthographicCamera();
        // Definimos la resolución virtual de la cámara igual al tamaño de la ventana
        camara.setToOrtho(false, anchoPantalla, altoPantalla);
    }

    /**
     * Actualiza la posición de la cámara centrando su eje X en la posición del auto.
     */
    public void actualizar(Auto auto) {
        // La cámara sigue al auto en X (sumando la mitad del ancho del auto para centrarlo)
        camara.position.x = auto.getPosX() + 50f;
        camara.update();
    }

    /**
     * Aplica la proyección de la cámara al SpriteBatch antes de dibujar.
     */
    public void aplicarACamara(SpriteBatch batch) {
        batch.setProjectionMatrix(camara.combined);
    }

    /**
     * Reajusta la vista si cambia el tamaño de la ventana.
     */
    public void resize(float ancho, float alto) {
        camara.setToOrtho(false, ancho, alto);
    }
}
