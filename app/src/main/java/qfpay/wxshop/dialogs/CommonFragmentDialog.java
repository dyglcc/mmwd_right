package qfpay.wxshop.dialogs;

import qfpay.wxshop.utils.MobAgentTools;
public class CommonFragmentDialog extends SimpleDialogFragment {
	ISimpleDialogListener dialogListener;
	ISimpleDialogCancelListener cancelListener;
	
	public CommonFragmentDialog setCancelListener(ISimpleDialogCancelListener cancelListener) {
		this.cancelListener = cancelListener;
		return this;
	}
	
	@Override
	protected ISimpleDialogCancelListener getCancelListener() {
		return cancelListener;
	}
	
	public CommonFragmentDialog setDialogListener(ISimpleDialogListener linstener) {
		this.dialogListener = linstener;
		return this;
	}
	
	@Override
	protected ISimpleDialogListener getDialogListener() {
		return dialogListener;
	}
}
