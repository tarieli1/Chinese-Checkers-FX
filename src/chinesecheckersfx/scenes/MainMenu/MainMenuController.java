/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesecheckersfx.scenes.MainMenu;

import chinesecheckersfx.scenes.ControlledScreen;
import chinesecheckersfx.scenes.ScreensController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;


public class MainMenuController implements Initializable ,ControlledScreen {
   
    ScreensController myController;
    private SimpleBooleanProperty isNewGame;
    private SimpleBooleanProperty isLoadGame;
    
    @FXML
    private void handleLoadGameAction(ActionEvent event) {
        System.out.println("You Load me!");
        isLoadGame.set(true);
    }
    
    @FXML
    private void handleNewGameAction(ActionEvent event) {
        System.out.println("You New me!");
        isNewGame.set(true);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isLoadGame = new SimpleBooleanProperty(false);
        isNewGame = new SimpleBooleanProperty(false);

    }    
       
    public SimpleBooleanProperty getIsNewGame() {
        return isNewGame;
    }

    public SimpleBooleanProperty getIsLoadGame() {
        return isLoadGame;
    }

    @Override
    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }
}
