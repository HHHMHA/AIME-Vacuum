package aime.search.vacuum;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import aime.search.vacuum.State.Action;

// TODO remember to test the exception edge case for all tests
class StateTest {
    private Floor[] world;
    private State state;
    private Set<Action> actions;

    @Test void testAvailableActions() throws LocationOutOfBoundsException {
        testWhenCleanWorld();

        testWhenInTheMiddleAndDirty();
        testWhenInTheMiddleAndCleanFloor();

        testWhenOnLeftAndDirty();
        testWhenOnLeftAndCleanFloor();

        testWhenOnRightAndDirty();
        testWhenOnRightAndCleanFloor();
    }

    private void testWhenInTheMiddleAndCleanFloor() throws LocationOutOfBoundsException {
        world = new Floor[] { Floor.DIRTY, Floor.CLEAN, Floor.CLEAN };
        state = State.from( world, 1 );
        actions = new HashSet<>();
        actions.add( Action.LEFT );
        actions.add( Action.RIGHT );
        assertEquals( actions, state.getAvailableActions() );
    }

    private void testWhenInTheMiddleAndDirty() throws LocationOutOfBoundsException {
        world = new Floor[] { Floor.DIRTY, Floor.DIRTY, Floor.CLEAN };
        state = State.from( world, 1 );
        actions = new HashSet<>();
        actions.add( Action.LEFT );
        actions.add( Action.RIGHT );
        actions.add( Action.SUCK );
        assertEquals( actions, state.getAvailableActions() );
    }

    private void testWhenCleanWorld() throws LocationOutOfBoundsException {
        world = new Floor[] { Floor.CLEAN, Floor.CLEAN, Floor.CLEAN };
        state = State.from( world, 1 );
        actions = new HashSet<>();
        actions.add( Action.LEFT );
        actions.add( Action.RIGHT );
        assertEquals( actions, state.getAvailableActions() );
    }

    private void testWhenOnRightAndCleanFloor() throws LocationOutOfBoundsException {
        world = new Floor[] { Floor.DIRTY, Floor.CLEAN, Floor.CLEAN };
        state = State.from( world, world.length - 1 );
        actions = new HashSet<>();
        actions.add( Action.LEFT );
        assertEquals( actions, state.getAvailableActions() );
    }

    private void testWhenOnRightAndDirty() throws LocationOutOfBoundsException {
        world = new Floor[] { Floor.CLEAN, Floor.CLEAN, Floor.DIRTY };
        state = State.from( world, world.length - 1 );
        actions = new HashSet<>();
        actions.add( Action.LEFT );
        actions.add( Action.SUCK );
        assertEquals( actions, state.getAvailableActions() );
    }

    private void testWhenOnLeftAndCleanFloor() {
        world = new Floor[] { Floor.CLEAN, Floor.CLEAN, Floor.DIRTY };
        state = State.from( world );
        actions = new HashSet<>();
        actions.add( Action.RIGHT );
        assertEquals( actions, state.getAvailableActions() );
    }

    private void testWhenOnLeftAndDirty() {
        world = new Floor[] { Floor.DIRTY, Floor.CLEAN, Floor.DIRTY };
        state = State.from( world );
        actions = new HashSet<>();
        actions.add( Action.RIGHT );
        actions.add( Action.SUCK );
        assertEquals( actions, state.getAvailableActions() );
    }

    @Test void testIsWorldClean() {
        world = new Floor[] { Floor.CLEAN, Floor.CLEAN, Floor.CLEAN };
        state = State.from( world );
        assertTrue( state.isWorldClean() );

        world = new Floor[] { Floor.CLEAN, Floor.DIRTY, Floor.CLEAN };
        state = State.from( world );
        assertFalse( state.isWorldClean() );
    }

    // Test for doAction
    @Test void testDoAction() throws LocationOutOfBoundsException, InvalidActionException {
        world = new Floor[] { Floor.CLEAN, Floor.DIRTY, Floor.CLEAN };
        state = State.from( world, 1 );

        State stateAfterMovingLeft = state.doAction( Action.LEFT );
        State stateAfterMovingRight = state.doAction( Action.RIGHT );
        State stateAfterCleaning = state.doAction( Action.SUCK );

        assertEquals( State.from( world ), stateAfterMovingLeft );
        assertEquals( State.from( world, 2 ), stateAfterMovingRight );
        world = new Floor[] { Floor.CLEAN, Floor.CLEAN, Floor.CLEAN };
        assertEquals( State.from( world, 1 ), stateAfterCleaning  );

        testInvalidActions( stateAfterMovingLeft, stateAfterMovingRight, stateAfterCleaning );
    }

    private void testInvalidActions( State stateAfterMovingLeft, State stateAfterMovingRight, State stateAfterCleaning ) {
        assertThrows( InvalidActionException.class, () -> stateAfterMovingLeft.doAction( Action.LEFT ) );
        assertThrows( InvalidActionException.class, () -> stateAfterMovingRight.doAction( Action.RIGHT ) );
        assertThrows( InvalidActionException.class, () -> stateAfterCleaning.doAction( Action.SUCK ) );
    }

    @Test void testActionsToRoot() throws LocationOutOfBoundsException, InvalidActionException {
        world = new Floor[] { Floor.CLEAN, Floor.DIRTY, Floor.CLEAN };
        State finalState = State.from( world, 1 ).forkChild( Action.RIGHT ).forkChild( Action.LEFT ).forkChild( Action.SUCK );

        List<Action> actions = new LinkedList<>();
        actions.add( Action.RIGHT );
        actions.add( Action.LEFT );
        actions.add( Action.SUCK );
        assertEquals( actions, finalState.getActionsToRoot() );
    }

    @Test void testInvalidLocation() {
        world = new Floor[] { Floor.CLEAN };
        assertThrows( LocationOutOfBoundsException.class, () -> State.from( world, 3 ) );
    }

    // TODO test for equality, hashcode and forkChild
}
