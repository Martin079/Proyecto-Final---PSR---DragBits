package com.afs.dragbits.screens;

import com.afs.dragbits.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen extends InputAdapter implements Screen {

    private final Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;

    private BitmapFont titleFont;
    private BitmapFont optionFont;
    private GlyphLayout layout;

    private final String titleText = "DRAG BITS";
    private final String[] options = {"START GAME", "EXIT"};
    private int selectedIndex = 0;

    private final float NORMAL_SCALE = 2.0f;
    private final float SELECTED_SCALE = 2.5f;
    private float[] currentScales = {NORMAL_SCALE, NORMAL_SCALE};

    private float[] optionYPositions = new float[2];
    private float optionHeight = 60f;

    // Instancia de música de fondo
    private Music musicaFondo;

    public MainMenuScreen(Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280f, 720f, camera);
        batch = new SpriteBatch();
        layout = new GlyphLayout();

        titleFont = new BitmapFont();
        optionFont = new BitmapFont();

        // Cargar y configurar la música
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("audio/Musica/musica 1.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(1.0f); // Volumen alto para el Menú
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);

        // Ajustar volumen del menú (Fuerte)
        if (game.getMusicaFondo() != null) {
            game.getMusicaFondo().setVolume(1.0f);
            if (!game.getMusicaFondo().isPlaying()) {
                game.getMusicaFondo().play();
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (int i = 0; i < options.length; i++) {
            float targetScale = (i == selectedIndex) ? SELECTED_SCALE : NORMAL_SCALE;
            currentScales[i] = MathUtils.lerp(currentScales[i], targetScale, delta * 12f);
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        titleFont.getData().setScale(4.0f);
        layout.setText(titleFont, titleText);
        float titleX = (viewport.getWorldWidth() - layout.width) / 2;
        float titleY = viewport.getWorldHeight() * 0.75f;
        titleFont.draw(batch, titleText, titleX, titleY);

        float startY = viewport.getWorldHeight() * 0.45f;
        float spacing = 80f;

        for (int i = 0; i < options.length; i++) {
            optionFont.getData().setScale(currentScales[i]);
            layout.setText(optionFont, options[i]);

            float x = (viewport.getWorldWidth() - layout.width) / 2;
            float y = startY - (i * spacing);
            optionYPositions[i] = y;

            optionFont.draw(batch, options[i], x, y);

            if (i == selectedIndex) {
                float arrowX = x - 60f;
                optionFont.draw(batch, ">", arrowX, y);
            }
        }

        batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.UP) {
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = options.length - 1;
        } else if (keycode == Input.Keys.DOWN) {
            selectedIndex++;
            if (selectedIndex >= options.length) selectedIndex = 0;
        } else if (keycode == Input.Keys.ENTER) {
            executeSelectedOption();
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 worldCoords = viewport.unproject(new Vector3(screenX, screenY, 0));

        for (int i = 0; i < options.length; i++) {
            float y = optionYPositions[i];
            if (worldCoords.y <= y && worldCoords.y >= y - optionHeight) {
                selectedIndex = i;
            }
        }
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            executeSelectedOption();
        }
        return true;
    }

    private void executeSelectedOption() {
        if (selectedIndex == 0) {
            game.setScreen(new MapaScreen(game));
            this.dispose();
        } else if (selectedIndex == 1) {
            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        optionFont.dispose();
        if (musicaFondo != null) {
            musicaFondo.dispose();
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
}
