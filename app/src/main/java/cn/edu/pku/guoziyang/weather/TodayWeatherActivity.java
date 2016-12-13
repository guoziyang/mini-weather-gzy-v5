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

import cn.edu.pku.guoziyang.bean.TodayWeather;
import cn.edu.pku.guoziyang.util.NetUtil;

/**
 * 今日天气信息界面
 */

public class TodayWeatherActivity extends Activity {

    //Log信息标签
    private String TAG = "mini-weather";

    //按钮
    private ImageView mUpdateBtn;//信息更新按钮
    private ImageView mCitySelect;//城市选择按钮

    //今日天气详细信息
    private TextView cityTv, wenduTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windOTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;

    //近三日天气信息
    private TextView temperatureTv1,temperatureTv2,temperatureTv3;



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
        //点击更新按钮
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //存储从网络获取的天气信息
                SharedPreferences sharedPreferences = getSharedPreferences("today_weather", 0);
                //城市编码，默认为北京,需要将给定城市名称city转为城市代码city_code（之后在添加数据库中实现）
                String city_code_save = sharedPreferences.getString("city_code_save", "101010100");
                System.out.println(city_code_save);
                //从网络获取天气信息
                queryWeatherCode(city_code_save);
                //显示到UI
                updateTodayWeather();
                //提示更新成功
                Toast.makeText(TodayWeatherActivity.this,"更新成功!",Toast.LENGTH_SHORT).show();
            }
        });
        //初始化控件
        initView();
    }


    //初始化页面显示控件
    void initView() {
        //顶部工具栏城市名称
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        //实时温度
        wenduTv = (TextView) findViewById(R.id.wendu);
        //信息发布时间
        timeTv = (TextView) findViewById(R.id.update_time);
        //日期，如8日星期四
        weekTv = (TextView) findViewById(R.id.date);
        //PM2.5数值
        pmDataTv = (TextView) findViewById(R.id.pm25);
        //污染等级，如重度污染
        pmQualityTv = (TextView) findViewById(R.id.quality);
        //PM2.5图
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        //天气状况,例,晴
        climateTv = (TextView) findViewById(R.id.type);
        //天气状况图片(未来3天中的今日)
        weatherImg = (ImageView) findViewById(R.id.weather01_pic);


        //温度范围(未来3天中的今日)
        temperatureTv1 = (TextView) findViewById(R.id.weather01);//今日
        temperatureTv2 = (TextView) findViewById(R.id.weather02);//明日
        temperatureTv3 = (TextView) findViewById(R.id.weather03);//后日

        //读取从网络获取的天气信息
        SharedPreferences sharedPreferences = getSharedPreferences("today_weather", 0);
        //城市编码，默认为北京,需要将给定城市名称city转为城市代码city_code（之后在添加数据库中实现）
        String city_code_save = sharedPreferences.getString("city_code_save", "101010100");
        updateTodayWeather();

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


    /**
     * 从网络获取城市编码为cityCode的城市的天气信息
     * 并将其存入Sharepreference的today_weather文件中
     * @param cityCode 城市编码（默认北京101010100）
     */
    private void queryWeatherCode(String cityCode) {
        //http://wthrcdn.etouch.cn/WeatherApi?citykey=101010100
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d(TAG, "XML文件网络访问URL：" + address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                //定义TodayWeather对象
                TodayWeather todayWeather = null;

                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                    }
                    String responseStr = response.toString();
                    //调用解析函数,解析获取到的网络数据
                    //responseStr获取的网络数据结果
                    parseXML(responseStr);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }


            }
        }).start();
    }



    /**
     *
     * 解析从网络获取的XML数据文件（天气信息），并将获取信息存入sharepreferce的today_weather文件
     *
     * 返回TodayWeather对象
     *
     * @param xmldata xml文件内容
     */
    private void parseXML(String xmldata) {

        TodayWeather todayWeather = null;

        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;

        String temp;

        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();

            //sharePreferences存入数据到today_weather
            //将从网络获取的今日天气信息存入today_weather文件
            SharedPreferences setting = getApplicationContext().getSharedPreferences("today_weather",0);
            SharedPreferences.Editor editor = setting.edit();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        //????
                        if(xmlPullParser.getName().equals("resp")){
                        }
                        if (todayWeather != null) {
                            //city即xml文件中的city标签
                            if (xmlPullParser.getName().equals("city")) {//城市
                                //进入下一个元素,并触发相应事件
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("city",temp);//存入后，最后需要commit提交
                            } else if (xmlPullParser.getName().equals("updatetime")) {//更新时间
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("updatetime",temp);//存入sharepreferce
                                // Log.d("myWeather", "updatetime:    " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {//湿度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("shidu",temp);
                            } else if (xmlPullParser.getName().equals("wendu")) {//实时温度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("wendu",temp);
                                // Log.d("myWeather", "wendu:    " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {//pm2.5数值
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("pm25",temp);
                            } else if (xmlPullParser.getName().equals("quality")) {//空气质量
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("quality",temp);
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {//风向
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("fengxiang",temp);
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {//
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("fengli",temp);
                                fengliCount++;

                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("date",temp);
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {//今日最高温度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("hign",temp.substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("low",temp.substring(2).trim());
                                lowCount++;
                            }else if (xmlPullParser.getName().equals("high") && highCount == 1) {//明日最高温度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("hign2",temp.substring(2).trim());
                                highCount++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 1) {//明日最低温度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("low2",temp.substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 2) {//后日最高温度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("hign3",temp.substring(2).trim());
                                highCount++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 2) {//后日最低温度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("low3",temp.substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                editor.putString("type",temp);
                                typeCount++;
                            }
                        }

                        break;
                    //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;

                }
                //进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
            editor.commit();//全部存入sharepreference后提交
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * UI中的控件中显示获取的天气信息
     *
     */
    void updateTodayWeather(){
        //更新数据到控件
        String city_name,wendu,time,pmData,pmQuality,week,climate;
        String low,low2,low3,hign,hign2,hign3;

        //获取更新的数据（从Sharepreference中获取）
        SharedPreferences sharedPreferences = getSharedPreferences("today_weather",0);
        city_name = sharedPreferences.getString("city","N/A");
        wendu = sharedPreferences.getString("wendu","N/A");
        time = sharedPreferences.getString("updatetime","N/A");
        pmData = sharedPreferences.getString("pm25","N/A");
        pmQuality = sharedPreferences.getString("quality","N/A");
        week = sharedPreferences.getString("date","N/A");
        climate = sharedPreferences.getString("type","N/A");

        //今日
        hign = sharedPreferences.getString("hign", "--");
        low = sharedPreferences.getString("low", "--");
        //明日
        hign2 = sharedPreferences.getString("hign2", "--");
        low2 = sharedPreferences.getString("low2", "--");
        //后日
        hign3 = sharedPreferences.getString("hign3", "--");
        low3 = sharedPreferences.getString("low3", "--");


        //显示到控件显示
        city_name_Tv.setText(city_name);
        wenduTv.setText(wendu + "°");
        timeTv.setText(time + "发布");
        pmDataTv.setText(pmData);
        pmQualityTv.setText(pmQuality);
        weekTv.setText(week);
        climateTv.setText(climate);

        //近三日天气
        temperatureTv1.setText(low + "~" + hign);
        temperatureTv2.setText(low2 + "~" + hign2);
        temperatureTv3.setText(low3 + "~" + hign3);

    }
}
