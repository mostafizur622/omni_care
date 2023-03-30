package com.srapp.Util;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.RequestBuilder;
import com.srapp.Db_Actions.URL;
import com.srapp.print.newprint.App;
import com.tanvir.BasicFun.BasicFunction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okio.Buffer;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(15, TimeUnit.MINUTES)
                    .readTimeout(15, TimeUnit.MINUTES)
                    .writeTimeout(15, TimeUnit.MINUTES)
                    .addInterceptor(new Interceptor() {
                @NonNull
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {

                    try {
                        Request request = chain.request();
                        RequestBody oldBody = request.body();

                        Buffer buffer = new Buffer();
                        oldBody.writeTo(buffer);

                        String strOldBody = buffer.readUtf8();
                        Log.e("test", "intercept: "+chain.request().body().toString() );
                        JSONObject finaljsonObject = new JSONObject(strOldBody);


                        finaljsonObject.put("office_id", getPreference("office_id"));
                        finaljsonObject.put("distributor_id", getPreference("db_id"));
                        finaljsonObject.put("store_id", getPreference("store_id"));
                        finaljsonObject.put("sales_representative_id", getPreference("sr_id"));
                        finaljsonObject.put("sales_representative_code", getPreference("sr_code"));
                        finaljsonObject.put("territory_id", getPreference("territory_id"));
                        finaljsonObject.put("tso_id", getPreference("tso_id"));
                        finaljsonObject.put("ae_id", getPreference("ae_id"));

                        Request.Builder rb =chain.request().newBuilder();


                        Request r = rb.post(
                                RequestBody.create(
                                        MediaType.get("application/json; charset=utf-8"),finaljsonObject.toString()
                                )
                        ).build();

                        Log.e("request", finaljsonObject.toString() );
                     Response response =   chain.proceed(r);
                        return response;
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }



                }
            }).build();




            retrofit = new Retrofit.Builder()
                    .baseUrl(URL.Domain)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(client)
                    .build();



            return retrofit;
        }



    public static String getPreference(String key)
    {
        String value="";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        value = prefs.getString(key, "null");

        return value;

    }

    }

