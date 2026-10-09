package com.afs.dragbits.rivales;

import com.afs.dragbits.menurivales.TipoCarrera;

public class CatalogoRivales {
    private static final RivalConfig[] LEGALES = new RivalConfig[5];
    private static final RivalConfig[] ILEGALES = new RivalConfig[5];

    static {
        LEGALES[0] = new RivalConfig("Principiante", "sprites/Autos/Legales/renault 12-sheet.png", 155f, 23f, 23f, 0f, 800);
        LEGALES[1] = new RivalConfig("Amateur", "sprites/Autos/Legales/fiat 147-sheet.png", 185f, 28f, 28f, 0f, 2200);
        LEGALES[2] = new RivalConfig("Callejero", "sprites/Autos/Legales/mustang-sheet.png", 210f, 35f, 35f, 2f, 5000);
        LEGALES[3] = new RivalConfig("Pro-Racer", "sprites/Autos/Legales/corvette-sheet.png", 240f, 43f, 43f, 3f, 10000);
        LEGALES[4] = new RivalConfig("Campeon", "sprites/Autos/Legales/dragster-sheet.png", 275f, 58f, 58f, 4f, 18000);

        ILEGALES[0] = new RivalConfig("Novato Nocturno", "sprites/Autos/Ilegales/focus-sheet.png", 158f, 24f, 24f, 0f, 150);
        ILEGALES[1] = new RivalConfig("Corredor Urbano", "sprites/Autos/Ilegales/taxi-sheet.png", 190f, 29f, 29f, 0f, 350);
        ILEGALES[2] = new RivalConfig("Apostador", "sprites/Autos/Ilegales/zanella-sheet.png", 220f, 37f, 37f, 2f, 700);
        ILEGALES[3] = new RivalConfig("Rey de la Pista", "sprites/Autos/Ilegales/colectivo-sheet.png", 250f, 47f, 47f, 3.5f, 1200);
        ILEGALES[4] = new RivalConfig("Lider de la Red", "sprites/Autos/Ilegales/amarok-sheet.png", 285f, 63f, 63f, 5f, 2000);
    }

    public static RivalConfig obtener(TipoCarrera tipo, int indice) {
        if (indice < 0 || indice > 4) {
            throw new IllegalArgumentException("Índice de rival inválido: " + indice);
        }

        if (tipo == TipoCarrera.LEGAL) {
            return LEGALES[indice];
        } else if (tipo == TipoCarrera.ILEGAL) {
            return ILEGALES[indice];
        } else {
            throw new IllegalArgumentException("Tipo de carrera inválido: " + tipo);
        }
    }

    public static int cantidad() {
        return 5;
    }
}
