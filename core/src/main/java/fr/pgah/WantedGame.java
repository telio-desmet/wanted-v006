package fr.pgah;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class WantedGame extends ApplicationAdapter {

  private SpriteBatch batch;
  private Texture luigiTxt;
  private Texture marioTxt;
  private Texture warioTxt;
  private Texture yoshiTxt;
  private int characterWidth = 83;
  private int characterHeight = 95;
  private Rectangle luigiBounds;
  private Rectangle marioBounds;
  private Rectangle warioBounds;
  private Rectangle yoshiBounds;
  private Vector2 loseCoord;
  private boolean win;
  private boolean lose;
  private BitmapFont font;
  private Vector2 click;
  private boolean gameInPlay;
  private int score;
  private Vector2 scoreCoord;

  @Override
  public void create() {
    batch = new SpriteBatch();
    luigiTxt = new Texture("luigi.png");
    marioTxt = new Texture("mario.png");
    warioTxt = new Texture("wario.png");
    yoshiTxt = new Texture("yoshi.png");
    generateStage();
    win = false;
    lose = false;
    gameInPlay = true;
    score = 10;
    scoreCoord = new Vector2(750, 580);
    font = new BitmapFont();
    font.getData().setScale(2);
  }

  private void generateStage() {
    luigiBounds = generateRandomBounds();
    marioBounds = generateRandomBounds();
    warioBounds = generateRandomBounds();
    yoshiBounds = generateRandomBounds();
  }

  private Rectangle generateRandomBounds() {
    int x = MathUtils.random(0, 800 - characterWidth);
    int y = MathUtils.random(0, 600 - characterHeight);
    return new Rectangle(x, y, characterWidth, characterHeight);
  }

  @Override
  public void render() {
    captureInputs();
    updateState();
    drawNewFrame();
  }

  private void captureInputs() {
    click = null;
    if (Gdx.input.isTouched()) {
      click = new Vector2(Gdx.input.getX(), Gdx.input.getY());
      // il faut "retourner" la coordonnée Y
      click.y = Gdx.graphics.getHeight() - click.y;
    }
  }

  private void updateState() {
    // Évite de mettre à jour le score plusieurs fois par tour
    if (win || lose) {
      return;
    }

    if (click != null) {
      if (luigiBounds.contains(click) || warioBounds.contains(click)
          || yoshiBounds.contains(click)) {
        lose = true;
        // pour savoir plus tard où afficher "PERDU"
        loseCoord = new Vector2(click.x, click.y);
        score -= 5;
      }
      if (marioBounds.contains(click)) {
        win = true;
        score += 5;
      }
    }
  }

  private void drawNewFrame() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    batch.begin();
    if (!win && !lose && gameInPlay) {
      batch.draw(luigiTxt, luigiBounds.getX(), luigiBounds.getY(), characterWidth, characterHeight);
      batch.draw(marioTxt, marioBounds.getX(), marioBounds.getY(), characterWidth, characterHeight);
      batch.draw(warioTxt, warioBounds.getX(), warioBounds.getY(), characterWidth, characterHeight);
      batch.draw(yoshiTxt, yoshiBounds.getX(), yoshiBounds.getY(), characterWidth, characterHeight);
    }
    if (lose && !win) {
      String loseMessage = "-5";
      font.draw(batch, loseMessage, loseCoord.x, loseCoord.y);
      batch.draw(marioTxt, marioBounds.getX(), marioBounds.getY(), characterWidth, characterHeight);
    }
    if (win) {
      String winMessage = "+5";
      font.draw(batch, winMessage, marioBounds.getX(), marioBounds.getY() + characterHeight);
    }
    if (gameInPlay && (win || lose)) {
      gameInPlay = false;
      Timer.schedule(new Task() {
        @Override
        public void run() {
          resetScreen();
        }
      }, 2);
    }
    font.draw(batch, String.valueOf(score), scoreCoord.x, scoreCoord.y);
    batch.end();
  }

  private void resetScreen() {
    win = false;
    lose = false;
    generateStage();
    gameInPlay = true;
  }

  @Override
  public void dispose() {
    batch.dispose();
    marioTxt.dispose();
  }
}
