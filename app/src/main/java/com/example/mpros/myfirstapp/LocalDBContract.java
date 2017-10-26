package com.example.mpros.myfirstapp;

import android.provider.BaseColumns;

public final class LocalDBContract {
    private LocalDBContract() {}

    public static class LocationInfo implements BaseColumns {
        public static final String TABLE_NAME = "LocationInfo";
        public static final String COL_NAME_TIME = "Time";
        public static final String COL_NAME_LAT = "Latitude";
        public static final String COL_NAME_LONG = "Longitude";
        public static final String COL_NAME_PROVIDER = "Provider";
    }
}
