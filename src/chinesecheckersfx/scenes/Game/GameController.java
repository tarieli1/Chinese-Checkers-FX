/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesecheckersfx.scenes.Game;

import static chinesecheckersfx.ChineseCheckersFX.GAME_SCREEN;
import chinesecheckersfx.engine.Model.Board;
import chinesecheckersfx.engine.Model.ChineseCheckersFactory;
import chinesecheckersfx.engine.Model.Color;
import chinesecheckersfx.engine.Model.Engine;
import chinesecheckersfx.engine.Model.FileManager;
import chinesecheckersfx.engine.Model.Player;
import chinesecheckersfx.scenes.ControlledScreen;
import chinesecheckersfx.scenes.GameSettings.GameSettingsController;
import chinesecheckersfx.scenes.MainMenu.MainMenuController;
import chinesecheckersfx.scenes.ScreensController;
import generated.ChineseCheckers;
import java.awt.Point;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author shahar2
 */
public class GameController implements Initializable ,ControlledScreen {
   
    private ScreensController screensController;
    private Engine gameEngine;
    @FXML Button[][] buttonBoard = new Button[Board.ROWS][Board.COLS];
    @FXML GridPane boardPane;
    @FXML Button quitBtn;
    @FXML Button saveBtn;
    @FXML Text helperText;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
    
    @Override
    public void setScreenParent(ScreensController screenParent) {
        screensController = screenParent;
    }
    
    @Override
    public void initListners() {
        finishedGameSettingsListner();
        finishLoadGameListner();
    }

    private void finishedGameSettingsListner() {
        FXMLLoader settingsFXML = screensController.getFXMLLoader(chinesecheckersfx.ChineseCheckersFX.GAME_SETTINGS_SCREEN);
        GameSettingsController settingsController  = settingsFXML.getController();
        setFinishedGameSettingsListner(settingsController);
    }
    
    private void setFinishedGameSettingsListner(GameSettingsController settingsController) {
        settingsController.getFinishedSettings().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                gameEngine = new Engine(settingsController.getGameSettings());
                startGame();
            }
        });
    }    
    
    private void finishLoadGameListner() {
        FXMLLoader mainFXML = screensController.getFXMLLoader(chinesecheckersfx.ChineseCheckersFX.MAIN_SCREEN);
        MainMenuController mainMenuController  = mainFXML.getController();
        setLoadGameListner(mainMenuController);
    }

    private void setLoadGameListner(MainMenuController mainMenuController) {
        mainMenuController.getIsLoadGameFinished().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                gameEngine = mainMenuController.getLoadedGame();
                startGame();
            }
        });
    }
    
    private void startGame() {
        initGameComponents();
        doIteration();
        screensController.setScreen(GAME_SCREEN, 720, 1000);
        
    }

    private void initGameComponents() {
        Board gameBoard = gameEngine.getGameBoard();
       
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                Point curPoint = new Point(i, j);
                Color buttonColor = gameBoard.getColorByPoint(curPoint);
                if(buttonColor != Color.TRANSPARENT){
                    addButtonToPane(curPoint, buttonColor);
                }
            }
        }
    }

    private void addButtonToPane(Point curPoint, Color buttonColor) {
        buttonBoard[curPoint.x][curPoint.y] = new Button();
        setButton(curPoint, buttonColor);
        buttonBoard[curPoint.x][curPoint.y].setOnAction((ActionEvent event) -> {
            onMarblePicked(event.getSource());
            
        });
        boardPane.add(buttonBoard[curPoint.x][curPoint.y], curPoint.y + 10, curPoint.x + 100);
    }

    private void setButton(Point curPoint, Color buttonColor) {
        buttonBoard[curPoint.x][curPoint.y].setDisable(true);
        if(buttonColor != Color.EMPTY)
            buttonBoard[curPoint.x][curPoint.y].setStyle("-fx-base:" + buttonColor);
        buttonBoard[curPoint.x][curPoint.y].setId(buttonColor.toString());
    }

    private void onMarblePicked(Object source) {
        //disableAllPoints();
        //enablePoints(gameEngine.getCurrentPlayer().getPossibleMoves());
        System.out.println("click");
    }
    
    @FXML
    private void onSaveGameClick(ActionEvent event){
        FileChooser fc = createXML_FC();
            File file = fc.showSaveDialog(null);
            if (file != null) 
                saveGame(file);
    }

    private void saveGame(File file) {
        ChineseCheckers curGame;
        curGame = ChineseCheckersFactory.createSavedGameObject(gameEngine);
        FileManager.saveGame(file.getAbsolutePath(), curGame);
        helperText.setText("Game Saved!, You can now continue.");
    }

    private FileChooser createXML_FC() {
        final FileChooser fc = new FileChooser();
        fc.setTitle("Choose the destination and name for your saved game");
        addXMLExt(fc);
        return fc;
    }

    private void addXMLExt(final FileChooser fc) {
        FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("xml files (*.xml)", "*.xml");
        fc.getExtensionFilters().clear();
        fc.getExtensionFilters().add(xmlFilter);
    }

    private void doIteration() {
        Player curPlayer = gameEngine.getCurrentPlayer();
        String helpMessage = createTurnStartMessage(curPlayer.getName(),curPlayer.getColors());
        helperText.setText(helpMessage);
        disableAllPoints();
        enablePoints(curPlayer.getPoints());
    }

    private void enablePoints(ArrayList<Point> points) {       
        for (Point point : points) {
            Button bt = buttonBoard[point.x][point.y];
            bt.disableProperty().set(false);
        }
    }

    private String createTurnStartMessage(String name, ArrayList<Color> colors) {
        String colorsFinal = "";
        for (Color color : colors) {
            colorsFinal += color.toString() + " ";
        }
        return "It's " + name +"'s turn(" + colorsFinal + ")";
    }

    private void disableAllPoints() {
        for (Button[] buttonBoardRow : buttonBoard) 
            for (Button button : buttonBoardRow) 
                if(button != null)
                    button.disableProperty().set(true);
    }
}
