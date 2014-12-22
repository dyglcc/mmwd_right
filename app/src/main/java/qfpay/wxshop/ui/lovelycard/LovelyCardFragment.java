package qfpay.wxshop.ui.lovelycard;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import qfpay.wxshop.R;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.share.wexinShare.UtilsWeixinShare;
import qfpay.wxshop.share.wexinShare.WeiXinDataBean;
import qfpay.wxshop.ui.main.AppStateSharePreferences_;
import qfpay.wxshop.ui.main.fragment.BaseFragment;
import qfpay.wxshop.ui.main.fragment.PopularizingFragment;
import qfpay.wxshop.ui.view.BadgeView;
import qfpay.wxshop.utils.*;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@EFragment(R.layout.lovelycard_main)
public class LovelyCardFragment extends BaseFragment {
	@ViewById TextView tv_comment;
	@ViewById LinearLayout ll_comment, ll_share;
	@ViewById RelativeLayout rl_tips;
	@Pref AppStateSharePreferences_ pref;
	@Pref LovelyCardPref_ mLCPref;

	BadgeView mBadge;
	String[] shareItems = new String[]{"分享到微信好友", "分享到朋友圈"};

	@AfterViews
	void onInit() {
		processBadge(pref.isLCNew().get());
	}

	@Override
	public void onFragmentRefresh() {
		if (mLCPref == null || ll_share == null || rl_tips == null) return;
		if (mLCPref.name().get().equals("")) {
			ll_share.setVisibility(View.GONE);
			rl_tips.setVisibility(View.VISIBLE);
		} else {
			ll_share.setVisibility(View.VISIBLE);
			rl_tips.setVisibility(View.GONE);
		}
	}

	@Click
	void btn_tips() {
		rl_tips.setVisibility(View.GONE);
	}

	@Click
	void ll_edit() {
		QFCommonUtils.collect("namecard_edit", getActivity());
		MobAgentTools.OnEventMobOnDiffUser(getActivity(), "namecard_edit");
		LovelyCardEditActivity_.intent(this).start();
	}

	@Click
	public void ll_comment_outside() {
		QFCommonUtils.collect("namecard_comment", getActivity());
		MobAgentTools.OnEventMobOnDiffUser(getActivity(), "namecard_comment");
		LovelyCardCommentActivity_.intent(this).start();

		pref.isLCNew().put(false);
		((PopularizingFragment) mParentFragmentRef.get()).setLCBadger(false);
		processBadge(false);
	}

	@Click
	void ll_share() {
		if (mLCPref.imgUrl().get() == null || mLCPref.imgUrl().get().equals("")) {
			Toaster.s(getActivity(), "您没有上传背景图,请上传一张再分享吧~");
			return;
		}

		QFCommonUtils.collect("namecard_share", getActivity());
		MobAgentTools.OnEventMobOnDiffUser(getActivity(), "namecard_share");

		QMMAlert.showAlert(getActivity(), getString(R.string.share2), shareItems, null, new QMMAlert.OnAlertSelectId() {
			@Override
			public void onClick(int whichButton) {
				switch (whichButton) {
					case 0:
						shareToFriend();
						break;
					case 1:
						shareToMoment();
						break;
				}
			}
		});
	}

	private void shareToFriend() {
		WeiXinDataBean wdb = new WeiXinDataBean();
		wdb.title = String.format(LovelyCardEditActivity.SHARE_TITLE, mLCPref.name().get());
		wdb.description = LovelyCardEditActivity.SHARE_CONTENT;
		wdb.url = LovelyCardEditActivity.getLovelyCardUrl();
		wdb.imgUrl = Utils.getThumblePic(mLCPref.imgUrl().get(), 120);
		wdb.scope = ConstValue.friend_share;
		UtilsWeixinShare.shareWeb(wdb, ConstValue.android_mmwdapp_home_wcfriend, getActivity());
	}

	private void shareToMoment() {
		WeiXinDataBean wdb = new WeiXinDataBean();
		wdb.title = String.format(LovelyCardEditActivity.SHARE_TITLE, mLCPref.name().get());
		wdb.description = LovelyCardEditActivity.SHARE_CONTENT;
		wdb.url = LovelyCardEditActivity.getLovelyCardUrl();
		wdb.imgUrl = Utils.getThumblePic(mLCPref.imgUrl().get(), 120);
		wdb.scope = ConstValue.circle_share;
		UtilsWeixinShare.shareWeb(wdb, ConstValue.android_mmwdapp_home_wctimeline, getActivity());
	}

	private void processBadge(boolean isShow) {
		if (isShow) {
			mBadge = new BadgeView(getActivity(), ll_comment);
			mBadge.setText("new");
			mBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
			mBadge.show();
		} else {
			if (mBadge != null) {
				mBadge.hide();
			}
		}
	}
}
