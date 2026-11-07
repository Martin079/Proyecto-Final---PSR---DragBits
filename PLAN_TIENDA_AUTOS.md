# Plan de Implementación: Tienda de Autos

## Resumen
Implementar una tienda de autos (Toyota y Challenger) accesible desde el mapa de la ciudad, con interfaz estilo "VentanaSeleccionRival", sistema de colores (verde=adquirido, gris=comprable, rojo=bloqueado por nivel/dinero), y cambio automático de auto al comprar.

## Stats de los Autos (desde NOTAS.md)

| Auto | Costo | Nivel Req | V-Max | 0-100 | Tracción | Nitro |
|------|-------|-----------|-------|-------|----------|-------|
| **Gol** (Base) | Gratis | 1 | 160 km/h | 9.5s | 60% | No |
| **Toyota** (Coupe Sport) | $15,000 | 7 | 205 km/h | 6.8s | 75% | Sí (1 carga, 2s) |
| **Challenger** (Supercar) | $45,000 | 14 | 255 km/h | 4.5s | 85% | Sí (2 cargas, 3s) |

---

## Commits Propuestos

### Commit 1: "feat: Añadir enum AutoJugadorTipo y modelo AutoConfig para stats de tienda"
**Archivos:** Nuevo `core/src/main/java/com/afs/dragbits/autos/AutoJugadorTipo.java`, Nuevo `core/src/main/java/com/afs/dragbits/autos/AutoConfig.java`

Crear enum para identificar los autos del jugador y clase de configuración con stats, costo, nivel requerido, ruta de sprite, y capacidad de nitro.

```java
// AutoJugadorTipo.java
public enum AutoJugadorTipo {
    GOL(0, "Gol", "sprites/Autos/Jugador/gol-sheet.png"),
    TOYOTA(1, "Toyota", "sprites/Autos/Jugador/toyota-sheet.png"),
    CHALLENGER(2, "Challenger", "sprites/Autos/Jugador/challenger-sheet.png");

    private final int id;
    private final String nombre;
    private final String rutaSprite;

    AutoJugadorTipo(int id, String nombre, String rutaSprite) {
        this.id = id;
        this.nombre = nombre;
        this.rutaSprite = rutaSprite;
    }
    // getters...
}

// AutoConfig.java - Stats completas de cada auto
public class AutoConfig {
    private final AutoJugadorTipo tipo;
    private final long costo;
    private final int nivelRequerido;
    private final float velocidadMaxima;
    private final float aceleracion; // 0-100 en segundos (inverso para fórmula)
    private final float traccion;
    private final float capacidadNitro; // segundos
    private final int cargasNitro;

    // Factory methods
    public static AutoConfig gol() { ... }
    public static AutoConfig toyota() { ... }
    public static AutoConfig challenger() { ... }
}
```

---

### Commit 2: "feat: Extender Jugador con auto actual y garage (autos comprados)"
**Archivos:** Modificar `core/src/main/java/com/afs/dragbits/jugador/Jugador.java`, Modificar `core/src/main/java/com/afs/dragbits/jugador/RepositorioJugador.java`

Añadir campos al Jugador:
- `AutoJugadorTipo autoActual` - auto equipado
- `EnumSet<AutoJugadorTipo> autosComprados` - garage (persistir en Preferences)

Métodos nuevos:
- `boolean tieneAuto(AutoJugadorTipo tipo)`
- `void comprarAuto(AutoJugadorTipo tipo)` - valida nivel/dinero, resta dinero, añade al garage, cambia autoActual
- `void equiparAuto(AutoJugadorTipo tipo)` - solo si está comprado
- `AutoConfig getConfigAutoActual()` - devuelve stats del auto equipado

En RepositorioJugador:
- Añadir keys: `KEY_AUTO_ACTUAL`, `KEY_AUTOS_COMPRADOS` (guardar como string CSV o bitset)
- Actualizar `guardarProgreso()` y `cargarProgreso()`

---

### Commit 3: "feat: Actualizar AutoJugador para usar AutoConfig dinámico"
**Archivos:** Modificar `core/src/main/java/com/afs/dragbits/autos/AutoJugador.java`

Cambiar constructor para recibir `AutoConfig` en lugar de valores hardcoded:

```java
public AutoJugador(float posX, float posY, AutoConfig config, Main mainGame) {
    super(posX, posY, 
          config.getVelocidadMaxima(),
          calcularAceleracionDesde0a100(config.getAceleracion()), // convertir 6.8s a valor interno
          config.getTraccion(),
          config.getRutaSprite(),
          mainGame);
    this.capacidadNitro = config.getCapacidadNitro();
    this.nitroRestante = config.getCapacidadNitro();
    // configurar cargas de nitro si aplica
}
```

Añadir método helper para convertir tiempo 0-100 a aceleración interna del motor.

---

### Commit 4: "feat: Crear VentanaTiendaAutos (UI estilo VentanaSeleccionRival)"
**Archivos:** Nuevo `core/src/main/java/com/afs/dragbits/menutienda/VentanaTiendaAutos.java`

Clase que implementa `Disposable` con:
- Stage propio, viewport
- Fondo semitransparente gris oscuro
- Grid 1 fila x 2 columnas (Toyota | Challenger)
- Cada celda: ImageButton con icono del auto (usar frames del sheet "Iconos autos-sheet.png")
- Overlay de color semitransparente según estado:
  - **Verde (0.3f alpha)**: `jugador.tieneAuto(tipo)` → "ADQUIRIDO", deshabilitado
  - **Gris (0.3f alpha)**: `!jugador.tieneAuto(tipo) && jugador.getNivel() >= nivelReq && jugador.getDinero() >= costo` → "COMPRAR $X", clickable
  - **Rojo (0.3f alpha)**: `!jugador.tieneAuto(tipo) && (jugador.getNivel() < nivelReq || jugador.getDinero() < costo)` → "NIVEL X REQ" o "$X FALTAN", deshabilitado
- Label con nombre del auto, stats resumidas (V-Max, 0-100, Tracción, Nitro)
- Botón cerrar (reutilizar textura Boton cerrar.png)
- Callback `Consumer<AutoJugadorTipo> alComprarAuto` para notificar compra exitosa

Métodos:
- `mostrar(Jugador jugador)` - construye UI dinámica según estado del jugador
- `ocultar()`
- `render(delta)`, `resize()`, `dispose()`

---

### Commit 5: "feat: Añadir burbuja 'Tienda Autos' en MapaScreen y conectar VentanaTiendaAutos"
**Archivos:** Modificar `core/src/main/java/com/afs/dragbits/screens/MapaScreen.java`

- Importar `VentanaTiendaAutos`
- En `show()`: instanciar `ventanaTienda = new VentanaTiendaAutos(viewport, () -> ..., (tipo) -> { ... })`
- En burbujas: la burbuja `frameAutos` (línea 118) ya existe pero su acción está vacía `() -> {}`. Cambiar a:
  ```java
  () -> abrirVentanaTienda()
  ```
- Método `abrirVentanaTienda()`:
  ```java
  private void abrirVentanaTienda() {
      ventanaTienda.mostrar(jugador);
      Gdx.input.setInputProcessor(ventanaTienda.getStage());
  }
  ```
- Callback de compra: al comprar, `jugador.comprarAuto(tipo)`, `REPOSITORIO_JUGADOR.guardarProgreso(jugador)`, `ventanaTienda.mostrar(jugador)` (refrescar UI), y opcionalmente feedback visual/sonido.

---

### Commit 6: "feat: Actualizar GameScreen para usar auto equipado del jugador"
**Archivos:** Modificar `core/src/main/java/com/afs/dragbits/screens/GameScreen.java`

En `show()`:
- Cargar `datosJugador` del repositorio (ya se hace)
- Obtener `AutoConfig configActual = datosJugador.getConfigAutoActual()`
- Crear `autoJugador = new AutoJugador(pista.getPosicionSpawnX(), pista.getYJugador(), configActual, mainGame);`

Esto asegura que al entrar a carrera, use el auto que el jugador tenga equipado.

---

### Commit 7: "feat: Persistencia y migración de datos - añadir campos a RepositorioJugador"
**Archivos:** Modificar `core/src/main/java/com/afs/dragbits/jugador/RepositorioJugador.java`

- Añadir constants: `KEY_AUTO_ACTUAL = "autoActual"`, `KEY_AUTOS_COMPRADOS = "autosComprados"`
- En `guardarProgreso()`: guardar `autoActual.ordinal()` y bitset/CSV de `autosComprados`
- En `cargarProgreso()`: leer y reconstruir `EnumSet`, por defecto GOL comprado y equipado
- Manejar migración: si keys no existen (partida antigua), inicializar con GOL

---

### Commit 8: "fix: Ajustar stats de aceleración - conversión 0-100s a valor interno"
**Archivos:** Modificar `core/src/main/java/com/afs/dragbits/autos/AutoJugador.java` o `AutoConfig.java`

Verificar que la fórmula de aceleración interna coincida con los tiempos 0-100 deseados:
- Gol: 9.5s → valor actual 24f (verificar)
- Toyota: 6.8s → calcular valor equivalente
- Challenger: 4.5s → calcular valor equivalente

Ajustar `calcularAceleracionDesde0a100()` o usar factor de conversión empírico.

---

### Commit 9: "feat: Feedback visual y sonoro en compra"
**Archivos:** Modificar `VentanaTiendaAutos.java`, opcional `MapaScreen.java`

- Sonido de compra exitosa (reutilizar sistema de audio)
- Animación breve del botón (escala, color flash verde)
- Toast/label temporal "¡COMPRA EXITOSA!" 
- Auto-equipar el auto comprado (cambia `autoActual`)

---

### Commit 10: "test: Verificación completa - flujo tienda → carrera"
**Archivos:** Ninguno (pruebas manuales)

Checklist:
- [ ] Partida nueva: solo Gol disponible, Toyota/Challenger en rojo (nivel 1)
- [ ] Subir a nivel 7 + $15k: Toyota en gris (comprable), Challenger en rojo
- [ ] Comprar Toyota: se pone verde, auto cambia a Toyota en HUD/carrera
- [ ] Subir a nivel 14 + $45k: Challenger comprable
- [ ] Comprar Challenger: se pone verde, auto cambia a Challenger
- [ ] Reentrar a tienda: autos comprados en verde, no se pueden recomprar
- [ ] Entrar a carrera: auto correcto con stats correctas
- [ ] Persistencia: cerrar/reabrir juego mantiene autos y auto equipado

---

## Archivos a Crear
1. `core/src/main/java/com/afs/dragbits/autos/AutoJugadorTipo.java` (nuevo)
2. `core/src/main/java/com/afs/dragbits/autos/AutoConfig.java` (nuevo)
3. `core/src/main/java/com/afs/dragbits/menutienda/VentanaTiendaAutos.java` (nuevo, package nuevo)

## Archivos a Modificar
1. `core/src/main/java/com/afs/dragbits/jugador/Jugador.java`
2. `core/src/main/java/com/afs/dragbits/jugador/RepositorioJugador.java`
3. `core/src/main/java/com/afs/dragbits/autos/AutoJugador.java`
4. `core/src/main/java/com/afs/dragbits/screens/MapaScreen.java`
5. `core/src/main/java/com/afs/dragbits/screens/GameScreen.java`

## Notas para Windsurf/IntelliJ
- Cada commit es atómico y compila independientemente
- Hacer backup antes de Commit 2 (cambios en modelo de datos/persistencia)
- Commit 4 es el más complejo (UI nueva), probar aisladamente
- Los sprites ya existen en `assets/sprites/Autos/Jugador/` y `assets/sprites/MenuAutos/Iconos autos-sheet.png`

---

## +changelog last version