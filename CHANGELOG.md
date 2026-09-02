# Changelog

## [Unreleased]

### Added
- Configuración inicial del proyecto con LibGDX usando la herramienta gdx-liftoff.
- Configuración del repositorio Git local y vinculación con GitHub.
- Creación del archivo README.md con la información del proyecto.
- Creación del archivo CHANGELOG.md para el registro de modificaciones.
- Creación y confifuración de la wiki de GitHub

### Added 5/8/2026

- Version Inicial del control de aceleracion y cambio
- HUD simplificado por texto para mostrar informacion basica 
- Version preliminar de la clase Auto y AutoJugador con velocidad maxima, aceleracion y rpm maximas
- Diseño simplificado de la pista y del auto


### Updated 10/8/2026

- Diseño basico de un primer auto con spritesheet, y reaciona segun la situacion (cambio, aceleracion, estatico).

### Added 10/8/2026

- HUD basico de la palanca y diseño preliminar de la palanca de cambios
- movimiento de la palanca en el HUD siguiendo las flechas

### Added 19/8/2026

- Secuencia del semaforo y penalizacion al arrancar antes.
- Añadido del mapa y los globos/iconos en su posicion correspondiente
- Al tocar el icono de carrera legal se inicia la carrera

### Updated 22/8/2026

- Cambiado el sistema de aceleracion traccion y velocidad maxima.
- Implementada la base para el nitro
- Ventana forzada en pantalla completa y sin bordes

### Updated 24/8/2026

- Aumentado el tamaño del semaforo
- Reducido las estadisticas del auto del jugador al inicio

### Added 24/8/2026 

- Clase jugador con dinero, experiencia, nivel, y el calculo para la progresión
- Implementacion de guardado de estadisticas del jugador en un archivo
- HUD en la ciudad indicando dinero y nivel

### Added 30/8/2026
- Añadido de menu para elegir rival para las carreras legales e ilegales

### Updated 31/8/2026
- Actualizacion sprites de la pista

### Added 31/8/2026
- Posicion de largada y meta, y vuelta al menu al llegar a la meta


### Added 1/9/2026
- Primer bot rival de carreras legales con dificultad facil
- Otorgamiento de recompensas al finalizar la carrera

### Updated 1/9/2026
- Sprite del primer auto y mejores efectos de velocidad y sensacion de movimiento

### Updated 2/9/2026
- Cambio en el sprite para el auto del primer rival

### Added 2/9/2026
- Añadido de un único tema de fondo para cada "pantalla", con volumen distinto en cada uno

### Fix 2/9/2026
- Cambios en el nombre de los sprites para evitar errores por mayusculas/minusculas
- Cambios de nombre de Mayuscula a minuscula en los package
- Cambio de clase nota a archivo .md
- Correccion de carga de musica dos veces donde una no se utilizaba ocupando memoria
- Añadido clase SpriteSheetLoader para reutilizar la carga de sprites
- Añadido clase TexturaSolidaFactory para reutilizar la creacion de fondos solidos y lisos
