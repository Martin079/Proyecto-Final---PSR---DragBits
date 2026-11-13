# Instrucciones para Windsurf - Balanceo Caja de Cambios vs IA

## Problema
El sistema de caja de cambios basado en potencia-velocidad funciona bien para el jugador, pero la IA resulta **demasiado rápida** porque:
1. La IA usa los mismos multiplicadores de potencia por marcha (`MULTIPLICADOR_POTENCIA_POR_MARCHA`)
2. Pero cambia de marcha **óptimamente y sin penalización de embrague** (solo 0.15s)
3. Los rivales tienen valores de `aceleracion` muy altos (23-63) vs jugador (~31-66 calculado desde 0-100)

---

## SOLUCIONES PROPUESTAS (elegir UNA)

### OPCIÓN A: Limitar IA - Desactivar multiplicadores de potencia para IA (RECOMENDADA)
La IA acelera con potencia "plana" (sin multiplicador por marcha), simulando una CVT o cambio automático ideal.

**Archivo: `AutoRival.java`**

```java
// En AutoRival, sobrescribir acelerar() para ignorar multiplicadores de marcha
@Override
public void acelerar(float delta) {
    this.estaAcelerando = true;
    if (!embraguePresionado && marchaActual > 0) {
        int marchaValida = Math.min(marchaActual, relacionesTransmision.length - 1);
        float velMaxMarcha = velocidadMaxima * relacionesTransmision[marchaValida];

        // IA usa aceleración BASE sin multiplicador de potencia por marcha
        float aceleracionEfectiva = aceleracion * calcularFactorTraccion(marchaValida);
        // NO aplicar: * MULTIPLICADOR_POTENCIA_POR_MARCHA[marchaValida]

        if (velocidad < UMBRAL_ARRANQUE) {
            aceleracionEfectiva *= FACTOR_ARRANQUE;
        }

        if (velMaxMarcha > 0f) {
            float margen = MathUtils.clamp((velMaxMarcha - velocidad) / velMaxMarcha, PISO_CURVA_ACELERACION, 1f);
            aceleracionEfectiva *= margen;
        }

        if (nitroActivo && nitroRestante > 0) {
            aceleracionEfectiva *= potenciaNitro;
            velMaxMarcha *= 1.15f;
            nitroRestante -= delta;
            if (nitroRestante <= 0) {
                nitroRestante = 0;
                nitroActivo = false;
            }
        }

        if (velocidad < velMaxMarcha) {
            velocidad += aceleracionEfectiva * delta;
            if (velocidad > velMaxMarcha) velocidad = velMaxMarcha;
        }
    }

    // RPM logic unchanged...
    if (embraguePresionado || marchaActual == 0) {
        rpm += 5500f * delta;
        if (rpm > rpmMaximas) rpm = rpmMaximas;
    } else {
        int marchaValida = Math.min(marchaActual, relacionesTransmision.length - 1);
        float velMaxMarcha = velocidadMaxima * relacionesTransmision[marchaValida];
        if (velMaxMarcha > 0) {
            rpm = 800f + (velocidad / velMaxMarcha) * (rpmMaximas - 800f);
        }
        if (rpm > rpmMaximas) rpm = rpmMaximas;
    }
}
```

**Ventaja:** Mínimo cambio, IA se siente "automática suave", jugador tiene ventaja táctica con marchas cortas.

---

### OPCIÓN B: Limitar IA - Factor de penalización configurable
Aplicar un factor < 1.0 a la aceleración efectiva de la IA para compensar sus cambios perfectos.

**Archivo: `AutoRival.java`** (añadir constante y usarla en `acelerar()` sobrescrito)

```java
private static final float FACTOR_BALANCE_IA = 0.75f; // Ajustar 0.65-0.85 según testing

// En el acelerar() sobrescrito:
float aceleracionEfectiva = aceleracion * calcularFactorTraccion(marchaValida) * FACTOR_BALANCE_IA;
```

**Ventaja:** Fácil de tunear sin tocar lógica compleja.

---

### OPCIÓN C: Ajustar Player - Reducir multiplicadores de marcha del jugador
Hacer que las marchas cortas del jugador sean menos extremas para que no sea tan lento arrancando.

**Archivo: `Auto.java`** - Cambiar `MULTIPLICADOR_POTENCIA_POR_MARCHA`:

```java
// ANTES (muy agresivo):
private static final float[] MULTIPLICADOR_POTENCIA_POR_MARCHA = {
    1.0f,   // neutro
    2.50f,  // 1ra
    1.80f,  // 2da
    1.20f,  // 3ra
    0.85f,  // 4ta
    0.55f   // 5ta
};

// DESPUÉS (más suave, menos diferencia entre marchas):
private static final float[] MULTIPLICADOR_POTENCIA_POR_MARCHA = {
    1.0f,   // neutro
    2.00f,  // 1ra (era 2.5)
    1.55f,  // 2da (era 1.8)
    1.15f,  // 3ra (era 1.2)
    0.90f,  // 4ta (era 0.85)
    0.65f   // 5ta (era 0.55)
};
```

**Ventaja:** Jugador menos penalizado en marchas altas, IA relativamente más lenta.
**Riesgo:** Puede romper el "feel" de la caja de cambios real.

---

### OPCIÓN D: Híbrido - IA usa multiplicadores REDUCIDOS
La IA sí usa multiplicadores pero atenuados (ej. 50% del efecto).

**Archivo: `AutoRival.java`** - En `acelerar()` sobrescrito:

```java
private static final float FACTOR_MULTIPLICADOR_IA = 0.5f;

// ...
float mult = 1.0f + (MULTIPLICADOR_POTENCIA_POR_MARCHA[marchaValida] - 1.0f) * FACTOR_MULTIPLICADOR_IA;
float aceleracionEfectiva = aceleracion * calcularFactorTraccion(marchaValida) * mult;
```

---

## RECOMENDACIÓN: **OPCIÓN A** (más limpia y predecible)

### Pasos de implementación:

1. **Crear `acelerarIA()` en `AutoRival.java`** que copie la lógica de `Auto.acelerar()` pero **SIN** la línea:
   ```java
   * MULTIPLICADOR_POTENCIA_POR_MARCHA[marchaValida]
   ```

2. **En `actualizarIA()`**, cambiar `acelerar(delta)` por `acelerarIA(delta)`

3. **Testear**: La IA ahora acelera "lineal" por marcha (solo límite de velocidad por marcha), jugador tiene ventaja en 1ra/2da

---

## VALORES DE REFERENCIA PARA TESTING

### Jugador (AutoConfig) - Aceleración calculada desde 0-100:
- GOL: 9.5s → `300/9.5 = 31.6`
- TOYOTA: 6.8s → `300/6.8 = 44.1`
- CHALLENGER: 4.5s → `300/4.5 = 66.7`

### Rivales (CatalogoRivales) - Aceleración DIRECTA:
- Principiante: 23
- Amateur: 28
- Callejero: 35
- Pro-Racer: 43
- Campeon: 58
- Novato Nocturno: 24
- Corredor Urbano: 29
- Apostador: 37
- Rey de la Pista: 47
- Lider de la Red: 63

**Observación:** Rivales de nivel medio-alto tienen aceleración **mayor** que autos equivalentes del jugador. Con Opción A se iguala porque IA no multiplica en 1ra/2da.

---

## ARCHIVOS A MODIFICAR

| Archivo | Cambio |
|---------|--------|
| `core/src/main/java/com/afs/dragbits/autos/AutoRival.java` | Sobrescribir `acelerar()` → `acelerarIA()` sin multiplicador de marcha |

---

## TESTING SUGERIDO

1. Carrera GOL (31.6 accel) vs Principiante (23 accel) → Jugador debe ganar en 1ra/2da, rival recupera en 3ra+
2. Carrera CHALLENGER (66.7 accel) vs Campeon (58 accel) → Jugador ventaja clara
3. Verificar que IA no se "atasca" en marcha baja (límite de velocidad por marcha sigue funcionando)

---

## NOTA SOBRE `calcularFactorTraccion()`
Este método **SÍ debe seguir aplicándose** a la IA (patinaje en 1ra/2da si potencia > tracción). No quitar esa llamada.