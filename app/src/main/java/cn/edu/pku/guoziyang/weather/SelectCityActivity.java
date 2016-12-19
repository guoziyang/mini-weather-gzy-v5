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
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Iterator;
import java.util.List;

import cn.edu.pku.guoziyang.app.MyApplication;
import cn.edu.pku.guoziyang.bean.City;
import cn.edu.pku.guoziyang.bean.TodayWeather;

/**
 * 选择城市界面
 *
 * 功能：在城市列表中选择城市，更新该城市的天气信息，并在主界面显示该城市的天气
 */

public class SelectCityActivity extends Activity {

    //Log信息标签
    private String TAG = "myWeather";

    //城市列表数据
    public static List city_list;
    //listview的监听器
    ArrayAdapter<String> adapter;


    //显示全部城市列表为false,显示匹配的城市列表ture
    boolean isMatchMode = false;

    //定义TodayWeather对象
    TodayWeather todayWeather = null;

    //界面返回按钮
    private ImageView mBackBtn;
    //搜索框
    public EditText search_edit;

    //界面标题
    private TextView selectCityName;

    City city;

    //ListView控件，存放全部程序列表
    private List<String> data = new ArrayList<String>();
    //用户存放城市名称和对应城市编码（用于天气更新）
    //其中key为城市编码，value为城市名称
    private HashMap hm = new HashMap();
    //ListView控件，存放根据搜索匹配的城市列表
    private List<String> dataMatch = new ArrayList<String>();
    private HashMap hmMatch = new HashMap();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //与布局文件关联
        setContentView(R.layout.activity_select_city);


        //获取全部城市列表（从数据库中）
        MyApplication myApplication = MyApplication.getInstance();
        city_list = myApplication.getmCityList();

        //返回今日天气信息界面按钮
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
//                i.putExtra("select_city_code", "101160101");
//                setResult(1,i);
                finish();
            }
        });


        //标题栏，当前城市
        selectCityName = (TextView) findViewById(R.id.title_name);

        //对搜索框的输入文本监听
        TextWatcher searchTextWatcher = new TextWatcher() {
            private CharSequence temp;
            private int editStart ;
            private int editEnd ;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG,"onTextChanged:"+charSequence) ;
                //清空listView(清空data数组)
                //根据输入，显示匹配的结果
                Log.d("match","监听的输入字符串：" + charSequence.toString());
                dataMatch.clear();
                matchCity(charSequence.toString());
                isMatchMode = true;



            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };

        //搜索框
        search_edit = (EditText)findViewById(R.id.search_edit);
        search_edit.addTextChangedListener(searchTextWatcher);


        //ListView控件
        ListView mlistView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(
                SelectCityActivity.this, android.R.layout.simple_list_item_1, data//data数据数据将在listview列表中显示
        );
        mlistView.setAdapter(adapter);





        //遍历城市列表，并将其添加到data数据（在listview中显示）
        Iterator it = city_list.iterator();
        while (it.hasNext()) {
            city = (City) it.next();
            //其中key为城市名称,value为城市编码
            hm.put(city.getCity(),city.getNumber());
            //将城市名称，添加到数组（用于在listView中显示）
            data.add(city.getCity());

        }


        //为ListView表项添加单击事件
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //列表项为城市名称，单击城市名称，返回城市名称，并用取出对应城市编号，去网络更新该城市天气信息
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String s_city_name = data.get(i);

                //显示被匹配的城市列表时
                if(isMatchMode){
                    s_city_name = dataMatch.get(i);
                }

                //单击内容测试 - 可删除
                Toast.makeText(SelectCityActivity.this, "选中城市：" + s_city_name, Toast.LENGTH_SHORT).show();
                //设置标题为当前城市
                selectCityName.setText("当前选择城市：" + s_city_name);

                //通过城市名称取出城市编码
                //取出的城市编码为Object类型，需要转换
                String select_city_code = hm.get(s_city_name).toString();

                //测试选中的城市的名称和编码--可删除
                Log.d(TAG,"选中的城市名称和编码：" + s_city_name + " "+ select_city_code);


                //选中城市后，将城市编码回传给TodayWeatherActivity,会用finish结束当前活动生命周期
                Intent i2 = new Intent();
                i2.putExtra("select_city_code",select_city_code);
                setResult(1,i2);

                finish();

            }
        });


    }


    //搜索包含inputStr记录，并将匹配结果显示到listview
    public void matchCity(String inputStr){

        boolean isMatch;


        //ListView控件
        ListView mlistView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(
                SelectCityActivity.this, android.R.layout.simple_list_item_1, dataMatch//data数据数据将在listview列表中显示
        );
        mlistView.setAdapter(adapter);

        //遍历城市列表，判断记录中是否含inputStr,并将符合的记录添加到data数据（在listview中显示）
        Iterator it = city_list.iterator();
        while (it.hasNext()) {
            city = (City) it.next();
            isMatch = city.getCity().equals(inputStr);
            if(isMatch) {
                hmMatch.put(city.getCity(),city.getNumber());
                dataMatch.add(city.getCity());//将城市名称，添加到数组（用于在listView中显示）
                Log.d("match", "match方法相匹配的城市：" + city.getCity());
            }

        }
        adapter.notifyDataSetChanged();


    }





}
