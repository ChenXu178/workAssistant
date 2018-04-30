package com.chenxu.workassistant.photoRecognition;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LexerModel implements PhotoRecognitionContract.Model {

    private static final String TAG = "LexerModel";
    private PhotoRecognitionContract.Presenter mPresenter;

    public LexerModel(PhotoRecognitionContract.Presenter presenter){
        this.mPresenter = presenter;
    }

    @Override
    public void toLexer(String text) {
        Gson gson = new Gson();
        String paramsString = gson.toJson(text);
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, paramsString);
        Request request = new Request.Builder()
                .url("http://api.bosonnlp.com/tag/analysis?space_mode=0&oov_level=3&t2s=0&&special_char_conv=0")
                .addHeader("Content-Type","application/json")
                .addHeader("Accept","application/json")
                .addHeader("X-Token","_T7G9xzp.24935.IzUa28V5FIQJ")
                .post(body)
                .build();
        OkHttpClientInstance.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure","错误");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String result = response.body().string();
                Log.e("onResponse",result);
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject object = jsonArray.getJSONObject(0);
                    JSONArray list = object.getJSONArray("word");
                    Log.e("list",list.toString());
                    Message message = new Message();
                    message.what = 1;
                    message.obj = list;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(2);
                }
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    mPresenter.dataHandle((JSONArray)msg.obj);
                    break;
                case 2:
                    mPresenter.onError(000000);
                    break;
            }
        }
    };
}
