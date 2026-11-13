# Instrucciones para Windsurf: Física real de marchas (par motor por marcha)

**Contexto:** Se decidió **eliminar** la mecánica de "cambio perfecto" / "zona de sincronización" / vidas por fallar el cambio. Ese código ya existe en `Auto.java` pero **no se usa en ningún lado** (ningún HUD, IA, screen o controlador lo llama). Es código muerto aislado.

**Objetivo final:** Implementar multiplicador de potencia por marcha para que:
- Marchas bajas (1ra, 2da) = **más potencia/aceleración**, menos velocidad tope
- Marchas altas (4ta, 5ta) = **menos potencia/aceleración**, más velocidad tope
- Arrancar en 5ta sea **muy lento** (penalidad realista), obligando al jugador a escalar marchas

---

## Commit 0 — Eliminar código muerto: "cambio perfecto" / "zona de sincronización"

**Archivo:** `core/src/main/java/com/afs/dragbits/autos/Auto.java`

### 0.1 Borrar campo (línea ~30)
```java
// BORRAR esta línea:
protected float zonaSincronizacion;
```

### 0.2 Borrar inicialización (línea ~99)
```java
// BORRAR esta línea:
this.zonaSincronizacion = 400f;
```

### 0.3 Borrar método `esCambioPerfecto()` (líneas 250-252)
```java
// BORRAR este método completo:
public boolean esCambioPerfecto() {
    return (rpm >= (rpmMaximas - zonaSincronizacion));
}
```

### 0.4 Borrar setter `setZonaSincronizacion()` (línea ~331)
```java
// BORRAR este método:
public void setZonaSincronizacion(float zona) { this.zonaSincronizacion = zona; }
```

> **Verificación:** Compilar (`./gradlew :core:compileJava`). No debe haber errores — nada referencia estos miembros.

---

## Commit 1 — Agregar multiplicador de potencia por marcha en `Auto.java`

**Archivo:** `core/src/main/java/com/afs/dragbits/autos/Auto.java`

### 1.1 Agregar constante (cerca de línea 39, junto a `relacionesTransmision`)

```java
// NUEVO: multiplicador de par motor / potencia efectiva por marcha
// 1ra = 2.5x, 2da = 1.8x, 3ra = 1.2x, 4ta = 0.85x, 5ta = 0.55x
// Valores elegidos para que arrancar en 5ta sea ~5x más lento que en 1ra
private static final float[] MULTIPLICADOR_POTENCIA_POR_MARCHA = {
    1.0f,   // 0 = neutro (no se usa)
    2.50f,  // 1ra
    1.80f,  // 2da
    1.20f,  // 3ra
    0.85f,  // 4ta
    0.55f   // 5ta
};
```

> **Nota:** El índice 0 queda en 1.0f por seguridad (neutro no acelera). Ajustable en balanceo.

### 1.2 Usar el multiplicador en `acelerar()` (línea ~140)

**Buscar esta línea:**
```java
float aceleracionEfectiva = aceleracion * calcularFactorTraccion(marchaValida);
```

**Cambiarla por:**
```java
float aceleracionEfectiva = aceleracion 
    * calcularFactorTraccion(marchaValida) 
    * MULTIPLICADOR_POTENCIA_POR_MARCHA[marchaValida];
```

> Esto aplica el multiplicador **además** del factor de tracción existente (patinaje en 1ra/2da se conserva).

---

## Commit 2 — Ajustar IA rival para que no meta 5ta de arranque

**Archivo:** `core/src/main/java/com/afs/dragbits/autos/AutoRival.java`

El rival hoy sube marchas solo por RPM (`rpm >= rpmMaximas - 600`). Con la nueva física, si el rival mete 5ta muy temprano se arrastra. Agregar condición de **velocidad mínima por marcha** antes de subir.

### 2.1 Agregar array de velocidades mínimas (cerca de línea 12, tras `MARCHA_ACTIVACION_NITRO`)

```java
// NUEVO: velocidad mínima recomendada para entrar a cada marcha (en unidades del juego)
// Evita que la IA meta 5ta a baja velocidad y se arrastre
private static final float[] VELOCIDAD_MINIMA_MARCHA = {
    0f,      // 0 = neutro
    0f,      // 1ra: desde 0
    120f,    // 2da
    220f,    // 3ra
    320f,    // 4ta
    420f     // 5ta
};
```

> Valores orientativos; se ajustan en pruebas. Dependen de `velocidadMaxima` del auto rival.

### 2.2 Modificar `actualizarIA()` (línea ~46)

**Buscar:**
```java
if (rpm >= (rpmMaximas - 600f) && marchaActual < (relacionesTransmision.length - 1)) {
    marchaActual++;
    embraguePresionado = true;
    tiempoSiguienteCambio = 0.15f;
}
```

**Cambiar por:**
```java
boolean rpmLista = rpm >= (rpmMaximas - 600f);
boolean hayMarchaSuperior = marchaActual < (relacionesTransmision.length - 1);
boolean velocidadSuficiente = velocidad >= VELOCIDAD_MINIMA_MARCHA[marchaActual + 1];

if (rpmLista && hayMarchaSuperior && velocidadSuficiente) {
    marchaActual++;
    embraguePresionado = true;
    tiempoSiguienteCambio = 0.15f;
}
```

> Ahora la IA **no sube marcha** si no tiene velocidad suficiente para la siguiente, aunque las RPM pidan cambio.

---

## Commit 3 — Tests de balanceo / verificación manual

No es código, pero **pasos obligatorios** antes de cerrar la PR:

1. **Arranque en 1ra vs 5ta:** Parado, embrague, meter 1ra y acelerar → medir tiempo a 100 km/h equivalente. Repetir en 5ta. Debe ser **~5x más lento en 5ta**.
2. **Escalada 1→2→3→4→5 a RPM óptima:** Verificar que el auto acelera fuerte en bajas y estira en altas sin "huecos" de potencia.
3. **IA rival:** Correr 3-4 carreras. El rival no debe meter 5ta temprano y arrastrarse. Debe ser competitivo.
4. **Patinaje:** Confirmar que 1ra/2da siguen patinando si el auto tiene mucha aceleración y poca tracción (Gol, Toyota).
5. **Nitro:** Verificar que el nitro multiplica sobre la aceleración **ya modificada por marcha** (hoy hace `aceleracionEfectiva *= potenciaNitro` — correcto).
6. **Regresión:** Verificar que no queda ningún uso de `esCambioPerfecto`, `zonaSincronizacion` o `setZonaSincronizacion` en el codebase (`grep -r` en todo `core/src`).

---

## Valores de referencia para balanceo (Commit 1)

| Marcha | `relacionesTransmision` (velMax %) | `MULTIPLICADOR_POTENCIA_POR_MARCHA` | Efecto combinado |
|--------|-----------------------------------|-------------------------------------|------------------|
| 1ra    | 30%                               | 2.50x                               | Arranque brutal, tope bajo |
| 2da    | 50%                               | 1.80x                               | Fuerte, tope medio |
| 3ra    | 70%                               | 1.20x                               | Equilibrada |
| 4ta    | 85%                               | 0.85x                               | Estirada, menos push |
| 5ta    | 100%                              | 0.55x                               | Solo para velocidad final, arranque muy lento |

**Ratio 1ra/5ta en aceleración pura:** 2.50 / 0.55 ≈ **4.5x** — coincide con objetivo "realista".

---

## Archivos que NO tocar

- `CajaDeCambios.java` — solo maneja input de la palanca, no física.
- `Acelerador.java` — solo detecta input del pedal.
- `ControladorCarrera.java` — solo lógica de victoria/derrota.
- `Palanca.java` (HUD) — solo dibuja la posición visual.

---

## Checklist final antes de commit

- [ ] **Commit 0:** `Auto.java` — borrado `zonaSincronizacion`, `esCambioPerfecto()`, `setZonaSincronizacion()`
- [ ] **Commit 0:** Compila sin errores (`./gradlew :core:compileJava`)
- [ ] **Commit 1:** `Auto.java` — constante `MULTIPLICADOR_POTENCIA_POR_MARCHA` agregada
- [ ] **Commit 1:** `Auto.java` — `acelerar()` usa el multiplicador
- [ ] **Commit 2:** `AutoRival.java` — constante `VELOCIDAD_MINIMA_MARCHA` agregada
- [ ] **Commit 2:** `AutoRival.java` — `actualizarIA()` chequea velocidad antes de subir marcha
- [ ] **Commit 3:** Tests manuales pasan (puntos 1-6 arriba)
- [ ] **Commit 3:** `grep -r "esCambioPerfecto\|zonaSincronizacion" core/src` → **0 resultados**
- [ ] Build completo pasa (`./gradlew build`)