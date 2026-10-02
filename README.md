# Nombre del proyecto
DragBits

# Integrantes
- Axel Anza
- Juan Ignacio Findlay
- Martin Sambon

# Acerca del juego
DragBits es un juego de picadas 2D estilo PixelArt donde se cuenta con un modo offline contra un bot o un modo multijugador LAN,
donde la principal función es el cambio manual de marchas moviendo la palanca de cambios a través de las flechas. Se contará también con funciones
como mejoras (nitro, aceleración, velocidad máxima), la compra de nuevos autos, y un sistema de dinero y nivel donde se conseguirá en carreras legales o
ilegales respectivamente.

# Tecnologías aplicadas
- **Lenguaje:** Java 21 (JDK 21)
- **Motor / framework:** [LibGDX](https://libgdx.com/) 1.14.2
- **Plataforma:** escritorio (PC) mediante el backend LWJGL3 (`gdx-backend-lwjgl3`)
- **Sistema de build:** Gradle (se utiliza el Gradle Wrapper incluido en el repositorio, no hace falta instalar Gradle)
- **IDE utilizado en el desarrollo:** IntelliJ IDEA

# Compilar y ejecutar

## Requisitos previos
- **JDK 21** instalado y configurado (por ejemplo [Eclipse Temurin 21](https://adoptium.net/)). Se puede verificar con:
  ```bash
  java -version
  ```
  La salida debe indicar la versión `21.x`.
- **Git** para clonar el repositorio.
- Conexión a internet la primera vez que se ejecuta (el Gradle Wrapper descarga Gradle y las dependencias del proyecto).

## 1. Clonar el repositorio
```bash
git clone https://github.com/Martin079/Proyecto-Final---PSR---DragBits.git
cd Proyecto-Final---PSR---DragBits
```

## 2. Ejecutar el juego con Gradle

**Windows** (PowerShell):
```powershell
.\gradlew.bat lwjgl3:run
```
En el Símbolo del sistema (CMD) el comando es `gradlew.bat lwjgl3:run`.

**Linux / macOS**:
```bash
./gradlew lwjgl3:run
```
Si aparece el error `Permission denied`, dar permisos de ejecución al wrapper y volver a intentar:
```bash
chmod +x gradlew
./gradlew lwjgl3:run
```

## 3. Compilar sin ejecutar (opcional)

- **Windows** (PowerShell): `.\gradlew.bat build`
- **Linux / macOS**: `./gradlew build`

Para generar un JAR ejecutable (queda en `lwjgl3/build/libs/`):

- **Windows** (PowerShell): `.\gradlew.bat lwjgl3:jar`
- **Linux / macOS**: `./gradlew lwjgl3:jar`

Luego se puede ejecutar con `java -jar lwjgl3/build/libs/DragBits-<versión>.jar`.

## Desde IntelliJ IDEA
Abrir el proyecto en IntelliJ IDEA (seleccionando `build.gradle`), esperar a que Gradle sincronice, buscar `Lwjgl3Launcher.java` (módulo `lwjgl3`) y ejecutarlo con RUN.

# Estado Actual

Prototipo jugable de la **segunda preentrega**. Se puede recorrer el flujo completo
**menú → mapa → selección de rival → carrera contra un bot → cartel de resultado → vuelta al mapa**,
con la mecánica central del juego (aceleración y cambio manual de marchas) funcionando.

## Características implementadas

**Mecánica de carrera**
- Aceleración y desaceleración con física propia: velocidad máxima por marcha, curva de aceleración, RPM que suben y bajan según la marcha.
  - Caja de cambios manual de 5 marchas + punto muerto, con embrague y movimiento de la palanca en dos ejes.
  - Sistema de **tracción / patinaje**: si la aceleración del auto supera lo que la tracción puede aprovechar en las primeras marchas, el auto pierde potencia.
  - **Semáforo de largada** con secuencia de luces de duración aleatoria y **salida en falso**: si el jugador se mueve antes del verde, el auto vuelve a la posición inicial y la secuencia se reinicia.
  - **Rival controlado por IA** (Renault 12, dificultad fácil): arranca con el verde, acelera y cambia de marcha por sí solo.
  - Detección de línea de meta, determinación del ganador y **cartel de resultado** (victoria / derrota) con botón para volver al mapa.

**Mapa y progresión**
- Mapa de la ciudad con 5 burbujas clickeables (carreras legales, carreras ilegales, tienda de mejoras, tienda de autos y modo online).
  - Las burbujas de **carreras legales e ilegales** abren una ventana para elegir rival (5 por tipo).
  - Sistema de **dinero y nivel** del jugador, mostrado en una barra superior con animación al subir de nivel.
  - Al ganar una carrera se otorga la recompensa en dinero y el progreso se **guarda automáticamente** (`Preferences` de LibGDX), por lo que se conserva al cerrar y volver a abrir el juego.

**Pantallas y estados**
- Menú principal (jugar, controles, salir), mapa, carrera, pantalla de controles y cartel de resultado.
  - Cambio entre pantallas sin reiniciar la aplicación; cada pantalla libera sus recursos al dejar de usarse.

**Gráficos, animaciones y HUD**
- Spritesheets con `Animation` de LibGDX: auto (estático, avanzando, cambiando de marcha y nitro), semáforo, ícono de nivel e indicador de palanca.
  - Pista de 3 tipos de sección (largada, intermedias y meta) y cámara que sigue al auto del jugador.
  - HUD de carrera, fijo y separado del mundo: velocidad, RPM, marcha actual, indicador de embrague y posición de la palanca.

**Audio**
- Música de fondo en loop, con volumen distinto según la pantalla (menú, mapa, carrera).



# Documentación del Proyecto
* [Ver el Historial de Cambios (Changelog)](CHANGELOG.md)
* [Ver la Propuesta Detallada en la Wiki](https://github.com/Martin079/Proyecto-Final---PSR---DragBits/wiki/Propuesta-del-Proyecto-‐-DragBITS-‐-Anza-Findlay-Sambon)
* [Ver tablero Kanban en Trello](https://trello.com/invite/b/6a84409df5f62a64764640ff/ATTI62343de83d9d45696e73ba36a80e3bba9F7BA9B8/kanban-dragbits)
* [Ver video de presentacion segunda pre entrega](https://drive.google.com/file/d/1Yo3qDl7lS2GEJ5hapYPGE5kMvdHSfgcT/view?usp=drive_link)
