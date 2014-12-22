package qfpay.wxshop.data.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qfpay.wxshop.image.ImageWrapper;

import com.j256.ormlite.dao.Dao;

public class ImageUploaderDaoImpl {
	private Dao<ImageWrapper, Integer> mDao;
	
	public ImageUploaderDaoImpl(Dao<ImageWrapper, Integer> mDao) {
		super();
		this.mDao = mDao;
	}

	public String findImageUrl(String key) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("key", key);
			List<ImageWrapper> list = mDao.queryForFieldValues(map);
			if (list != null && ! list.isEmpty()) {
				return list.get(0).getNetUrl();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public boolean addImageData(ImageWrapper bean) {
		try {
			mDao.createOrUpdate(bean);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
