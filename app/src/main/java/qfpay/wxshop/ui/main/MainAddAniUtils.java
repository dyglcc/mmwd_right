package qfpay.wxshop.ui.main;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;
import org.androidannotations.annotations.UiThread;

import qfpay.wxshop.R;
import qfpay.wxshop.data.net.ConstValue;
import android.widget.ImageView;

@EBean(scope = Scope.Singleton)
public class MainAddAniUtils {
	private static final int DURATION = 120;
	
	private Integer[] openArray = {R.drawable.ani_add_open_01, R.drawable.ani_add_open_02, R.drawable.ani_add_open_03, 
								R.drawable.ani_add_open_04, R.drawable.ani_add_open_05, R.drawable.ani_add_open_06};
	private Integer[] closeArray = {R.drawable.ani_add_close_01, R.drawable.ani_add_close_02, R.drawable.ani_add_close_03,
								R.drawable.ani_add_close_04};
	private Integer[] startArray = {R.drawable.ani_add_start_01, R.drawable.ani_add_start_02, R.drawable.ani_add_start_03,
								R.drawable.ani_add_start_04, R.drawable.ani_add_start_05, R.drawable.ani_add_start_06,
								R.drawable.ani_add_start_07, R.drawable.ani_add_start_08, R.drawable.ani_add_start_09,
								R.drawable.ani_add_start_10, R.drawable.ani_add_start_11, R.drawable.ani_add_start_12,
								R.drawable.ani_add_start_13, R.drawable.ani_add_start_14, R.drawable.ani_add_start_15,
								R.drawable.ani_add_start_16, R.drawable.ani_add_start_17, R.drawable.ani_add_start_18,
								R.drawable.ani_add_start_19, R.drawable.ani_add_start_20};
	private Integer[] stableArray = {R.drawable.ani_add_stable_01, R.drawable.ani_add_stable_02, R.drawable.ani_add_stable_03,
								R.drawable.ani_add_stable_04, R.drawable.ani_add_stable_05, R.drawable.ani_add_stable_06,
								R.drawable.ani_add_stable_07, R.drawable.ani_add_stable_08, R.drawable.ani_add_stable_09, 
								R.drawable.ani_add_stable_10, R.drawable.ani_add_stable_11, R.drawable.ani_add_stable_12};
	
	private enum AniState {
		OPEN, CLOSE, START, STABLE
	}
	
	private ImageView view;
	private BlockingQueue<Integer> resQueue = new LinkedBlockingQueue<Integer>();
	private AniState state;
	private boolean isRun = true;
	private boolean isStable = false;
	
	@AfterInject public void init() {
		run();
		isRun = true;
		isStable = true;
	}
	
	public void setImageView(ImageView view) {
		this.view = view;
	}
	
	public void open() {
		isStable = false;
		state = AniState.OPEN;
		resQueue.addAll(Arrays.asList(openArray));
	}
	
	public void close() {
		isStable = false;
		state = AniState.CLOSE;
		resQueue.removeAll(resQueue);
		resQueue.addAll(Arrays.asList(closeArray));
		start(false);
	}
	
	public void start(boolean isFromMain) {
		isStable = false;
		if (isFromMain) {
			state = AniState.START;
		}
		resQueue.addAll(Arrays.asList(startArray));
	}
	
	public void stop() {
		isRun = false;
		isStable = false;
	}
	
	@Background(id = ConstValue.THREAD_CANCELABLE)
	void run() {
		isRun = true;
		while (isRun) {
			try {
				Integer res = resQueue.poll();
				if (res == null) {
					if (state == AniState.CLOSE || state == AniState.START) {
						runStable();
					}
				} else {
					setRes(res);
				}
				Thread.sleep(DURATION);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void runStable() {
		isStable = true;
		int index = 0;
		while (isStable) {
			if (index == stableArray.length) {
				index = 0;
			}
			setRes(stableArray[index]);
			index ++;
			try { Thread.sleep(DURATION); } catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
	@UiThread void setRes(int res) {
		try {
			view.setImageResource(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
