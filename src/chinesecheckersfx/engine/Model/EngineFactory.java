package chinesecheckersfx.engine.Model;

import chinesecheckersfx.engine.Model.Player.Type;
import generated.Cell;
import generated.ChineseCheckers;
import generated.ColorType;
import generated.PlayerType;
import generated.Players;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class EngineFactory {

    public static Engine createEngine(ChineseCheckers savedGame) throws Exception {
        Engine.Settings savedGameSettings = createGameSettings(savedGame);
        Engine engine = new Engine(savedGameSettings);
        initializeComponents(savedGame, engine);

        validateEngine(engine);
        return engine;
    }

    private static void initializeComponents(ChineseCheckers savedGame, Engine engine) throws Exception {
        setEnginePlayers(savedGame, engine);
        engine.setCurrentPlayerIndx(getCurrentPlayerIndx(savedGame, engine.getPlayers()));
        engine.setGameBoard(createGameBoard(savedGame.getBoard()));
        engine.initPointsToPlayers();

        engine.setPossibleMovesForPlayer(engine.getCurrentPlayer());
    }

    public static Point createGamePoint(Point p, chinesecheckersfx.engine.Model.Board board) {

        int counter = 1;
        int i = -1;
        while (counter <= p.y && i < chinesecheckersfx.engine.Model.Board.COLS - 1) {
            ++i;
            chinesecheckersfx.engine.Model.Color color = board.getColorByPoint(new Point(p.x - 1, i));
            if (color != Color.TRANSPARENT) {
                counter++;
            }
        }
        return new Point(p.x - 1, i);
    }

    private static Engine.Settings createGameSettings(ChineseCheckers savedGame) throws Exception {
        Engine.Settings gameSetting = new Engine.Settings();
        List<Players.Player> players = savedGame.getPlayers().getPlayer();
        gameSetting.setTotalPlayers(players.size());
        gameSetting.setPlayerNames(createNamesList(players));
        gameSetting.setColorNumber(getColorNumberEach(savedGame));
        gameSetting.setHumanPlayers(getHumanPlayers(savedGame));

        return gameSetting;
    }

    private static int getCurrentPlayerIndx(ChineseCheckers savedGame, List<Player> players) throws Exception {
        int indx = 0;
        boolean found = false;
        String curPlayerString = savedGame.getCurrentPlayer();

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(curPlayerString)) {
                indx = i;
                found = true;
            }
        }
        if (!found) {
            throw new Exception("Current player doesn't exist");
        }
        return indx;
    }

    private static List<String> createNamesList(List<Players.Player> players) {
        ArrayList<String> playersNames = new ArrayList<>();

        for (Players.Player player : players) {
            playersNames.add(player.getName());
        }

        return playersNames;
    }

    private static chinesecheckersfx.engine.Model.Board createGameBoard(generated.Board board) throws Exception {
        chinesecheckersfx.engine.Model.Board gameBoard = new chinesecheckersfx.engine.Model.Board();
        gameBoard.makeEmpty();
        List<Cell> cells = board.getCell();
        for (Cell cell : cells) {
            chinesecheckersfx.engine.Model.Color color = createColorFromSaveGameColor(cell.getColor());
            Point point = createGamePoint(new Point(cell.getRow(), cell.getCol()), gameBoard);
            chinesecheckersfx.engine.Model.Color gameColor = gameBoard.getColorByPoint(point);
            if (gameColor == Color.TRANSPARENT) {
                throw new Exception(String.format("invalid point {%d,%d}", cell.getRow(), cell.getCol()));
            }

            gameBoard.setColorByPoint(point, color);
        }

        return gameBoard;
    }

    private static void setEnginePlayers(ChineseCheckers savedGame, Engine engine) {
        ArrayList<Player> gamePlayers = engine.getPlayers();
        gamePlayers.clear();
        List<Players.Player> savedPlayers = savedGame.getPlayers().getPlayer();

        for (Players.Player savedPlayer : savedPlayers) {
            gamePlayers.add(createGamePlayer(savedPlayer));
        }
    }

    private static Player createGamePlayer(Players.Player savedPlayer) {
        String playerName = savedPlayer.getName();
        Type playerType = createTypeFromSavedType(savedPlayer.getType());
        Player gamePlayer = new Player(playerName, playerType);

        setPlayerColors(gamePlayer, savedPlayer);

        return gamePlayer;
    }

    private static Type createTypeFromSavedType(PlayerType type) {
        Type gameType;
        if (type.equals(PlayerType.COMPUTER)) {
            gameType = Type.COMPUTER;
        } else {
            gameType = Type.PLAYER;
        }
        return gameType;
    }

    private static void setPlayerColors(Player gamePlayer, Players.Player savedPlayer) {
        List<ColorType> savedColors = savedPlayer.getColorDef();
        ArrayList<Color> playerColors = gamePlayer.getColors();
        ArrayList<Point> playerTargets = gamePlayer.getTargets();
        playerColors.clear();
        playerTargets.clear();

        for (ColorType color : savedColors) {
            playerColors.add(createColorFromSaveGameColor(color.getColor()));
            playerTargets.add(createTargetPointFromSavedGameTarget(color.getTarget()));
        }
    }

    private static Color createColorFromSaveGameColor(generated.Color color) {
        Color gameColor;

        switch (color) {
            case BLACK:
                gameColor = Color.BLACK;
                break;
            case BLUE:
                gameColor = Color.BLUE;
                break;
            case GREEN:
                gameColor = Color.GREEN;
                break;
            case RED:
                gameColor = Color.RED;
                break;
            case WHITE:
                gameColor = Color.WHITE;
                break;
            case YELLOW:
                gameColor = Color.YELLOW;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return gameColor;
    }

    private static Point createTargetPointFromSavedGameTarget(ColorType.Target savedTarget) {
        return new Point(savedTarget.getRow(), savedTarget.getCol());
    }

    private static void validateEngine(Engine engine) throws Exception {
        eachColorHasTenMarbles(engine.getGameBoard());
        targetsAreLegit(engine);

    }

    private static void eachColorHasTenMarbles(chinesecheckersfx.engine.Model.Board gameBoard) throws Exception {
        HashMap<chinesecheckersfx.engine.Model.Color, AtomicInteger> colorCounterMap = createTheMap(gameBoard);
        validateMap(colorCounterMap);
    }

    private static HashMap<chinesecheckersfx.engine.Model.Color, AtomicInteger> createTheMap(chinesecheckersfx.engine.Model.Board gameBoard) {
        HashMap<chinesecheckersfx.engine.Model.Color, AtomicInteger> colorCounterMap = new HashMap<>();

        int rows = chinesecheckersfx.engine.Model.Board.ROWS;
        int cols = chinesecheckersfx.engine.Model.Board.COLS;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                updateMap(gameBoard, new Point(i, j), colorCounterMap);
            }
        }
        return colorCounterMap;
    }

    private static void updateMap(chinesecheckersfx.engine.Model.Board gameBoard, Point curPoint, HashMap<Color, AtomicInteger> colorCounterMap) {
        chinesecheckersfx.engine.Model.Color curColor = gameBoard.getColorByPoint(curPoint);
        if (isMarble(curColor)) {
            if (!colorCounterMap.containsKey(curColor)) {
                colorCounterMap.put(curColor, new AtomicInteger(0));
            }

            colorCounterMap.get(curColor).getAndIncrement();
        }
    }

    private static void validateMap(HashMap<Color, AtomicInteger> colorCounterMap) throws Exception {
        for (chinesecheckersfx.engine.Model.Color key : colorCounterMap.keySet()) {
            if (colorCounterMap.get(key).intValue() != 10) {
                throw new Exception("There is a color with less or more then 10 marbles!");
            }
        }
    }

    private static void targetsAreLegit(Engine engine) throws Exception {
        ArrayList<Player> players = engine.getPlayers();
        TargetMapper targetMap = engine.getTargetMap();
        Set<Point> validTargets = targetMap.getVertexToSet().keySet();
        for (Player player : players) {
            ArrayList<Point> targets = player.getTargets();
            for (Point target : targets) {
                if (!validTargets.contains(target)) {
                    throw new Exception("not valid target point");
                }
            }
        }
    }

    private static boolean isMarble(Color color) {
        return color != Color.TRANSPARENT && color != Color.EMPTY;
    }

    private static int getColorNumberEach(ChineseCheckers savedGame) throws Exception {
        Integer size = null;
        Players players = savedGame.getPlayers();
        List<Players.Player> playerList = players.getPlayer();
        for (Players.Player player : playerList) {
            int curPlayerSize = player.getColorDef().size();
            if (size == null) 
                size = curPlayerSize;
            else if(size != curPlayerSize)
                throw new Exception("There are 2 players with diffrent color size");
        }
        return size;
    }

    private static int getHumanPlayers(ChineseCheckers savedGame) throws Exception {
        int humanCount = 0;
        Players players = savedGame.getPlayers();
        List<Players.Player> playerList = players.getPlayer();
        for (Players.Player player : playerList) {
            if (isHuman(player))
                humanCount++;
        }
        if(humanCount == 0)
            throw new Exception("There are no human players!");
        return humanCount;
    }

    private static boolean isHuman(Players.Player player) {
        if (PlayerType.HUMAN == player.getType()) 
            return true;
        return false;
    }
}
