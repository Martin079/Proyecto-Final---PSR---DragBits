# Instrucciones de correcciones para DragBits

> **Contexto**: Basado en la devolución del proyecto. Objetivo: corregir los 3 puntos señalados para avanzar la 2da preentrega.
> **Workflow**: IntelliJ + Windsurf. Cada ítem = un commit atómico con mensaje convencional. Windsurf actualiza CHANGELOG automáticamente al hacer commit.
> **Chat**: Continuar en **este mismo chat** para mantener contexto; no abrir chat nuevo.

---

## Resumen de cambios necesarios

| # | Problema | Archivos principales |
|---|----------|----------------------|
| 1 | Efectos de sonido referencian archivos que no existen → **eliminar carga y llamadas** | `GestorDeAudio.java`, `Auto.java` |
| 2 | Volumen GAMEPLAY no se aplica a efectos → **conectar `volumenGameplay` a `GestorDeAudio`** | `Main.java`, `GestorDeAudio.java`, `AudioMenuScreen.java` |
| 3 | Ajustes menores: `EntradaJugador` en AudioMenuScreen, ESC doble en pausa, CHANGELOG | `AudioMenuScreen.java`, `GameScreen.java`, `CHANGELOG.md` |

---

## PASO A PASO — UN COMMIT POR ÍTEM

### Commit 1: Eliminar carga de efectos de sonido inexistentes

**Archivos**: `core/src/main/java/com/afs/dragbits/audio/GestorDeAudio.java`

**Cambios**:
- Eliminar `cargarEfectos()` y su llamada en el constructor.
- Eliminar `cargarSonidoSiExiste()`.
- Eliminar campo `SONIDOS` (ObjectMap<String, Sound>).
- Eliminar método `reproducirEfecto(String)`.
- Mantener solo: música de fondo, volumen maestro, mute, modificador de pantalla.

**Mensaje de commit**:
```
fix(audio): remove missing sound effects loading

- Delete cargarEfectos(), cargarSonidoSiExiste(), SONIDOS map
- Delete reproducirEfecto() — referenced files don't exist in repo
- Keep music playback, master volume, mute, screen modifier
- Sound effects can be re-added later when assets are available
```

---

### Commit 2: Eliminar reproducción de sonido de motor en Auto (usa GestorDeAudio)

**Archivos**: `core/src/main/java/com/afs/dragbits/autos/Auto.java`

**Cambios**:
- Eliminar campo `sonidoMotor`, `idMotor`, `estaAcelerando`.
- Eliminar método `actualizarAudioMotor()` y su llamada en `actualizar(float)`.
- Eliminar `apagarMotor()` (ya no hace falta parar loop de Sound).
- En `dispose()`: quitar `sonidoMotor.dispose()`.
- **Nota**: El sonido de motor ahora se manejará centralizadamente desde `GestorDeAudio` en un futuro; por ahora queda mudo.

**Mensaje de commit**:
```
fix(audio): remove per-car motor sound loop

- Delete sonidoMotor, idMotor, estaAcelerando fields
- Delete actualizarAudioMotor() and call from actualizar()
- Delete apagarMotor() and sonidoMotor.dispose() in dispose()
- Motor sound will be handled by GestorDeAudio centrally later
```

---

### Commit 3: Conectar volumen GAMEPLAY a GestorDeAudio

**Archivos**: 
- `core/src/main/java/com/afs/dragbits/Main.java`
- `core/src/main/java/com/afs/dragbits/audio/GestorDeAudio.java`
- `core/src/main/java/com/afs/dragbits/screens/AudioMenuScreen.java`

**Cambios en `GestorDeAudio.java`**:
- Añadir campo `private float volumenGameplay = 1.0f;`
- Añadir `setVolumenGameplay(float)` y `getVolumenGameplay()`.
- Modificar `getVolumenEfectivo()` para que devuelva `volumenMaestro * modificadorPantalla * volumenGameplay` (si no muteado).
- `actualizarVolumenMusica()` **no** usa `volumenGameplay` (solo música usa volumen maestro + modificador).
- El método `reproducirEfecto()` (eliminado en commit 1) ya no existe; cuando se reimplemente, usará `getVolumenEfectivo()`.

**Cambios en `Main.java`**:
- En `setVolumenGameplay(float)`: además de guardar en campo, llamar `gestorDeAudio.setVolumenGameplay(volumenGameplay)`.

**Cambios en `AudioMenuScreen.java`**:
- La opción "GAMEPLAY" ya modifica `MAIN_GAME.setVolumenGameplay()` → ahora propagará a `GestorDeAudio`.

**Mensaje de commit**:
```
feat(audio): connect GAMEPLAY volume to GestorDeAudio

- GestorDeAudio: add volumenGameplay field, setter/getter
- getVolumenEfectivo() now includes volumenGameplay factor
- Main.setVolumenGameplay() propagates to GestorDeAudio
- Music volume unaffected (uses master + screen modifier only)
- AudioMenuScreen GAMEPLAY slider now works end-to-end
```

---

### Commit 4: Fix EntradaJugador en AudioMenuScreen (usar teclas, no polling)

**Archivos**: `core/src/main/java/com/afs/dragbits/screens/AudioMenuScreen.java`

**Problema**: El `InputAdapter` anónimo en `show()` hace polling de teclas (UP/DOWN/LEFT/RIGHT/ENTER/ESCAPE) duplicando lógica que ya está en `EntradaJugador`.

**Cambios**:
- En `show()`: crear `EntradaJugador entrada = new EntradaJugador();` y `Gdx.input.setInputProcessor(entrada);`
- En `render()`: usar `entrada.consumirArriba()`, `entrada.consumirAbajo()`, `entrada.consumirConfirmar()`, `entrada.consumirCancelar()` en lugar del polling manual.
- Eliminar el `InputAdapter` anónimo completo.
- Mover lógica de navegación a `render()` consumiendo eventos de `EntradaJugador`.

**Mensaje de commit**:
```
fix(input): use EntradaJugador in AudioMenuScreen

- Replace anonymous InputAdapter with EntradaJugador instance
- Consume navigation via consumirArriba/Abajo/Confirmar/Cancelar
- Remove duplicate key polling logic
- Consistent input handling across all screens
```

---

### Commit 5: Fix doble consumo de ESC en pausa (GameScreen)

**Archivos**: `core/src/main/java/com/afs/dragbits/screens/GameScreen.java`

**Problema**: En `render()`, `entradaJugador.consumirCancelar()` se llama **dos veces**:
1. Línea 115: para pausar/despausar.
2. Línea 133: dentro del bloque `if (estadoJuego == EstadoJuego.PAUSADO)`.

Esto hace que al presionar ESC una vez en pausa, se consuma en la primera llamada y la segunda no haga nada, pero rompe el flujo si se aprieta rápido.

**Cambios**:
- Eliminar la segunda llamada (línea 133).
- La primera llamada (línea 115) ya alterna entre EN_CURSO ↔ PAUSADO.
- Dentro del bloque PAUSADO, **no** volver a consumir cancelar; usar solo toque para botones Reanudar/Abandonar.

**Mensaje de commit**:
```
fix(input): remove duplicate ESC consumption in pause

- Delete second consumirCancelar() inside PAUSADO block
- First call at top of render() already toggles EN_CURSO <-> PAUSADO
- Pause menu now only uses touch for Resume/Abandon buttons
```

---

### Commit 6: Ajustar títulos y fechas en CHANGELOG

**Archivos**: `CHANGELOG.md`

**Problemas detectados**:
- Versión `[0.8.9] - 2026-10-09` usa sección `### Updated` (no estándar; debe ser `Changed` o `Fixed`).
- Fecha `2026-10-09` duplicada en 0.8.9 y 0.8.8.
- Sección `[Unreleased]` vacía — mover cambios pendientes allí.

**Cambios**:
- Renombrar `### Updated` → `### Changed` en 0.8.9.
- Ajustar fecha de 0.8.9 a `2026-10-10` (o siguiente día lógico).
- En `[Unreleased]` añadir entradas preliminares para los fixes de esta sesión:
  - `Fixed: remove missing sound effects loading`
  - `Fixed: connect GAMEPLAY volume to GestorDeAudio`
  - `Fixed: use EntradaJugador in AudioMenuScreen`
  - `Fixed: remove duplicate ESC consumption in pause`

**Mensaje de commit**:
```
chore(changelog): fix section names and dates

- 0.8.9: Updated -> Changed, date 2026-10-10
- Add Unreleased entries for current fixes
- Keep Keep a Changelog format (Added/Changed/Fixed/Removed)
```

---

## VERIFICACIÓN POST-COMMITS

Ejecutar en terminal (desde `C:\DragBits`):

```bash
# Compilar y testear que no hay errores
./gradlew :core:compileJava

# Ejecutar juego (desktop)
./gradlew :lwjgl3:run
```

**Checklist manual**:
- [ ] Menú → Audio → slider GAMEPLAY cambia valor y persiste al volver.
- [ ] ESC en pausa: una pulsación pausa/despausa, no doble consumo.
- [ ] AudioMenuScreen navega con teclas (arriba/abajo/enter/esc) sin polling.
- [ ] Juego compila y arranca sin errores de assets faltantes.
- [ ] CHANGELOG renderiza bien en GitHub (secciones correctas).

---

## NOTAS PARA WINDSURF

- Windsurf detecta commits convencionales (`fix:`, `feat:`, `chore:`) y **agrega entradas al CHANGELOG automáticamente** en `[Unreleased]`.
- Después de cada commit, revisar que Windsurf haya añadido la línea correspondiente en `CHANGELOG.md` bajo `[Unreleased]`.
- Si no lo hace, añadir manualmente antes del siguiente commit.
- No hacer `git commit --amend` tras push; mejor commit nuevo `chore(changelog): ...`.

---

## PRÓXIMOS PASOS (fuera de scope esta devolución)

- Buscar/crear archivos `cambio_marcha.wav`, `patinaje.wav`, `victoria.wav`, `derrota.wav` en `assets/audio/Sonidos/`.
- Reimplementar `reproducirEfecto()` en `GestorDeAudio` y llamarlo desde:
  - `CajaDeCambios` al cambiar marcha → "cambio_marcha"
  - `Auto.calcularFactorTraccion()` cuando `patinando=true` → "patinaje"
  - `ControladorCarrera` al finalizar → "victoria"/"derrota"
- Añadir sonido de motor centralizado en `GestorDeAudio` (loop global) en vez de por Auto.

---