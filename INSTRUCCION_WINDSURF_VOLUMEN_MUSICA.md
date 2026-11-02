# Instrucción para Windsurf: Separar volumen música del slider GAMEPLAY

## Cambio requerido
En `core/src/main/java/com/afs/dragbits/audio/GestorDeAudio.java`, método `actualizarVolumenMusica()` (línea ~70):

**Antes:**
```java
private void actualizarVolumenMusica() {
    if (musicaFondo != null) {
        musicaFondo.setVolume(getVolumenEfectivo());
    }
}
```

**Después:**
```java
private void actualizarVolumenMusica() {
    if (musicaFondo != null) {
        // Música usa solo volumenMaestro + modificadorPantalla (independiente de volumenGameplay)
        float volumenMusica = volumenMaestro * modificadorPantalla;
        if (muteado) volumenMusica = 0.0f;
        musicaFondo.setVolume(volumenMusica);
    }
}
```

> `getVolumenEfectivo()` queda reservado para futuros efectos de sonido (cuando se agreguen assets).

---

## CHANGELOG
Añadir en **`## [0.8.10] - 2026-11-2`** bajo sección `### Fixed`:

```markdown
### Fixed
- Volumen de música ahora es independiente del slider GAMEPLAY: `actualizarVolumenMusica()` usa solo `volumenMaestro * modificadorPantalla` (respeta mute), mientras `getVolumenEfectivo()` incluye `volumenGameplay` para efectos futuros.
```

---

## Commit message (español)
```
fix(audio): volumen música independiente del slider GAMEPLAY

- actualizarVolumenMusica() ya no usa getVolumenEfectivo()
- Música respeta solo volumenMaestro + modificadorPantalla + mute
- getVolumenEfectivo() reservado para efectos de sonido futuros
- Cumple requisito: "afectar realmente el volumen de efectos, de manera independiente del volumen musical"
```