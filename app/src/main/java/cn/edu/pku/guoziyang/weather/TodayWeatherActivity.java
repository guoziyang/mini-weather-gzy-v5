package cn.edu.pku.guoziyang.weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.pku.guoziyang.bean.TodayWeather;
import cn.edu.pku.guoziyang.util.NetUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.zip.GZIPInputStream;

import cn.edu.pku.guoziyang.util.PinYin;


/**
 * 今日天气信息界面
 */

public class TodayWeatherActivity extends Activity {

    //Log信息标签
    private String TAG = "myWeather";

    //按钮
    private ImageView mUpdateBtn;//信息更新按钮
    private ImageView mUpdateBtn_cancel;//信息更新按钮,正在更新时显示
    private ImageView mCitySelect;//城市选择按钮
    private ProgressBar title_update_progressbar;
    private ImageView mShareBtn;//分享按钮


    //今日天气详细信息
    private TextView cityTv, wenduTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windOTv, windTv, city_name_Tv;
    private ImageView weatherImg,weatherImg2,weatherImg3, pmImg;
    private RelativeLayout relativeLayout;


    //近三日天气信息
    private TextView temperatureTv1,temperatureTv2,temperatureTv3;//最高气温最低气温
    private TextView typeTv,type2Tv,type3Tv;//天气状况，如，今日 晴


    //城市编码
    String cityCode,currentCityCode,selectCityCode;

    TodayWeather todayWeather = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_today);//关联布局

        //程序第一次执行跳转到引导界面
        SharedPreferences preferences = getSharedPreferences("count", MODE_PRIVATE);
        int count = preferences.getInt("count", 0);
        //判断程序与第几次运行，如果是第一次运行则跳转到引导页面
        if (count == 0) {
            Intent i = new Intent(TodayWeatherActivity.this,Guide.class);
            startActivity(i);
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("count", ++count);
        editor.commit();


        //选择城市按钮
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //为城市管理按钮添加意图,实现功能:点击后跳转到selectCity页面
                Intent i = new Intent(TodayWeatherActivity.this,SelectCityActivity.class);
                if(todayWeather != null){
                    i.putExtra("cityName",todayWeather.getCity());
                }else{
                    i.putExtra("cityName","北京");//默认城市为北京
                }
                startActivityForResult(i,1);
            }
        });

        //更新天气信息按钮
        //点击更新按钮
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更新天气信息
                preUpdateWeather();
                //提示更新成功
                Toast.makeText(TodayWeatherActivity.this,"更新成功!",Toast.LENGTH_SHORT).show();
            }
        });

        //分享按钮
        mShareBtn = (ImageView) findViewById(R.id.btn_share);
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                String str = todayWeather.getCity() + "今日天气：" + "当前温度"+todayWeather.getWendu()+"℃,"+todayWeather.getType() +
                        "," + todayWeather.getFengxiang() + todayWeather.getFengli()+
                        ","+ todayWeather.getHigh() + "~" + todayWeather.getLow()  +
                        ",湿度" + todayWeather.getShidu() +
                        ",pm2.5指数" + todayWeather.getPm25() +
                        ",空气质量为" + todayWeather.getQuality() +
                        "," + todayWeather.getDate() + " "+ todayWeather.getUpdatetime() + "发布";
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                intent.putExtra(Intent.EXTRA_TEXT,str+"."+ " (分享自mini-weather)");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getTitle()));
            }
        });

        //初始化UI控件
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
        weatherImg = (ImageView) findViewById(R.id.weather01_pic);//今日
        weatherImg2 = (ImageView) findViewById(R.id.weather02_pic);//明日
        weatherImg3 = (ImageView) findViewById(R.id.weather03_pic);//后日

        relativeLayout =  (RelativeLayout)findViewById(R.id.bg_weather_today);

        //温度范围(未来3天中的今日)
        temperatureTv1 = (TextView) findViewById(R.id.weather01);//今日
        temperatureTv2 = (TextView) findViewById(R.id.weather02);//明日
        temperatureTv3 = (TextView) findViewById(R.id.weather03);//后日

        //近3天天气状况，如，今日 晴
        typeTv = (TextView) findViewById(R.id.weather01_tips);//今日
        type2Tv = (TextView) findViewById(R.id.weather02_tips);//明日
        type3Tv = (TextView) findViewById(R.id.weather03_tips);//后日

        //获取默认保存的天气信息
        preUpdateWeather();

    }

    //更新默认选择城市的天气信息
    protected void preUpdateWeather(){

        Log.d(TAG,"preudateweather()");

        //config文件的cityCode中保存用户选择的城市编码
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        //默认城市代码为北京
        cityCode=sharedPreferences.getString("cityCode","101010100");

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d(TAG, "网络OK" );
            //更新天气信息
            queryWeatherCode(cityCode);

        } else {
            Log.d(TAG, "网络挂了");
            Toast.makeText(TodayWeatherActivity.this, "网络连接失败，请检查网络!", Toast.LENGTH_LONG).show();
            //初始化UI
            city_name_Tv.setText("N/A");
            wenduTv.setText("N/A");
            timeTv.setText("N/A");
            pmDataTv.setText("N/A");
            pmQualityTv.setText("N/A");
            weekTv.setText("N/A");
            climateTv.setText("N/A");
            temperatureTv1.setText("--");
            temperatureTv2.setText("--");
            temperatureTv3.setText("--");
        }
    }




    //更新UI控件
    private static final int UPDATE_TODAY_WEATHER = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 从网络获取城市编码为cityCode的城市的天气信息
     * @param cityCode 城市编码（默认北京101010100）
     */
    private void queryWeatherCode(String cityCode) {
        //http://wthrcdn.etouch.cn/WeatherApi?citykey=101010100
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d(TAG, "XML文件网络访问URL：" + address);
        Log.d("update", "queryWeatherCode中参数citycode=：" + cityCode);

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
                    todayWeather = parseXML(responseStr);

                    //将获取的天气信息传递给UI
                    if(todayWeather != null){
                        Message msg= new Message();
                        msg.what=UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                        mHandler.sendMessage(msg);
                    }
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
     * 解析从网络获取的XML数据文件（天气信息），并将获取的天气信息存入TodayWeather对象
     * 返回TodayWeather对象
     *
     * @param xmldata xml文件内容
     */
    private TodayWeather  parseXML(String xmldata) {

        //保存更新的天气信息
//        TodayWeather todayWeather = null;

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

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            //city即xml文件中的city标签
                            if (xmlPullParser.getName().equals("city")) {//城市名称
                                //进入下一个元素,并触发相应事件
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setCity(temp);
                            } else if (xmlPullParser.getName().equals("updatetime")) {//更新时间
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setUpdatetime(temp);
                            } else if (xmlPullParser.getName().equals("shidu")) {//湿度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setShidu(temp);
                            } else if (xmlPullParser.getName().equals("wendu")) {//实时温度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setWendu(temp);
                            } else if (xmlPullParser.getName().equals("pm25")) {//pm2.5数值
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setPm25(temp);
                            } else if (xmlPullParser.getName().equals("aqi")) {//空气污染指数aqi
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setAqi(temp);
                            } else if (xmlPullParser.getName().equals("quality")) {//空气质量
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setQuality(temp);
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {//风向
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setFengxiang(temp);
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {//
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setFengli(temp);
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setDate(temp);
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {//今日最高温度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setHigh(temp.substring(2).trim());
                                Log.d("myapp", "high:" + xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setLow(temp.substring(2).trim());
                                lowCount++;
                            }else if (xmlPullParser.getName().equals("high") && highCount == 1) {//明日最高温度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setHigh2(temp.substring(2).trim());
                                highCount++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 1) {//明日最低温度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setLow2(temp.substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 2) {//后日最高温度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setHigh3(temp.substring(2).trim());
                                highCount++;
                            }else if (xmlPullParser.getName().equals("low") && lowCount == 2) {//后日最低温度
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setLow3(temp.substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {//今日天气状况
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setType(temp);
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 1) {//明日天气状况
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setType2(temp);
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 2) {//后日天气状况
                                eventType = xmlPullParser.next();
                                temp = xmlPullParser.getText();
                                todayWeather.setType3(temp);
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

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //测试是否获取到天气--可删除
        if(todayWeather == null){
            Log.d(TAG,"todayWeather未存入");
        }else {
            Log.d(TAG,todayWeather.toString());
        }

        return todayWeather;
    }


    /**
     * 更新天气信息到UI控件（从todayweather）
     *
     */
    void updateTodayWeather(TodayWeather todayWeather){
        if(todayWeather.getCity()==null){
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("cityCode",currentCityCode);//保存选择的城市
            editor.commit();
            Toast.makeText(TodayWeatherActivity.this, "未找到该城市天气信息！", Toast.LENGTH_SHORT).show();
        }else {
            currentCityCode = selectCityCode;

            //更新数据到控件
            String city_name, wendu, time, pmData, pmQuality, week;
            String low, low2, low3, hign, hign2, hign3;
            //天气状况，今日，明日，后日
            String climate,climate2,climate3;

            //获取更新的数据（从Sharepreference中获取）
            city_name = todayWeather.getCity();
            wendu = todayWeather.getWendu();
            time = todayWeather.getUpdatetime();
            pmData = todayWeather.getPm25();

            pmQuality = todayWeather.getQuality();
            week = todayWeather.getDate();


            //近3日天气状况
            climate = todayWeather.getType();
            climate2 = todayWeather.getType2();
            climate3 = todayWeather.getType3();


            //今日
            hign = todayWeather.getHigh();
            low = todayWeather.getLow();
            //明日
            hign2 = todayWeather.getHigh2();
            low2 = todayWeather.getLow2();
            //后日
            hign3 = todayWeather.getHigh3();
            low3 = todayWeather.getLow3();


            //显示到控件显示
            city_name_Tv.setText(city_name);
            wenduTv.setText(wendu + "°");
            timeTv.setText(time + "发布");
            pmDataTv.setText(pmData);
            pmQualityTv.setText(pmQuality);
            weekTv.setText(week);
            climateTv.setText(climate);


            //根据aqi不同，显示不同图标（aqi为空气污染指数）
            //当前城市无pm数据时，不显示PM2.5图标
            if(pmData==null){
                pmImg.setVisibility(View.INVISIBLE);
            }else{
                pmImg.setVisibility(View.VISIBLE);
                int pmValue = Integer.parseInt(pmData.trim());
                int aqiValue = Integer.parseInt(todayWeather.getAqi().trim());
                //新pm2.5图片id
                int pmImgId = -1;
                if(aqiValue <= 50){//aqi 0~50 空气质量状况属于优
                    pmImgId = R.drawable.city_weather_pm25_green;
                }else if (aqiValue > 50 && aqiValue < 101) {//aqi 50~100空气质量状况属于良
                    pmImgId = R.drawable.city_weather_pm25_yellow;
                }else if (aqiValue >= 101 && aqiValue < 151) {//aqi 101~150空气质量状况属于轻度污染
                    pmImgId = R.drawable.city_weather_pm25_orange;
                } else if (aqiValue >= 151 && aqiValue < 201) {//aqi 151~200空气质量状况属于中度污染
                    pmImgId = R.drawable.city_weather_pm25_purple;
                } else if (aqiValue >= 201 && aqiValue < 301) {//aqi 201~300空气质量状况属于重度污染
                    pmImgId = R.drawable.city_weather_pm25_brown;
                } else if (aqiValue >= 301) {//aqi 大于300 空气质量状况属于严重污染
                    pmImgId = R.drawable.city_weather_pm25_red;
                }
                //显示更改的图片
                Drawable drawablePm25Img = getResources().getDrawable(pmImgId);
                pmImg.setImageDrawable(drawablePm25Img);
            }
            //根据天气状况显示不同背景（用文字反射拼音）
            String typeBgImg = "bg_day_" + PinYin.converterToSpell(todayWeather.getType());
            Class aClass = R.drawable.class;
            int typeBgId = -1;
            try {
                Field field = aClass.getField(typeBgImg);
                Object value = field.get(new Integer(0));
                typeBgId = (int) value;
            } catch (Exception e) {
                if (-1 == typeBgId)
                    typeBgId = R.drawable.bg_day_qing;
            } finally {
                Drawable drawable = getResources().getDrawable(typeBgId);
                relativeLayout.setBackground(drawable);
            }

            //更新近三日天气的最低最高温度
            temperatureTv1.setText(low + "~" + hign);
            temperatureTv2.setText(low2 + "~" + hign2);
            temperatureTv3.setText(low3 + "~" + hign3);
            //更新近三日天气的天气状况，如，今日 晴
            typeTv.setText("今日 " + todayWeather.getType());
            type2Tv.setText("明日 " + todayWeather.getType2());
            type3Tv.setText("后日 " + todayWeather.getType3());

            //根据天气状况设置近3天的天气图标(用文字反射拼音)
            String typeImg = "weather_icon_day_" + PinYin.converterToSpell(todayWeather.getType());
            String type2Img = "weather_icon_day_" + PinYin.converterToSpell(todayWeather.getType2());
            String type3Img = "weather_icon_day_" + PinYin.converterToSpell(todayWeather.getType3());
            Class bClass = R.drawable.class;
            int typeId = -1;
            int type2Id = -1;
            int type3Id = -1;
            try {
                Field field = aClass.getField(typeImg);
                Object value = field.get(new Integer(0));
                typeId = (int) value;
                Field field2 = aClass.getField(type2Img);
                Object value2 = field2.get(new Integer(0));
                type2Id = (int) value2;
                Field field3 = aClass.getField(type3Img);
                Object value3 = field3.get(new Integer(0));
                type3Id = (int) value3;
            } catch (Exception e) {
                if (-1 == typeId & -1 == type2Id & -1 == type3Id)
                    typeId = R.drawable.weather_icon_day_qing;
                type2Id = R.drawable.weather_icon_day_qing;
                type3Id = R.drawable.weather_icon_day_qing;
            } finally {
                //weatherImg为今日天气情况的图标
                Drawable drawableTypeImg = getResources().getDrawable(typeId);
                weatherImg.setImageDrawable(drawableTypeImg);
                //weatherImg为明日天气情况的图标
                Drawable drawableTypeImg2 = getResources().getDrawable(type2Id);
                weatherImg2.setImageDrawable(drawableTypeImg2);
                //weatherImg为后日天气情况的图标
                Drawable drawableTypeImg3 = getResources().getDrawable(type3Id);
                weatherImg3.setImageDrawable(drawableTypeImg3);
            }

        }
    }


    //选择城市后，将该城市的编码回传
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1&&resultCode==1){
            //获取用户选择的城市编码
            String select_city_code = data.getStringExtra("select_city_code");
            Log.d(TAG,"接收到的选择城市编码----------------->："+ select_city_code);

            //将用户选择的城市编码保存，用于下次访问自动更新天气信息
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("cityCode",select_city_code);
            editor.commit();

            //更新天气
            preUpdateWeather();

            //提示更新成功
            Toast.makeText(TodayWeatherActivity.this,"更新成功!",Toast.LENGTH_SHORT).show();

        }
    }
}
