package cn.edu.pku.guoziyang.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.guoziyang.bean.City;

/**
 * cityDB操作类
 *
 * 功能：从数据库中取出城市列表
 *
 * 数据表格式如下：
 *
 */

public class CityDB {

    public static final String CITY_DB_NAME = "city.db";
    public static final String CITY_TABLE_NAME = "city";

    private SQLiteDatabase db;

    public CityDB(Context context, String path) {
        db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE,null);
    }

    //取出结果放入List中，元素类型为City
    public List<City> getAllCity(){
        List<City> list = new ArrayList<City>();
        Cursor c = db.rawQuery("SELECT * from " +  CITY_TABLE_NAME, null);
        while(c.moveToNext()){
            String province = c.getString(c.getColumnIndex("province"));
            String city = c.getString(c.getColumnIndex("city"));
            String number = c.getString(c.getColumnIndex("number"));
            String allPY = c.getString(c.getColumnIndex("allpy"));
            String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY = c.getString(c.getColumnIndex("firstpy"));
            City item = new City(province,city,number,firstPY,allPY,allFirstPY);
            list.add(item);

        }
        return list;

    }
}
