/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesecheckersfx;

import chinesecheckersfx.scenes.MainMenu.MainMenuController;
import chinesecheckersfx.scenes.ScreensController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author shahar2press ztrl f
 *
 */
public class ChineseCheckersFX extends Application {

    public static final String MAIN_SCREEN = "MainMenu";
    public static final String MAIN_SCREEN_FXML = "MainMenu/MainMenu.fxml";
    public static final String GAME_SCREEN = "Game";
    public static final String GAME_SCREEN_FXML = "Game/Game.fxml";
    public static final String GAME_SETTINGS_SCREEN = "GameSettings";
    public static final String GAME_SETTINGS_SCREEN_FXML = "GameSettings/GameSettings.fxml";

    ScreensController mainContainer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainContainer = new ScreensController(primaryStage);
        mainContainer.loadScreen(MAIN_SCREEN, MAIN_SCREEN_FXML);
        mainContainer.loadScreen(GAME_SETTINGS_SCREEN, GAME_SETTINGS_SCREEN_FXML);
        mainContainer.loadScreen(GAME_SCREEN, GAME_SCREEN_FXML);

        mainContainer.setScreen(MAIN_SCREEN);
        initMainScreen(mainContainer.getFXMLLoader(MAIN_SCREEN));

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private MainMenuController initMainScreen(FXMLLoader fxmlLoader) {

        MainMenuController menuController = (MainMenuController) fxmlLoader.getController();

        return menuController;
    }
}
