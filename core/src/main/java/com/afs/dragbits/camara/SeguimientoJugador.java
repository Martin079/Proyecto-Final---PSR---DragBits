package com.afs.dragbits.camara;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.afs.dragbits.autos.Auto;

/**sigue la posición X del auto del jugador*/
public class SeguimientoJugador {

    private final OrthographicCamera CAMARA;

    public SeguimientoJugador(float anchoVirtual, float altoVirtual) {
        CAMARA = new OrthographicCamera();
        CAMARA.position.set(anchoVirtual / 2f, altoVirtual / 2f, 0);
        CAMARA.update();
    }

    /** actualiza la posicion de la camara centrando su eje X en la posicion del auto.*/
    public void actualizar(Auto auto) {
        CAMARA.position.x = auto.getPosX() + 100f;
        CAMARA.update();
    }

    public void aplicarACamara(SpriteBatch batch) {
        batch.setProjectionMatrix(CAMARA.combined);
    }

    public OrthographicCamera getCamara() {
        return CAMARA;
    }

}
