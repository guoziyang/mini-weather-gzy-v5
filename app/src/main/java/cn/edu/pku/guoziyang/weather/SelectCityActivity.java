package cn.edu.pku.guoziyang.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    //定义TodayWeather对象
    TodayWeather todayWeather = null;

    //界面返回按钮
    private ImageView mBackBtn;
    public EditText search_edit;
    public EditText mEditText;

    //界面标题
    private TextView selectCityName;

    City city;

    //ListView控件显示的数据
    private List<String> data = new ArrayList<String>();
    //用户存放城市名称和对应城市编码（用于天气更新）
    //其中key为城市编码，value为城市名称
    private HashMap hm = new HashMap();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //与布局文件关联
        setContentView(R.layout.activity_select_city);

        //返回主活动按钮
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("citycode", "101160101");
                setResult(RESULT_OK, i);
                finish();
            }
        });


        //标题栏，当前城市
        selectCityName = (TextView) findViewById(R.id.title_name);


        //ListView控件
        ListView mlistView = (ListView) findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SelectCityActivity.this, android.R.layout.simple_list_item_1, data//data数据数据将在listview列表中显示
        );
        mlistView.setAdapter(adapter);


        //MyApplication中已经将城市列表从数据库读出
        MyApplication myApplication = MyApplication.getInstance();
        //获取城市列表
        List res = myApplication.getmCityList();

        //遍历城市列表，并将其添加到data数据（在listview中显示）
        Iterator it = res.iterator();
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
                //单击内容测试 - 可删除
                Toast.makeText(SelectCityActivity.this, "单击,i值测试：" + data.get(i), Toast.LENGTH_SHORT).show();
                //设置标题为当前城市
                selectCityName.setText("当前选择城市：" + data.get(i));

                //获取当前选择城市的城市编码
                String citycode = "101280601";

                //选中的城市的名称
                Log.d(TAG,"选中的城市名称：" + data.get(i));

                //通过城市名称取出城市编码
                //取出的城市编码为Object类型，需要转换
                String code = hm.get(data.get(i)).toString();
                Log.d(TAG,"选中的城市编码：" + code);

                //从网络获取该城市天气信息
//                queryWeatherCode(code);

                if(todayWeather != null){

                    Log.d(TAG,"从网络获取选中的城市天气信息：" + todayWeather.toString());
                }else {
                    Log.d(TAG,"从网络获取选中的城市天气信息为空");
                }

            }
        });


    }

}
