package qfpay.wxshop.takepicUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import qfpay.wxshop.R;
import qfpay.wxshop.WxShopApplication;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.ui.selectpic.AlbumActivity;
import qfpay.wxshop.utils.MobAgentTools;
import qfpay.wxshop.utils.QMMAlert;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Toaster;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

public class TakePicUtils {
	private Context context;
	private static TakePicUtils instance = null;

	private static final int MMAlertSelect1 = 0;
	private static final int MMAlertSelect2 = 1;
	public static final int TAKE_PIC_MODE_CROP = 1;
	public static final int TAKE_PIC_MODE_ONLY = 2;
	public static final int TAKE_PIC_MODE_ONLY_SELECT_MUL_PICS = 3;
	public static final int TAKE_PICTURE = 10;
	public static final int SELECT_PIC = 1;
	public static final int CROP_PICTURE_DONE = TAKE_PICTURE + 1;
	private String selectedPic;

	private int currentTakePicType = TAKE_PIC_MODE_ONLY;

	private String tempFileName;

	private int currentMode;

	public static final int TAKE_PIC_REQUEST_CODE = 90;
	public static final int SELECT_PIC_REQUEST_CODE = TAKE_PIC_REQUEST_CODE + 1;
	private static final int IMG_QUILTY = 40;

	private TakePicUtils() {

	}

	public static TakePicUtils getInstance() {
		if (instance == null) {
			instance = new TakePicUtils();
		}
		return instance;
	}
	
//	private SherlockFragment fragment;

	public void init(Context context) {
		this.context = context;
//		this.fragment = shopFragment;
		if (!ConstValue.haveSdcard()) {
			Toast.makeText(context,
					context.getResources().getString(R.string.no_found_SD_2),
					Toast.LENGTH_LONG).show();
			return;
		}
		File temFiles = new File(ConstValue.getPICTURE_DIR());
		boolean succ = false;
		if (!temFiles.exists()) {
			succ = temFiles.mkdirs();
			if (!succ) {
				Toast.makeText(
						context,
						context.getResources().getString(R.string.fail_new_tmp),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * 封装拍照程序
	 * */
	public void takePic(final int type) {

		String[] items = context.getResources().getStringArray(
				R.array.reg_id_popmenu);
		// 是否需要crop
		currentTakePicType = type;
		QMMAlert.showAlert(context,
				context.getResources().getString(R.string.plaeas_select),
				items, null, new QMMAlert.OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						if (!ConstValue.haveSdcard()) {
							Toast.makeText(
									context,
									context.getResources().getString(
											R.string.no_found_SD),
									Toast.LENGTH_LONG).show();
							return;
						}
						switch (whichButton) {

						case MMAlertSelect1:
							// 点击拍照
							MobAgentTools.OnEventMobOnDiffUser(context, "click_camera");
							paizhao();
							currentMode = TAKE_PICTURE;
							break;
						case MMAlertSelect2:
							MobAgentTools.OnEventMobOnDiffUser(context, "click_roll");
							if (currentTakePicType == TAKE_PIC_MODE_ONLY_SELECT_MUL_PICS) {
//								if(fragment!=null){
//								fragment
//									.startActivityForResult(new Intent(
//											context, AlbumActivity.class),
//											SELECT_PIC_REQUEST_CODE);
//								}else{
									((Activity) context)
									.startActivityForResult(new Intent(
											context, AlbumActivity.class),
											SELECT_PIC_REQUEST_CODE);
//								}
								
								break;
							}
							// 点击从照片库选择
							currentMode = SELECT_PIC;
							getMediaDatabase();
							break;
						default:
							break;
						}
					}

				});
	}

	private void paizhao() {

		tempFileName = ConstValue.getPICTURE_DIR() + "carema"
				+ System.currentTimeMillis() + ".jpg";
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(tempFileName)));
//		if(fragment!=null){
//			fragment.startActivityForResult(intent,
//					TAKE_PIC_REQUEST_CODE);
//		}else{
			((Activity) context).startActivityForResult(intent,
					TAKE_PIC_REQUEST_CODE);
//		}
		

	}

	public String getTempFileName() {
		return tempFileName;
	}

	public void getMediaDatabase() {
		// Intent intent = new Intent();
		// /* 开启Pictures画面Type设定为image */
		// intent.setType("image/*");
		// /* 使用Intent.ACTION_GET_CONTENT这个Action */
		// intent.setAction(Intent.ACTION_GET_CONTENT);
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		/* 取得相片后返回本画面 */
//		if(fragment!=null){
//			fragment.startActivityForResult(i, TAKE_PIC_REQUEST_CODE);
//		}else{
			((Activity) context).startActivityForResult(i, TAKE_PIC_REQUEST_CODE);
//		}
	}

	public PictureBean receivePics(int requestCode, int resultCode, Intent data) {
		PictureBean pb = new PictureBean();
		Uri uri = null;
		System.gc();
		String fileName = System.currentTimeMillis() +"";
		selectedPic = ConstValue.getPICTURE_DIR() + fileName;
		if (resultCode == -1) {// result ok
			switch (currentMode) {
			case SELECT_PIC:
				// 剪切图片 保存图片到本地
				if (data == null) {
					Toaster.s(
							context,
							context.getResources().getString(
									R.string.open_file_wrong));
				}
				uri = data.getData();

				String sourcePath1 = getFilePath(uri);
				if (sourcePath1.equals("")) {
					return null;
				}
				if (currentTakePicType == TAKE_PIC_MODE_CROP) {
					cropPicture(uri, WxShopApplication.crop_w,
							WxShopApplication.crop_h,
							WxShopApplication.aspectX,
							WxShopApplication.aspectY);
				} else if (currentTakePicType == TAKE_PIC_MODE_ONLY) {
					dealPic(selectedPic, sourcePath1, context);
				}
				break;
			case TAKE_PICTURE:
				// 剪切图片之后保存

				if (currentTakePicType == TAKE_PIC_MODE_CROP) {

					cropPicture(Uri.fromFile(new File(tempFileName)),
							WxShopApplication.crop_w, WxShopApplication.crop_h,
							WxShopApplication.aspectX,
							WxShopApplication.aspectY);

				} else {

					// deal(data);
					// 处理照片
					dealPic(selectedPic, tempFileName, context);

				}

				break;

			case CROP_PICTURE_DONE:

				if (data != null) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						Bitmap photo = extras.getParcelable("data");

						saveSmallPic2SdCard(photo, selectedPic, context,
								IMG_QUILTY, null);
						photo = null;

					}
				}
				System.gc();
				break;
			}

		}
		pb.setFileStr(selectedPic);
		return pb;
	}

	private void cropPicture(Uri uri, int wPx, int hPx, int aspectX, int aspectY) {
		currentMode = CROP_PICTURE_DONE;
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", aspectX);
		intent.putExtra("aspectY", aspectY);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", wPx);
		intent.putExtra("outputY", hPx);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
//		if(fragment!=null){
//			fragment.startActivityForResult(intent, CROP_PICTURE_DONE);
//		}else{
			((Activity) context).startActivityForResult(intent, CROP_PICTURE_DONE);
//		}
	}

	// private boolean noPress = false;
	Bitmap bitmap;

	// private void deal(Intent data) {
	// if (tempFileName == null) {
	// T.i("拍照失败，返回数据是空！");
	// return;
	// }
	//
	// }

	private String getFilePath(Uri uri) {
		Cursor cursor = null;
		String img_path = "";
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = ((Activity) context).managedQuery(uri, proj, null, null,
					null);
			if (cursor == null || !cursor.moveToFirst()) {
				Toaster.s(
						context,
						context.getResources().getString(
								R.string.filepathException));
				return img_path;
			}
			int actual_image_column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			img_path = cursor.getString(actual_image_column_index);
			if (img_path == null || img_path.equals("")) {
				Toaster.s(
						context,
						context.getResources().getString(
								R.string.filepathException));
				return "";
			}
		} catch (Exception e) {
			T.e(e);
		} finally {
			// if(cursor!=null)
			// cursor.close();
		}
		return img_path;
	}

	public static void dealPic(String destationPath, String srcPath,
			Context context) {

		// 缩放
		Bitmap bitmapSource = dealBitmap2Small(srcPath);
		if (bitmapSource != null) {
			// 旋转图片
			Bitmap afterDealBitmap = zoomDrawable(srcPath, bitmapSource);
			saveSmallPic2SdCard(afterDealBitmap, destationPath, context,
					IMG_QUILTY, bitmapSource);
			// 释放图片资源
			bitmapSource = null;
			afterDealBitmap = null;
			System.gc();
		} else {
			T.i(context.getResources().getString(R.string.pic_no_exist));
		}

	}

	/**
	 * 保存处理过的小图到指定路径
	 * 
	 * @param bitmapSource
	 * */
	static void saveSmallPic2SdCard(Bitmap bitmap, String path,
			Context context, int quility, Bitmap bitmapSource) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e1) {
			T.e(e1);
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, quility, out)) {
				out.flush();
				out.close();
			}
			T.i(context.getResources().getString(R.string.file_size)
					+ file.length() / 1024);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bitmap != null) {
			bitmap.recycle();
		}
		if (bitmapSource != null) {
			bitmapSource.recycle();
		}
	}

	/**
	 * 旋转并返回新的pic,回收 bitmapsource
	 * */
	public static Bitmap zoomDrawable(String path, Bitmap srcBitmap) {

		int angle = readPictureDegree(path);
		int width = srcBitmap.getWidth();
		int height = srcBitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		Bitmap newbmp = Bitmap.createBitmap(srcBitmap, 0, 0, width, height,
				matrix, true);
		System.gc();
		return newbmp;
	}

	public static Bitmap dealBitmap2Small(String tempfilenameString) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(tempfilenameString, options);

		options.inJustDecodeBounds = false;

		// if (be1 <= 0)
		// be1 = 1;
		options.inSampleSize = calculateInSampleSize(options, 480, 800);

		// 重新读入bitmap
		Bitmap bitmap1 = BitmapFactory.decodeFile(tempfilenameString, options);
		return bitmap1;
	}

	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	/**
	 * 得到图片角度
	 * */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			T.e(e);
		}
		return degree;
	}
}
