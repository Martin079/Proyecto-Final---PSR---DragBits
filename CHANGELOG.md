# Changelog

Todos los cambios relevantes de este proyecto se documentan en este archivo.

El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y el proyecto utiliza [Versionado Semántico](https://semver.org/lang/es/).
Convención utilizada en todo el historial:

- Cada versión se escribe como `## [X.Y.Z] - AAAA-MM-DD` (fecha en formato ISO 8601).
- Dentro de cada versión, los cambios se agrupan únicamente en: `Added` (nuevas funcionalidades), `Changed` (cambios y refactorizaciones), `Fixed` (corrección de errores) y `Removed` (elementos eliminados).
- Mientras el juego esté en desarrollo inicial se usan versiones `0.x.y`; la `1.0.0` se reserva para la entrega final.
- Los cambios aún no versionados van en `[Unreleased]`.

## [Unreleased]

## [0.8.8] - 2026-10-09

### Added
- Catálogo de rivales con paquete `com.afs.dragbits.rivales` y clases `RivalConfig` (configuración inmutable de rival) y `CatalogoRivales` (datos de 10 rivales legales e ilegales con estadísticas calibradas).

### Changed
- `Auto` ahora calcula dinámicamente el ancho y alto del sprite según el tamaño del frame original multiplicado por `ESCALA_SPRITE` (2.8f), permitiendo que sprites de diferentes tamaños (100x40 vs 200x80) se dibujen correctamente.
- Agregada constante `ANCHO_BASE` (280f) y método `alinearFrente(float frenteX)` en `Auto` para alinear el frente de cualquier auto a una posición X específica, evitando ventajas por diferencias de longitud.
- `GameScreen` ahora alinea el frente del rival con la línea de largada usando `alinearFrente()`, asegurando que todos los autos larguen en igualdad de condiciones.
- `AutoRival` agregó constructor que acepta `RivalConfig` para inicializar estadísticas desde el catálogo de rivales, configurando capacidad y nitro restante según el rival.
- `AutoRival` ahora activa nitro automáticamente en la IA cuando alcanza la marcha 3 o superior y tiene nitro disponible (constante `MARCHA_ACTIVACION_NITRO = 3`).
- `AutoJugador` ajustó aceleración de 80f a 24f para coherencia con las notas de diseño (tiempo 0-100 km/h ~9.5s).
- Eliminado constructor `Color` fallback de `AutoRival` al no tener usos en el código.
- `GameScreen` ahora recibe `indiceRival` en el constructor y usa `CatalogoRivales.obtener()` para cargar las estadísticas del rival seleccionado en lugar de valores hardcodeados.
- `MapaScreen` ahora pasa el `indiceRival` seleccionado por el jugador al crear `GameScreen`, permitiendo elegir cualquier rival del catálogo.
- `Jugador` agregó campos `derrotadosLegales` y `derrotadosIlegales` (0-5) para rastrear el progreso de desbloqueo de rivales.
- `Jugador` agregó métodos `getDerrotados()`, `setDerrotados()`, `getMaxRivalDesbloqueado()`, `esPrimeraVictoria()` y `registrarVictoria()` para gestionar el sistema de desbloqueo progresivo.
- `RepositorioJugador` ahora guarda y carga el progreso de rivales derrotados en disco con claves `KEY_DERROTADOS_LEGALES` y `KEY_DERROTADOS_ILEGALES`, con default 0 para compatibilidad con guardados viejos.
- `MapaScreen` ahora usa `jugador.getMaxRivalDesbloqueado()` para pasar el progreso real al menú de selección de rival, desbloqueando rivales secuencialmente.
- `ControladorCarrera` ahora recibe `TipoCarrera` e `indiceRival` en el constructor y llama a `jugador.registrarVictoria()` cuando el jugador gana, permitiendo que el siguiente rival se desbloquee correctamente.
- `ControladorCarrera` agregó campos `recompensaObtenida`, `primeraVictoria` y `subioDeNivel` con getters para rastrear los resultados de la carrera.
- `ControladorCarrera` ahora calcula recompensas con bonus de primera victoria (+50%) y otorga dinero en carreras legales o experiencia en carreras ilegales según el tipo.
- `CartelResultado` actualizó firma de `dibujar()` para recibir `TipoCarrera`, `primeraVictoria`, `subioDeNivel` y `Jugador`, mostrando recompensas diferenciadas por tipo (LEGAL: dinero, ILEGAL: XP con nivel).
- `CartelResultado` ahora muestra mensaje de bonus de primera victoria (+50%) y notificación de subió de nivel cuando corresponda.
- `GameScreen` actualizó llamada a `cartelResultado.dibujar()` usando los getters del `ControladorCarrera` en lugar de valores hardcodeados.

### Updated
- Actualizado el sprite de la pista de carreras, y cambiada las posiciones de inicio y del rival para encajar con el nuevo sprite.


## [0.8.7] - 2026-10-07

### Added
- Se añadio los sprites de todos los autos en la carpeta sheet. Aun no implementados en el juego.
- Se añadio la imagen del mapa de la ciudad para las carreras ilegales en la carpeta sheet
- Interfaz `Pista` para abstraer el comportamiento y constantes espaciales de los circuitos (`getYJugador`, `getYRival`, `getPosicionSpawnX`, `getPosicionLineaMeta`, `dibujar`).
- Implementación `PistaCiudad` utilizando el recurso gráfico `sprites/Pistas/Ciudad.png` dibujado en mosaico y línea de meta blanca provisoria.

### Changed
- `Picodromo` ahora implementa la interfaz `Pista` y encapsula las posiciones verticales de spawn de los autos.
- `ControladorCarrera` desacoplado de `Picodromo`, aceptando cualquier instancia de `Pista`.
- `GameScreen` pasa a recibir `TipoCarrera` en su constructor e instancia dinámicamente `Picodromo` (carrera legal) o `PistaCiudad` (carrera ilegal).
- `MapaScreen` almacena y propaga el `tipoSeleccionado` hacia `GameScreen` al iniciar la carrera desde el menú de selección de rival.

### Fixed 
- Posicion de largada de los autos se movio para atras, para que coincida con la linea de largada

## [0.8.6] - 2026-10-05

### Added
- Se creó el nuevo enum EstadoJuego para centralizar el control del flujo del juego.
- Se integró la alternancia del estado con consumirCancelar(). En estado PAUSADO, se omite la actualizacion de fisica, IA, semáforo y stateTime (deteniendo animaciones).
- En pausa, la tecla de confirmación (consumirConfirmar()) permite regresar al mapa inmediatamente sin otorgar recompensas.
- Se creó la clase visual CartelPausa para dibujar el mensaje de pausa con un fondo semitransparente durante la pasada del HUD.

### Fixed
- Añadido de metodos para eventos de navegacion, movimiento de mouse y posicionamiento de toque. 
- Se unifico la obtención de coordenadas del mouse.
- Se elimino el InputAdapter anonimo interno, se delega el control de la interfaz a EntradaJugador mediante procesarEntrada(). 
- Se evito la seleccion por polling del mouse para no sobreescribir la navegación por teclado.
- Se simplifico la asignacion del procesador reemplazando el InputMultiplexer por EntradaJugador.
- Se agrego una verificacion tras el procesamiento de entrada para evitar llamadas adicionales a renderizado/dispose de recursos cuando la pantalla cambia.
- Se limpió el estado de toque persistente durante la carrera ejecutando descartarToque(), evitando que clics pasados activen botones al finalizar la partida.
- Se diferió la transición a la pantalla de juego (GAME.setScreen(...)) envolviéndola en Gdx.app.postRunnable().
- Se estandarizó la resolución virtual a 1920×1080 implementando FitViewport independientes para Mundo (viewportMundo) y HUD (viewportHUD), eliminando la dependencia de píxeles reales de ventana.
- Se separó el flujo de renderizado en dos pasadas independientes, asegurando la llamada a viewport.apply() antes de cada una.
- Se desproyectaron los clics de la interfaz contra viewportHUD.
- Se eliminaron referencias a la cámara en las clases hijas de ElementoHUD (Basicos, Palanca, Semaforo).
- Se reescalaron gráficos y fuentes al espacio 1920×1080 (factor 1.5) y se reemplazaron los offsets fijos de texto por centrado dinámico mediante GlyphLayout.


## [0.8.5] - 2026-10-03

### Fixed
- Cambio de escritura de final para cumplir con las normas
- Cambio de la dependencia en `VentanaSeleccionRival` con `GameScreen`
- Añadido dispose en hide de `MapaScreen`

## [0.8.4] - 2026-10-02

### Fixed
- Agregada clase `GestorDeAudio` para centralizar la musica y los sonidos
- `EntradaJugador` ahora cubre el mouse en el las screens. 
- Checkeo del click en menu con la coordenada X

## [0.8.3] - 2026-09-28

### Fixed
- Corrección del click en el menú principal.
- Corrección de error que no permitía abrir las burbujas del mapa.
- Corrección de error que no permitía elegir un rival y forzaba el cierre del juego.
- Corrección de bug en el viewport de `CartelResultado`.

### Changed
- Se agregó un `hide` funcional en `GameScreen`, `MainMenuScreen` y `MapaScreen`.
- Desacoplamiento de las screens en `Main`.
- La gestión de la música se separó de `Main` mediante la interfaz `ProveedorMusica`.

## [0.8.2] - 2026-09-27

### Changed
- Cambios en los comentarios del código.
- Cambio en el `extends` de `AutoRival`.
- Se puso en mayúscula el `final` de `Semaforo`.
- Cambio de `List` a `ArrayList` en `MapaScreen`.

### Removed
- Se eliminó `cargarYCortar` de `SpriteSheetLoader`.
- Se eliminó el `Runnable accionCerrar`, que no se usaba, en `VentanaSeleccionRival`.

## [0.8.1] - 2026-09-24

### Changed
- Creación de las clases `enum` `EstadoAuto`, `EstadoSemaforo` y `TipoCarrera`.

## [0.8.0] - 2026-09-02

### Added
- Un único tema de fondo para cada "pantalla", con volumen distinto en cada uno.
- Clase `SpriteSheetLoader` para reutilizar la carga de sprites.
- Clase `TexturaSolidaFactory` para reutilizar la creación de fondos sólidos y lisos.
- Clase `RepositorioJugador` para el manejo de carga y guardado de datos del jugador.
- Clase `CartelResultado` para el cartel que se muestra al finalizar la carrera.
- Clase `ControladorCarrera` para la gestión de la carrera.

### Changed
- Nuevo sprite para el auto del primer rival.
- Renombrado de los sprites para evitar errores por mayúsculas/minúsculas.
- Renombrado de los packages de mayúscula a minúscula.
- La clase `Nota` pasó a ser el archivo `NOTAS.md`.
- Refactorización de `VentanaSeleccionRival` para evitar código duplicado.
- Refactorización de `Jugador`, delegando la carga y el guardado a `RepositorioJugador`.
- Refactorización de `Basicos`, `Palanca` y `Semaforo` con `ElementoHUD` para compartir el campo `OrthographicCamera`.
- Refactorización de `GameScreen`: el cartel de fin de carrera pasó a `CartelResultado` y la gestión de la carrera a `ControladorCarrera`.

### Fixed
- La música se cargaba dos veces y una de las cargas no se utilizaba, ocupando memoria.

## [0.7.0] - 2026-09-01

### Added
- Primer bot rival de carreras legales con dificultad fácil.
- Otorgamiento de recompensas al finalizar la carrera.

### Changed
- Sprite del primer auto y mejores efectos de velocidad y sensación de movimiento.

## [0.6.0] - 2026-08-31

### Added
- Menú para elegir rival en las carreras legales e ilegales.
- Posición de largada y de meta, y vuelta al menú al llegar a la meta.

### Changed
- Actualización de los sprites de la pista.

## [0.5.0] - 2026-08-24

### Added
- Clase `Jugador` con dinero, experiencia, nivel y el cálculo de la progresión.
- Guardado de las estadísticas del jugador en un archivo.
- HUD en la ciudad que indica dinero y nivel.

### Changed
- Mayor tamaño del semáforo.
- Reducción de las estadísticas iniciales del auto del jugador.

## [0.4.0] - 2026-08-22

### Added
- Base para el sistema de nitro.

### Changed
- Nuevo sistema de aceleración, tracción y velocidad máxima.
- Ventana forzada a pantalla completa y sin bordes.

## [0.3.0] - 2026-08-19

### Added
- Secuencia del semáforo y penalización por arrancar antes.
- Mapa con los globos/íconos de cada zona en su posición correspondiente.
- Al tocar el ícono de carrera legal se inicia la carrera.

## [0.2.0] - 2026-08-10

### Added
- Versión inicial del control de aceleración y cambio de marchas.
- HUD simplificado por texto para mostrar información básica.
- Versión preliminar de las clases `Auto` y `AutoJugador` con velocidad máxima, aceleración y RPM máximas.
- Diseño simplificado de la pista y del auto.
- Primer auto con spritesheet, que reacciona según la situación (cambio, aceleración, estático).
- HUD básico de la palanca y diseño preliminar de la palanca de cambios.
- Movimiento de la palanca en el HUD siguiendo las flechas.

## [0.1.0] - AAAA-MM-DD

### Added
- Configuración inicial del proyecto con LibGDX usando la herramienta gdx-liftoff.
- Configuración del repositorio Git local y vinculación con GitHub.
- Creación del archivo `README.md` con la información del proyecto.
- Creación del archivo `CHANGELOG.md` para el registro de modificaciones.
- Creación y configuración de la wiki de GitHub.
