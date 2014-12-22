package qfpay.wxshop.ui.view;

import java.util.List;

import qfpay.wxshop.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FilterViewHis extends ViewGroup {

	private Context mContext;

	private List<Tag> mData;

	private int mHorizontalMargin;
	private int mVerticalMargin;
	private int mTextColor;
	private int mTextTouchColor;

	private float mTextSize = 16;

	private boolean isViewInit = false;

	private int mCurSelectedPosition = 0;

	private OnItemClickListener onItemClickListener;

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id != mCurSelectedPosition) {
				mCurSelectedPosition = id;
		    }
			Tag tag = mData.get(id);
			if(tag.isDisplay()){
				tag.setDisplay(false);
			}else{
				tag.setDisplay(true);
			}
			updateTextColor();
			if (null != onItemClickListener) {
				onItemClickListener.onItemClick(FilterViewHis.this, mData,
						mCurSelectedPosition);
			}
		}
	};

	public FilterViewHis(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context.getApplicationContext();
		initView();
	}

	public FilterViewHis(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FilterViewHis(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int count = this.getChildCount();
		if (count <= 0) {
			return;
		}

		int width = this.getMeasuredWidth();// 720
		int heigth = this.getMeasuredHeight(); // 0
		Rect mPadding = new Rect(getPaddingLeft(), getPaddingTop(),
				getPaddingRight(), getPaddingBottom());
		if (width <= (mPadding.left + mPadding.right)) {
			return;
		}

		int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
				MeasureSpec.UNSPECIFIED);
		int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heigth,
				MeasureSpec.UNSPECIFIED);

		int sumW = mPadding.left;
		int sumH = mPadding.top;
		int childW = 0;
		int childH = 0;
		View child = null;
		LayoutParams params = null;
		for (int i = 0; i < count; i++) {
			
			child = this.getChildAt(i);
			if (child == null) {
				continue;
			}

			params = (LayoutParams) child.getLayoutParams();
			if (params == null) {
				continue;
			}
		
//			if(childH != 0){
//				
//				if(sumH >= childH * 2 + mPadding.bottom + mVerticalMargin){
//					break;
//				}
//				
//			}
			
			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
			childW = child.getMeasuredWidth();
			childH = child.getMeasuredHeight();
			
			sumW += childW;
			
			if (i == 0) {
				sumH += childH;
			}

			if (sumW > width - mPadding.right) {
				if ((sumW - childW - mPadding.left) == 0) {
					width = sumW + mPadding.right;
				} else {
						
						sumW = mPadding.left + childW;
						sumH += (mVerticalMargin + childH);
				}
			}
			params.left = sumW - childW;
			params.top = sumH - childH;
			params.right = sumW;
			params.bottom = sumH;

			sumW += mHorizontalMargin;
		}
		sumH += mPadding.bottom;
		if(sumH >= childH * 2 + mPadding.bottom * 2 + mVerticalMargin){
			sumH = childH * 2 + mPadding.bottom * 2 + mVerticalMargin;
		}
		setMeasuredDimension(width, sumH);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = this.getChildCount();
		if (count > 0) {
			View child = null;
			LayoutParams params = null;
			for (int i = 0; i < count; i++) {
				child = this.getChildAt(i);
				if (null != child) {
					params = (LayoutParams) child.getLayoutParams();
					if (null != params) {
						child.layout(params.left, params.top, params.right,
								params.bottom);
					}
				}
			}
		}
	}

	public void setData(List<Tag> data) {
		this.mData = data;
		updateViews();
	}

	public void setTextSize(float size) {
		if (this.mTextSize != size) {
			this.mTextSize = size;
			if (isViewInit) {
				updateViews();
			}
		}
	}

	public void setTextColor(int color) {
		if (this.mTextColor != color) {
			this.mTextColor = color;
			updateTextColor();
		}
	}

	public void setTextTouchColor(int color) {
		if (this.mTextTouchColor != color) {
			mTextTouchColor = color;
			updateTextColor();
		}
	}

	public void setHorizontalMargin(int margin) {
		if (this.mHorizontalMargin != margin) {
			this.mHorizontalMargin = margin;
			if (isViewInit) {
				requestLayout();
			}
		}
	}

	public void setVerticalMargin(int margin) {
		if (this.mVerticalMargin != margin) {
			this.mVerticalMargin = margin;
			if (isViewInit) {
				requestLayout();
			}
		}
	}

	public int getSelectedPosition() {
		return mCurSelectedPosition;
	}

	public void setSelected(int position) {
//		if (this.mCurSelectedPosition != position) {
//			this.mCurSelectedPosition = position;
//			if (!isViewInit) {
//				return;
//			}
			updateTextColor();
//			if (null != onItemClickListener) {
//				onItemClickListener.onItemClick(this, mData, position);
//			}
//		}

	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	private LayoutParams getGeneralParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	private void initView() {
		mTextColor = Color.WHITE;
		mTextTouchColor = Color.parseColor("#ffff00");
		mTextSize = 16;
		mVerticalMargin = 8;
		mHorizontalMargin = 30;
	}

	public synchronized void updateViews() {
		isViewInit = true;
		this.removeAllViews();
		if (mData != null) {
			TextView tv = null;
			for (int i = 0, len = mData.size(); i < len; i++) {
				tv = new TextView(mContext);
				tv.setText(mData.get(i).getText());
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize);
				tv.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				tv.setOnClickListener(onClickListener);
				tv.setId(i);
				this.addView(tv, getGeneralParams());
			}
		}
		updateTextColor();
		requestLayout();
	}

	private void updateTextColor() {
		int count = this.getChildCount();
		int pix = getResources()
				.getDimensionPixelSize(R.dimen.activity_padding);
		int pixleft = getResources()
				.getDimensionPixelSize(R.dimen.activity_padding_left);
		int pixTop = getResources()
				.getDimensionPixelSize(R.dimen.tag_padding_top);
		if (count > 0) {
			TextView tv = null;
			for (int i = 0; i < count; i++) {
				tv = (TextView) this.getChildAt(i);
				Tag tag = mData.get(i);
				if (tag.isDisplay()) {
					tv.setTextColor(mTextTouchColor);
					tv.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.tag_bg));
					tv.setPadding(pixleft, pixTop, pix, pixTop);
				} else {
					tv.setTextColor(mTextColor);
					tv.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.tag_bg_none));
					tv.setPadding(pixleft, pixTop, pix, pixTop);
				}
			}
		}
	}

	static class LayoutParams extends ViewGroup.LayoutParams {

		public int left;
		public int top;
		public int right;
		public int bottom;

		public LayoutParams(Context arg0, AttributeSet arg1) {
			super(arg0, arg1);
		}

		public LayoutParams(int arg0, int arg1) {
			super(arg0, arg1);
		}

		public LayoutParams(ViewGroup.LayoutParams arg0) {
			super(arg0);
		}

	}

	public static interface OnItemClickListener {
		void onItemClick(View view, List<Tag> data, int position);
	}
}
