package aime.search.vacuum;

import java.util.*;

/**
 * The state for a vacuum agent.
 */
class State {
    enum Action { LEFT, RIGHT, SUCK, NOTHING }

    private Floor[] world;
    private int location;
    private State parent;

    private State( Floor[] world, int location ) {
        parent = this;
        setWorld( world );
        this.location = location;
    }

    private void setWorld( Floor[] world ) {
        this.world = new Floor[ world.length ];
        System.arraycopy( world, 0, this.world, 0, world.length );
    }

    private static boolean inBound( int position, int max ) {
        return 0 <= position && position <= max;
    }

    /**
     * Creates a state from the given environment and position the agent on the first floor.
     * @param world The environment.
     * @return A state.
     */
    static State from( Floor[] world ) {
            return new State( world, 0 );
    }

    /**
     * Creates a state from the given environment and position the agent on the given location.
     * @param world The environment.
     * @param location The agent location.
     * @return A state.
     * @throws LocationOutOfBoundsException if the location is outside the environment.
     */
    static State from( Floor[] world, int location ) throws LocationOutOfBoundsException {
        if ( !inBound( location, world.length - 1 ) )
            throw new LocationOutOfBoundsException( "The location provided should be inside the world" );
        return new State( world, location );
    }

    /**
     * Create a child state after executing the specified action on the current state.
     * @param action The action which will be executed on the current state to create the next child state.
     * @return A new state which is a child of the current state.
     * @throws InvalidActionException If the action can't be done on the current state.
     */
    State forkChild( Action action ) throws InvalidActionException {
        State child = doAction( action );
        child.parent = this;
        return child;
    }

    /**
     * Creates a state by executing the specified action on the current state (No parent relation).
     * @param action The action which will be executed on the current state to create the next state.
     * @return A new state.
     * @throws InvalidActionException If the action can't be done on the current state.
     */
    State doAction( Action action ) throws InvalidActionException {
        if ( !isValidAction( action ) )
            throw new InvalidActionException( "The action can't be done on the current state", action );
        return forecastAction( action );
    }

    private boolean isValidAction( Action action ) {
        return getAvailableActions().contains( action );
    }

    /**
     * @return The actions that can be done on the current state.
     */
    Set<Action> getAvailableActions() {
        Set<Action> actions = new HashSet<>();

        if ( isCurrentFloorDirty() )
            actions.add( Action.SUCK );

        if ( canMoveLeft() )
            actions.add( Action.LEFT );

        if ( canMoveRight() )
            actions.add( Action.RIGHT );

        return actions;
    }

    private State forecastAction( Action action ) {
        State result = new State( world, location );

        switch ( action ) {
            case LEFT: --result.location; break;
            case RIGHT: ++result.location; break;
            case SUCK: result.world[ location ] = Floor.CLEAN; break;
        }

        return result;
    }

    /**
     * @return true if a goal state (clean world) false otherwise.
     */
    boolean isWorldClean() {
        for ( Floor floor : world )
            if ( floor == Floor.DIRTY )
                return false;

        return true;
    }

    private boolean isCurrentFloorDirty() {
        return getCurrentFloorState() == Floor.DIRTY;
    }

    private Floor getCurrentFloorState() {
        return world[ location ];
    }

    private boolean canMoveRight() {
        return location != world.length - 1;
    }

    private boolean canMoveLeft() {
        return location != 0;
    }

    /**
     * @return Returns a list of action which transformed the root state into this state.
     */
    List<Action> getActionsToRoot() {
        LinkedList<Action> actionsToRoot = new LinkedList<>();

        State current = this;
        while ( current.hasParent() ) {
            actionsToRoot.addFirst( current.getParentAction() );
            current = current.parent;
        }

        return actionsToRoot;
    }

    private Action getParentAction() {
        /*
        if ( parent.forecastAction( Action.LEFT ).equals( this ) )
            return Action.LEFT;
        else if ( parent.forecastAction( Action.RIGHT ).equals( this ) )
            return Action.RIGHT;
        else if ( parent.isCurrentFloorDirty() && parent.forecastAction( Action.SUCK ).equals( this )  )
            return Action.SUCK;
        */
        for ( Action action : parent.getAvailableActions() )
            if ( parent.forecastAction( action ).equals( this ) )
                return action;

        return Action.NOTHING; // Unreachable since this is only called from getActionsToRoot()
    }

    private boolean hasParent() {
        return this != parent;
    }

    @Override public boolean equals( Object other ) {
        try {
            State otherState = (State)other;
            return Arrays.equals( otherState.world, world ) && otherState.location == location;
        }
        catch ( ClassCastException e ) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode( world ) + location;
    }
}
