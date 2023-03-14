package com.srapp.Util;

import com.srapp.Db_Actions.URL;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Creadted BY tanvir3488 on 3/14/2023.
 */
public class JAPIClient {


        private static Retrofit retrofit = null;

        public static Retrofit getClient() {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();



            retrofit = new Retrofit.Builder()
                    .baseUrl(URL.Domain)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(client)
                    .build();



            return retrofit;
        }

    }

