package com.kk.qinweather.gson;

import com.google.gson.annotations.SerializedName;

public class ForecastNow {
    public String status;

    @SerializedName("result")
    public Now now;

    public class Now {

        public String temperature;

        public Double humidity;

        public String skycon;

        public String aqi;

        public Wind wind;

        public class Wind{

            public String direction;

            public String speed;

        }

        public Comfort comfort;

        public class Comfort{   //舒适信息

            public String index;

            public String desc;

        }

        public Ultraviolet ultraviolet;

        public class Ultraviolet{   //紫外线指数

            public String index;

            public String desc;

        }

        public String visibility; //能见度
    }

    @SerializedName("placename")
    public String cityName;
}
