package qfpay.wxshop.data.net;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 临时数据保存<br>
 * 库存更新和销售记录查询的数据会在本类中临时保存
 * 
 */
public class CacheData {

	private HashMap<String, ArrayList<HashMap<String, Object>>> data = null;
	@SuppressWarnings("rawtypes")
	private HashMap<String, ArrayList> listData = null;

	private static CacheData instance = new CacheData();

	private CacheData() {
	}

	public static CacheData getInstance() {
		return instance;
	}

	/**
	 * 加入一个临时数据
	 * 
	 * @param key
	 *            键
	 * @param list
	 *            数据
	 */
	public void setData(String key, ArrayList<HashMap<String, Object>> list) {
		if (data == null) {
			data = new HashMap<String, ArrayList<HashMap<String, Object>>>();

		}
		data.put(key, list);
	}

	/**
	 * 加入一个临时数据
	 * 
	 * @param <T>
	 * 
	 * @param key
	 *            键
	 * @param list
	 *            数据
	 */
	@SuppressWarnings("rawtypes")
	public void setListData(String key, ArrayList list) {
		if (listData == null) {
			listData = new HashMap<String, ArrayList>();

		}
		listData.put(key, list);
	}

	/**
	 * 取一个临时数据
	 * 
	 * @param key
	 *            要取的数据的键
	 * @return 要取的数据
	 */
	public ArrayList<HashMap<String, Object>> getData(String key) {
		if (data == null)
			return null;
		ArrayList<HashMap<String, Object>> value = data.get(key);
//		if (value != null) {
//			data.remove(key);
//		}
		return value;
	}

	/**
	 * 取一个临时数据
	 * 
	 * @param key
	 *            要取的数据的键
	 * @return 要取的数据
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList getListData(String key) {
		if (listData == null)
			return null;
		ArrayList value = listData.get(key);
		if (value != null) {
			listData.remove(key);
		}
		return value;
	}

	public void destroy() {
		data = null;
		listData = null;
	}
}
