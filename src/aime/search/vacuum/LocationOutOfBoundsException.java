package aime.search.vacuum;

public class LocationOutOfBoundsException extends Exception {

    public LocationOutOfBoundsException() {

    }

    public LocationOutOfBoundsException( String message ) {
        super( message );
    }
}
