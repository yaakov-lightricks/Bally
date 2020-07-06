package lightricks.yaakov.bally.animation;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.widget.ImageView;

import androidx.core.graphics.ColorUtils;

import lightricks.yaakov.bally.R;

public class AnimatorHelper {

    public static AnimatorSet getHitWallAnimation (Context context, ImageView imageViewBall){
        int HIT_EFFECT_DURATION = context.getResources().getInteger(R.integer.hit_wall_animation_duration);

        AnimatorSet ballAnimator = new AnimatorSet();
        AnimatorSet start = new AnimatorSet();
        AnimatorSet ballAnimatorStart = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.hit_wall_animation);
        ballAnimatorStart.setTarget(imageViewBall);
        ValueAnimator colorStart = ValueAnimator.ofFloat(0.0f, 1.0f);
        colorStart.setDuration(HIT_EFFECT_DURATION);
        colorStart.addUpdateListener(valueAnimator -> {
            float fractionAnim = (float) valueAnimator.getAnimatedValue();
            int argb = ColorUtils.blendARGB(context.getResources().getColor(android.R.color.holo_orange_dark), context.getResources().getColor(android.R.color.holo_red_dark), fractionAnim);
            imageViewBall.getDrawable().setColorFilter(argb, PorterDuff.Mode.SRC_ATOP);
        });
        start.playTogether(ballAnimatorStart, colorStart);

        AnimatorSet back = new AnimatorSet();
        AnimatorSet ballAnimatorBack = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.hit_wall_animation_back);
        ballAnimatorBack.setTarget(imageViewBall);

        ValueAnimator colorBack = ValueAnimator.ofFloat(0.0f, 1.0f);
        colorBack.setDuration(HIT_EFFECT_DURATION);
        colorBack.addUpdateListener(valueAnimator -> {
            float fractionAnim = (float) valueAnimator.getAnimatedValue();
            int argb = ColorUtils.blendARGB(context.getResources().getColor(android.R.color.holo_red_dark), context.getResources().getColor(android.R.color.holo_orange_dark), fractionAnim);
            imageViewBall.getDrawable().setColorFilter(argb, PorterDuff.Mode.SRC_ATOP);
        });
        back.playTogether(ballAnimatorBack, colorBack);
        ballAnimator.playSequentially(start, back);
        return ballAnimator;
    }
}
