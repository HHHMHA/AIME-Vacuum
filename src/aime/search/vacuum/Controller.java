package aime.search.vacuum;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

// TODO consider creating a Square class.
// TODO lots of redundancy for setting square images find an alternative
// TODO test the controller
// TODO Refactor the hell out of it
// TODO make adding dirt dynamic
public class Controller {
    private static final int MAX_NUMBER_OF_SQUARES = 10000;
    private static final int MIN_NUMBER_OF_SQUARES = 1;
    public HBox world;
    public TextField numberOfSquaresField;
    public Button buildWorldButton;
    public Button finishConfigurationButton;
    public Label instructionsLabel;
    public Button cleanButton;
    private int numberOfSquares;
    private EventHandler<MouseEvent> squareOnMouseClickAction;
    private static final Bloom buttonEffect = new Bloom();
    private static final int AGENT_LOCATION_NOT_SET = -1;
    private int agentLocation = AGENT_LOCATION_NOT_SET;
    private boolean finishedConfiguration = false;
    private VacuumAgent agent;
    private Thread agentThread;

    public void buildWorld( ActionEvent actionEvent ) {
        try {
            numberOfSquares = Integer.parseInt( numberOfSquaresField.getText() );
        }
        catch ( NumberFormatException ex ) {
            numberOfSquares = -1;
        }
        if ( !numberOfSquaresInRange() ) {
            Util.createAlert( AlertType.ERROR,  "The number of squares must be between " + MIN_NUMBER_OF_SQUARES + " and " + MAX_NUMBER_OF_SQUARES + ".", null, ButtonType.OK ).show();
            return;
        }
        finishedConfiguration = false;
        cleanButton.setDisable( true );
        finishConfigurationButton.setDisable( true );
        agentLocation = AGENT_LOCATION_NOT_SET;
        buildSquares();
        instructionsLabel.setText( "Left click a square to toggle dirty\nRight click to set agent in that square." );
    }

    private boolean numberOfSquaresInRange() {
        return MIN_NUMBER_OF_SQUARES <= numberOfSquares && MAX_NUMBER_OF_SQUARES >= numberOfSquares;
    }

    private void buildSquares() {
        world.getChildren().clear();
        for ( int i = 1; i <= numberOfSquares; ++i )
            world.getChildren().add( getSquare() );
    }

    private ImageView getSquare() {
        ImageView square = new ImageView( Util.CLEAN_IMAGE );
        square.setFitWidth( 100 );
        square.setFitHeight( 100 );
        square.setOnMouseEntered( mouse -> {
            if ( isSquareDisabled() )
                return;
            square.setCursor( Cursor.HAND );
            darkenColor( square );
        });
        square.setOnMouseExited( mouse -> {
            if ( isSquareDisabled() )
                return;
            square.setCursor( Cursor.DEFAULT );
            restoreColor( square );
        });
        square.setOnMouseClicked( this::squareOnMouseClickedAction );
        return square;
    }

    private boolean isSquareDisabled() {
        return finishedConfiguration;
    }

    private void squareOnMouseClickedAction( MouseEvent mouseEvent ) {
        if ( isSquareDisabled() )
            return;
        ImageView square = (ImageView)mouseEvent.getSource();
        if ( mouseEvent.getButton() == MouseButton.PRIMARY )
            toggleDirty( square );
        else if ( mouseEvent.getButton() == MouseButton.SECONDARY )
            setAgentOnSquare( square );
    }

    private void setAgentOnSquare( ImageView square ) {
        int newLocation = world.getChildren().indexOf( square );
        if ( agentLocation == newLocation )
            return;
        finishConfigurationButton.setDisable( false );
        removeAgent();
        agentLocation = newLocation;
        Image squareState = square.getImage();
        if ( squareState == Util.CLEAN_IMAGE )
            square.setImage( Util.CLEAN_ON_IMAGE );
        else
            square.setImage( Util.DIRTY_ON_IMAGE );
    }

    private void removeAgent() {
        if ( agentLocation == AGENT_LOCATION_NOT_SET )
            return;
        ImageView square = (ImageView)world.getChildren().get( agentLocation );
        if ( square.getImage() == Util.CLEAN_ON_IMAGE )
            square.setImage( Util.CLEAN_IMAGE );
        else if ( square.getImage() == Util.DIRTY_ON_IMAGE )
            square.setImage( Util.DIRTY_IMAGE );
    }

    private void darkenColor( ImageView square ) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness( -0.5 );
        square.setEffect( colorAdjust );
    }

    private void restoreColor( ImageView square ) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness( 0 );
        square.setEffect( colorAdjust );
    }

    private void toggleDirty( ImageView square ) {
        Image squareState = square.getImage();
        if ( squareState == Util.CLEAN_IMAGE )
            square.setImage( Util.DIRTY_IMAGE );
        else if ( squareState == Util.DIRTY_IMAGE )
                square.setImage( Util.CLEAN_IMAGE );
        else if ( squareState == Util.CLEAN_ON_IMAGE )
                square.setImage( Util.DIRTY_ON_IMAGE );
        else if ( squareState == Util.DIRTY_ON_IMAGE )
                square.setImage( Util.CLEAN_ON_IMAGE );
    }

    public void finishConfiguration( ActionEvent actionEvent ) throws LocationOutOfBoundsException {
        finishedConfiguration = true;
        cleanButton.setDisable( false );
        int worldSize = world.getChildren().size();
        Floor[] floorWorld = new Floor[worldSize];
        for ( int i = 0; i < worldSize; ++i ) {
            Image state = ((ImageView)world.getChildren().get( i )).getImage();
            if ( state == Util.CLEAN_IMAGE || state == Util.CLEAN_ON_IMAGE )
                floorWorld[ i ] = Floor.CLEAN;
            else if ( state == Util.DIRTY_IMAGE || state == Util.DIRTY_ON_IMAGE )
                floorWorld[ i ] = Floor.DIRTY;
        }
        agent = new VacuumAgent( floorWorld, agentLocation );
    }

    public void clean( ActionEvent actionEvent ) throws InterruptedException {
        if ( agentThread != null )
            agentThread.interrupt();
        agentThread = new Thread( () -> {
            State.Action action = agent.clean();
            while ( action != State.Action.NOTHING ) {
                if ( action == State.Action.LEFT ) {
                    removeAgent();
                    ImageView square = (ImageView)world.getChildren().get( agentLocation - 1 );
                    setAgentOnSquare( square );
                }
                else if ( action == State.Action.RIGHT ) {
                    removeAgent();
                    ImageView square = (ImageView)world.getChildren().get( agentLocation + 1 );
                    setAgentOnSquare( square );
                }
                else if ( action == State.Action.SUCK ) {
                    ImageView square = (ImageView)world.getChildren().get( agentLocation );
                    square.setImage( Util.CLEAN_ON_IMAGE );
                }
                action = agent.clean();
                try {
                    Thread.sleep( 500 );
                } catch ( InterruptedException e ) {
                    break;
                }
            }
        });
        agentThread.start();
    }

    public void applyButtonHoverEffect( MouseEvent mouseEvent ) {
        ((Button)mouseEvent.getSource()).setEffect( buttonEffect );
    }

    public void disableButtonHoverEffect( MouseEvent mouseEvent ) {
        ((Button)mouseEvent.getSource()).setEffect( null );
    }
}
