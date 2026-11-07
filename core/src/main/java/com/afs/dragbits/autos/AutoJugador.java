package com.afs.dragbits.autos;

import com.afs.dragbits.Main;


public class AutoJugador extends Auto {

    public AutoJugador(float posX, float posY, AutoConfig config, Main mainGame) {
        super(posX, posY,
              config.getVelocidadMaxima(),
              calcularAceleracionDesde0a100(config.getAceleracion()),
              config.getTraccion(),
              config.getTipo().getRutaSprite(),
              mainGame);
        this.capacidadNitro = config.getCapacidadNitro();
        this.nitroRestante = config.getCapacidadNitro();
    }

    private static float calcularAceleracionDesde0a100(float segundos0a100) {
        if (segundos0a100 <= 0) return 50f;
        return 300f / segundos0a100;
    }
}



