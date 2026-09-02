package com.afs.dragbits.camara;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.afs.dragbits.autos.Auto;

/** Controla la camara. sigue la posición X del auto del jugador*/
public class SeguimientoJugador {

    private final OrthographicCamera camara;

    public SeguimientoJugador(float anchoPantalla, float altoPantalla) {
        camara = new OrthographicCamera();
        // se define la resolución virtual de la cámara igual al tamaño de la ventana
        camara.setToOrtho(false, anchoPantalla, altoPantalla);
    }

    /** actualiza la posición de la camara centrando su eje X en la posición del auto.*/
    public void actualizar(Auto auto) {
        //se suma la mitad del auto para centrar
        camara.position.x = auto.getPosX() + 100f;
        camara.update();
    }

    /** proyección de la camara al SpriteBatch antes de dibujar.*/
    public void aplicarACamara(SpriteBatch batch) {
        batch.setProjectionMatrix(camara.combined);
    }

    /**reajusta la vista si cambia el tamaño de la ventana.*/
    public void resize(float ancho, float alto) {
        camara.setToOrtho(false, ancho, alto);
    }

}
