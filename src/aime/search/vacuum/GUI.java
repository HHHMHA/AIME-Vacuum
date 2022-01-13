package aime.search.vacuum;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        primaryStage.setTitle( Util.TITLE );
        primaryStage.setScene( new Scene( root ) );
        primaryStage.getIcons().add( Util.ICON );
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}