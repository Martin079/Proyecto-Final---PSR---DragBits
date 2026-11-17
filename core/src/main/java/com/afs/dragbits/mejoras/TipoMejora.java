package com.afs.dragbits.mejoras;

public enum TipoMejora {
    ACELERACION("Aceleración", 1),
    VELOCIDAD_MAXIMA("Velocidad Máxima", 2),
    TRACCION("Tracción", 3),
    NITRO("Nitro", 4),
    POTENCIA_NITRO("Potencia del Nitro", 5);

    private final String nombreMostrar;
    private final int orden;

    TipoMejora(String nombreMostrar, int orden) {
        this.nombreMostrar = nombreMostrar;
        this.orden = orden;
    }

    public String getNombreMostrar() {
        return nombreMostrar;
    }

    public int getOrden() {
        return orden;
    }
}
