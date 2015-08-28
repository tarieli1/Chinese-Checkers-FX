 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesecheckersfx.scenes.MainMenu;

import chinesecheckersfx.engine.Model.Engine;
import chinesecheckersfx.engine.Model.EngineFactory;
import chinesecheckersfx.engine.Model.FileManager;
import chinesecheckersfx.scenes.ControlledScreen;
import chinesecheckersfx.scenes.ScreensController;
import generated.ChineseCheckers;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;


public class MainMenuController implements Initializable ,ControlledScreen {
   
    ScreensController screensController;
    private SimpleBooleanProperty isNewGame;
    private SimpleBooleanProperty isLoadGameFinished;
    private Engine loadedGame; 
    private FileChooser fc;
    @FXML Button loadGameButton;
    @FXML Button newGameButton;
    private File choosedFile;
    
    @FXML
    private void handleLoadGameAction(ActionEvent event) {
        startLoadGame();
    }
    
    @FXML
    private void handleNewGameAction() {
        isNewGame.set(true);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isNewGame = new SimpleBooleanProperty(false);
        isLoadGameFinished = new SimpleBooleanProperty(false);
        fc = createXML_FC();
    }    
       
    public SimpleBooleanProperty getIsNewGame() {
        return isNewGame;
    }

    @Override
    public void setScreenParent(ScreensController screenParent) {
        screensController = screenParent;
    }

    @Override
    public void initListners() {
        //No listners
    }

    private void startLoadGame() {
        loadGameButton.disableProperty().set(true);
        newGameButton.disableProperty().set(true);
        choosedFile = fc.showOpenDialog(screensController.getPrimaryStage());
        Thread thread = new Thread(this::loadGame);
        thread.setDaemon(true);
        thread.start();
        loadGame();
    }

    private void loadGame() {                
        boolean loaded = false;
        
        if(choosedFile != null)
            loaded = loadSavedGame(choosedFile);

        if(!loaded)
            loadFailed();
    }

    private void loadFailed() {
        isLoadGameFinished.set(false);
        loadGameButton.disableProperty().set(false);
        newGameButton.disableProperty().set(false);
        
    }
    
    private FileChooser createXML_FC() {
        //Create a file chooser
        fc = new FileChooser();
        fc.setTitle("Select the saveGame XML file");

        FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("xml files (*.xml)", "*.xml");
        fc.getExtensionFilters().clear();
        fc.getExtensionFilters().add(xmlFilter);
        return fc;
    }

    private boolean loadSavedGame(File savedGameXML){
            boolean isLoaded = true;
        try {
            ChineseCheckers savedGame = FileManager.loadGame(savedGameXML.getAbsolutePath());
            loadedGame = EngineFactory.createEngine(savedGame);
            Platform.runLater(()->isLoadGameFinished.set(true));
        } catch (Exception ex) {
            isLoaded = false;
        }
        return isLoaded;
    }

    public SimpleBooleanProperty getIsLoadGameFinished() {
        return isLoadGameFinished;
    }

    public Engine getLoadedGame() {
        return loadedGame;
    }
    
    
}
