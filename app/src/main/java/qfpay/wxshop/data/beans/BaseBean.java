package qfpay.wxshop.data.beans;

import qfpay.wxshop.utils.MobAgentTools;
import java.io.Serializable;
import java.util.Map;

public class BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 后续要做的功能, 从Bean中获取params, 包装成post请求需要的参数列表
	 */
	private Map<String, Object> getParams() {
		
		return null;
	}
}
