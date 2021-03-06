package application;

import editor.EZEditor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import menu.EZMenu;


/**
 * Driver of the entire applications, sets up basic JavaFX
 */
public class Main extends Application {

    private Scene scene;  // Main scene of entire application

    /**
     * JavaFx launch method call
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Setup main window
     * @param primaryStage See JavaFX documentation
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane layout = new BorderPane();
            scene = new Scene(layout, 600, 400);

            EZEditor edit = new EZEditor(scene);
            layout.setCenter(edit.getScrollPane());

            EZMenu menu = new EZMenu(scene, edit);
            layout.setTop(menu.getMenuBar());


            primaryStage.setScene(scene);
            primaryStage.setTitle("LC-3 EZ Edit");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
