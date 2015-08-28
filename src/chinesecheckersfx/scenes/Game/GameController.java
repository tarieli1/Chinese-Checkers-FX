package chinesecheckersfx.scenes.Game;

import static chinesecheckersfx.ChineseCheckersFX.GAME_SCREEN;
import static chinesecheckersfx.ChineseCheckersFX.GAME_SETTINGS_SCREEN;
import chinesecheckersfx.LoadSaveManager;
import chinesecheckersfx.engine.Model.Board;
import chinesecheckersfx.engine.Model.Color;
import chinesecheckersfx.engine.Model.Engine;
import chinesecheckersfx.engine.Model.Player;
import chinesecheckersfx.scenes.ControlledScreen;
import chinesecheckersfx.scenes.GameSettings.GameSettingsController;
import chinesecheckersfx.scenes.MainMenu.MainMenuController;
import chinesecheckersfx.scenes.ScreensController;
import java.awt.Point;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

public class GameController implements Initializable ,ControlledScreen {
    
    private ScreensController screensController;
    private Engine gameEngine;
    Button[][] buttonBoard;
    @FXML GridPane boardPane;
    @FXML Button quitBtn;
    @FXML Button saveBtn;
    @FXML Button saveAsBtn;
    @FXML Button resetBtn;
    @FXML Button newGameBtn;
    @FXML Button loadGameBtn;
    @FXML Text helperText;
    private LoadSaveManager LS_Mnger;
    private Stage currStage;
    private Point start;
    private SimpleBooleanProperty isGameOver;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LS_Mnger = new LoadSaveManager();
        isGameOver = new SimpleBooleanProperty(false);
        isGameOver.addListener((source, oldValue, newValue) -> {
            if (newValue) {
                doGameOver();
            }
        });
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
                //printBoard(gameEngine.getGameBoard());
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
        isGameOver.set(false);
        initGameComponents();
        doIteration();
        screensController.setScreen(GAME_SCREEN, 720, 1000);
        
    }

    private void initGameComponents() {
        buttonBoard = new Button[Board.ROWS][Board.COLS];
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
        else
            buttonBoard[curPoint.x][curPoint.y].setStyle(null);
        buttonBoard[curPoint.x][curPoint.y].setId(buttonColor.toString());
    }

    private void onMarblePicked(Object source) {
        Button buttonClicked = (Button)source;
        start = getPointClicked(buttonClicked);
        enableResetOn(start);
        Player  curPlayer = gameEngine.getCurrentPlayer();
        enableMoves(curPlayer.getPossibleMoves().get(start));

    }

    private Point getPointClicked(Button buttonClicked) {
        Point pointClicked = null;
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                Button bt = buttonBoard[i][j];
                if(bt != null && bt != buttonClicked)
                        bt.disableProperty().set(true);
                else if(bt != null)
                    pointClicked = new Point(i,j);
            }
        }
        return pointClicked;
    }

    private void doIteration() {
        Player curPlayer = gameEngine.getCurrentPlayer();
        initTurnForCurrentPlayer();
        String helpMessage = createTurnStartMessage(curPlayer.getName(),curPlayer.getColors());
        helperText.setText(helpMessage);
    }

    private void enablePoints(ArrayList<Point> points) {       
        for (Point point : points) {
            Button bt = buttonBoard[point.x][point.y];
            bt.disableProperty().set(false);
            bt.setOnAction((ActionEvent event) -> {
            onMarblePicked(event.getSource());
        });
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

    private void initTurnForCurrentPlayer() {
        start = null;
        disableAllPoints();
        enablePoints(filterEmpty(gameEngine.getCurrentPlayer().getPossibleMoves()));
    }

    private void enableMoves(ArrayList<Point> moves) {
        for (Point point : moves) {
            Button bt = buttonBoard[point.x][point.y];
            bt.disableProperty().set(false);
            bt.setOnAction((ActionEvent event) -> {
            onMarbleMoved(event.getSource());
        });
        }
    }

    private void onMarbleMoved(Object source) {
        Point end = getPointClicked((Button)source);
        isGameOver.set(gameEngine.doIteration(start, end)); 
        if(!isGameOver.get())
            doTurn();
    }

    private ArrayList<Point> filterEmpty(HashMap<Point, ArrayList<Point>> possibleMoves) {
        ArrayList<Point> notEmptyKeys = new ArrayList<>();
        
        Set<Point> starts = possibleMoves.keySet();
        for (Point possibleStart : starts) {
            if (!possibleMoves.get(possibleStart).isEmpty()) 
                notEmptyKeys.add(possibleStart);
        }
        
        return notEmptyKeys;
    }

    private void enableResetOn(Point start) {
        Button bt = buttonBoard[start.x][start.y];
        bt.setOnAction((ActionEvent event) -> {
            initTurnForCurrentPlayer();
        });
    }

    private void doTurn() {
        initBoard();
        Player curPlayer = gameEngine.getCurrentPlayer();
        Pair<Boolean,ArrayList<Point>> aiMove;
        if (curPlayer.getType() == Player.Type.PLAYER) 
            doIteration();
        else
            doAiIteration();

        
    }

    private void doAiIteration() {
        Pair<Boolean, ArrayList<Point>> aiMove;
        aiMove = gameEngine.doAiIteration();
        Point aiStart,aiEnd;
        if(aiMove.getKey())
            isGameOver.set(true);
        else{
            aiStart = aiMove.getValue().get(0);
            aiEnd = aiMove.getValue().get(1);
            doTurn();
        }
        //TODO Show the move to the player
    }

    private void initBoard(){
     newGameBtn.setDisable(true);
     loadGameBtn.setDisable(true);
     Board gameBoard = gameEngine.getGameBoard();
        
     for (int i = 0; i < Board.ROWS; i++) {
        for (int j = 0; j < Board.COLS; j++) {
            Point curPoint = new Point(i, j);
            Color buttonColor = gameBoard.getColorByPoint(curPoint);
            if(buttonColor != Color.TRANSPARENT){
                setButton(curPoint, buttonColor);
            }
        }
      }
    }

    private void doGameOver() {
        initBoard();
        newGameBtn.setDisable(false);
        loadGameBtn.setDisable(false);
        disableAllPoints();
        helperText.setText(gameEngine.getCurrentPlayer().getName() + " Won!, It was a nice game!, please select what you want to do");
    }
    
    private void loadGame() {
        loadGameBtn.disableProperty().set(true);
        failLoadListner();
        successLoadListner();
        LS_Mnger.startAsyncLoadGame();
    }

    private void successLoadListner() {
        LS_Mnger.getLoadGameFinished().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                gameEngine = LS_Mnger.getLoadedEngine();
                startGame();
            }
        });
    }

    private void failLoadListner() {
        LS_Mnger.getLoadGameFailed().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                helperText.setText("Could Not Load Game.. Try other file");
                loadGameBtn.disableProperty().set(false);
            }
        });
    }
        
    private void closeGame() {
        currStage = (Stage) quitBtn.getScene().getWindow();
        currStage.close();
    }
    
    private void saveGameRutine() {
        disableSavePoints();
        successSaveListner();
        failSaveListner();
    }

    private void disableSavePoints() {
        saveAsBtn.disableProperty().set(true);
        saveBtn.disableProperty().set(true);
    }

    private void successSaveListner() {
        LS_Mnger.getSaveGameFailed().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                helperText.setText("Could Not Save Game.. Try other location");
                enableSaveBtns();
            }
        });
    }
    
    private void failSaveListner() {
        LS_Mnger.getSaveGameFinished().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                helperText.setText("Game Saved!, You can now continue.");
                enableSaveBtns();
             }
        });
    }

    private void enableSaveBtns() {
        saveAsBtn.disableProperty().set(false);
        saveBtn.disableProperty().set(false);
    }
    
    @FXML
    private void onRestartClick(ActionEvent event){
        gameEngine.restart();
        isGameOver.set(false);
        doTurn();
    }
    
    @FXML
    private void onQuitClick(ActionEvent event){
        if(isGameOver.get())
            closeGame();
        else{
            isGameOver.set(gameEngine.userQuited(gameEngine.getCurrentPlayer()));
            if(!isGameOver.get())
                doTurn();
            else
                doGameOver();
            }
    }

    @FXML
    private void onLoadGameClick(ActionEvent event){
        loadGame();
    }
    
    @FXML
    private void onNewGameClick(ActionEvent event){
        FXMLLoader settingsLoader = screensController.getFXMLLoader(GAME_SETTINGS_SCREEN);
        GameSettingsController gsc = settingsLoader.getController();
        gsc.getFinishedSettings().set(false);
        screensController.setScreen(GAME_SETTINGS_SCREEN, 720, 1000);
    }
    
    @FXML
    private void onSaveGameClick(ActionEvent event){
        if(isGameOver.get())
            helperText.setText("Game is already over...Nothing to save");
        else{
            LS_Mnger.startAsyncSaveGame(gameEngine);
            saveGameRutine();
        }
    }
    
    @FXML
    private void onSaveAsClick(ActionEvent event){
        if(isGameOver.get())
            helperText.setText("Game is already over...Nothing to save");
        else{
            LS_Mnger.startAsyncSaveAsGame(gameEngine);
            saveGameRutine();
        }
    }

    private void printBoard(Board gameBoard) {
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                System.out.print(gameBoard.getColorByPoint(new Point(i,j)).toString());
            }
            System.out.println("");
        }
    }
}
