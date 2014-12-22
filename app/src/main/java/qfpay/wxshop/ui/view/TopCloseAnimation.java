package qfpay.wxshop.ui.view;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;

/**
 * This animation class is animating the expanding and reducing the size of a
 * view. The animation toggles between the Expand and Reduce, depending on the
 * current state of the view
 */
public class TopCloseAnimation extends Animation {
	private View mAnimatedView;
	private LayoutParams mViewLayoutParams;
	private int mMarginStart, mMarginEnd;
	private boolean mWasEndedAlready = false;

	/**
	 * Initialize the animation
	 * 
	 * @param view
	 *            The layout we want to animate
	 * @param duration
	 *            The duration of the animation, in ms
	 */
	public TopCloseAnimation(View view, int duration) {

		setDuration(duration);
		mAnimatedView = view;
		mViewLayoutParams = (LayoutParams) view.getLayoutParams();

		int height = view.getHeight();
		if (height == 0) {
			view.measure(0, 0);
			height = view.getMeasuredHeight();
		}
		mViewLayoutParams.topMargin = 0;
		// if the bottom margin is 0,
		// then after the animation will end it'll be negative, and invisible.

		mMarginStart = 0;
		mMarginEnd = -height;

		// T.i("animation OPEN startMargin,endMargin  : " + mMarginStart+"  "+
		// mMarginEnd);
		// view.setVisibility(View.VISIBLE);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);

		if (interpolatedTime < 1.0f) {

			// Calculating the new bottom margin, and setting it
			mViewLayoutParams.topMargin = (int) (mMarginEnd * interpolatedTime);

			// T.i("OPEN doing animation change : "+
			// mViewLayoutParams.bottomMargin);
			// Invalidating the layout, making us seeing the changes we made
			mAnimatedView.requestLayout();

			// Making sure we didn't run the ending before (it happens!)
		} else if (!mWasEndedAlready) {
			mViewLayoutParams.topMargin = mMarginEnd;
			mAnimatedView.requestLayout();
			
			mWasEndedAlready = true;
		}
	}
}
