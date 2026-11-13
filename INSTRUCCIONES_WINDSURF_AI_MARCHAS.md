# Instrucciones para Windsurf: Arreglar la IA del Bot (Cambios de Marcha)

## Problema Actual
El bot arranca bien en 1ra y 2da, pero cuando sube a 3ra/4ta/5ta marcha:
- El **multiplicador de potencia cae drásticamente** (1.2x → 0.85x → 0.55x)
- El bot **no reduce marcha** cuando se queda sin aceleración
- Termina "atrapado" en una marcha alta con poca potencia → fácil de ganar

## Archivos a Modificar
- `core/src/main/java/com/afs/dragbits/autos/AutoRival.java` ← **Principal**
- `core/src/main/java/com/afs/dragbits/autos/Auto.java` (referencia de constantes)

---

## Paso 1: Entender la Lógica Actual (AutoRival.java líneas 40-70)

```java
public void actualizarIA(float delta, EstadoSemaforo estadoSemaforo) {
    // Solo sube marcha, NUNCA baja
    if (rpmLista && hayMarchaSuperior && velocidadSuficiente) {
        marchaActual++;
        embraguePresionado = true;
        tiempoSiguienteCambio = 0.15f;
    }
}
```

**Falta:**
1. ✅ Detección de "estancamiento" (aceleración muy baja en marcha alta)
2. ✅ **Downshift** (reducir marcha) cuando no acelera
3. ✅ Uso inteligente del nitro (no solo en marcha ≥ 3)
4. ✅ Variabilidad/aleatoriedad para que no sea predecible

---

## Paso 2: Agregar Constantes en AutoRival.java

```java
// NUEVO: Umbral de RPM para considerar downshift (bajo = motor "ahogado")
private static final float UMBRAL_RPM_DOWNSHIFT = 2500f;  // RPM bajo = poca potencia

// NUEVO: Tiempo mínimo en una marcha antes de permitir downshift (evita ping-pong)
private static final float TIEMPO_MINIMO_MARCHA = 1.0f;   // segundos
private float tiempoEnMarchaActual = 0f;

// NUEVO: Factor de aleatoriedad para que el bot no sea robótico
private static final float FACTOR_VARIABILIDAD = 0.15f;   // ±15% en decisiones

// NUEVO: Umbral de aceleración mínima esperada (si está por debajo → downshift)
private static final float ACELERACION_MINIMA_ESPERADA = 8f;  // ajustar según testing
```

---

## Paso 3: Reescribir `actualizarIA` Completo

```java
public void actualizarIA(float delta, EstadoSemaforo estadoSemaforo) {
    // 1. ARRANQUE: meter 1ra en verde
    if (estadoSemaforo == EstadoSemaforo.VERDE && marchaActual == 0) {
        marchaActual = 1;
        tiempoEnMarchaActual = 0f;
    }

    if (marchaActual > 0) {
        // 2. MANEJO EMBRAGUE (tiempo de cambio)
        if (embraguePresionado) {
            tiempoSiguienteCambio -= delta;
            if (tiempoSiguienteCambio <= 0) {
                embraguePresionado = false;
            }
        } else {
            // 3. ACUMULAR tiempo en marcha actual (para anti-ping-pong)
            tiempoEnMarchaActual += delta;

            // 4. NITRO INTELIGENTE: usarlo si vamos perdiendo o en recta final
            boolean debeUsarNitro = decidirUsoNitro(delta);
            if (debeUsarNitro && !nitroActivo && nitroRestante > 0) {
                nitroActivo = true;
            }

            // 5. ACELERAR (física normal)
            acelerar(delta);

            // 6. CALCULAR aceleración real de este frame
            float aceleracionReal = calcularAceleracionReal(delta);

            // 7. DECISIONES DE CAMBIO DE MARCHA
            evaluarCambiosMarcha(aceleracionReal, delta);
        }
    }

    actualizar(delta);
}
```

---

## Paso 4: Método `calcularAceleracionReal`

```java
private float velocidadAnterior = 0f;

private float calcularAceleracionReal(float delta) {
    float aceleracionReal = (velocidad - velocidadAnterior) / delta;
    velocidadAnterior = velocidad;
    return Math.max(0f, aceleracionReal);  // solo nos interesa si acelera hacia adelante
}
```

---

## Paso 5: Método `evaluarCambiosMarcha` (EL CORAZÓN DEL FIX)

```java
private void evaluarCambiosMarcha(float aceleracionReal, float delta) {
    boolean rpmLista = rpm >= (rpmMaximas - 600f);
    boolean hayMarchaSuperior = marchaActual < (relacionesTransmision.length - 1);
    boolean hayMarchaInferior = marchaActual > 1;
    boolean velocidadSuficiente = velocidad >= VELOCIDAD_MINIMA_MARCHA[marchaActual + 1];
    boolean rpmBajaParaMarcha = rpm < UMBRAL_RPM_DOWNSHIFT;
    boolean aceleracionInsuficiente = aceleracionReal < ACELERACION_MINIMA_ESPERADA;
    boolean tiempoMinimoCumplido = tiempoEnMarchaActual >= TIEMPO_MINIMO_MARCHA;

    // ===== UPSHIFT (subir marcha) =====
    // Solo si: RPM alta + hay marcha superior + velocidad suficiente + variabilidad
    if (rpmLista && hayMarchaSuperior && velocidadSuficiente && tiempoMinimoCumplido) {
        // Variabilidad: bot a veces "duda" y no sube inmediato
        if (Math.random() > FACTOR_VARIABILIDAD) {
            subirMarcha();
            return;  // un cambio por frame
        }
    }

    // ===== DOWNSHIFT (bajar marcha) - NUEVO =====
    // Si: RPM baja + aceleración insuficiente + hay marcha inferior + tiempo mínimo
    if (rpmBajaParaMarcha && aceleracionInsuficiente && hayMarchaInferior && tiempoMinimoCumplido) {
        // Más agresivo en marchas altas (3→2, 4→3, 5→4)
        float probabilidadDownshift = (marchaActual >= 4) ? 0.9f : 0.7f;
        if (Math.random() < probabilidadDownshift) {
            bajarMarcha();
            return;
        }
    }

    // ===== PROTECCIÓN: Si en neutro por error, meter 1ra =====
    if (marchaActual == 0 && velocidad > 10f) {
        marchaActual = 1;
        tiempoEnMarchaActual = 0f;
    }
}

private void subirMarcha() {
    marchaActual++;
    embraguePresionado = true;
    tiempoSiguienteCambio = 0.15f;
    tiempoEnMarchaActual = 0f;
}

private void bajarMarcha() {
    marchaActual--;
    embraguePresionado = true;
    tiempoSiguienteCambio = 0.2f;  // un pelín más lento al bajar
    tiempoEnMarchaActual = 0f;
}
```

---

## Paso 6: Método `decidirUsoNitro` (Nitro Inteligente)

```java
private boolean decidirUsoNitro(float delta) {
    // No usar nitro si ya está activo
    if (nitroActivo) return false;

    // Usar nitro si:
    // 1. Estamos en marcha 3+ (potencia base decente)
    // 2. O si vamos perdiendo contra el jugador (necesita acceso al jugador)
    // 3. O aleatoriamente en marchas altas para presión

    boolean enMarchaAlta = marchaActual >= 3;
    boolean rpmBuena = rpm > (rpmMaximas * 0.6f);  // motor en zona de potencia

    // Para que el bot "vea" al jugador, necesitas pasar la referencia
    // Opción A: Agregar campo `AutoJugador objetivo` al constructor
    // Opción B: Pasar `posXJugador` como parámetro a actualizarIA

    // Versión simple (sin ver jugador):
    return enMarchaAlta && rpmBuena && (Math.random() < 0.3f);  // 30% chance
}
```

---

## Paso 7: Modificar Constructor para Recibir Referencia al Jugador (Opcional pero Recomendado)

```java
// En AutoRival.java - agregar campo
private AutoJugador objetivoJugador;

// Constructor modificado
public AutoRival(float posX, float posY, RivalConfig config, int recompensa, Main mainGame, AutoJugador objetivoJugador) {
    super(posX, posY, config.getVelocidadMaxima(), config.getAceleracion(), config.getTraccion(), config.getRutaSprite(), mainGame);
    this.recompensa = recompensa;
    this.capacidadNitro = config.getNitroSegundos();
    this.nitroRestante = config.getNitroSegundos();
    this.objetivoJugador = objetivoJugador;
}

// En decidirUsoNitro():
private boolean decidirUsoNitro(float delta) {
    if (nitroActivo || nitroRestante <= 0) return false;

    boolean enMarchaAlta = marchaActual >= 3;
    boolean rpmBuena = rpm > (rpmMaximas * 0.6f);

    // NUEVO: Usar nitro si el jugador nos está ganando por poco
    boolean jugadorCerca = false;
    if (objetivoJugador != null) {
        float distancia = objetivoJugador.getFrenteX() - this.getFrenteX();
        jugadorCerca = distancia > 0 && distancia < 500f;  // jugador adelante pero cerca
    }

    return (enMarchaAlta && rpmBuena && (Math.random() < 0.3f)) || jugadorCerca;
}
```

---

## Paso 8: Ajustar `ControladorCarrera.java` para Pasar la Referencia

En `ControladorCarrera` constructor, pasar `AUTO_JUGADOR` al crear `AutoRival`:

```java
// Buscar donde se crea el AutoRival y cambiar a:
this.AUTO_RIVAL = new AutoRival(..., AUTO_JUGADOR);
```

---

## Paso 9: Ajustar Constantes de `Auto.java` (Referencia)

Verifica que `MULTIPLICADOR_POTENCIA_POR_MARCHA` en `Auto.java` (líneas 43-50) sea:
```java
private static final float[] MULTIPLICADOR_POTENCIA_POR_MARCHA = {
    1.0f,   // 0 = neutro
    2.50f,  // 1ra
    1.80f,  // 2da
    1.20f,  // 3ra
    0.85f,  // 4ta
    0.55f   // 5ta  ← MUY BAJO, por eso el bot se arrastra
};
```

**Sugerencia:** Si sigue muy fácil, sube ligeramente 4ta y 5ta:
```java
0.95f,  // 4ta (era 0.85)
0.70f   // 5ta (era 0.55)
```

---

## Paso 10: Test y Ajuste Fino

### Valores para empezar (en `AutoRival.java`):
```java
private static final float UMBRAL_RPM_DOWNSHIFT = 2500f;
private static final float TIEMPO_MINIMO_MARCHA = 1.0f;
private static final float ACELERACION_MINIMA_ESPERADA = 8f;
private static final float FACTOR_VARIABILIDAD = 0.15f;
```

### Qué observar en juego:
1. **Bot en 3ra/4ta/5ta**: ¿Reduce marcha cuando RPM caen < 2500?
2. **Arranque**: ¿Meten 1ra suave o "revolucionan" en neutro?
3. **Nitro**: ¿Lo usan en momentos clave (recta final, adelantamiento)?
4. **Variabilidad**: ¿A veces dudan antes de subir/bajar? (no robótico)

### Ajustes típicos:
| Síntoma | Ajuste |
|---------|--------|
| Bot reduce marcha muy seguido (ping-pong) | Subir `TIEMPO_MINIMO_MARCHA` a 1.5-2.0s |
| Bot nunca reduce | Bajar `UMBRAL_RPM_DOWNSHIFT` a 2000f o subir `ACELERACION_MINIMA_ESPERADA` |
| Bot sube muy agresivo | Subir `FACTOR_VARIABILIDAD` a 0.25f |
| Bot usa nitro mal | Ajustar `decidirUsoNitro()` con más condiciones |

---

## Resumen de Cambios Mínimos (Si no quieres reescribir todo)

Si solo quieres el fix esencial, cambia **solo** `AutoRival.java` en `actualizarIA`:

```java
// AGREGAR campos:
private float tiempoEnMarchaActual = 0f;
private float velocidadAnterior = 0f;
private static final float UMBRAL_RPM_DOWNSHIFT = 2500f;
private static final float TIEMPO_MINIMO_MARCHA = 1.0f;
private static final float ACELERACION_MINIMA_ESPERADA = 8f;

// EN actualizarIA, DESPUÉS de acelerar(delta):
tiempoEnMarchaActual += delta;
float aceleracionReal = (velocidad - velocidadAnterior) / delta;
velocidadAnterior = velocidad;

// AGREGAR antes del final (dentro del else !embraguePresionado):
boolean rpmBaja = rpm < UMBRAL_RPM_DOWNSHIFT;
boolean acelInsuficiente = aceleracionReal < ACELERACION_MINIMA_ESPERADA;
boolean puedeBajar = marchaActual > 1 && tiempoEnMarchaActual >= TIEMPO_MINIMO_MARCHA;

if (rpmBaja && acelInsuficiente && puedeBajar) {
    marchaActual--;
    embraguePresionado = true;
    tiempoSiguienteCambio = 0.2f;
    tiempoEnMarchaActual = 0f;
}
```

---

## Archivos Creados/Modificados
- ✅ `core/src/main/java/com/afs/dragbits/autos/AutoRival.java` — Lógica IA completa
- ⚠️ `core/src/main/java/com/afs/dragbits/funcionalidades/ControladorCarrera.java` — Si agregas referencia al jugador
- ⚠️ `core/src/main/java/com/afs/dragbits/autos/Auto.java` — Solo si ajustas multiplicadores

---

## Cómo Probar en Windsurf
1. Abre el proyecto en IntelliJ con Windsurf
2. Aplica los cambios en `AutoRival.java`
3. Run → `Lwjgl3Launcher` (o tu main class)
4. Juega una carrera contra bot nivel 3-4 (Callejero/Pro-Racer)
5. Observa: ¿El bot reduce marcha en 3ra/4ta cuando se "ahoga"?
6. Ajusta constantes según tabla de "Ajustes típicos"

---

**Nota:** El fix clave es el **downshift basado en RPM baja + aceleración insuficiente**. Sin eso, el bot física-matemáticamente no puede acelerar en 5ta (0.55x potencia) una vez que pierde inercia.