package lightricks.yaakov.bally.physics;

import android.graphics.Point;

/**
 * class to make calculation for where to put object in the screen when dragging it
 */
public interface BallTracker {

    /**
     * supply data of state when start tracking object
     * @param objectX x coordinate of object
     * @param objectY y coordinate of object
     * @param touchX x coordinate of touch
     * @param touchY y coordinate of touch
     */
    void onStartTracking(int objectX, int objectY, float touchX, float touchY);

    /**
     * calculate new location for move event
     * @param x absolute x coordinate
     * @param y absolute y coordinate
     * @return new position for object
     */
    Point onMove(float x, float y);
}
