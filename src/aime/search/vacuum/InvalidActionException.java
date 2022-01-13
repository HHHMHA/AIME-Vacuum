package aime.search.vacuum;

import aime.search.vacuum.State.Action;

public class InvalidActionException extends Exception {
    private Action action;

    public InvalidActionException( Action action ) {
        this.action = action;
    }

    public InvalidActionException( String message, Action action ) {
        super( message );
        this.action = action;
    }

    public Action getAction() {
        return action;
    }
}
