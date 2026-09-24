package com.afs.dragbits.hud;

public enum EstadoSemaforo {
    APAGADO,      // Frame 0
    LUZ_1,        // Frame 1
    LUZ_2,        // Frame 2
    LUZ_3,        // Frame 3
    VERDE,        // Frame 4 (Largada)
    SALIDA_FALSO, // Frame 5 (Infracción)
    FINALIZADO    // Desaparece de pantalla
}
