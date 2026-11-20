package com.afs.dragbits.network;

import com.badlogic.gdx.math.MathUtils;

/**
 * Motor de física determinista para DragBits.
 * Puro Java (sin dependencias de LibGDX excepto MathUtils).
 * Usado tanto por el cliente (Auto) como por el servidor (ServerAuto).
 */
public final class PhysicsEngine {

    private PhysicsEngine() {}

    /**
     * Simula un paso de física para un auto.
     * @param auto Estado actual del auto (se modifica in-place)
     * @param input Entrada del jugador
     * @param delta Tiempo en segundos desde el último frame
     */
    public static void step(AutoState auto, Input input, float delta) {
        // Guardar estado anterior para detección de cambios
        boolean wasAccelerating = auto.velocity > 0 && !auto.clutchPressed && auto.gear > 0;

        // Actualizar embrague y marcha (input inmediato)
        auto.clutchPressed = input.clutch;
        if (input.clutch && (input.leverX != 0 || input.leverY != 0)) {
            int newGear = calculateGear(input.leverX, input.leverY);
            if (newGear != auto.gear) {
                auto.gear = newGear;
            }
        }

        // Nitro
        if (input.nitro && auto.nitroRemaining > 0 && auto.nitroCapacity > 0) {
            auto.nitroActive = true;
        }

        // Física de aceleración/desaceleración
        if (input.accelerator) {
            accelerate(auto, delta);
        } else {
            decelerate(auto, delta);
        }

        // Actualizar RPM
        updateRPM(auto, delta);

        // Actualizar posición
        auto.posX += (auto.velocity * AutoState.FACTOR_MOVIMIENTO) * delta;

        // Actualizar estado visual
        updateVisualState(auto);
    }

    private static int calculateGear(int leverX, int leverY) {
        if (leverX == -1 && leverY == 1)      return 1;
        else if (leverX == -1 && leverY == -1) return 2;
        else if (leverX == 0  && leverY == 1)  return 3;
        else if (leverX == 0  && leverY == -1) return 4;
        else if (leverX == 1  && leverY == 1)  return 5;
        return 0;
    }

    private static void accelerate(AutoState auto, float delta) {
        if (auto.clutchPressed || auto.gear == 0) {
            // En punto muerto o con embrague: solo suben RPMs
            return;
        }

        int validGear = Math.min(auto.gear, auto.gearRatios.length - 1);
        float maxSpeedInGear = auto.maxVelocity * auto.gearRatios[validGear];

        float effectiveAcceleration = auto.acceleration
                * calculateTractionFactor(auto, validGear)
                * auto.gearPowerMultipliers[validGear];

        // Boost de arranque
        if (auto.velocity < AutoState.UMBRAL_ARRANQUE) {
            effectiveAcceleration *= AutoState.FACTOR_ARRANQUE;
        }

        // Curva de aceleración
        if (maxSpeedInGear > 0f) {
            float margin = MathUtils.clamp((maxSpeedInGear - auto.velocity) / maxSpeedInGear,
                    AutoState.PISO_CURVA_ACELERACION, 1f);
            effectiveAcceleration *= margin;
        }

        // Nitro
        if (auto.nitroActive && auto.nitroRemaining > 0) {
            effectiveAcceleration *= auto.nitroPower;
            maxSpeedInGear *= 1.15f;
            auto.nitroRemaining -= delta;
            if (auto.nitroRemaining <= 0) {
                auto.nitroRemaining = 0;
                auto.nitroActive = false;
            }
        }

        if (auto.velocity < maxSpeedInGear) {
            auto.velocity += effectiveAcceleration * delta;
            if (auto.velocity > maxSpeedInGear) {
                auto.velocity = maxSpeedInGear;
            }
        }
    }

    private static void decelerate(AutoState auto, float delta) {
        if (auto.velocity > 0) {
            auto.velocity -= (auto.acceleration * 0.3f) * delta;
            if (auto.velocity < 0) {
                auto.velocity = 0;
            }
        }
        auto.nitroActive = false;
        auto.spinning = false;
    }

    private static float calculateTractionFactor(AutoState auto, int validGear) {
        if (validGear > 2 || auto.traction <= 0f) {
            auto.spinning = false;
            return 1f;
        }

        float powerGripRatio = auto.acceleration / auto.traction;

        if (powerGripRatio <= AutoState.UMBRAL_PATINAJE) {
            auto.spinning = false;
            return 1f;
        }

        auto.spinning = true;
        float excess = powerGripRatio - AutoState.UMBRAL_PATINAJE;
        return MathUtils.clamp(1f - excess * 0.5f,
                AutoState.PERDIDA_MIN_PATINAJE, AutoState.PERDIDA_MAX_PATINAJE);
    }

    private static void updateRPM(AutoState auto, float delta) {
        if (auto.clutchPressed || auto.gear == 0) {
            auto.rpm += 5500f * delta;
            if (auto.rpm > AutoState.RPM_MAXIMAS) {
                auto.rpm = AutoState.RPM_MAXIMAS;
            }
        } else {
            int validGear = Math.min(auto.gear, auto.gearRatios.length - 1);
            float maxSpeedInGear = auto.maxVelocity * auto.gearRatios[validGear];

            if (maxSpeedInGear > 0) {
                auto.rpm = 800f + (auto.velocity / maxSpeedInGear) * (AutoState.RPM_MAXIMAS - 800f);
            }
            if (auto.rpm > AutoState.RPM_MAXIMAS) {
                auto.rpm = AutoState.RPM_MAXIMAS;
            }
        }
    }

    private static void updateVisualState(AutoState auto) {
        if (auto.nitroActive && auto.nitroRemaining > 0) {
            auto.state = 3; // NITRO
        } else if (auto.clutchPressed) {
            auto.state = 2; // CAMBIANDO_MARCHA
        } else if (auto.spinning && auto.velocity > 0) {
            auto.state = 4; // PATINANDO
        } else if (auto.velocity > 0) {
            auto.state = 1; // AVANZANDO
        } else {
            auto.state = 0; // ESTATICO
        }
    }

    /**
     * Crea un AutoState configurado para el modo online (CHALLENGER base).
     * Igual para ambos jugadores - solo cambia el color/sprite en el cliente.
     */
    public static AutoState createOnlineChallenger(float posX, float posY) {
        // Valores base del CHALLENGER (mejor auto del juego)
        return new AutoState(posX, posY,
                255f,    // velocidadMaxima
                4.5f,    // aceleracion (segundos 0-100)
                0.85f,   // traccion
                3f,      // capacidadNitro
                1f       // potenciaNitro (base, se multiplica)
        );
    }

    /**
     * Crea AutoState para un auto específico (para testing offline).
     */
    public static AutoState createForCar(CarType carType, float posX, float posY) {
        switch (carType) {
            case GOL:
                return new AutoState(posX, posY, 160f, 9.5f, 0.6f, 0f, 1f);
            case TOYOTA:
                return new AutoState(posX, posY, 205f, 6.8f, 0.75f, 2f, 1f);
            case CHALLENGER:
                return new AutoState(posX, posY, 255f, 4.5f, 0.85f, 3f, 1f);
            default:
                return createOnlineChallenger(posX, posY);
        }
    }

    public enum CarType {
        GOL, TOYOTA, CHALLENGER
    }
}