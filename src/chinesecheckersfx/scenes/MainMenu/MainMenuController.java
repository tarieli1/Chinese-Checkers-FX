/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesecheckersfx.scenes.MainMenu;

import chinesecheckersfx.ChineseCheckersFX;
import chinesecheckersfx.engine.Model.Engine;
import chinesecheckersfx.engine.Model.EngineFactory;
import chinesecheckersfx.engine.Model.FileManager;
import chinesecheckersfx.scenes.ControlledScreen;
import chinesecheckersfx.scenes.ScreensController;
import generated.ChineseCheckers;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;


public class MainMenuController implements Initializable ,ControlledScreen {
   
    ScreensController screensController;
    private SimpleBooleanProperty isNewGame;
    private SimpleBooleanProperty isLoadGameFinished;
    private Engine loadedGame; 
    
    @FXML
    private void handleLoadGameAction(ActionEvent event) {
        loadGame();
    }
    
    @FXML
    private void handleNewGameAction(ActionEvent event) {
        isNewGame.set(true);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isNewGame = new SimpleBooleanProperty(false);
        isLoadGameFinished = new SimpleBooleanProperty(false);

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

    private void loadGame() {
     FileChooser fc = createXML_FC();
                
                //In response to a button click:
                
        File choosedFile = fc.showOpenDialog(null);
        if(choosedFile != null){
            try {
                loadSavedGame(choosedFile);
                } catch (Exception ex) {
                    Logger.getLogger(ChineseCheckersFX.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }
    
    private FileChooser createXML_FC() {
        //Create a file chooser
        final FileChooser fc = new FileChooser();
        fc.setTitle("Select the saveGame XML file");

        FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("xml files (*.xml)", "*.xml");
        fc.getExtensionFilters().clear();
        fc.getExtensionFilters().add(xmlFilter);
        return fc;
    }

    private void loadSavedGame(File savedGameXML) throws Exception {
        ChineseCheckers savedGame = FileManager.loadGame(savedGameXML.getAbsolutePath());
        loadedGame = EngineFactory.createEngine(savedGame);
        isLoadGameFinished.set(true);
    }

    public SimpleBooleanProperty getIsLoadGameFinished() {
        return isLoadGameFinished;
    }

    public Engine getLoadedGame() {
        return loadedGame;
    }
    
    
}
