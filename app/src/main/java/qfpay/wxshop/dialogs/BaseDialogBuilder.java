package qfpay.wxshop.dialogs;

import qfpay.wxshop.utils.MobAgentTools;
import java.io.Serializable;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View.OnClickListener;

/**
 * Internal base builder that holds common values for all dialog fragment builders.
 *
 * @author Tomas Vondracek
 */
abstract class BaseDialogBuilder<T extends BaseDialogBuilder<T>> implements Serializable {

	private static final long serialVersionUID = 1L;
	public final static String ARG_REQUEST_CODE = "request_code";
	public final static String ARG_CANCELABLE_ON_TOUCH_OUTSIDE = "cancelable_oto";
	public final static String DEFAULT_TAG = "simple_dialog";
	public final static int DEFAULT_REQUEST_CODE = -42;
	public final static String BASE_BUILDER = "basebuilder";
	public final static String POSITIVE_CLICK = "basebuilder";

	protected final Context mContext;
	protected final FragmentManager mFragmentManager;
	protected final Class<? extends BaseDialogFragment> mClass;

	private Fragment mTargetFragment;
	private boolean mCancelable = true;
	private boolean mCancelableOnTouchOutside = true;

	private String mTag = DEFAULT_TAG;
	private int mRequestCode = DEFAULT_REQUEST_CODE;
	private OnClickListener onPositiveClick = null;

	public BaseDialogBuilder(Context context, FragmentManager fragmentManager, Class<? extends BaseDialogFragment> clazz) {
		mFragmentManager = fragmentManager;
		mContext = context.getApplicationContext();
		mClass = clazz;
	}

	protected abstract T self();

	protected abstract Bundle prepareArguments();

	public T setCancelable(boolean cancelable) {
		mCancelable = cancelable;
		return self();
	}
	
	public T setCancelableOnTouchOutside(boolean cancelable) {
		mCancelableOnTouchOutside = cancelable;
		if (cancelable) {
			mCancelable = cancelable;
		}
		return self();
	}

	public T setTargetFragment(Fragment fragment, int requestCode) {
		mTargetFragment = fragment;
		mRequestCode = requestCode;
		return self();
	}

	public T setRequestCode(int requestCode) {
		mRequestCode = requestCode;
		return self();
	}

	public T setTag(String tag) {
		mTag = tag;
		return self();
	}

	public T setPositiveClick(OnClickListener listener) {
		this.onPositiveClick = listener;
		return self();
	}
	
	public OnClickListener getPositiveClick() {
		return onPositiveClick;
	}

	public DialogFragment show() {
		final Bundle args = prepareArguments();

		final BaseDialogFragment fragment = (BaseDialogFragment) Fragment.instantiate(mContext, mClass.getName(), args);
	
		args.putBoolean(ARG_CANCELABLE_ON_TOUCH_OUTSIDE, mCancelableOnTouchOutside);
		args.putSerializable(BASE_BUILDER, this);
		
		if (mTargetFragment != null) {
			fragment.setTargetFragment(mTargetFragment, mRequestCode);
		} else {
			args.putInt(ARG_REQUEST_CODE, mRequestCode);
		}
		fragment.setCancelable(mCancelable);
		fragment.show(mFragmentManager, mTag);
		
		return fragment;
	}
}
