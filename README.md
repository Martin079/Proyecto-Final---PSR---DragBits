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
Mapa simple con burbujas de cada zona. Ventana para elegir rival de carreras legales e ilegales al clickear la
burbuja correspondiente. Sistema de dinero y nivel para el jugador y otorgar recompensas al finalizar la carrera.
Primer bot rival en dificultad fácil con su respectiva recompensa.

# Documentación del Proyecto
* [Ver el Historial de Cambios (Changelog)](CHANGELOG.md)
* [Ver la Propuesta Detallada en la Wiki](https://github.com/Martin079/Proyecto-Final---PSR---DragBits/wiki/Propuesta-del-Proyecto-‐-DragBITS-‐-Anza-Findlay-Sambon)
* [Ver tablero Kanban en Trello](https://trello.com/invite/b/6a84409df5f62a64764640ff/ATTI62343de83d9d45696e73ba36a80e3bba9F7BA9B8/kanban-dragbits)
* [Ver video de presentacion segunda pre entrega](https://drive.google.com/file/d/1Yo3qDl7lS2GEJ5hapYPGE5kMvdHSfgcT/view?usp=drive_link)
