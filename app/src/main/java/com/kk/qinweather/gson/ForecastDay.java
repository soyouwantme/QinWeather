package com.kk.qinweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastDay {

    public String status;

    @SerializedName("result")
    public Day day;

    public class Day{

        @SerializedName("hourly")
        public Hourly hourly;

        public class Hourly{

            @SerializedName("temperature")
            public List<Temperature> dayTemperatureList;

            public class Temperature{

                public String value;

                public String datetime;
            }
        }

    }

}
