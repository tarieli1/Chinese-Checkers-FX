/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chinesecheckersfx.scenes;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author shahar2
 */
 public class ScreensController extends StackPane {
     
    private final HashMap<String, Node> screens = new HashMap<>(); 
    private final HashMap<String, FXMLLoader> fxmls = new HashMap<>();
    private final Stage primaryStage;
    
    public ScreensController(Stage primaryStage){
        this.primaryStage = primaryStage;
    }
    
    public boolean loadScreen(String name, String resource) throws IOException {
     //try { 
       FXMLLoader myLoader = createFXMLLoader(resource);
       Parent loadScreen = (Parent) myLoader.load(); 
       ControlledScreen myScreenControler = ((ControlledScreen) myLoader.getController());
       myScreenControler.setScreenParent(this);
       myScreenControler.initListners();
       addScreen(name, loadScreen); 
       addFxmlLoader(name, myLoader);
       return true; 
    /*}catch(Exception e) { 
       System.out.println(e.getMessage()); 
       return false; 
     }*/ //TODO re Added the catch at end
   }
    
    public boolean setScreen(final String name,double width,double heigth) { 

     if(screens.get(name) != null) { //screen loaded 
       final DoubleProperty opacity = opacityProperty(); 
       
       //Is there is more than one screen 
       if(!getChildren().isEmpty()){ 
         EventHandler handler = (EventHandler) (Event event) -> removeDisplayedScreen(name, opacity);
         KeyFrame firstKF = new KeyFrame(javafx.util.Duration.ZERO, new KeyValue(opacity, 1.0));
         KeyFrame secondKF = new KeyFrame(new javafx.util.Duration(800), handler, new KeyValue(opacity, 0.0));    
         Timeline fade = new Timeline(firstKF,secondKF); 
         fade.play(); 
       } else { 
         //no one else been displayed, then just show 
         setOpacity(0.0); 
         getChildren().add(screens.get(name)); 
         KeyFrame firstFadeInKF = new KeyFrame(javafx.util.Duration.ZERO, new KeyValue(opacity, 0.0));
         KeyFrame secondFadeInKF = new KeyFrame(new javafx.util.Duration(1300), new KeyValue(opacity, 1.0));
         Timeline fadeIn = new Timeline(firstFadeInKF,secondFadeInKF);
         fadeIn.play(); 
       } 
       primaryStage.setHeight(720);
       primaryStage.setWidth(784);
       return true; 
     } 
    else //Load First 
         return false; 
        
    }

    private void removeDisplayedScreen(final String name, final DoubleProperty opacity) {
        getChildren().remove(0);
        //add new screen
        getChildren().add(0, screens.get(name));
        KeyFrame firstKF = new KeyFrame(javafx.util.Duration.ZERO, new KeyValue(opacity, 0.0));
        KeyFrame secondKF = new KeyFrame(new javafx.util.Duration(500), new KeyValue(opacity, 1.0));
        
        Timeline fadeIn = new Timeline(firstKF,secondKF);
        fadeIn.play();
    }

    public boolean unloadScreen(String name) { 
     if(screens.remove(name) == null) { 
       System.out.println("Screen didn't exist"); 
       return false; 
     } else { 
       return true; 
     } 
    } 
    
    public void addScreen(String name, Node screen) { 
       screens.put(name, screen);  
    } 
    
    public void addFxmlLoader(String name, FXMLLoader fxmll) { 
       fxmls.put(name, fxmll);  
    } 
     
    public FXMLLoader getFXMLLoader(String fxmlPath) {

        return fxmls.get(fxmlPath);
    }

    private FXMLLoader createFXMLLoader(String resource) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(resource);
        fxmlLoader.setLocation(url);
        return fxmlLoader;
    }
 }
