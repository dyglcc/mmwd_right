package com.loveplusplus.demo.image;

import qfpay.wxshop.R;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.co.senab.photoview.PhotoViewAttacher;
import com.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;

	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url")
				: null;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.image_detail_fragment,
				container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);
		mAttacher = new PhotoViewAttacher(mImageView);

		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				getActivity().finish();
			}
		});

		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		AQuery aquery = new AQuery(getActivity());
		// aquery.id(mImageView).progress(progressBar).image(new
		// BitmapAjaxCallback(){
		//
		// @Override
		// protected void callback(String url, ImageView iv, Bitmap bm,
		// AjaxStatus status) {
		// super.callback(url, iv, bm, status);
		//
		//
		// }
		//
		// });
		aquery.id(mImageView).progress(progressBar).image(mImageUrl, true, true, 0, 0,
				new BitmapAjaxCallback() {

					@Override
					public void callback(String url, ImageView iv, Bitmap bm,
							AjaxStatus status) {

						iv.setImageBitmap(bm);
						mAttacher.update();
					}

				});

	}

}
