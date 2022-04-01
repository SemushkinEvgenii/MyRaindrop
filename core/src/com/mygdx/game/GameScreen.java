package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;

public class GameScreen implements Screen {
    final MyGdxGame game;
    SpriteBatch batch;
    Texture background;
    Texture playerImage;
    Texture dropImage;
    Music music;
    OrthographicCamera camera;
    Rectangle player;
    Array<Raindrop> raindrops = new Array<>();
    long dropTime;
    int score = 0;
    long startTime;
    long finishTime;

    public GameScreen(final MyGdxGame Keys) {
        this.game = Keys;
        background = new Texture("pictures/back.png");
        playerImage = new Texture(Gdx.files.internal("pictures/Key-man.png"));
        dropImage = new Texture(Gdx.files.internal("pictures/Key.png"));
        music = Gdx.audio.newMusic(Gdx.files.internal("song/muz.mp3"));
        music.setLooping(true);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        spawnPlayer();
        spawnBottledrops();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0);
        game.font.draw(game.batch, "Me collected: " + score, 0, 470);
        game.batch.draw(playerImage, player.x, player.y);
        for(Raindrop raindrop: raindrops) {
            game.batch.draw(raindrop.texture, raindrop.x, raindrop.y);
        }
        game.batch.end();
        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            player.x = touchPos.x - 32;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            player.x -= 500 * Gdx.graphics.getDeltaTime();}
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            player.x += 500 * Gdx.graphics.getDeltaTime();}
        if(player.x < 0){
            player.x = 0;}
        if(player.x > 800 - player.width){
            player.x = 800 - player.width;}
        if(TimeUtils.nanoTime() - dropTime > 1000000000){
            spawnBottledrops();}

        Iterator<Raindrop> iterator = raindrops.iterator();
        while(iterator.hasNext()) {
            Raindrop raindrop = iterator.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if(raindrop.y + 64 < 0){
                iterator.remove();
            }
            if(raindrop.overlaps(player)) {
                score++;
                iterator.remove();
            }
        }
        finishTime = System.currentTimeMillis();
        if (finishTime - startTime > 65000){
            raindrops.clear();
            music.stop();
            game.setScreen(new EndGameScreen(game, score));
        }
    }
    @Override
    public void dispose(){
        playerImage.dispose();
        music.dispose();
        batch.dispose();
        background.dispose();
    }

    private void spawnKeysdrops() {
        Raindrop raindrop = new Raindrop();
        raindrop.x = MathUtils.random(0, 800);
        raindrop.y = 550;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrop.texture = dropImage;
        raindrop.type = "sarsaparilla";
        raindrops.add(raindrop);
        dropTime = TimeUtils.nanoTime();
    }

    private void spawnPlayer() {
        player = new Rectangle();
        player.x = 340;
        player.y = 0;
        player.width = 140;
        player.height = 100;
    }

    @Override
    public void resize(int width, int height){}
    @Override
    public void show(){
        music.play();
    }
    @Override
    public void hide(){}
    @Override
    public void pause(){}
    @Override
    public void resume(){}
}
