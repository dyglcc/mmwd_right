package qfpay.wxshop.data.dao;

import java.sql.SQLException;

import qfpay.wxshop.image.ImageWrapper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class QFDataHelper extends OrmLiteSqliteOpenHelper {
	private static final String DATABASE_NAME    = "OrmliteDemo.db";
	public  static       int    DATABASE_VERSION = 2;

	public QFDataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override 
	public void onCreate(SQLiteDatabase arg0, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, ImageWrapper.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override 
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int arg2, int arg3) {
		try {
			TableUtils.dropTable(connectionSource, ImageWrapper.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override 
	public void close() {
		super.close();
	}
}
