package lightricks.yaakov.bally;

import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import lightricks.yaakov.bally.physics.BallTracker;
import lightricks.yaakov.bally.physics.DefaultBallTracker;
import lightricks.yaakov.bally.physics.TrackedObject;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final float HIT_SCALE_UP_ANIMATION_FACTOR = 1.3f;
    private static final float FRICTION = 1.5f;
    private static int HIT_EFFECT_DURATION;

    private VelocityTracker velocityTracker;
    private BallTracker ballTracker;
    private ImageView imageViewBall;
    private TrackedObject trackedObject;
    private int maxValueX;
    private int maxValueY;
    private TransitionDrawable ballTransitionDrawable;
    private GestureDetector gestureDetector;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int ballWidth = (int) getResources().getDimension(R.dimen.ball_width);
        int ballHeight = (int) getResources().getDimension(R.dimen.ball_height);
        HIT_EFFECT_DURATION = getResources().getInteger(R.integer.hit_wall_animation_duration);
        imageViewBall = findViewById(R.id.image_view_ball);
        ballTransitionDrawable = (TransitionDrawable) imageViewBall.getDrawable();
        gestureDetector = new GestureDetector(this, new MyGestureListener(this::onDoubleTap));
        imageViewBall.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageViewBall.getViewTreeObserver().removeOnPreDrawListener(this);
                int containerWidth = ((ViewGroup) imageViewBall.getParent()).getWidth();
                int containerHeight = ((ViewGroup) imageViewBall.getParent()).getHeight();
                //we will put the ball in the middle
                imageViewBall.setX((containerWidth / 2f - ballWidth / 2f));
                imageViewBall.setY(containerHeight / 2f - ballHeight / 2f);
                trackedObject = TrackedObject.create(containerWidth, containerHeight, ballWidth, ballHeight);
                ballTracker = new DefaultBallTracker(trackedObject);
                imageViewBall.setOnTouchListener(MainActivity.this);
                maxValueX = trackedObject.containerWidth() - trackedObject.objectWidth();
                maxValueY = trackedObject.containerHeight() - trackedObject.objectHeight();
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        velocityTracker = VelocityTracker.obtain();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Return a VelocityTracker object back to be re-used by others.
        velocityTracker.recycle();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                velocityTracker.clear();
                velocityTracker.addMovement(event);
                ballTracker.onStartTracking((int) imageViewBall.getX(), (int) imageViewBall.getY(), event.getRawX(), event.getRawY());
                break;
            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(event);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                Point newLocation = ballTracker.onMove(event.getRawX(), event.getRawY());
                imageViewBall.setX(newLocation.x);
                imageViewBall.setY(newLocation.y);
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(1000);
                // Log velocity of pixels per second
                float xVelocity = velocityTracker.getXVelocity(pointerId);
                float yVelocity = velocityTracker.getYVelocity(pointerId);
                setFlingAnimation(xVelocity, DynamicAnimation.X, maxValueX);
                setFlingAnimation(yVelocity, DynamicAnimation.Y, maxValueY);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    /**
     * making animation to move the ball across the container
     *
     * @param startVelocity     start velocity value for the ball
     * @param directionProperty which direction, can be either x or y
     * @param maxValue          max value we can move the ball to
     */
    private void setFlingAnimation(float startVelocity, DynamicAnimation.ViewProperty directionProperty, float maxValue) {
        FlingAnimation flingX = new FlingAnimation(imageViewBall, directionProperty);
        flingX
                .setStartVelocity(startVelocity)
                .setFriction(FRICTION)
                .setMinValue(0)
                .setMaxValue(maxValue)
                .addEndListener((animation, canceled, value, velocity) -> {
                    if (value >= maxValue || value <= 0) {
                        //we hit the wall, we first want to add cool hitting effect!
                        ballTransitionDrawable.startTransition(HIT_EFFECT_DURATION);
                        imageViewBall
                                .animate()
                                .scaleX(HIT_SCALE_UP_ANIMATION_FACTOR)
                                .scaleY(HIT_SCALE_UP_ANIMATION_FACTOR)
                                .setInterpolator(new AccelerateInterpolator())
                                .setDuration(HIT_EFFECT_DURATION)
                                .start();
                        //animate back
                        imageViewBall.postDelayed(() -> {
                            imageViewBall
                                    .animate()
                                    .scaleX(1)
                                    .scaleY(1)
                                    .setInterpolator(new AccelerateInterpolator())
                                    .setDuration(HIT_EFFECT_DURATION)
                                    .start();
                            ballTransitionDrawable.reverseTransition(HIT_EFFECT_DURATION);
                        }, HIT_EFFECT_DURATION);
                        //we hit the wall, we don't want to stop, we want ot accelerate to the second direction!
                        setFlingAnimation(-velocity, directionProperty, maxValue);
                    } else {
                        ballTransitionDrawable.resetTransition();
                    }
                })
                .start();
    }

    private void onDoubleTap() {
        imageViewBall.animate().setDuration(500).scaleX(2.5f).scaleY(2.5f).alpha(0.2f)
                .withEndAction(() -> imageViewBall.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(500).start())
            .start();
    }

    interface OnDoubleTap {
        void callTap();
    }


    static class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private final OnDoubleTap onDoubleTap;

        MyGestureListener(OnDoubleTap onDoubleTap) {
            this.onDoubleTap = onDoubleTap;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            onDoubleTap.callTap();
            return super.onDoubleTap(event);
        }


    }
}