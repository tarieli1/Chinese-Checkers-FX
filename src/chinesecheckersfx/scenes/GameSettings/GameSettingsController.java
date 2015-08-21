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
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author shahar2
 */
public class GameSettingsController implements Initializable ,ControlledScreen {
   
    ScreensController myController;
    Engine.Settings gameSettings;
    private SimpleBooleanProperty finishedSettings;
    @FXML private TextField user1;
    @FXML private TextField user2;
    @FXML private TextField user3;
    @FXML private TextField user4;
    @FXML private TextField user5;
    @FXML private TextField user6;
    @FXML private CheckBox isHuman2;
    @FXML private CheckBox isHuman3;
    @FXML private CheckBox isHuman4;
    @FXML private CheckBox isHuman5;
    @FXML private CheckBox isHuman6;
    @FXML private CheckBox active3;
    @FXML private CheckBox active4;
    @FXML private CheckBox active5;
    @FXML private CheckBox active6;

    @FXML
    protected void handleActiveAction(ActionEvent event){
        user3.setDisable(false);
    }
    
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
