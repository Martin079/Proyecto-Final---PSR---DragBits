package com.afs.dragbits.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteSheetLoader {


    public static Texture cargarTextura(String ruta) {
        Texture textura = new Texture(Gdx.files.internal(ruta));
        textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return textura;
    }


    public static TextureRegion[] recortar(Texture textura, int anchoFrame, int altoFrame) {
        TextureRegion[][] tmp = TextureRegion.split(textura, anchoFrame, altoFrame);
        TextureRegion[] frames = new TextureRegion[tmp[0].length];
        System.arraycopy(tmp[0], 0, frames, 0, tmp[0].length);
        return frames;
    }


    public static TextureRegion[] recortar(Texture textura, int anchoFrame, int altoFrame, int cantidadFrames) {
        TextureRegion[][] tmp = TextureRegion.split(textura, anchoFrame, altoFrame);
        int limite = Math.min(cantidadFrames, tmp[0].length);
        TextureRegion[] frames = new TextureRegion[limite];
        System.arraycopy(tmp[0], 0, frames, 0, limite);
        return frames;
    }
}
