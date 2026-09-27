package com.afs.dragbits.camara;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.afs.dragbits.autos.Auto;

/**sigue la posición X del auto del jugador*/
public class SeguimientoJugador {

    private final OrthographicCamera camara;

    public SeguimientoJugador(float anchoPantalla, float altoPantalla) {
        camara = new OrthographicCamera();
        //resolución virtual de la cámara igual al tamaño de la ventana
        camara.setToOrtho(false, anchoPantalla, altoPantalla);
    }

    /** actualiza la posicion de la camara centrando su eje X en la posicion del auto.*/
    public void actualizar(Auto auto) {
        camara.position.x = auto.getPosX() + 100f;
        camara.update();
    }

    public void aplicarACamara(SpriteBatch batch) {
        batch.setProjectionMatrix(camara.combined);
    }

    /**reajusta la vista si cambia el tamaño de la ventana.*/
    public void resize(float ancho, float alto) {
        camara.setToOrtho(false, ancho, alto);
    }

}
