package snake;

import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author Timothy
 */
public abstract class Window {

    private final String TITLE;
    private final int WIDTH;
    private final int HEIGHT;
    private final Scene SCENE;
    private Stage stage;

    /**
     *
     * @param title
     * @param width
     * @param height
     * @param xPos
     * @param yPos
     * @param scene
     */
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

    /**
     *
     */
    public void show() {
        stage.show();
    }

    /**
     *
     */
    public void hide() {
        stage.hide();
    }

    /**
     *
     * @return
     */
    public int getWidth() {
        return WIDTH;
    }

    /**
     *
     * @return
     */
    public int getHeight() {
        return HEIGHT;
    }

    /**
     *
     * @return
     */
    public boolean getVisible() {
        return stage.isShowing();
    }

    /**
     *
     */
    public void close() {
        stage.close();
    }

    /**
     *
     * @return
     */
    public Stage getStage() {
        return this.stage;
    }

    /**
     *
     * @return
     */
    public Scene getScene() {
        return SCENE;
    }

    /**
     *
     * @param event
     */
    public void handleMouseClicked(MouseEvent event) {
    }

    /**
     *
     */
    public void setMousePressedHandler() {
        SCENE.setOnMousePressed((MouseEvent event) -> {
            handleMouseClicked(event);
        });
    }
}
