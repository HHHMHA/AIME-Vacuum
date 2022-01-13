package aime.search.vacuum;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

class Util {
    static final Image CLEAN_IMAGE = new Image( Util.class.getResource( "resources/images/clean.png" ).toString() ),
                       CLEAN_ON_IMAGE = new Image( Util.class.getResource( "resources/images/clean_on.png" ).toString() ),
                       DIRTY_IMAGE = new Image( Util.class.getResource( "resources/images/dirty.png" ).toString() ),
                       DIRTY_ON_IMAGE = new Image( Util.class.getResource( "resources/images/dirty_on.png" ).toString() ),
                       ICON = new Image( Util.class.getResource( "resources/images/dirty_on.png" ).toString() );

    static final String STYLESHEET_PATH = Util.class.getResource( "resources/style.css" ).toString(),
                        TITLE = "Vacuum Agent";

    static Alert createAlert( Alert.AlertType alertType, String contentText, Window owner, ButtonType... buttons ) {
        Alert alert = new Alert( alertType, contentText, buttons );
        alert.getDialogPane().getStyleClass().add( "dark-background" );
        alert.setHeaderText( null );
        alert.initModality( Modality.WINDOW_MODAL );
        alert.initOwner( owner );
        alert.getDialogPane().getScene().getStylesheets().add( STYLESHEET_PATH );
        ( (Stage) alert.getDialogPane().getScene().getWindow() ).getIcons().add( ICON );
        return alert;
    }
}
