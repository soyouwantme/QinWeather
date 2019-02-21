package com.kk.qinweather.gson;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ForecastWeek {

    public String status;

    @SerializedName("result")
    public Week week;

    public class Week{

        @SerializedName("daily")
        public Daily daily;

        public class Daily{

            @SerializedName("temperature")
            public List<Temperature> temperaturesList;

            @SerializedName("skycon")
            public List<Skycon> skyconList;

            public class Temperature{

                public String date;

                public String max;

                public String avg;

                public String min;
            }

            public class Skycon{

                public String date;

                public String value;
            }
        }
    }


}
