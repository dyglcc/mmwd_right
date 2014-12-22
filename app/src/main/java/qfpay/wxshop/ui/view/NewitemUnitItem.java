package qfpay.wxshop.ui.view;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import qfpay.wxshop.R;
import qfpay.wxshop.ui.commodity.EdititemPresenter;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

@EViewGroup(R.layout.newitem_unititem)
public class NewitemUnitItem extends LinearLayout {
	public static final int STATES_ONLY_STOCK = 0;
	public static final int STATES_DEFAULT = 1;
	
	@ViewById EditText et_unit, et_quantity;
	@ViewById TextView tv_quantity;
	@ViewById LinearLayout ll_unit, ll_quantity;
	@ViewById View bottomLine, iv_delete;
	EdititemPresenter presenter;
	int states = STATES_DEFAULT;
	private int unitId = -1;

	public NewitemUnitItem(Context context) {
		super(context);
	}
	
	@AfterViews
	void init(){
		bottomLine.setVisibility(View.GONE);
	}
	
	/**
	 * 控制View的显示状态
	 * @param states view的状态, STATES_ONLY_STOCK, STATES_DEFAULT
	 * @param isAnmi 是否有动画动作
	 */
	public NewitemUnitItem setStates(int states, boolean isAnmi, EdititemPresenter presenter) {
		this.states = states;
		if (presenter != null) {
			this.presenter = presenter;
		}
		if (states == STATES_DEFAULT) {
			ll_unit.setVisibility(View.VISIBLE);
			ll_quantity.setVisibility(View.VISIBLE);
			processWeight(tv_quantity, 1, et_quantity, 1);
		} else {
			ll_unit.setVisibility(View.GONE);
			ll_quantity.setVisibility(View.VISIBLE);
			et_quantity.setText("");
			processWeight(tv_quantity, 1, et_quantity, 3);
		}
		return this;
	}
	
	void processWeight(TextView title, int titleWeight, EditText content, int contentWeight) {
		LinearLayout.LayoutParams titleParams = (LayoutParams) title.getLayoutParams();
		titleParams.weight = titleWeight;
		title.setLayoutParams(titleParams);
		LinearLayout.LayoutParams contentParams = (LayoutParams) content.getLayoutParams();
		contentParams.weight = contentWeight;
		content.setLayoutParams(contentParams);
	}
	
	/**
	 * 返回是否成功改变状态(如果状态重复返回false)
	 */
	public boolean changeStates(int states) {
		if (states == this.states) {
			return false;
		} else {
			setStates(states, true, null);
			return true;
		}
	}
	
	public boolean isOnlyStock() {
		return states == STATES_ONLY_STOCK;
	}
	
	@Click
	void iv_delete() {
		if (this.presenter != null) {
			this.presenter.deleteUnit(this);
		}
	}
	
	public void setDeleteVisibility(boolean isVisible) {
		if (isVisible) {
			iv_delete.setVisibility(View.VISIBLE);
		} else {
			iv_delete.setVisibility(View.GONE);
		}
	}
	
	public NewitemUnitItem setUnitName(String name) {
		et_unit.setText(name);
		return this;
	}
	
	public NewitemUnitItem setStockCount(String stockCount) {
		et_quantity.setText(stockCount);
		return this;
	}
	
	public String getUnitName() {
		return et_unit.getText().toString();
	}
	
	public Integer getStockCount() {
		if (et_quantity.getText().toString().equals("")) {
			return 0;
		}
		return Integer.parseInt(et_quantity.getText().toString(), 10);
	}
	
	public int getStates() {
		return states;
	}
	
	public void setBottomVisibility(int visibility) {
		bottomLine.setVisibility(visibility);
	}
	public int getUnitId() {
		return unitId;
	}
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
}
