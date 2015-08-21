/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesecheckersfx.scenes.GameSettings;

import static chinesecheckersfx.ChineseCheckersFX.GAME_SETTINGS_SCREEN;
import chinesecheckersfx.engine.Model.Engine;
import chinesecheckersfx.scenes.ControlledScreen;
import chinesecheckersfx.scenes.MainMenu.MainMenuController;
import chinesecheckersfx.scenes.ScreensController;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author shahar2
 */
public class GameSettingsController implements Initializable ,ControlledScreen {
   
    ScreensController myController;
    Engine.Settings gameSettings;
    private SimpleBooleanProperty finishedSettings;

    @FXML
    protected void handleStartAction(ActionEvent event){
       finishedSettings.set(true);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        gameSettings = new Engine.Settings();
        //TODO Remove HC settings...
        gameSettings.setColorNumber(2);
        gameSettings.setHumanPlayers(2);
        gameSettings.setTotalPlayers(2);
        ArrayList<String> names = new ArrayList<>();
        names.add("Shahar");
        names.add("Tamir");
        gameSettings.setPlayerNames(names);
        finishedSettings = new SimpleBooleanProperty(false);
    }    

    @Override
    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }
    
    public Engine.Settings getGameSettings() {
        return gameSettings;
    }

    public SimpleBooleanProperty getFinishedSettings() {
        return finishedSettings;
    }

    @Override
    public void initListners() {
        MainMenuController controller = myController.getFXMLLoader(chinesecheckersfx.ChineseCheckersFX.MAIN_SCREEN)
                                                    .getController();
        setNewGameListner(controller);
    }
    
    private void setNewGameListner(MainMenuController menuController) {
        menuController.getIsNewGame().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                myController.setScreen(GAME_SETTINGS_SCREEN,500,500);
            }
        });
    }
}
