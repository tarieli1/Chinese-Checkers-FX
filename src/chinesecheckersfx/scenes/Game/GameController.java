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
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

public class GameController implements Initializable, ControlledScreen {

    private ScreensController screensController;
    private Engine gameEngine;
    Button[][] buttonBoard;
    @FXML
    GridPane boardPane;
    @FXML
    Button quitBtn;
    @FXML
    Button saveBtn;
    @FXML
    Button saveAsBtn;
    @FXML
    Button resetBtn;
    @FXML
    Button newGameBtn;
    @FXML
    Button loadGameBtn;
    @FXML
    Text helperText;
    private LoadSaveManager LS_Mnger;
    private Stage currStage;
    private Point start;
    private SimpleBooleanProperty isGameOver;
    private SimpleBooleanProperty graphicSleep;
    private Button nodeMoving;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LS_Mnger = new LoadSaveManager();
        isGameOver = new SimpleBooleanProperty(false);
        graphicSleep = new SimpleBooleanProperty(false);
        gameOverListner();
        sleepListner();
    }

    private void gameOverListner() {
        isGameOver.addListener((source, oldValue, newValue) -> {
            if (newValue) {
                doGameOver(newValue);
                disableSavePoints();
            } else {
                enableSaveBtns();
            }
        });
    }

    private void sleepListner() {
        graphicSleep.addListener((source, oldValue, newValue) -> {
            if (newValue) {
                //Remove node
                boardPane.getChildren().remove(nodeMoving);
                if (!isGameOver.get()) {
                    doTurn();
                }
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
        GameSettingsController settingsController = settingsFXML.getController();
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
        MainMenuController mainMenuController = mainFXML.getController();
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
        screensController.setScreen(GAME_SCREEN);

    }

    private void initGameComponents() {
        buttonBoard = new Button[Board.ROWS][Board.COLS];
        Board gameBoard = gameEngine.getGameBoard();
        disableNewGameAndLoadBtn();

        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                Point curPoint = new Point(i, j);
                Color buttonColor = gameBoard.getColorByPoint(curPoint);
                if (buttonColor != Color.TRANSPARENT) {
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
        if (buttonColor != Color.EMPTY) {
            buttonBoard[curPoint.x][curPoint.y].setStyle("-fx-base:" + buttonColor);
        } else {
            buttonBoard[curPoint.x][curPoint.y].setStyle("-fx-base: #B8B8B8");
        }
        buttonBoard[curPoint.x][curPoint.y].setId(buttonColor.toString());
    }

    private void onMarblePicked(Object source) {
        Button buttonClicked = (Button) source;
        start = getPointClicked(buttonClicked);
        enableResetOn(start);
        Player curPlayer = gameEngine.getCurrentPlayer();
        enableMoves(curPlayer.getPossibleMoves().get(start));

    }

    private Point getPointClicked(Button buttonClicked) {
        Point pointClicked = null;
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                Button bt = buttonBoard[i][j];
                if (bt != null && bt != buttonClicked) {
                    bt.disableProperty().set(true);
                } else if (bt != null) {
                    pointClicked = new Point(i, j);
                }
            }
        }
        return pointClicked;
    }

    private void doIteration() {
        quitBtn.disableProperty().set(false);
        initTurnForCurrentPlayer();
        showCurPlayerText();
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
        return "It's " + name + "'s turn(" + colorsFinal + ")";
    }

    private void disableAllPoints() {
        for (Button[] buttonBoardRow : buttonBoard) {
            for (Button button : buttonBoardRow) {
                if (button != null) {
                    button.disableProperty().set(true);
                }
            }
        }
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
        Point end = getPointClicked((Button) source);
        isGameOver.set(gameEngine.doIteration(start, end));
        graphicMove(start, end);//TODO remove graphic if not working
    }

    private ArrayList<Point> filterEmpty(HashMap<Point, ArrayList<Point>> possibleMoves) {
        ArrayList<Point> notEmptyKeys = new ArrayList<>();

        Set<Point> starts = possibleMoves.keySet();
        for (Point possibleStart : starts) {
            if (!possibleMoves.get(possibleStart).isEmpty()) {
                notEmptyKeys.add(possibleStart);
            }
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
        showCurPlayerText();
        Pair<Boolean, ArrayList<Point>> aiMove;
        if (curPlayer.getType() == Player.Type.PLAYER) {
            doIteration();
        } else {
            doAiIteration();
        }
    }

    private void doAiIteration() {
        quitBtn.disableProperty().set(true);
        Pair<Boolean, ArrayList<Point>> aiMove;
        aiMove = gameEngine.doAiIteration();
        Point aiStart, aiEnd;
        if (aiMove.getKey()) {
            isGameOver.set(true);
        } else {
            if (!aiMove.getValue().isEmpty()) {
                aiStart = aiMove.getValue().get(0);
                aiEnd = aiMove.getValue().get(1);
                graphicMove(aiStart, aiEnd);
            }
        }
        //TODO Show the move to the player
    }

    private void initBoard() {
        disableNewGameAndLoadBtn();
        Board gameBoard = gameEngine.getGameBoard();

        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                Point curPoint = new Point(i, j);
                Color buttonColor = gameBoard.getColorByPoint(curPoint);
                if (buttonColor != Color.TRANSPARENT) {
                    setButton(curPoint, buttonColor);
                }
            }
        }
    }

    private void disableNewGameAndLoadBtn() {
        newGameBtn.setDisable(true);
        loadGameBtn.setDisable(true);
    }

    private void doGameOver(boolean gameOver) {
        initBoard();
        newGameBtn.setDisable(false);
        loadGameBtn.setDisable(false);
        disableAllPoints();
        String text;
        if (gameOver) {
            text = gameEngine.getCurrentPlayer().getName()
                    + " Won!, It was a nice game!, please select what you want to do";
        } else {
            text = "Only AI players remain, Game is Over!";
        }

        helperText.setText(text);
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
    private void onRestartClick(ActionEvent event) {
        gameEngine.restart();
        isGameOver.set(false);
        doTurn();
    }

    @FXML
    private void onQuitClick(ActionEvent event) {
        if (isGameOver.get()) {
            closeGame();
        } else {
            quitPlayer();
        }
    }

    private void quitPlayer() {
        boolean over = gameEngine.userQuited(gameEngine.getCurrentPlayer());
        ArrayList<Player> players = gameEngine.getPlayers();
        boolean onlyAI = isOnlyAIRemain(players);

        isGameOver.set(over || onlyAI);
        if (!isGameOver.get()) {
            doTurn();
        } else {
            doGameOver(over);
        }
    }

    private boolean isOnlyAIRemain(ArrayList<Player> players) {
        int humanCount = 0;
        for (Player player : players) {
            if (player.getType() == Player.Type.PLAYER) {
                humanCount++;
            }
        }
        boolean onlyAI = false;
        if (humanCount == 0) {
            onlyAI = true;
        }
        return onlyAI;
    }

    @FXML
    private void onLoadGameClick(ActionEvent event) {
        gameEngine.clearUsersFromBoard();
        initBoard();
        loadGame();
    }

    @FXML
    private void onNewGameClick(ActionEvent event) {
        FXMLLoader settingsLoader = screensController.getFXMLLoader(GAME_SETTINGS_SCREEN);
        GameSettingsController gsc = settingsLoader.getController();
        gameEngine.clearUsersFromBoard();
        initBoard();
        gsc.getFinishedSettings().set(false);
        gsc.initGameSettings();
        screensController.setScreen(GAME_SETTINGS_SCREEN);
    }

    @FXML
    private void onSaveGameClick(ActionEvent event) {
        LS_Mnger.startAsyncSaveGame(gameEngine);
        saveGameRutine();
    }

    @FXML
    private void onSaveAsClick(ActionEvent event) {
        LS_Mnger.startAsyncSaveAsGame(gameEngine);
        saveGameRutine();
    }

    private void startGraphicWait() {
        Thread thread = new Thread(this::waitASec);
        thread.setDaemon(true);
        thread.start();
    }

    private void showCurPlayerText() {
        Player curPlayer = gameEngine.getCurrentPlayer();
        String helpMessage = createTurnStartMessage(curPlayer.getName(), curPlayer.getColors());
        helperText.setText(helpMessage);
    }

    private void waitASec() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        } finally {
            Platform.runLater(() -> {
                graphicSleep.set(true);
                graphicSleep.set(false);
            });
        }
    }

    private void graphicMove(Point start, Point end) {

        Button startBtn = buttonBoard[start.x][start.y];
        Button endBtn = buttonBoard[end.x][end.y];

        endBtn.disableProperty().set(true);
        addButtonToPane(start, Color.EMPTY);

        double x = endBtn.getLayoutX() - startBtn.getLayoutX();
        double y = endBtn.getLayoutY() - startBtn.getLayoutY();

        int padding = 15;
        Path path = new Path();
        path.getElements().add(new MoveTo(padding, padding));
        path.getElements().add(new LineTo(x + padding, y + padding));

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(1000));
        pathTransition.setPath(path);
        pathTransition.setNode(startBtn);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.play();
        nodeMoving = startBtn;
        startGraphicWait();
    }
}
