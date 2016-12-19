package cn.edu.pku.guoziyang.weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.String;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.edu.pku.guoziyang.app.MyApplication;
import cn.edu.pku.guoziyang.bean.City;
import cn.edu.pku.guoziyang.bean.TodayWeather;
import cn.edu.pku.guoziyang.db.CityDB;

/**
 * 选择城市界面
 *
 * 功能：在城市列表中选择城市，更新该城市的天气信息，并在主界面显示该城市的天气
 */

public class SelectCityActivity extends Activity{

    private ImageView mBackBtn;
    private ListView mListView;
    private CityDB myCityDB;
    private MyApplication myapp;
    private List<City> myList;
    private TextView titleName;
    private EditText eSearch;
    private SimpleAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        eSearch=(EditText)findViewById(R.id.search_edit);

        Intent intent=this.getIntent();
        String cityName=intent.getStringExtra("cityName");

        //标题当前城市
        titleName=(TextView)findViewById(R.id.title_name);
        titleName.setText("当前城市："+cityName);


        //返回按钮
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                finish();
            }
        });

        //显示城市列表
        mListView = (ListView)findViewById(R.id.list_view);
        adapter=new SimpleAdapter(this, (List<? extends Map<String, ?>>) getdata(),R.layout.activity_listview_item,
                new String[]{"cityName","cityCode"},
                new int[] {R.id.cityName,R.id.cityCode});
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new ItemClickEvent());

        //监听搜索框文本
        eSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //搜索匹配
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    //继承OnItemClickListener，当子项目被点击的时候触发
    private final class ItemClickEvent implements AdapterView.OnItemClickListener {

        //将选择的城市编码，传给主界面
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            HashMap<String,Object> city = (HashMap<String,Object>)mListView.getItemAtPosition(position);
            String select_city_code=city.get("cityCode").toString();
            Log.d("选择的城市编码：",select_city_code);
            Intent i2 = new Intent();
            i2.putExtra("select_city_code",select_city_code);
            setResult(1,i2);

            finish();

        }
    }

    //列表显示的内容
    private List<Map<String, Object>> getdata() {
        myList= new ArrayList<City>();
        //获取全部城市列表（从数据库中）
        MyApplication myApplication = MyApplication.getInstance();
        myList = myApplication.getmCityList();

        String []cityName=new String[myList.size()];
        String []cityCode=new String[myList.size()];
        int i=0;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for(City city : myList) {
            Map<String, Object> map = new HashMap<String, Object>();
            cityName[i] = city.getCity();
            cityCode[i] = city.getNumber();
            map.put("cityName",cityName[i]);
            map.put("cityCode",cityCode[i]);
            list.add(map);
            i++;
        }
        return list;
    }



}
