package qfpay.wxshop.utils;

import qfpay.wxshop.utils.MobAgentTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import qfpay.wxshop.data.beans.BankBean;
import qfpay.wxshop.data.beans.CityBean;
import qfpay.wxshop.data.beans.ProvinceBean;
import android.content.Context;

public class RegisterJsonTool {

	private Context context;
	private static RegisterJsonTool instance = null;

	public void init(Context contex) {
		this.context = contex;
	}

	private RegisterJsonTool() {
	}

	public static RegisterJsonTool getInstance() {
		if (instance == null) {
			instance = new RegisterJsonTool();
		}
		return instance;
	}

	public String[] parseBankBeans() {

		String banksStrs = getJsonStringByFileName("bank.txt","utf-16");
		String[] banks = banksStrs.split(";");
		return banks;

	}

	public ArrayList<String> getStrBank(ArrayList<BankBean> bankBeanList) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < bankBeanList.size(); i++) {
			list.add(bankBeanList.get(i).getbName());
			T.i(list.get(i));
		}
		return list;
	}

	public void destory() {
		if (instance != null) {
			instance = null;
		}
	}
	
	/**
	 * 得到城市的strs
	 * */
	public ArrayList<String> getStrCity(ArrayList<CityBean> cityBeanList) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < cityBeanList.size(); i++) {
			list.add(cityBeanList.get(i).getcName());
		}
		return list;
	}
	public String getJsonStringByFileName(String fileName,String charset) {
		String jsonStr = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					context.getAssets().open(fileName),charset));
			jsonStr = reader.readLine();
			reader.close();
		} catch (IOException e) {
			T.e(e);
		}
		return jsonStr;
	}

	public String getStringMulLine(String fileName) {
		StringBuilder builder = new StringBuilder();
		String str = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
			while ((str = reader.readLine()) != null) {
				builder.append(str + "\n");
			}
			reader.close();
		} catch (IOException e) {
			T.e(e);
		}
		return builder.toString();
	}
	
	private String getJsonStringByFileName(String fileName) {
		String jsonStr = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					context.getAssets().open(fileName)));
			jsonStr = reader.readLine();
			reader.close();
		} catch (IOException e) {
			T.e(e);
		}
		return jsonStr;
	}

	public ArrayList<ProvinceBean> parseProvinceBeans() {
		ArrayList<ProvinceBean> list = new ArrayList<ProvinceBean>();
		String provinceStringjson = getJsonStringByFileName("province.json");
		try {
			JSONArray arrays = new JSONArray(provinceStringjson);
			for (int i = 0; i < arrays.length(); i++) {

				JSONObject obj = arrays.getJSONObject(i);

				ProvinceBean province = new ProvinceBean();
				String name = obj.getString("area_name");
				String number = obj.getString("area_no");
				province.setId(number);
				province.setpName(name);
				list.add(province);
			}
		} catch (JSONException e) {
			T.e(e);
		}
		return list;
	}

	
	/**
	 * 得到省市的strs
	 * 
	 * */
	public ArrayList<String> getStrProvince(
			ArrayList<ProvinceBean> provinceBeanList) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < provinceBeanList.size(); i++) {
			list.add(provinceBeanList.get(i).getpName());
		}
		return list;
	}

	public ArrayList<CityBean> parseCityBeans() {

		ArrayList<CityBean> list = new ArrayList<CityBean>();
		String cityStringjson = getJsonStringByFileName("city.json");
		try {
			JSONArray arrays = new JSONArray(cityStringjson);
			for (int i = 0; i < arrays.length(); i++) {

				JSONObject obj = arrays.getJSONObject(i);

				CityBean city = new CityBean();
				String name = obj.getString("city_name");
				String number = obj.getString("city_no");
				city.setcNo(number);
				city.setcName(name);
				list.add(city);
			}
		} catch (JSONException e) {
			T.e(e);
		}
		return list;
	
	}

	public ArrayList<CityBean> getCityBeanByProvinceId(String no, ArrayList<CityBean> mCitys) {
		ArrayList<CityBean> list = new ArrayList<CityBean>();
		for(int i=0;i<mCitys.size();i++){
			CityBean cb = mCitys.get(i);
			if(cb.getcNo().startsWith(no)){
				list.add(cb);
			}
		}
		return list;
	}

}
