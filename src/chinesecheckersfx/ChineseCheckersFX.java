/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesecheckersfx;

import chinesecheckersfx.scenes.MainMenu.MainMenuController;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author shahar2
 */
public class ChineseCheckersFX extends Application {
    private final String MENU_SCENE_FXML_PATH = "scenes/MainMenu/MainMenu.fxml";
    private final String SETTINGS_SCENE_FXML_PATH = "scenes/GameSettings/GameSettings.fxml";
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = getFXMLLoader(MENU_SCENE_FXML_PATH);
        Parent sceneRoot = getSceneRoot(fxmlLoader);
        MainMenuController menuController = getMenuController(fxmlLoader, primaryStage);

        Scene scene = new Scene(sceneRoot);
        
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private FXMLLoader getFXMLLoader(String fxmlPath) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(fxmlPath);
        fxmlLoader.setLocation(url);
        return fxmlLoader;
    }

    private Parent getSceneRoot(FXMLLoader fxmlLoader) throws IOException {
        return (Parent) fxmlLoader.load(fxmlLoader.getLocation().openStream());

    }

    private MainMenuController getMenuController(FXMLLoader fxmlLoader, Stage primaryStage) {
        MainMenuController menuController = (MainMenuController) fxmlLoader.getController();       
        setNewGameListner(menuController, primaryStage);              
        setLoadGameListner(menuController, primaryStage);
        
        return menuController;
    }

    private void setNewGameListner(MainMenuController menuController, Stage primaryStage) {
        menuController.getIsNewGame().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                try {
                    changeScene(primaryStage,SETTINGS_SCENE_FXML_PATH);
                } catch (IOException ex) {
                    Logger.getLogger(ChineseCheckersFX.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("new");
            }
        });
    }

    private void setLoadGameListner(MainMenuController menuController, Stage primaryStage) {
        menuController.getIsLoadGame().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                //primaryStage.setScene(new GameScene(playersManager));
                System.out.println("Loading a Saved Game!");
                //Create a file chooser
                final JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter
                ("xml files (*.xml)", "xml");
                fc.setFileFilter(xmlfilter); //adds the .xml option
                fc.setAcceptAllFileFilterUsed(false);//disables ALL FILES option
                
                //In response to a button click:
                int returnVal = fc.showOpenDialog(fc);
                if (returnVal == JFileChooser.APPROVE_OPTION) 
                    System.out.println("Starting" + fc.getSelectedFile());
                
                //Enables Load Game button.
                menuController.getIsLoadGame().set(false);
                    
            }
        });
    }

    private void changeScene(Stage primaryStage, String SETTINGS_SCENE_FXML_PATH) throws IOException {
        FXMLLoader fxmlLoader = getFXMLLoader(SETTINGS_SCENE_FXML_PATH);
        Parent sceneRoot = getSceneRoot(fxmlLoader);
        
        Scene scene = new Scene(sceneRoot);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
}
