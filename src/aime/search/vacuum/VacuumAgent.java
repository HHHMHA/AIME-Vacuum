package aime.search.vacuum;

import aime.search.vacuum.State.Action;
import java.util.*;

/***
 * A vacuum agent that can clean horizontal environments
 */
public class VacuumAgent {
    private State state;

    /**
     * @param world The environment that the agent will be responsible for cleaning
     */
    public VacuumAgent( Floor[] world ) {
        state = State.from( world );
    }

    /**
     * @param world The environment that the agent will be responsible for cleaning
     * @param location The agent starting location
     * @throws LocationOutOfBoundsException when the location provided is outside the environment
     */
    public VacuumAgent( Floor[] world, int location ) throws LocationOutOfBoundsException {
        state = State.from( world, location );
    }

    /**
     * Performs a single action to get closer to the goal state
     * @return The action that the agent has taken or {@link Action#NOTHING} if no actions can be taken, or the agent is in a goal state.
     */
    public Action clean() {
        if ( state.isWorldClean() )
            return Action.NOTHING;

        Queue<State> frontier = new LinkedList<>();
        HashSet<State> explored = new HashSet<>();
        frontier.add( state );

        while ( !frontier.isEmpty() ) {
            State currentState = frontier.poll();
            explored.add( currentState );
            for ( Action action : currentState.getAvailableActions() ) {
                try {
                    State child = currentState.forkChild( action );

                    if ( child.isWorldClean() ) {
                        Action actionToNextState = child.getActionsToRoot().get( 0 );
                        state = state.doAction( actionToNextState );
                        return actionToNextState;
                    }

                    if ( !frontier.contains( child ) && !explored.contains( child ) )
                        frontier.add( child );
                } catch ( InvalidActionException e ) {
                    // Ignore because we already checked that the action is valid
                }
            }
        }
        return Action.NOTHING;
    }

    /**
     * The goal state for the agent
     * @return true if the environment is in a goat state (clean world), false otherwise.
     */
    public boolean isWorldClean() {
        return state.isWorldClean();
    }
}
