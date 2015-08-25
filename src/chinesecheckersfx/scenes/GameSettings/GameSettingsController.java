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
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.naming.Binding;

public class GameSettingsController implements Initializable ,ControlledScreen {
   
    ScreensController screenController;
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
    @FXML private ComboBox colorNumber;
    @FXML private Label alertLabel;
    @FXML private Button startGame;

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
        int totalPlayers = getAndSetTotalPlayers();
        getAndSetPlayerNames();
        getAndSetHumanPlayers();
        getAndSetColorNumber(totalPlayers);
    }
    
    @FXML
    private void getAndSetColorNumber(int totalPlayers) throws NumberFormatException {
        int colorNumberValue = Integer.parseInt(colorNumber.getValue().toString());
        gameSettings.setColorNumber((colorNumberValue));
        
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
    private int getAndSetTotalPlayers() {
        int totalPlayers = 2;
        for (CheckBox active : actives)
            if(active.isSelected())
                totalPlayers++;
        gameSettings.setTotalPlayers(totalPlayers);
        
        return totalPlayers;
    }
    
    @FXML
    protected void handleColorNumberAction(ActionEvent event){
        
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        BooleanBinding bb = bindFormValidation();
        setIsActiveCheckBox();
        setIsHumansCheckBox();
        setPlayerNameTextField();
        setColorNumberComboBox();
        startGame.disableProperty().bind(bb);
        finishedSettings = new SimpleBooleanProperty(false);
    }

    private BooleanBinding bindFormValidation() {
        BooleanBinding bb = new BooleanBinding() {
            {
                super.bind(user1.textProperty(),
                        active3.selectedProperty(),
                        active4.selectedProperty(),
                        active5.selectedProperty(),
                        active6.selectedProperty(),
                        user2.textProperty(),
                        user4.textProperty(),
                        user5.textProperty(),
                        user6.textProperty(),
                        user3.textProperty());
            }

            @Override
            protected boolean computeValue() {
                boolean isEmpty = isNotEmpty();
                boolean isUniqe = isUniqueNames();
                if(!isUniqe)
                    alertLabel.setText("Names need to be unique..");
                else if(isEmpty)
                    alertLabel.setText("Some values are empty.. pay attention");
                else
                    alertLabel.setText("");
                return isEmpty || !isUniqe;
            }
        };
        return bb;
    }
    
    private BooleanBinding bindColorNumberValidation() {
        BooleanBinding bb = new BooleanBinding() {
            {
                super.bind(colorNumber.valueProperty(),
                        active3.selectedProperty(),
                        active4.selectedProperty(),
                        active5.selectedProperty(),
                        active6.selectedProperty());
            }

            @Override
            protected boolean computeValue() {
                boolean isValid = isValidColorPick();
                if(!isValid)
                    alertLabel.setText("Hey, you cant pick more so many colors..");
                else
                    alertLabel.setText("");
                return isValid;
            }

            private boolean isValidColorPick() {
                int colorNumPicked = Integer.parseInt(colorNumber.getSelectionModel().getSelectedItem().toString());
                int totalPlayers = 2;
                for (CheckBox active : actives) 
                    if(active.isSelected())
                        totalPlayers++;
                return (colorNumPicked * totalPlayers) > 6;
            }
        };
        return bb;
    }
    
    @FXML
     private boolean isUniqueNames() {
         for (int i = 0; i < playerNames.size()-1; i++) {
             for (int j = i+1; j < playerNames.size(); j++) {
                 if(!playerNames.get(i).isDisabled() && !playerNames.get(j).isDisabled())
                 if(playerNames.get(i).getText().equals(playerNames.get(j).getText()))
                     return false;
             }
        }
         return true;
    }

    private boolean isNotEmpty() {
        return (user1.getText().isEmpty())
                || (user2.getText().isEmpty())
                || (user4.getText().isEmpty() && active4.isSelected())
                || (user5.getText().isEmpty() && active5.isSelected())
                || (user6.getText().isEmpty() && active6.isSelected())
                || (user3.getText().isEmpty() && active3.isSelected());
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
    
    private void setColorNumberComboBox() {
        colorNumber.setItems(FXCollections.observableArrayList(1, 2, 3));
    }    

    @Override
    public void setScreenParent(ScreensController screenParent) {
        screenController = screenParent;
    }
    
    public Engine.Settings getGameSettings() {
        return gameSettings;
    }

    public SimpleBooleanProperty getFinishedSettings() {
        return finishedSettings;
    }

    @Override
    public void initListners() {
        MainMenuController controller = screenController.getFXMLLoader(chinesecheckersfx.ChineseCheckersFX.MAIN_SCREEN)
                                                    .getController();
        setNewGameListner(controller);
    }
    
    private void setNewGameListner(MainMenuController menuController) {
        menuController.getIsNewGame().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                screenController.setScreen(GAME_SETTINGS_SCREEN,500,500);
            }
        });
    }
}
