package chinesecheckersfx;

import chinesecheckersfx.engine.Model.ChineseCheckersFactory;
import chinesecheckersfx.engine.Model.Engine;
import chinesecheckersfx.engine.Model.EngineFactory;
import chinesecheckersfx.engine.Model.FileManager;
import generated.ChineseCheckers;
import java.io.File;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.FileChooser;

public class LoadSaveManager {
    
    private final FileChooser saveFC;
    private final FileChooser loadFC;
    private File lastSavedFile = null;
    private File lastLoadedFile = null;
    private final SimpleBooleanProperty saveGameFinished;
    private final SimpleBooleanProperty loadGameFinished;
    private final SimpleBooleanProperty saveGameFailed;
    private final SimpleBooleanProperty loadGameFailed;
    private Engine loadedEngine;
    private Engine engineToSave;

    public LoadSaveManager() {
        String saveTitle = "Choose The Destination And Name To Save Your Game";
        String loadTitle = "Select The XML File Of Your Saved Game ";
        saveFC = createFC(saveTitle); 
        loadFC = createFC(loadTitle);
        saveGameFinished = new SimpleBooleanProperty(false);
        loadGameFinished = new SimpleBooleanProperty(false);
        saveGameFailed = new SimpleBooleanProperty(false);
        loadGameFailed = new SimpleBooleanProperty(false);
    }

    public FileChooser getSaveFC() {
        return saveFC;
    }

    public FileChooser getLoadFC() {
        return loadFC;
    }
    
    private FileChooser createFC(String title) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        addXMLExt(fc);
        return fc;
    }
    
    private void addXMLExt(final FileChooser fc) {
        FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("xml files (*.xml)", "*.xml");
        fc.getExtensionFilters().clear();
        fc.getExtensionFilters().add(xmlFilter);
    }
    
    public void startAsyncLoadGame(){
        lastLoadedFile = loadFC.showOpenDialog(null);
        Thread thread = new Thread(this::loadGame);
        thread.setDaemon(true);
        thread.start();
    }
        
    private void loadGame() {                
        boolean loaded = false;
        
        if(lastLoadedFile != null)
            loaded = loadSavedGame();

        if(loaded){
            Platform.runLater(()->{
                loadGameFinished.set(true);
                loadGameFinished.set(false);
            });
        }else{
            Platform.runLater(()->{
                loadGameFailed.set(true);
                loadGameFailed.set(false);
            });
        }
    }
    
    private boolean loadSavedGame(){
        boolean isLoaded = true;
        try {
            ChineseCheckers savedGame = FileManager.loadGame(lastLoadedFile.getAbsolutePath());
            loadedEngine = EngineFactory.createEngine(savedGame);
        } catch (Exception ex) {
            isLoaded = false;
        }
        return isLoaded;
    }
    
    public void startAsyncSaveGame(Engine gameEngine) {
        if(lastSavedFile == null)
            startAsyncSaveAsGame(gameEngine);
        else
            saveGame();
    }

    public void startAsyncSaveAsGame(Engine gameEngine) {
        engineToSave = gameEngine;
        lastSavedFile = saveFC.showSaveDialog(null);
        saveGame();
    }
    
    private void saveGame() {
        Thread thread = new Thread(this::saveGameInPath);
        thread.setDaemon(true);
        thread.start();
    }

    private void saveGameInPath() {
        boolean saved = true;
        try{
            ChineseCheckers curGame;
            curGame = ChineseCheckersFactory.createSavedGameObject(engineToSave);
            FileManager.saveGame(lastSavedFile.getAbsolutePath(), curGame);
        }catch(Exception e){
            saved = false;
        }
        
        if(saved){
            Platform.runLater(()->{
            saveGameFinished.set(true);
            saveGameFinished.set(false);
            });
        }else{
            Platform.runLater(()->{
            saveGameFailed.set(true);
            saveGameFailed.set(false);
            });
        }
    }
    
    public SimpleBooleanProperty getSaveGameFinished() {
        return saveGameFinished;
    }

    public SimpleBooleanProperty getLoadGameFinished() {
        return loadGameFinished;
    }

    public SimpleBooleanProperty getSaveGameFailed() {
        return saveGameFailed;
    }

    public SimpleBooleanProperty getLoadGameFailed() {
        return loadGameFailed;
    }

    public Engine getLoadedEngine() {
        return loadedEngine;
    }
}
