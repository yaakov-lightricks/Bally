package lightricks.yaakov.bally.physics;

import android.graphics.Point;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class DefaultBallTrackerTest {

    @Test(expected= IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNonPositiveArguments() {
        TrackedObject.create(0, 1, 1, 1);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfObjectIsBiggerThanContainer() {
        TrackedObject trackedObject = TrackedObject.create(100, 100, 101, 200);
        new DefaultBallTracker(trackedObject);
    }

    @Test
    public void shouldStayInContainerWhenMoveObjectOutsideContainer() {
        TrackedObject trackedObject = TrackedObject.create(500, 1000, 50, 50);
        DefaultBallTracker ballTracker = new DefaultBallTracker(trackedObject);
        //start touch event
        ballTracker.onStartTracking(trackedObject.objectWidth(), trackedObject.objectWidth(), trackedObject.objectWidth(), trackedObject.objectWidth());
        //move it to the edge
        Point point = ballTracker.onMove(trackedObject.containerWidth(), trackedObject.objectWidth());
        //it has to be in the edge minus object size
        assertEquals(trackedObject.containerWidth() - trackedObject.objectWidth(), point.x);
    }
}