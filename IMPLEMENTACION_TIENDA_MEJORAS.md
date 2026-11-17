# Instrucciones de Implementación: Tienda de Mejoras (Upgrade Shop)

Este documento contiene las instrucciones paso a paso para implementar la tienda de mejoras en DragBits, diseñadas para ser ejecutadas como commits individuales en WindSurf (integrado en IntelliJ).

---

## Resumen de la Funcionalidad

La tienda de mejoras permitirá al jugador comprar mejoras para su auto actual en 5 categorías:
1. **Aceleración** (5 niveles)
2. **Velocidad Máxima** (5 niveles)
3. **Tracción** (5 niveles)
4. **Nitro** (4 niveles - capacidad/duración)
5. **Potencia del Nitro** (4 niveles)

**Nota**: La categoría "Caja de Cambios" se descartó según NOTAS.md.

### Comportamiento UI Requerido
- Ventana modal similar a `VentanaTiendaAutos` / `VentanaSeleccionRival`
- Botón cerrar (X) en esquina superior derecha
- Columna izquierda: nombre de la mejora (Aceleración, Tracción, etc.)
- Fila por mejora: cuadrados pequeños por nivel
  - **Gris**: se puede comprar (nivel requerido alcanzado, dinero suficiente)
  - **Rojo**: bloqueado (nivel de jugador insuficiente)
  - **Verde**: ya adquirida
- Debajo de cada cuadrado: costo en $
- Cuadrados pequeños (aprox. 40x40px)
- Compra = equipamiento automático (se aplica al auto actual inmediatamente)

---

## Paso 1: Crear Enum `TipoMejora` y Clase `MejoraConfig`

**Archivos a crear:**
- `core/src/main/java/com/afs/dragbits/mejoras/TipoMejora.java`
- `core/src/main/java/com/afs/dragbits/mejoras/MejoraConfig.java`

**Qué se modifica/añade:**
- Nuevo paquete `com.afs.dragbits.mejoras`
- `TipoMejora`: enum con 5 valores (ACELERACION, VELOCIDAD_MAXIMA, TRACCION, NITRO, POTENCIA_NITRO), cada uno con nombre para mostrar y orden
- `MejoraConfig`: clase inmutable estilo `AutoConfig` con campos: tipo, nivel (1-5), nivelJugadorRequerido, costo, valorMejora (float), descripción
- Factory methods estáticos para crear todas las mejoras según NOTAS.md

**Cálculo de costos (economía del juego):**
- Base: nivel 1 ≈ $500-800, nivel 5 ≈ $8,000-12,000
- Progresión exponencial suave para que sea difícil pero no imposible
- Referencia: Rival 1 legal fácil da $800, Rival 3 medio da $6,000, Rival 5 difícil da $28,800

**Datos según NOTAS.md (valores de mejora y nivel de desbloqueo):**

| Mejora | Nivel | Nivel Jugador | Mejora | Costo Estimado |
|--------|-------|---------------|--------|----------------|
| Aceleración I | 1 | 2 | -0.15s 0-100 | $800 |
| Tracción I | 1 | 2 | -10% patinaje | $800 |
| Velocidad Máxima I | 1 | 3 | +3 km/h | $1,000 |
| Nitro I (desbloqueo) | 1 | 5 | 1 carga, 2s | $2,500 |
| Aceleración II | 2 | 6 | -0.20s 0-100 | $2,000 |
| Potencia Nitro I | 1 | 6 | +5% empuje | $2,000 |
| Tracción II | 2 | 7 | -15% patinaje | $2,200 |
| Velocidad Máxima II | 2 | 8 | +4 km/h | $2,500 |
| Nitro II | 2 | 8 | +1s duración | $3,000 |
| Aceleración III | 3 | 10 | -0.25s 0-100 | $4,000 |
| Potencia Nitro II | 2 | 10 | +8% empuje | $3,800 |
| Tracción III | 3 | 12 | -20% patinaje | $4,500 |
| Velocidad Máxima III | 3 | 12 | +5 km/h | $4,500 |
| Nitro III | 3 | 13 | +1.5s duración | $5,500 |
| Aceleración IV | 4 | 14 | -0.30s 0-100 | $7,000 |
| Caja Cambios III | - | 14 | (descartada) | - |
| Potencia Nitro III | 3 | 15 | +10% empuje | $7,500 |
| Velocidad Máxima IV | 4 | 16 | +6 km/h | $8,500 |
| Tracción IV | 4 | 16 | -25% patinaje | $8,500 |
| Aceleración V | 5 | 18 | -0.35s 0-100 | $12,000 |
| Nitro IV | 4 | 18 | Carga máx +2s | $11,000 |
| Potencia Nitro IV | 4 | 19 | +12% empuje | $13,000 |
| Velocidad Máxima V | 5 | 20 | +8 km/h (Élite) | $16,000 |
| Tracción V | 5 | 20 | Patinaje casi nulo | $16,000 |

**Mensaje de commit:**
```
feat: Añadir TipoMejora enum y MejoraConfig con datos de mejoras según NOTAS.md
```

---

## Paso 2: Añadir Sistema de Mejoras a la Clase `Jugador`

**Archivo a modificar:**
- `core/src/main/java/com/afs/dragbits/jugador/Jugador.java`

**Qué se modifica/añade:**
- Nuevo campo: `EnumMap<TipoMejora, Integer> mejorasCompradas` (nivel máximo comprado por tipo, 0 = nada)
- Método `getNivelMejora(TipoMejora tipo)` → retorna nivel actual (0-5)
- Método `tieneMejora(TipoMejora tipo, int nivel)` → boolean
- Método `puedeComprarMejora(TipoMejora tipo, int nivel)` → valida: nivel jugador requerido, dinero, no tener ya ese nivel
- Método `comprarMejora(TipoMejora tipo, int nivel)` → resta dinero, actualiza mapa, retorna boolean
- Método `aplicarMejorasAAuto(AutoConfig config)` → retorna nuevo `AutoConfig` con stats modificadas según mejoras compradas
- Actualizar constructores para inicializar `mejorasCompradas` vacío

**Lógica de aplicación de mejoras (en `aplicarMejorasAAuto`):**
- Aceleración: reduce tiempo 0-100 (restar mejora acumulada)
- Velocidad Máxima: suma km/h acumulados
- Tracción: mejora factor tracción (multiplicador o valor base)
- Nitro: aumenta capacidad/duración y cargas
- Potencia Nitro: aumenta multiplicador potenciaNitro

**Mensaje de commit:**
```
feat: Añadir sistema de mejoras a Jugador (compra, consulta, aplicación a AutoConfig)
```

---

## Paso 3: Persistencia de Mejoras en `RepositorioJugador`

**Archivo a modificar:**
- `core/src/main/java/com/afs/dragbits/jugador/RepositorioJugador.java`

**Qué se modifica/añade:**
- Nueva constante: `KEY_MEJORAS = "mejoras"`
- En `guardarProgreso()`: serializar `mejorasCompradas` como CSV "tipoId:nivel,tipoId:nivel..."
- En `cargarProgreso()`: deserializar y reconstruir `EnumMap`
- Métodos auxiliares `enumMapToCsv()` y `csvToEnumMap()`
- Mantener compatibilidad con guardados antiguos (si no existe key, mapa vacío)

**Mensaje de commit:**
```
feat: Persistir mejoras compradas en RepositorioJugador (guardar/cargar)
```

---

## Paso 4: Crear Ventana de Tienda de Mejoras `VentanaTiendaMejoras`

**Archivo a crear:**
- `core/src/main/java/com/afs/dragbits/menutienda/VentanaTiendaMejoras.java`

**Qué se modifica/añade:**
- Nueva clase siguiendo patrón de `VentanaTiendaAutos`
- Constructor: `(Viewport viewport, Runnable accionCerrar, Consumer<Void> alComprarMejora, Jugador jugador)`
- UI:
  - Fondo semitransparente (reutilizar `TexturaSolidaFactory`)
  - Botón cerrar (X) esquina superior derecha (reutilizar `Boton cerrar.png`)
  - Tabla principal con scroll si no cabe todo
  - **Columna izquierda**: Label con nombre de la mejora (Aceleración, Tracción, Velocidad Máxima, Nitro, Potencia Nitro)
  - **Filas por nivel**: Para cada nivel de esa mejora (1 a max), un cuadrado pequeño (40x40px)
    - Estado: `VERDE` si `jugador.tieneMejora(tipo, nivel)`
    - Estado: `GRIS` si `jugador.puedeComprarMejora(tipo, nivel)` (nivel jugador OK + dinero OK)
    - Estado: `ROJO` si bloqueado (nivel jugador insuficiente o ya tiene nivel superior)
  - Debajo de cada cuadrado: Label pequeño con costo "$XXXX"
  - Click en cuadrado GRIS → compra → refresca UI → callback `alComprarMejora`
- Métodos: `mostrar(Jugador jugador)`, `ocultar()`, `render(delta)`, `resize()`, `dispose()`, `isVisible()`

**Detalles visuales:**
- Usar `Table` anidados: Table principal → filas por tipo de mejora → Table horizontal por tipo (label nombre + Table de niveles)
- Cuadrados: `Image` con texturas sólidas de colores (reutilizar `TexturaSolidaFactory.crearTextura(40, 40, color)`)
- Overlay de estado con alpha ~0.3f como en `VentanaTiendaAutos`
- Fuente pequeña para costos (BitmapFont scale ~0.8f)

**Mensaje de commit:**
```
feat: Crear VentanaTiendaMejoras con UI de grid (tipo mejora + niveles con cuadrados de estado)
```

---

## Paso 5: Integrar Tienda de Mejoras en `MapaScreen`

**Archivo a modificar:**
- `core/src/main/java/com/afs/dragbits/screens/MapaScreen.java`

**Qué se modifica/añade:**
- Import `VentanaTiendaMejoras`
- Campo privado: `private VentanaTiendaMejoras ventanaMejoras;`
- En `show()`: instanciar `ventanaMejoras` con viewport, acción cerrar (restaurar input processor), callback de compra (guardar progreso + refrescar UI)
- En inicialización de burbujas: la burbuja de mejoras (frameMejoras, índice 2 del sheet) ya existe en línea 132, **cambiar su acción vacía `() -> {}` por `this::abrirVentanaMejoras`**
- Método privado `abrirVentanaMejoras()`: `ventanaMejoras.mostrar(jugador); Gdx.input.setInputProcessor(ventanaMejoras.getStage());`
- En `render()`: añadir `ventanaMejoras.render(delta)` si visible
- En `resize()`: `ventanaMejoras.resize(width, height)`
- En `dispose()`: `ventanaMejoras.dispose()`

**Mensaje de commit:**
```
feat: Integrar VentanaTiendaMejoras en MapaScreen (burbuja mejoras funcional)
```

---

## Paso 6: Aplicar Mejoras al Auto del Jugador en `GameScreen`

**Archivo a modificar:**
- `core/src/main/java/com/afs/dragbits/screens/GameScreen.java`

**Qué se modifica/añade:**
- En creación de `AutoJugador`: en lugar de usar `datosJugador.getConfigAutoActual()` directamente, llamar a `datosJugador.aplicarMejorasAAuto(configBase)` para obtener config con mejoras aplicadas
- Verificar que `AutoJugador` reciba los valores modificados correctamente (velocidadMaxima, aceleracion, traccion, capacidadNitro, potenciaNitro)

**Mensaje de commit:**
```
feat: Aplicar mejoras compradas al auto del jugador al iniciar carrera
```

---

## Paso 7: Actualizar CHANGELOG.md

**Archivo a modificar:**
- `CHANGELOG.md`

**Qué se añade en sección `[Unreleased]`:**

```markdown
## [Unreleased]

### Added
- Sistema completo de tienda de mejoras con 5 categorías (Aceleración, Velocidad Máxima, Tracción, Nitro, Potencia Nitro) y 23 niveles totales
- Enum `TipoMejora` y clase `MejoraConfig` con datos calibrados según NOTAS.md
- Sistema de compra y persistencia de mejoras en `Jugador` y `RepositorioJugador`
- Aplicación automática de mejoras al auto equipado al iniciar carrera
- `VentanaTiendaMejoras` con UI tipo grid: columna de tipos + cuadrados por nivel (gris=comprar, rojo=bloqueado, verde=adquirido) con costo
- Integración en `MapaScreen`: burbuja de mejoras ahora abre la tienda

### Changed
- `AutoJugador` ahora recibe stats con mejoras aplicadas desde `Jugador.aplicarMejorasAAuto()`
- `RepositorioJugador` serializa/deserializa mapa de mejoras compradas
```

**Mensaje de commit:**
```
docs: Actualizar CHANGELOG con tienda de mejoras (Unreleased)
```

---

## Paso 8: Testing y Ajustes Finales (Commit Opcional)

**Verificaciones manuales:**
1. Iniciar juego nuevo → nivel 1 → ir a mejoras → solo nivel 1 de Aceleración/Tracción deberían estar grises (comprueba nivel jugador requerido = 2)
2. Comprar Aceleración I → cuadrado se pone verde → dinero descuenta → stats del auto cambian
3. Subir nivel (trampa: editar prefs o ganar carreras) → nuevos niveles se desbloquean en gris
4. Verificar que mejoras persisten al cerrar/abrir juego
5. Verificar que en carrera el auto tiene stats mejoradas (0-100 más rápido, V-Max más alta, menos patinaje, nitro funciona)
6. UI: cuadrados pequeños, legibles, botón cerrar funciona, scroll si hace falta

**Mensaje de commit:**
```
test: Verificación funcional tienda de mejoras y ajustes menores de UI/balance
```

---

## Resumen de Archivos Creados/Modificados

| Archivo | Acción |
|---------|--------|
| `core/src/main/java/com/afs/dragbits/mejoras/TipoMejora.java` | **NUEVO** |
| `core/src/main/java/com/afs/dragbits/mejoras/MejoraConfig.java` | **NUEVO** |
| `core/src/main/java/com/afs/dragbits/jugador/Jugador.java` | Modificar |
| `core/src/main/java/com/afs/dragbits/jugador/RepositorioJugador.java` | Modificar |
| `core/src/main/java/com/afs/dragbits/menutienda/VentanaTiendaMejoras.java` | **NUEVO** |
| `core/src/main/java/com/afs/dragbits/screens/MapaScreen.java` | Modificar |
| `core/src/main/java/com/afs/dragbits/screens/GameScreen.java` | Modificar |
| `CHANGELOG.md` | Modificar |

---

## Notas para WindSurf / IntelliJ

- Cada paso = **un commit atómico** con el mensaje indicado
- Ejecutar `./gradlew build` tras cada paso para verificar compilación
- Probar en `Lwjgl3Launcher` (desktop) tras pasos 4-6
- Si hay conflictos de merge, resolver antes de continuar al siguiente paso
- Los costos son orientativos; ajustar en `MejoraConfig` si el playtesting lo requiere

---

## Referencias de Código Existente a Reutilizar

- `VentanaTiendaAutos.java`: patrón de ventana modal, botón cerrar, callbacks, render loop
- `AutoConfig.java`: patrón de clase de configuración inmutable con factory methods
- `Jugador.java`: patrón de compra/validación (`comprarAuto`), persistencia EnumSet
- `RepositorioJugador.java`: serialización CSV de EnumSet → adaptar a EnumMap
- `TexturaSolidaFactory.java`: crear texturas de color sólido para cuadrados de estado
- `SpriteSheetLoader.java`: cargar textura botón cerrar
- `Burbuja.java` + `MapaScreen.java`: patrón de acción al cliquear burbuja