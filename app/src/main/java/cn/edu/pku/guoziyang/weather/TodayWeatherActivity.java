package cn.edu.pku.guoziyang.weather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.edu.pku.guoziyang.util.NetUtil;

/**
 * 今日天气信息界面
 */

public class TodayWeatherActivity extends Activity {

    //Log信息标签
    private String TAG = "myWeather";


    //按钮
    private ImageView mUpdateBtn;//信息更新按钮
    private ImageView mCitySelect;//城市选择按钮



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_today);//关联布局


        //检测是否连接到网络
        checkNetworkState();

        //选择城市按钮
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TodayWeatherActivity.this,
                        "点击城市管理按钮！ ",
                        Toast.LENGTH_SHORT).show();
            }
        });
        //更新天气信息按钮
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TodayWeatherActivity.this,
                        "点击天气更新按钮！ ",
                        Toast.LENGTH_SHORT).show();
            }
        });
        //初始化控件
    }


    //检测是否连接到网络
    protected void checkNetworkState(){
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d(TAG, "网络OK" );
            Toast.makeText(TodayWeatherActivity.this, "网络OK!", Toast.LENGTH_LONG).show();
            //更新天气信息，不需要提示网络连接成功
        } else {
            Log.d(TAG, "网络挂了");
            Toast.makeText(TodayWeatherActivity.this, "网络连接失败，请检查网络!", Toast.LENGTH_LONG).show();
        }
    }
}
