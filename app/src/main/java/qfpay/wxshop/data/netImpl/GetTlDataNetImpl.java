package qfpay.wxshop.data.netImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import qfpay.wxshop.config.WDConfig;
import qfpay.wxshop.data.beans.GoodMSBean;
import qfpay.wxshop.data.beans.GoodsBean;
import qfpay.wxshop.data.net.AbstractNet;
import qfpay.wxshop.data.net.CacheData;
import qfpay.wxshop.data.net.ConstValue;
import qfpay.wxshop.utils.T;
import qfpay.wxshop.utils.Utils;
import android.content.Context;
import android.os.Bundle;

public class GetTlDataNetImpl extends AbstractNet {

	public GetTlDataNetImpl(Context act) {
		super(act);
		setNoNeedShowDialog();
	}

	@Override
	protected Map<String, Object> setRequestParameter(Bundle parameter2) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
//			// start 记录的id
//			if (parameter2.containsKey("start")) {
//				map.put("start", parameter2.getString("start"));
//			}
			if (parameter2.containsKey("page")) {
				map.put("page", parameter2.getString("page"));
			}
			if (parameter2.containsKey("fixnum")) {
				map.put("fixnum", parameter2.getString("fixnum"));
			}
			map.put("length", ConstValue.PAGE_SIZE_MANAGE);
			map.put("good_status", "0");
			map.put("format", "app");

			map.put(ConstValue.REQUEST_URL, WDConfig.getInstance()
					.getManageDatalist(ConstValue.HTTP_GET));
			map.put(ConstValue.HTTP_METHOD, ConstValue.HTTP_GET);
			
			/** 参数正确 */
			map.put(ConstValue.NET_RETURN, ConstValue.NET_RETURN_SUCCESS);

		} catch (Exception e) {
			T.e(e);
			/** 请求参数错误 */
			map.put(ConstValue.NET_RETURN, ConstValue.NET_RETURN_PARAMETER);
		}
		return map;
	}

	@Override
	protected Bundle jsonParse(String jsonStr) {
		ArrayList<HashMap<String, Object>> list = null;
		HashMap<String, Object> map = null;
		ArrayList<GoodsBean> tempList = new ArrayList<GoodsBean>();
		GoodsBean bean = null;
		if (jsonStr != null && jsonStr.length() > 0) {
			try {
				// json数组构建
				JSONObject root = new JSONObject(jsonStr);
				String resultState = root.getString("respcd");
				if (!resultState.equals("0000")) {
                    String errorMsg = root.getString("resperr");
                    T.i("error mess :" + errorMsg);
                    bundle.putString(ConstValue.ERROR_MSG,
                            errorMsg);
				}else{
                    JSONObject obj = root.getJSONObject("data");
                    JSONArray jsonData = obj.getJSONArray("items");
                    list = new ArrayList<HashMap<String, Object>>();
                    map = new HashMap<String, Object>();
                    for (int i = 0; i < jsonData.length(); i++) {
                        bean = new GoodsBean();
                        String qf_uid = null;
                        String update_time = null;
                        String good_prize = null;
                        String sales = null;
                        String postage = null;
                        String good_amount = null;
                        String create_time = null;

                        String good_name = null;
                        String good_img = null;
                        String good_state = null;
                        String good_desc = null;
                        String id = null;

                        JSONObject recordObject = jsonData.getJSONObject(i);
                        if (!recordObject.isNull("qf_uid")
                                && recordObject.has("qf_uid")) {

                            qf_uid = recordObject.getString("qf_uid");
                        }
                        if (!recordObject.isNull("update_time")
                                && recordObject.has("update_time")) {
                            update_time = recordObject.getString("update_time");
                        }
                        if (!recordObject.isNull("good_prize")
                                && recordObject.has("good_prize")) {
                            good_prize = recordObject.getString("good_prize");
                        }
                        if (!recordObject.isNull("sales")
                                && recordObject.has("sales")) {
                            sales = recordObject.getString("sales");
                        }
                        if (!recordObject.isNull("postage")
                                && recordObject.has("postage")) {
                            postage = recordObject.getString("postage");
                        }
                        if (!recordObject.isNull("good_amount")
                                && recordObject.has("good_amount")) {
                            good_amount = recordObject.getString("good_amount");
                        }
                        if (!recordObject.isNull("create_time")
                                && recordObject.has("create_time")) {
                            create_time = recordObject.getString("create_time");
                        }
                        if (!recordObject.isNull("good_name")
                                && recordObject.has("good_name")) {
                            good_name = recordObject.getString("good_name");
                        }
                        if (!recordObject.isNull("good_img")
                                && recordObject.has("good_img")) {
                            good_img = recordObject.getString("good_img");
                        }
                        if (!recordObject.isNull("good_state")
                                && recordObject.has("good_state")) {
                            good_state = recordObject.getString("good_state");
                        }
                        if (!recordObject.isNull("good_desc")
                                && recordObject.has("good_desc")) {
                            good_desc = recordObject.getString("good_desc");
                        }
                        if (!recordObject.isNull("id") && recordObject.has("id")) {
                            id = recordObject.getString("id");
                        }
                        if (!recordObject.isNull("weight") && recordObject.has("weight")) {
                            bean.setWeight(recordObject.getInt("weight"));
                        }

                        bean.setQfUid(qf_uid);
                        bean.setUpdateTime(update_time);
                        bean.setPriceStr(good_prize);
                        bean.setSaled(sales);
                        bean.setPostage(postage);
                        bean.setStock(good_amount);
                        bean.setCreateDateStr(create_time);
                        bean.setGoodName(good_name);
                        bean.setSrcimgUrl(good_img);
                        bean.setImageUrl(Utils.getThumblePic(good_img, 120));
                        bean.setGoodstate(good_state);
                        bean.setGoodDesc(good_desc);
                        bean.setGoodsId(id);
                        // 记录最后一个id
                        if(i == jsonData.length()-1){
                            bundle.putString("lastid", id);
                        }

                        if (!recordObject.isNull("goodpanic") && recordObject.has("goodpanic")) {
                            JSONObject panic = recordObject.getJSONObject("goodpanic");
                            String newprice = null;
                            String goodid = null;
                            String status_flag = null;
                            String starttime = null;
                            String endtime = null;
                            String createtime = null;

                            if (!panic.isNull("newprice") && panic.has("newprice")) {
                                newprice = panic.getString("newprice");
                            }
                            if (!panic.isNull("goodid") && panic.has("goodid")) {
                                goodid = panic.getString("goodid");
                            }
                            if (!panic.isNull("status_flag")
                                    && panic.has("status_flag")) {
                                status_flag = panic.getString("status_flag");
                            }
                            if (!panic.isNull("starttime")
                                    && panic.has("starttime")) {
                                starttime = panic.getString("starttime");
                            }
                            if (!panic.isNull("endtime") && panic.has("endtime")) {
                                endtime = panic.getString("endtime");
                            }
                            if (!panic.isNull("createtime")
                                    && panic.has("createtime")) {
                                createtime = panic.getString("createtime");
                            }
                            GoodMSBean gms = new GoodMSBean();
                            gms.setCreatetime(createtime);
                            gms.setEndtime(endtime);
                            gms.setId(goodid);
                            gms.setNewprice(newprice);
                            gms.setStarttime(starttime);
                            gms.setStatus_flag(status_flag);
                            bean.setMsBean(gms);
                        }
                        if (bean.getWeight() > 0) {
                            tempList.add(0, bean);
                        } else {
                            tempList.add(bean);
                        }
                    }

                    map.put("orderList", tempList);
                    list.add(map);

                    Long key = System.currentTimeMillis();
                    CacheData.getInstance().setData(key + "", list);
                    /** 界面上展示的时候直接根据key取存储类的数据 */
                    bundle.putString(ConstValue.CACHE_KEY, key + "");
                }

				bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_SUCCESS);
			} catch (Exception e) {
				T.e(e);
				bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_FAILED);
			}
		} else {
			bundle.putInt(ConstValue.JSON_RETURN, ConstValue.JSON_FAILED);
			T.i("jsonStr is null or jsonStr.length is 0");
		}
		return bundle;
	}
}
