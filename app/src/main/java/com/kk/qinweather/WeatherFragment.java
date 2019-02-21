package com.kk.qinweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kk.qinweather.gson.ForecastDay;
import com.kk.qinweather.gson.ForecastNow;
import com.kk.qinweather.gson.ForecastWeek;
import com.kk.qinweather.util.HttpUtil;
import com.kk.qinweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherFragment extends Fragment {

    private ScrollView weatherLayout;
    private LinearLayout forecastLayout;
    private TextView titleCity;
    private TextView degreeText;
    private TextView humidityText;
    private TextView aqiText;
    private TextView aqiStateText;
    private TextView windLevelText;
    private TextView windDirectionText;
    private TextView skyconText;
    private ImageView backgroundImg;
    private TextView temperatureMaxText;
    private TextView temperatureMinText;
    private TextView visibilityText;
    private TextView ultravioletText;
    private TextView feelingText;
    private TextView dateText;
    public SwipeRefreshLayout swipeRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Fragmentude","onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Fragmentude","onCreateView");
        View view = inflater.inflate(R.layout.weather_fragment, container, false);

        weatherLayout = (ScrollView) view.findViewById(R.id.weather_layout);
        forecastLayout = (LinearLayout)view.findViewById(R.id.forecast_layout);
        titleCity = (TextView) view.findViewById(R.id.title_city);
        degreeText = (TextView) view.findViewById(R.id.temperatureNum);
        humidityText = (TextView) view.findViewById(R.id.humidityNum);
        aqiText = (TextView) view.findViewById(R.id.aqiNum);
        aqiStateText = (TextView) view.findViewById(R.id.aqiText);
        windLevelText = (TextView) view.findViewById(R.id.windLevel);
        windDirectionText = (TextView) view.findViewById(R.id.windText);
        skyconText = (TextView) view.findViewById(R.id.skyconText);
        backgroundImg = (ImageView) view.findViewById(R.id.background);
        temperatureMaxText = (TextView) view.findViewById(R.id.temperatureMax);
        temperatureMinText = (TextView) view.findViewById(R.id.temperatureMin);
        visibilityText = (TextView) view.findViewById(R.id.visibility_status);
        ultravioletText = (TextView) view.findViewById(R.id.ultraviolet_status);
        feelingText = (TextView) view.findViewById(R.id.feeling_status);
        dateText = (TextView) view.findViewById(R.id.dateText);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String weatherStringNow = prefs.getString("StringNow", null);
        String weatherStringDay = prefs.getString("StringDay", null);
        String weatherStringWeek = prefs.getString("StringWeek", null);

        SharedPreferences position = getActivity().getSharedPreferences("position_data",Context.MODE_PRIVATE);
        String latitude = position.getString("latitude","");
        String longitude = position.getString("longitude","");
        String cityName = position.getString("cityName","NULL");
        final String existAddress = longitude + "," + latitude;
        if (weatherStringNow != null && weatherStringDay != null && weatherStringWeek != null) {
            ForecastNow now = Utility.handleWeatherNowResponse(weatherStringNow);
            ForecastDay day = Utility.handleWeatherDayResponse(weatherStringDay);
            ForecastWeek week = Utility.handleWeatherWeekResponse(weatherStringWeek);
            now.cityName = cityName;
            showWeatherInfo(now,day,week);
        } else {
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(existAddress);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(existAddress);
            }
        });
        return view;
    }





    public void requestWeather(String position) {
        String weatherUrlNow = "https://api.caiyunapp.com/v2/wolwce9hPB01FPAQ/" + position + "/realtime";
        final String weatherUrlDay = "https://api.caiyunapp.com/v2/wolwce9hPB01FPAQ/" + position + "/hourly";
        final String weatherUrlWeek = "https://api.caiyunapp.com/v2/wolwce9hPB01FPAQ/" + position + "/daily";
        //first floor
        HttpUtil.sendOkHttpRequest(weatherUrlNow, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "获取天气数据失败(now)", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseNow = response.body().string();
                final ForecastNow forecastNow = Utility.handleWeatherNowResponse(responseNow);
                //second floor
                HttpUtil.sendOkHttpRequest(weatherUrlDay, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "获取天气数据失败(day)", Toast.LENGTH_SHORT).show();
                                swipeRefresh.setRefreshing(false);
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseDay = response.body().string();
                        final ForecastDay forecastDay = Utility.handleWeatherDayResponse(responseDay);
                        //third
                        HttpUtil.sendOkHttpRequest(weatherUrlWeek, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "获取天气数据失败(week)", Toast.LENGTH_SHORT).show();
                                        swipeRefresh.setRefreshing(false);
                                    }
                                });
                            }
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String responseWeek = response.body().string();
                                final ForecastWeek forecastWeek = Utility.handleWeatherWeekResponse(responseWeek);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (forecastNow != null && forecastDay != null && forecastWeek != null &&"ok".equals(forecastNow.status) &&"ok".equals(forecastDay.status)&&"ok".equals(forecastWeek.status)) {
                                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                                            editor.putString("StringNow", responseNow);
                                            editor.putString("StringDay", responseDay);
                                            editor.putString("StringWeek", responseWeek);
                                            editor.apply();
                                            showWeatherInfo(forecastNow,forecastDay,forecastWeek);
                                        } else {
                                            Toast.makeText(getContext(), "获取天气数据失败(now & day & week)", Toast.LENGTH_SHORT).show();
                                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                                            editor.putString("status", null);
                                            editor.apply();
                                        }
                                        swipeRefresh.setRefreshing(false);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void showWeatherInfo(ForecastNow now,ForecastDay day,ForecastWeek week) {
        String degree = null;
        if(now.now.temperature.contains(".")) {
            degree = now.now.temperature.substring(0, now.now.temperature.indexOf(".")) + "°";
        }else {
            degree = now.now.temperature + "°";
        }

        Double humidityPart = now.now.humidity * 100;
        String humidity = humidityPart.toString()
                .substring(0, humidityPart.toString().indexOf(".")) + "%";
        String aqi = now.now.aqi;
        String windSpeed = now.now.wind.speed;
        String windDirection = now.now.wind.direction;
        String skyCon = now.now.skycon;
        String cityName = now.cityName;
        String temperatureMax = null;
        String temperatureMin = null;
        if(week.week.daily.temperaturesList.get(0).max.contains(".")) {
            temperatureMax = week.week.daily.temperaturesList.get(0).max.substring(0, week.week.daily.temperaturesList.get(0).max.indexOf(".")) + "°C";
        }else {
            temperatureMax = week.week.daily.temperaturesList.get(0).max + "°C";
        }
        if (week.week.daily.temperaturesList.get(0).min.contains(".")){
            temperatureMin = week.week.daily.temperaturesList.get(0).min.substring(0, week.week.daily.temperaturesList.get(0).min.indexOf(".")) + "°C";
        }else {
            temperatureMin = week.week.daily.temperaturesList.get(0).min + "°C";
        }
        String visibility = now.now.visibility;
        String ultraviolet = now.now.ultraviolet.desc;
        String feeling  = now.now.comfort.desc;

        titleCity.setText(cityName);
        degreeText.setText(degree);
        humidityText.setText(humidity);
        aqiText.setText(aqi);
        temperatureMaxText.setText(temperatureMax);
        temperatureMinText.setText(temperatureMin);
        visibilityText.setText(visibility);
        ultravioletText.setText(ultraviolet);
        feelingText.setText(feeling);
        DateTime dateNow = new DateTime();
        String date = dateNow.get(DateTimeFieldType.monthOfYear()) +"月"+ dateNow.get(DateTimeFieldType.dayOfMonth()) +"日";
        dateText.setText(date);

        forecastLayout.removeAllViews();
        for(int i = 0 ; i <week.week.daily.temperaturesList.size();i++){
            View view = LayoutInflater.from(getContext()).inflate(R.layout.forecast_item,forecastLayout,false);
            ForecastWeek.Week.Daily.Temperature forecastItem = week.week.daily.temperaturesList.get(i);

            TextView forecastDateText = (TextView) view.findViewById(R.id.forecast_date);
            ImageView forecastImgView = (ImageView) view.findViewById(R.id.forecast_img);
            TextView forecastTemperatureText = (TextView) view.findViewById(R.id.forecast_temperature);

            switch (week.week.daily.skyconList.get(i).value) {
                case "CLEAR_DAY":
                    Glide.with(getContext()).load(R.drawable.sunny).into(forecastImgView);
                    break;
                case "CLEAR_NIGHT":
                    Glide.with(getContext()).load(R.drawable.sunny).into(forecastImgView);
                    break;
                case "PARTLY_CLOUDY_DAY":
                    Glide.with(getContext()).load(R.drawable.partly_cloudy).into(forecastImgView);
                    break;
                case "PARTLY_CLOUDY_NIGHT":
                    Glide.with(getContext()).load(R.drawable.partly_cloudy).into(forecastImgView);
                    break;
                case "CLOUDY":
                    Glide.with(getContext()).load(R.drawable.cloudy).into(forecastImgView);
                    break;
                case "WIND":
                    Glide.with(getContext()).load(R.drawable.windy).into(forecastImgView);
                    break;
                case "HAZE":
                    Glide.with(getContext()).load(R.drawable.foggy).into(forecastImgView);
                    break;
                case "RAIN":
                    Glide.with(getContext()).load(R.drawable.rainy).into(forecastImgView);
                    break;
                case "SNOW":
                    Glide.with(getContext()).load(R.drawable.snowy).into(forecastImgView);
                    break;
                default:
                    break;
            }

            String maxTemperature = null;
            String minTemperature = null;
            if(forecastItem.max.contains(".")) {
                maxTemperature = forecastItem.max.substring(0, forecastItem.max.indexOf("."));
            }else {
                maxTemperature = forecastItem.max;
            }
            if (forecastItem.min.contains(".")){
                minTemperature = forecastItem.min.substring(0, forecastItem.min.indexOf("."));
            }else {
                minTemperature = forecastItem.min;
            }
            String Temp = maxTemperature + "°/" + minTemperature + "°";
            forecastTemperatureText.setText(Temp);

            DateTime dateTime = new DateTime(forecastItem.date);
            if (Integer.toString(dateTime.getDayOfMonth()).equals(Integer.toString(new DateTime().getDayOfMonth()))){
                forecastDateText.setText("今天");
            }else if (dateTime.isBeforeNow()){
                forecastDateText.setText("昨天");
                forecastImgView.setAlpha(0.5f);
                forecastDateText.setAlpha(0.5f);
                forecastTemperatureText.setAlpha(0.5f);
            }
            else if(Integer.toString(dateTime.getDayOfMonth()).equals(Integer.toString(new DateTime().plusDays(1).getDayOfMonth()))){
                forecastDateText.setText("明天");
            }else{
                switch (Integer.toString(dateTime.getDayOfWeek())){
                    case "1":
                        forecastDateText.setText("周一");
                        break;
                    case "2":
                        forecastDateText.setText("周二");
                        break;
                    case "3":
                        forecastDateText.setText("周三");
                        break;
                    case "4":
                        forecastDateText.setText("周四");
                        break;
                    case "5":
                        forecastDateText.setText("周五");
                        break;
                    case "6":
                        forecastDateText.setText("周六");
                        break;
                    case "7":
                        forecastDateText.setText("周日");
                        break;
                    default:
                        break;
                }
            }
            forecastLayout.addView(view);
        }
        //空气质量等级判断
        if (Integer.parseInt(aqi) <= 50 && Integer.parseInt(aqi) >= 0) {
            aqiStateText.setText("空气优");
            aqiStateText.setTextColor(Color.parseColor("#00e400"));
        } else if (Integer.parseInt(aqi) <= 100) {
            aqiStateText.setText("空气良好");
            aqiStateText.setTextColor(Color.parseColor("#ffff00"));
        } else if (Integer.parseInt(aqi) <= 150) {
            aqiStateText.setText("轻度污染");
            aqiStateText.setTextColor(Color.parseColor("#ff7e00"));
        } else if (Integer.parseInt(aqi) <= 200) {
            aqiStateText.setText("中度污染");
            aqiStateText.setTextColor(Color.parseColor("#ff0000"));
        } else if (Integer.parseInt(aqi) <= 300) {
            aqiStateText.setText("重度污染");
            aqiStateText.setTextColor(Color.parseColor("#99004c"));
        } else if (Integer.parseInt(aqi) > 300) {
            aqiStateText.setText("严重污染");
            aqiStateText.setTextColor(Color.parseColor("#7e0023"));
        } else {
            aqiStateText.setText("NULL");
        }
        //风速对应等级判断
        if (Double.parseDouble(windSpeed) < 0.3 && Double.parseDouble(windSpeed) >= 0) {
            windLevelText.setText("0级");
        } else if (Double.parseDouble(windSpeed) < 1.6) {
            windLevelText.setText("1级");
        } else if (Double.parseDouble(windSpeed) < 3.4) {
            windLevelText.setText("2级");
        } else if (Double.parseDouble(windSpeed) < 5.5) {
            windLevelText.setText("3级");
        } else if (Double.parseDouble(windSpeed) < 8.0) {
            windLevelText.setText("4级");
        } else if (Double.parseDouble(windSpeed) < 10.8) {
            windLevelText.setText("5级");
        } else if (Double.parseDouble(windSpeed) < 13.9) {
            windLevelText.setText("6级");
        } else if (Double.parseDouble(windSpeed) < 17.2) {
            windLevelText.setText("7级");
        } else if (Double.parseDouble(windSpeed) < 20.8) {
            windLevelText.setText("8级");
        } else if (Double.parseDouble(windSpeed) < 24.5) {
            windLevelText.setText("9级");
        } else if (Double.parseDouble(windSpeed) < 28.5) {
            windLevelText.setText("10级");
        } else if (Double.parseDouble(windSpeed) < 32.7) {
            windLevelText.setText("11级");
        } else if (Double.parseDouble(windSpeed) >= 32.7) {
            windLevelText.setText("12级");
        } else {
            windLevelText.setText("NULL");
        }
        //风向判断
        if (Double.parseDouble(windDirection) <= 22.5 || Double.parseDouble(windDirection) > 337.5) {
            windDirectionText.setText("北风");
        } else if (Double.parseDouble(windDirection) > 22.5 || Double.parseDouble(windDirection) <= 67.5) {
            windDirectionText.setText("东北风");
        } else if (Double.parseDouble(windDirection) > 67.5 || Double.parseDouble(windDirection) <= 112.5) {
            windDirectionText.setText("东风");
        } else if (Double.parseDouble(windDirection) > 112.5 || Double.parseDouble(windDirection) <= 157.5) {
            windDirectionText.setText("东南风");
        } else if (Double.parseDouble(windDirection) > 157.5 || Double.parseDouble(windDirection) <= 202.5) {
            windDirectionText.setText("南风");
        } else if (Double.parseDouble(windDirection) > 202.5 || Double.parseDouble(windDirection) <= 247.5) {
            windDirectionText.setText("西南风");
        } else if (Double.parseDouble(windDirection) > 247.5 || Double.parseDouble(windDirection) <= 292.5) {
            windDirectionText.setText("西风");
        } else if (Double.parseDouble(windDirection) > 292.5 || Double.parseDouble(windDirection) <= 337.5) {
            windDirectionText.setText("西北风");
        } else {
            windDirectionText.setText("NULL");
        }
        //天气状况判断
        switch (skyCon) {
            case "CLEAR_DAY":
                skyconText.setText("晴");
                Glide.with(getContext()).load(R.drawable.sunny_bg).into(backgroundImg);
                break;
            case "CLEAR_NIGHT":
                skyconText.setText("晴");
                Glide.with(getContext()).load(R.drawable.sunny_bg).into(backgroundImg);
                break;
            case "PARTLY_CLOUDY_DAY":
                skyconText.setText("多云");
                Glide.with(getContext()).load(R.drawable.partly_cloudy_bg).into(backgroundImg);
                break;
            case "PARTLY_CLOUDY_NIGHT":
                skyconText.setText("多云");
                Glide.with(getContext()).load(R.drawable.cloudy_bg).into(backgroundImg);
                break;
            case "CLOUDY":
                skyconText.setText("阴");
                Glide.with(getContext()).load(R.drawable.cloudy_bg).into(backgroundImg);
                break;
            case "WIND":
                skyconText.setText("大风");
                Glide.with(getContext()).load(R.drawable.windy_bg).into(backgroundImg);
                break;
            case "HAZE":
                skyconText.setText("雾霾");
                Glide.with(getContext()).load(R.drawable.foggy_bg).into(backgroundImg);
                break;
            case "RAIN":
                skyconText.setText("雨");
                Glide.with(getContext()).load(R.drawable.rainy_bg).into(backgroundImg);
                break;
            case "SNOW":
                skyconText.setText("雪");
                Glide.with(getContext()).load(R.drawable.snowy_bg).into(backgroundImg);
                break;
            default:
                break;
        }
        weatherLayout.setVisibility(View.VISIBLE);

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}


