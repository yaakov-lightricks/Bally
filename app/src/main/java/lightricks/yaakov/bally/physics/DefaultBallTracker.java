package lightricks.yaakov.bally.physics;

import android.graphics.Point;
import android.util.Log;

import androidx.core.math.MathUtils;

public class DefaultBallTracker implements BallTracker {

    private static final String TAG = DefaultBallTracker.class.getSimpleName();

    private final int maxX;
    private final int maxY;
    private final TrackedObject trackedObject;

    public DefaultBallTracker(TrackedObject trackedObject) {
        //we need to take care the object dimension as well when calculating
        //this will set the max x, y coordinate the object can be located
        this.trackedObject = trackedObject;
        this.maxX = this.trackedObject.containerWidth() - trackedObject.objectWidth();
        this.maxY = trackedObject.containerHeight() - trackedObject.objectHeight();
        if (this.maxX <= 0 || this.maxY <= 0){
            String ex = "illegal state: maxX and maxY have to be > 0\tactual\n:maxX=%s\n:maxY=%s";
            throw new IllegalStateException(String.format(ex, this.maxX, this.maxY));
        }
    }

    @Override
    public void onStartTracking(int objectX, int objectY, float touchX, float touchY) {
        Log.d(TAG, "touchX:" + touchX + "\ttouchY:" + touchY);
    }

    @Override
    public Point onMove(float x, float y) {
        int diffX = (int) (x - trackedObject.objectWidth() / 2);
        int diffY = (int) (y - trackedObject.objectHeight() * 2);
//        Log.d(TAG, "diffX:" + diffX + "\tdiffY:" + diffY);
        int xDestination = MathUtils.clamp(diffX, 0, maxX);
        int yDestination = MathUtils.clamp(diffY, 0, maxY);
        return new Point(xDestination , yDestination);
    }
}
