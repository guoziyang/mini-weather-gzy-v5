package cn.edu.pku.guoziyang.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.guoziyang.bean.City;
import cn.edu.pku.guoziyang.db.CityDB;

/**
 * 从数据库中读取城市列表,并将其存入MyApplication中
 * 使用时，可在其他类直接调用
 */

public class MyApplication extends Application {
    private static final String TAG = "MyAPP";
    private static MyApplication myApplication;

    //结果存到这里，取到的最后结果为List<City>类型
    private CityDB mCityDB;

    //初始化城市信息列表
    private List<City> mCityList;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"MyApplication->Oncreate");


        myApplication = this;
        mCityDB = openCityDB();
        //初始化城市信息列表
        initCityList();

    }

    /**
     * 初始化城市信息列表
     */
    private void initCityList(){
        mCityList = new ArrayList<City>();
        //新建子线程用于？？？
        new Thread(new Runnable(){
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    //获取城市数据
    private boolean prepareCityList(){
        mCityList = mCityDB.getAllCity();
        int i = 0;
        for(City city : mCityList){
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d(TAG,cityCode+":"+cityName);
        }
        Log.d(TAG,"i="+i);
        return true;
    }

    public List<City> getmCityList(){
        return mCityList;
    }

    public static MyApplication getInstance(){
        return myApplication;
    }

    private CityDB openCityDB(){
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "database1"
                + File.separator
                + CityDB.CITY_TABLE_NAME;
        Log.d(TAG,"dbpath---->" + path);
        File db = new File(path);
        Log.d(TAG,path);
        if(!db.exists()){
            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "database1"
                    + File.separator;
            File dirFirstFolder = new File(pathfolder);
            if(!dirFirstFolder.exists()){
                dirFirstFolder.mkdir();
                Log.i("MyApp","mkdirs");
            }
            Log.i("MyApp","db is not exists");

            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1){
                    fos.write(buffer,0,len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);

            }
        }
        return new CityDB(this,path);
    }
}
