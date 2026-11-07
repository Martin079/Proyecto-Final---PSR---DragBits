# Fix Críticos: Tienda de Autos

## Bugs Identificados

### Bug 1: AutoConfig stats NO coinciden con NOTAS.md
**Archivo:** `core/src/main/java/com/afs/dragbits/autos/AutoConfig.java`

| Auto | NOTAS.md (Correcto) | Actual (Incorrecto) |
|------|---------------------|---------------------|
| **Gol** | $160 km/h, 9.5s, 60%, Sin Nitro | 180f, 12.5f, 0.7f, 3f/3c |
| **Toyota** | $15,000, Nvl 7, 205 km/h, 6.8s, 75%, Nitro 1c/2s | $5,000, Nvl 3, 220f, 9.5f, 0.8f, 4f/5c |
| **Challenger** | $45,000, Nvl 14, 255 km/h, 4.5s, 85%, Nitro 2c/3s | $15,000, Nvl 5, 280f, 6.5f, 0.9f, 5f/7c |

### Bug 2: Doble llamada a `comprarAuto()` rompe guardado
**Archivos:** `VentanaTiendaAutos.java` (línea 189) + `MapaScreen.java` (línea 95)

Flujo actual roto:
```
1. Click en celda → VentanaTiendaAutos.crearCeldaAuto() línea 189: jugador.comprarAuto(tipo) → TRUE (resta dinero, cambia autoActual)
2. Callback alComprarAuto.accept(tipo) → MapaScreen línea 95: jugador.comprarAuto(tipo) → FALSE (ya comprado, no resta dinero, no guarda)
3. ventanaTienda.mostrar(jugador) se ejecuta PERO REPOSITORIO_JUGADOR.guardarProgreso(jugador) NO se ejecuta
4. Al salir → MapaScreen.hide() llama dispose() → progress NO guardado
```

### Bug 3: Doble carga en `MapaScreen.show()` sobrescribe cambios en memoria
**Archivo:** `MapaScreen.java` líneas 57 y 69-71

```java
// Constructor (línea 57)
this.jugador = REPOSITORIO_JUGADOR.cargarJugador();  // Carga 1

// show() (líneas 69-71)
if (jugador != null) {
    REPOSITORIO_JUGADOR.cargarProgreso(jugador);    // Carga 2 - SOBREESCRIBE cambios no guardados
}
```

Si compras en tienda → callback guarda → pero al volver a MapaScreen (p.ej. tras carrera) el `show()` recarga de disco y pierde la compra.

---

## Commits de Fix

### Commit 1: "fix: Corregir stats AutoConfig según NOTAS.md"
**Archivo:** `core/src/main/java/com/afs/dragbits/autos/AutoConfig.java`

```java
public static AutoConfig gol() {
    return new AutoConfig(
        AutoJugadorTipo.GOL,
        0,           // Gratis
        1,           // Nivel 1
        160f,        // V-Max 160 km/h
        9.5f,        // 0-100: 9.5s
        60f,         // Tracción 60% (NOTAS usa %, Auto usa valor absoluto)
        0f,          // Sin Nitro
        0
    );
}

public static AutoConfig toyota() {
    return new AutoConfig(
        AutoJugadorTipo.TOYOTA,
        15000,       // $15,000
        7,           // Nivel 7
        205f,        // V-Max 205 km/h
        6.8f,        // 0-100: 6.8s
        75f,         // Tracción 75%
        2f,          // Nitro 2s
        1            // 1 carga
    );
}

public static AutoConfig challenger() {
    return new AutoConfig(
        AutoJugadorTipo.CHALLENGER,
        45000,       // $45,000
        14,          // Nivel 14
        255f,        // V-Max 255 km/h
        4.5f,        // 0-100: 4.5s
        85f,         // Tracción 85%
        3f,          // Nitro 3s
        2            // 2 cargas
    );
}
```

**Nota:** Tracción en NOTAS.md es % (60%, 75%, 85%), en Auto.java se usa como valor absoluto (60f, 75f, 85f). La fórmula `calcularFactorTraccion` usa `aceleracion / traccion`, así que valores altos = mejor tracción.

---

### Commit 2: "fix: Eliminar doble llamada comprarAuto - solo callback guarda"
**Archivo:** `core/src/main/java/com/afs/dragbits/menutienda/VentanaTiendaAutos.java`

**Cambio en `crearCeldaAuto()` líneas 185-198:**
```java
// ANTES (roto):
if (puedeComprar) {
    celda.addListener(new InputListener() {
        @Override
        public boolean touchDown(...) {
            if (jugador.comprarAuto(tipo)) {      // ← LLAMADA 1: compra y gasta dinero
                if (alComprarAuto != null) {
                    alComprarAuto.accept(tipo);    // ← Callback llama a comprarAuto OTRA VEZ
                }
                mostrar(jugador);
            }
            return true;
        }
    });
}

// DESPUÉS (fix):
if (puedeComprar) {
    celda.addListener(new InputListener() {
        @Override
        public boolean touchDown(...) {
            // Solo notificar - NO comprar aquí
            if (alComprarAuto != null) {
                alComprarAuto.accept(tipo);        // ← Callback hace la compra Y guarda
            }
            return true;
        }
    });
}
```

**Archivo:** `core/src/main/java/com/afs/dragbits/screens/MapaScreen.java`

**Cambio en callback `ventanaTienda` constructor (líneas 94-100):**
```java
// ANTES (roto - doble compra):
(tipo) -> {
    if (jugador.comprarAuto(tipo)) {           // ← Segunda llamada falla
        REPOSITORIO_JUGADOR.guardarProgreso(jugador);
        ventanaTienda.mostrar(jugador);
        Gdx.app.log("MapaScreen", "Auto comprado: " + tipo.getNombre());
    }
}

// DESPUÉS (fix - compra una vez y guarda):
(tipo) -> {
    if (jugador.comprarAuto(tipo)) {           // ← Primera y única llamada
        REPOSITORIO_JUGADOR.guardarProgreso(jugador);  // ← Guardar INMEDIATAMENTE
        ventanaTienda.mostrar(jugador);        // ← Refrescar UI con datos guardados
        Gdx.app.log("MapaScreen", "Auto comprado y guardado: " + tipo.getNombre());
    } else {
        Gdx.app.log("MapaScreen", "Compra fallida: " + tipo.getNombre());
    }
}
```

---

### Commit 3: "fix: Eliminar doble carga en MapaScreen.show()"
**Archivo:** `core/src/main/java/com/afs/dragbits/screens/MapaScreen.java`

**Opción A (Recomendada): Eliminar carga del constructor, solo en show()**
```java
// Constructor - ELIMINAR línea 57:
public MapaScreen(Game game, Main mainGame) {
    this.GAME = game;
    this.MAIN_GAME = mainGame;
    this.REPOSITORIO_JUGADOR = new RepositorioJugador();
    // this.jugador = REPOSITORIO_JUGADOR.cargarJugador();  ← BORRAR
    this.jugador = new Jugador();  // Placeholder, se carga en show()
}

// show() - MANTENER solo esta carga (líneas 69-71):
@Override
public void show() {
    // ... código anterior ...
    if (jugador != null) {
        REPOSITORIO_JUGADOR.cargarProgreso(jugador);  // ← Única carga
    }
    // ... resto ...
}
```

**Opción B: Eliminar carga en show(), mantener solo en constructor**
```java
// Constructor - MANTENER carga (línea 57)
this.jugador = REPOSITORIO_JUGADOR.cargarJugador();

// show() - ELIMINAR líneas 69-71:
// if (jugador != null) {
//     REPOSITORIO_JUGADOR.cargarProgreso(jugador);
// }
```

**Recomendación: Opción A** - La carga en `show()` es más segura porque se ejecuta cada vez que se vuelve a la pantalla (tras carrera, tras tienda, etc.), asegurando datos frescos.

---

### Commit 4: "fix: Asegurar guardado al salir de MapaScreen (hide/dispose)"
**Archivo:** `core/src/main/java/com/afs/dragbits/screens/MapaScreen.java`

Añadir guardado defensivo en `hide()`:
```java
@Override
public void hide() {
    // Guardar progreso antes de salir (defensivo)
    if (jugador != null) {
        REPOSITORIO_JUGADOR.guardarProgreso(jugador);
    }
    Gdx.input.setInputProcessor(null);
    dispose();
}
```

---

### Commit 5: "fix: Ajustar AutoJugador.calcularAceleracionDesde0a100 para tracción en %"
**Archivo:** `core/src/main/java/com/afs/dragbits/autos/AutoJugador.java`

Verificar fórmula con nuevos valores (tracción 60f, 75f, 85f):
```java
private static float calcularAceleracionDesde0a100(float segundos0a100) {
    if (segundos0a100 <= 0) return 50f;
    // Fórmula: 300 / segundos → Gol: 300/9.5=31.6, Toyota: 300/6.8=44.1, Challenger: 300/4.5=66.7
    // Con tracción 60/75/85, la relación aceleración/traccion dará factor correcto
    return 300f / segundos0a100;
}
```

Si el comportamiento no es correcto, ajustar constante (300f → 250f o 350f) tras testear.

---

## Resumen de Cambios por Archivo

| Archivo | Commits |
|---------|---------|
| `AutoConfig.java` | Commit 1 (stats correctas) |
| `VentanaTiendaAutos.java` | Commit 2 (quitar compra local) |
| `MapaScreen.java` | Commit 2 (callback compra+guarda), Commit 3 (doble carga), Commit 4 (guardado en hide) |
| `AutoJugador.java` | Commit 5 (verificar fórmula) |

---

## Test Checklist Post-Fix

- [ ] Partida nueva: Gol gratis, Toyota/Challenger rojos (nivel 1)
- [ ] Nivel 7 + $15,000: Toyota gris (comprable), Challenger rojo
- [ ] Click Toyota → compra, gasta $15k, pone verde, autoActual=TOYOTA, **guarda en disco**
- [ ] Salir tienda → volver a entrar → Toyota verde (persistido)
- [ ] Entrar carrera → auto es Toyota (sprite + stats 205km/h, 6.8s, 75%, nitro 2s)
- [ ] Ganar carrera → ganar dinero → volver a mapa → dinero actualizado
- [ ] Cerrar juego → reabrir → Toyota equipado, $15k gastados, nivel correcto
- [ ] Nivel 14 + $45,000: Challenger comprable → mismo flujo
- [ ] Cambiar a Gol en tienda (si se implementa equipar) → carrera usa Gol

---

## +changelog last version