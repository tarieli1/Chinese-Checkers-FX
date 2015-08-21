/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesecheckersfx.scenes.Game;

import static chinesecheckersfx.ChineseCheckersFX.GAME_SCREEN;
import static chinesecheckersfx.ChineseCheckersFX.GAME_SETTINGS_SCREEN;
import chinesecheckersfx.engine.Model.Board;
import chinesecheckersfx.engine.Model.Color;
import chinesecheckersfx.engine.Model.Engine;
import chinesecheckersfx.scenes.ControlledScreen;
import chinesecheckersfx.scenes.GameSettings.GameSettingsController;
import chinesecheckersfx.scenes.ScreensController;
import java.awt.Point;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author shahar2
 */
public class GameController implements Initializable ,ControlledScreen {
   
    ScreensController myController;
    Engine gameEngine;
    
    @FXML
    GridPane boardPane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @Override
    public void initListners() {
        FXMLLoader settingsFXML = myController.getFXMLLoader(chinesecheckersfx.ChineseCheckersFX.GAME_SETTINGS_SCREEN);
        GameSettingsController settingsController  = settingsFXML.getController();
        setFinishedGameSettingsListner(settingsController);
    }
    
    private void setFinishedGameSettingsListner(GameSettingsController settingsController) {
        settingsController.getFinishedSettings().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                gameEngine = new Engine(settingsController.getGameSettings());
                initGameComponents();
                myController.setScreen(GAME_SCREEN,500,500);
            }
        });
    }    

    @Override
    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }



    private void initGameComponents() {
        Board gameBoard = gameEngine.getGameBoard();

        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                Point curPoint = new Point(i,j);
                Color buttonColor = gameBoard.getColorByPoint(curPoint);
                addButtonToPane(curPoint,buttonColor);
            }
        }
    }

    private void addButtonToPane(Point curPoint, Color buttonColor) {
        final int index = curPoint.x * Board.ROWS + curPoint.y;
        final Button button = new Button("Button " + index);
        button.setOnAction((ActionEvent event) -> {
            System.out.println("I was clicked");
            
        });
        boardPane.add(button, curPoint.x, curPoint.y);
    }
    

    
}
