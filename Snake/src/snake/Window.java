package snake;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Timothy
 */
public class Window {

    private final String TITLE;
    private final int WIDTH;
    private final int HEIGHT;
    private final Scene SCENE;
    private Stage stage;

    public Window(String title, int width, int height, int xPos, int yPos, Scene scene) {
        TITLE = title;
        WIDTH = width;
        HEIGHT = height;
        SCENE = scene;
        stage = new Stage();
        stage.setTitle(TITLE);
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        stage.setScene(scene);
        stage.setX(xPos);
        stage.setY(yPos);
        //stage.setResizable(false);
    }

    public void show() {
        stage.show();
    }

    public void hide() {
        stage.hide();
    }

    public boolean getVisible() {
        return stage.isShowing();
    }

    public void close() {
        stage.close();
    }

    public Stage getStage() {
        return this.stage;
    }
}
