 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesecheckersfx.scenes.MainMenu;

import chinesecheckersfx.LoadSaveManager;
import chinesecheckersfx.engine.Model.Engine;
import chinesecheckersfx.scenes.ControlledScreen;
import chinesecheckersfx.scenes.ScreensController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;


public class MainMenuController implements Initializable ,ControlledScreen {
   
    private ScreensController screensController;
    private SimpleBooleanProperty isNewGame;
    private SimpleBooleanProperty isLoadGameFinished;
    private Engine loadedGame; 
    private LoadSaveManager LS_Mnger;
    @FXML Button loadGameButton;
    @FXML Button newGameButton;
    
    
    @FXML
    private void handleLoadGameAction(ActionEvent event) {
        loadGame();
    }

    private void loadGame() {
        disableButtons();
        failLoadListner();
        successListner();
        LS_Mnger.startAsyncLoadGame();
    }

    private void successListner() {
        LS_Mnger.getLoadGameFinished().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                loadedGame = LS_Mnger.getLoadedEngine();
                isLoadGameFinished.set(true);
            }
        });
    }

    private void failLoadListner() {
        LS_Mnger.getLoadGameFailed().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                //TODO add text to show user error.
                enableButtons();
            }
        });
    }

    @FXML
    private void handleNewGameAction() {
        isNewGame.set(true);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isNewGame = new SimpleBooleanProperty(false);
        isLoadGameFinished = new SimpleBooleanProperty(false);
        LS_Mnger = new LoadSaveManager();
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

    public SimpleBooleanProperty getIsLoadGameFinished() {
        return isLoadGameFinished;
    }

    public Engine getLoadedGame() {
        return loadedGame;
    }
    
    private void disableButtons() {
        loadGameButton.disableProperty().set(true);
        newGameButton.disableProperty().set(true);
    }
    
    private void enableButtons() {
        loadGameButton.disableProperty().set(false);
        newGameButton.disableProperty().set(false);
    }
}
