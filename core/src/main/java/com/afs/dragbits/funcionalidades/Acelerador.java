package com.afs.dragbits.funcionalidades;

import com.afs.dragbits.autos.Auto;

public class Acelerador {

    public void actualizar(Auto auto, EntradaJugador entrada, float delta) {
        if (entrada.estaAcelerando()) {
            auto.acelerar(delta);
        } else {
            auto.desacelerar(delta);
        }
    }
}
