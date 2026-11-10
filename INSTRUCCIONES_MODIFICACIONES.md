# Instrucciones de Modificaciones para DragBits

Este documento describe los cambios a implementar en el juego, cada uno como un commit separado para integrarse con Windsurf/IntelliJ.

---

## Commit 1: Ajustar tamaño de la ventana de compra de autos

**Mensaje de commit:** `feat(tienda): reducir tamaño de la ventana de compra de autos`

**Archivos a modificar:** `core/src/main/java/com/afs/dragbits/menutienda/VentanaTiendaAutos.java`

**Cambios:**
- En el constructor, cambiar el tamaño de `mainTable` de `width(900).height(600)` a `width(750).height(500)` (o valores similares, reduciendo ~15-20%)
- En `mostrar()`, ajustar `gridTable.defaults().pad(30f).width(400f).height(500f)` a `gridTable.defaults().pad(20f).width(340f).height(420f)` para que las celdas se adapten a la ventana más pequeña

---

## Commit 2: Reducir tamaño del botón de compra

**Mensaje de commit:** `feat(tienda): reducir tamaño del botón de compra de autos`

**Archivos a modificar:** `core/src/main/java/com/afs/dragbits/menutienda/VentanaTiendaAutos.java`

**Cambios:**
- En `crearCeldaAuto()`, el botón de compra es toda la celda (`celda.addListener(...)` con `touchDown`). Para hacerlo más pequeño:
  - Cambiar el overlay de `TexturaSolidaFactory.crearTextura(400, 500, overlayColor)` a `crearTextura(340, 420, overlayColor)` (coincide con el nuevo tamaño de celda)
  - Reducir el tamaño del icono del auto: `iconoBtn.getImageCell().size(200f, 150f)` → `iconoBtn.getImageCell().size(160f, 120f)`
  - Reducir escalado de fuentes: `nombreLabel.setFontScale(2f)` → `1.6f`, `statsLabel.setFontScale(1.2f)` → `1f`, `estadoLabel.setFontScale(1.5f)` → `1.3f`
  - Ajustar paddings internos: `padTop(20f)` → `padTop(12f)`, `padTop(15f)` → `padTop(10f)`, etc.

---

## Commit 3: Reemplazar nombre del auto por imagen del sprite sheet (frames 12 y 13 / 11 y 12 base 0)

**Mensaje de commit:** `feat(tienda): mostrar imagen del auto en lugar de nombre usando sprite sheet Iconos autos-sheet.png`

**Archivos a modificar:** `core/src/main/java/com/afs/dragbits/menutienda/VentanaTiendaAutos.java`

**Cambios:**
- El sprite sheet `Iconos autos-sheet.png` tiene 14 frames (2800x200, cada frame 200x200)
- Frames 0-9: rivales, Frame 10: ?, Frame 11: ?, Frame 12: Toyota, Frame 13: Challenger (índices base 0)
- En `obtenerIconoAuto()`, cambiar la lógica para usar los frames 12 y 13 (índices 11 y 12 base 0, o 12 y 13 base 1):
  ```java
  private TextureRegion obtenerIconoAuto(AutoJugadorTipo tipo) {
      if (iconosTexture == null) return null;

      int anchoFrame = iconosTexture.getWidth() / 14; // 14 frames en total
      int altoFrame = iconosTexture.getHeight();

      int indice;
      switch (tipo) {
          case TOYOTA: indice = 12; break;    // Frame 13 (base 1) / 12 (base 0)
          case CHALLENGER: indice = 13; break; // Frame 14 (base 1) / 13 (base 0)
          default: indice = 0;
      }

      return new TextureRegion(iconosTexture, indice * anchoFrame, 0, anchoFrame, altoFrame);
  }
  ```
- En `crearCeldaAuto()`, eliminar el `Label nombreLabel` (líneas 167-169) ya que ahora se muestra la imagen
- Aumentar ligeramente el tamaño del icono para que se vea bien: `iconoBtn.getImageCell().size(180f, 180f)`

---

## Commit 4: Añadir tecla N para activar nitro (solo si tiene nitro comprado)

**Mensaje de commit:** `feat(gameplay): añadir activación de nitro con tecla N`

**Archivos a modificar:**
1. `core/src/main/java/com/afs/dragbits/funcionalidades/EntradaJugador.java`
2. `core/src/main/java/com/afs/dragbits/funcionalidades/Acelerador.java`
3. `core/src/main/java/com/afs/dragbits/autos/Auto.java`

**Cambios detallados:**

### EntradaJugador.java
- Añadir constante: `private int teclaNitro = Input.Keys.N;`
- Añadir campo: `private boolean huboNitro = false;`
- En `keyDown()`: añadir `else if (keycode == Input.Keys.N) huboNitro = true;`
- Añadir método:
  ```java
  public boolean consumirNitro() {
      boolean r = huboNitro;
      huboNitro = false;
      return r;
  }
  ```

### Acelerador.java
- Modificar `actualizar()` para recibir y procesar la activación de nitro:
  ```java
  public void actualizar(Auto auto, EntradaJugador entrada, float delta) {
      if (entrada.estaAcelerando()) {
          auto.acelerar(delta);
      } else {
          auto.desacelerar(delta);
      }

      // Activar nitro con tecla N (solo si el auto tiene capacidad de nitro)
      if (entrada.consumirNitro() && auto.getCapacidadNitro() > 0 && auto.getNitroRestante() > 0) {
          auto.setNitroActivo(true);
      }
  }
  ```
- **Nota:** Se necesitan getters en Auto: `getCapacidadNitro()` y `getNitroRestante()` (ya existen en Auto.java líneas 328 y 88-98)

### Auto.java
- La lógica de nitro ya existe en `acelerar()` (líneas 151-159): consume `nitroRestante` y multiplica aceleración por `potenciaNitro`
- Asegurar que `nitroActivo` se apague cuando `nitroRestante <= 0` (ya implementado)

---

## Commit 5: Añadir indicador de nitro en el HUD (formato "Nitro: 100%")

**Mensaje de commit:** `feat(hud): añadir indicador de nitro con porcentaje`

**Archivos a modificar:**
1. `core/src/main/java/com/afs/dragbits/hud/Basicos.java`
2. `core/src/main/java/com/afs/dragbits/screens/GameScreen.java` (posiblemente para pasar información)

**Cambios detallados:**

### Basicos.java
- Añadir campo para mostrar nitro: `private String nitroText = "";`
- En `dibujar()`, añadir dibujo del indicador de nitro:
  ```java
  public void dibujar(SpriteBatch batch, Auto auto, float anchoPantalla, boolean dibujarBotonPausa) {
      // ... código existente ...

      // Indicador de nitro (solo si el auto tiene capacidad de nitro > 0)
      if (auto.getCapacidadNitro() > 0) {
          int porcentajeNitro = (int) ((auto.getNitroRestante() / auto.getCapacidadNitro()) * 100);
          String nitroStr = "NITRO: " + porcentajeNitro + "%";
          Color colorNitro = porcentajeNitro > 25 ? Color.GREEN : (porcentajeNitro > 0 ? Color.YELLOW : Color.RED);
          FUENTE.setColor(colorNitro);
          FUENTE.draw(batch, nitroStr, posX, posY - 105f);
          FUENTE.setColor(Color.valueOf("4DA6FF")); // restaurar color original
      }

      // ... resto del código ...
  }
  ```
- Ajustar posiciones Y de los elementos existentes si es necesario para dar espacio

---

## Resumen del orden de implementación

1. **Commit 1** - VentanaTiendaAutos.java (tamaño ventana)
2. **Commit 2** - VentanaTiendaAutos.java (tamaño botón/celda)
3. **Commit 3** - VentanaTiendaAutos.java (imagen en lugar de nombre)
4. **Commit 4** - EntradaJugador.java + Acelerador.java (tecla N para nitro)
5. **Commit 5** - Basicos.java (HUD indicador nitro)

---

## Notas técnicas importantes

- **Sprite sheet Iconos autos-sheet.png**: 2800x200 px, 14 frames de 200x200 cada uno
  - Frames 0-9: rivales
  - Frame 10-11: (desconocido, posiblemente placeholders)
  - Frame 12: Toyota (índice 12 base 0)
  - Frame 13: Challenger (índice 13 base 0)

- **Nitro en AutoConfig**: 
  - GOL: capacidadNitro=0f, cargasNitro=0 (sin nitro)
  - TOYOTA: capacidadNitro=2f, cargasNitro=1
  - CHALLENGER: capacidadNitro=3f, cargasNitro=2

- **Tecla N**: Usar `Input.Keys.N` en EntradaJugador, consumir con patrón `consumirNitro()` igual que otras teclas

- **HUD**: El indicador debe mostrar porcentaje entero (0-100%), cambiar color según nivel (verde >25%, amarillo >0%, rojo =0%)