package com.afs.dragbits.network;

public final class AutoState {

    // Estado mínimo para simulación y renderizado
    public int id; // 0 or 1 (jugador 0 o 1)
    public float posX;
    public float posY;
    public float velocity;
    public float rpm;
    public int gear;
    public boolean clutchPressed;
    public boolean nitroActive;
    public float nitroRemaining;
    public float nitroCapacity;
    public boolean spinning;
    public int state; // 0=ESTATICO, 1=AVANZANDO, 2=CAMBIANDO_MARCHA, 3=NITRO, 4=PATINANDO
    public float width;
    public float height;

    // Configuración del auto (constante durante la carrera)
    public float maxVelocity;
    public float acceleration;
    public float traction;
    public float nitroPower;
    public float[] gearRatios = {0f, 0.30f, 0.50f, 0.70f, 0.85f, 1.0f};
    public float[] gearPowerMultipliers = {1.0f, 2.50f, 1.80f, 1.20f, 0.85f, 0.55f};

    public static final float UMBRAL_ARRANQUE = 25f;
    public static final float FACTOR_ARRANQUE = 1.25f;
    public static final float PISO_CURVA_ACELERACION = 0.4f;
    public static final float UMBRAL_PATINAJE = 1.2f;
    public static final float PERDIDA_MIN_PATINAJE = 0.45f;
    public static final float PERDIDA_MAX_PATINAJE = 0.85f;
    public static final float FACTOR_MOVIMIENTO = 4.2f;
    public static final float RPM_MAXIMAS = 7000f;

    public AutoState() {}

    public AutoState(float posX, float posY, float maxVelocity, float acceleration, float traction,
                     float nitroCapacity, float nitroPower) {
        this.posX = posX;
        this.posY = posY;
        this.maxVelocity = maxVelocity;
        this.acceleration = acceleration;
        this.traction = traction;
        this.nitroCapacity = nitroCapacity;
        this.nitroPower = nitroPower;
        this.nitroRemaining = nitroCapacity;
        this.velocity = 0f;
        this.rpm = 800f;
        this.gear = 0;
        this.clutchPressed = false;
        this.nitroActive = false;
        this.spinning = false;
        this.state = 0; // ESTATICO
        this.width = 280f * 2.8f; // ANCHO_BASE * ESCALA_SPRITE
        this.height = 112f * 2.8f;
    }

    // Copia profunda para envío por red
    public AutoState copy() {
        AutoState copy = new AutoState();
        copy.posX = this.posX;
        copy.posY = this.posY;
        copy.velocity = this.velocity;
        copy.rpm = this.rpm;
        copy.gear = this.gear;
        copy.clutchPressed = this.clutchPressed;
        copy.nitroActive = this.nitroActive;
        copy.nitroRemaining = this.nitroRemaining;
        copy.nitroCapacity = this.nitroCapacity;
        copy.spinning = this.spinning;
        copy.state = this.state;
        copy.width = this.width;
        copy.height = this.height;
        copy.maxVelocity = this.maxVelocity;
        copy.acceleration = this.acceleration;
        copy.traction = this.traction;
        copy.nitroPower = this.nitroPower;
        copy.gearRatios = this.gearRatios.clone();
        copy.gearPowerMultipliers = this.gearPowerMultipliers.clone();
        return copy;
    }

    public float getFrontX() {
        return posX + width;
    }

    public void alignFront(float frontX) {
        this.posX = frontX - width;
    }
}