package aime.search.vacuum;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import aime.search.vacuum.State.Action;

class VacuumAgentTest {

    @Test void testCleaning() throws LocationOutOfBoundsException {
        testWithDefaultLocation();
        testWithSpecifiedLocation();
        testWithInvalidLocation();
    }

    private void testWithDefaultLocation() {
        Floor[] world = new Floor[]{ Floor.DIRTY, Floor.CLEAN, Floor.DIRTY };
        VacuumAgent agent = new VacuumAgent( world );

        assertTimeoutPreemptively( Duration.ofMillis(100), () -> {
            Action action = null;
            while ( ( action = agent.clean() ) != Action.NOTHING ) {
                System.out.println( action );
            }
        });

        assertTrue( agent.isWorldClean() );
    }

    private void testWithSpecifiedLocation() throws LocationOutOfBoundsException {
        Floor[] world = new Floor[]{ Floor.DIRTY, Floor.CLEAN, Floor.DIRTY };
        VacuumAgent agent = new VacuumAgent( world, 1 );

        assertTimeoutPreemptively( Duration.ofMillis(100), () -> {
            Action action = null;
            while ( ( action = agent.clean() ) != Action.NOTHING ) {
                System.out.println( action );
            }
        });

        assertTrue( agent.isWorldClean() );
    }

    private void testWithInvalidLocation() {
        Floor[] world = new Floor[]{ Floor.DIRTY, Floor.CLEAN, Floor.DIRTY };
        assertThrows( LocationOutOfBoundsException.class, () -> new VacuumAgent( world, 3 ) );
    }
}
