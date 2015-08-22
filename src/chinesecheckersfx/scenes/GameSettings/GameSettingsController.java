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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author shahar2
 */
public class GameSettingsController implements Initializable ,ControlledScreen {
   
    ScreensController myController;
    Engine.Settings gameSettings = new Engine.Settings();
    private SimpleBooleanProperty finishedSettings;
    @FXML private ArrayList<TextField> playerNames = new ArrayList<>();
    @FXML private ArrayList<CheckBox> humans = new ArrayList<>();
    @FXML private ArrayList<CheckBox> actives = new ArrayList<>();
    @FXML private TextField user1;
    @FXML private TextField user2;
    @FXML private TextField user3;
    @FXML private TextField user4;
    @FXML private TextField user5;
    @FXML private TextField user6;
    @FXML private CheckBox isHuman1;
    @FXML private CheckBox isHuman2;
    @FXML private CheckBox isHuman3;
    @FXML private CheckBox isHuman4;
    @FXML private CheckBox isHuman5;
    @FXML private CheckBox isHuman6;
    @FXML private CheckBox active3;
    @FXML private CheckBox active4;
    @FXML private CheckBox active5;
    @FXML private CheckBox active6;
    @FXML private final ComboBox colorNumber = new ComboBox();

    @FXML
    protected void handleActiveAction(ActionEvent event){
        for (int i = 0; i < 4; i++) {
            if(actives.get(i).isSelected())
                activatePlayer(i);
            else
                deactivatePlayer(i);
        }
    }
    
    @FXML
    private void deactivatePlayer(int i) {
        playerNames.get(i + 2).setDisable(true);
        humans.get(i + 1).setDisable(true);
        humans.get(i + 1).setSelected(false);
    }
    
    @FXML
    private void activatePlayer(int i) {
        playerNames.get(i + 2).setDisable(false);
        humans.get(i + 1).setDisable(false);
    }
    
    @FXML
    protected void handleStartAction(ActionEvent event){
       initNewGame();
       finishedSettings.set(true);
    }

    private void initNewGame() {
        getAndSetTotalPlayers();
        getAndSetPlayerNames();
        getAndSetHumanPlayers();
        //gameSettings.setColorNumber(Integer.parseInt(colorNumber.getValue().toString()));
        gameSettings.setColorNumber(2);
        

    }

    @FXML
    private void getAndSetPlayerNames() {
        ArrayList<String> names = new ArrayList<>();
        for (TextField name : playerNames)
            if(!name.isDisable())
                names.add(name.getText());
        gameSettings.setPlayerNames(names);
        
    }

    @FXML
    private void getAndSetHumanPlayers() {
        int humanPlayers = 1;
        for (CheckBox human : humans)
            if(human.isSelected())
                humanPlayers++;
        gameSettings.setHumanPlayers(humanPlayers);
    }

    @FXML
    private void getAndSetTotalPlayers() {
        int totalPlayers = 2;
        for (CheckBox active : actives)
            if(active.isSelected())
                totalPlayers++;
        gameSettings.setTotalPlayers(totalPlayers);
    }
    
    @FXML
    protected void handleColorNumberAction(ActionEvent event){
        
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setIsActiveCheckBox();
        setIsHumansCheckBox();
        setPlayerNameTextField();
        setColorNumberComboBox();
       
        finishedSettings = new SimpleBooleanProperty(false);
    }    

    private void setIsActiveCheckBox() {
        actives.add(active3);
        actives.add(active4);
        actives.add(active5);
        actives.add(active6);
    }

    private void setIsHumansCheckBox() {
        humans.add(isHuman2);
        humans.add(isHuman3);
        humans.add(isHuman4);
        humans.add(isHuman5);
        humans.add(isHuman6);
    }

    private void setPlayerNameTextField() {
        playerNames.add(user1);
        playerNames.add(user2);
        playerNames.add(user3);
        playerNames.add(user4);
        playerNames.add(user5);
        playerNames.add(user6);
    }
    
    @FXML
    private void setColorNumberComboBox() {
        ArrayList<Integer> Items = new ArrayList();
        Items.add(1);
        Items.add(2);
        Items.add(3);
        ObservableList obList = FXCollections.observableList(Items);
        colorNumber.getItems().clear();
        colorNumber.setItems(obList);
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
