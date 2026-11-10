package com.afs.dragbits.funcionalidades;

import com.afs.dragbits.autos.Auto;

public class Acelerador {

    public void actualizar(Auto auto, EntradaJugador entrada, float delta) {
        if (entrada.estaAcelerando()) {
            auto.acelerar(delta);
        } else {
            auto.desacelerar(delta);
        }

        // Activar nitro con tecla N (solo si el auto tiene capacidad de nitro)
        if (entrada.consumirNitro() && auto.getCapacidadNitro() > 0 && auto.getNitroRestante() > 0) {
            auto.setNitroActivo(true);
        }
    }
}
