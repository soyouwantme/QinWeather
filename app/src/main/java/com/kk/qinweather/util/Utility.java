package com.kk.qinweather.util;

import com.google.gson.Gson;
import com.kk.qinweather.gson.ForecastDay;
import com.kk.qinweather.gson.ForecastNow;
import com.kk.qinweather.gson.ForecastWeek;

import org.json.JSONObject;

public class Utility {

    public static ForecastNow handleWeatherNowResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            return new Gson().fromJson(jsonObject.toString(), ForecastNow.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static ForecastDay handleWeatherDayResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            return new Gson().fromJson(jsonObject.toString(), ForecastDay.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static ForecastWeek handleWeatherWeekResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            return new Gson().fromJson(jsonObject.toString(), ForecastWeek.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
