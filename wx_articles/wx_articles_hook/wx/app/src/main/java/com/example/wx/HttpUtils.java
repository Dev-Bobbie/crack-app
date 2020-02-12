package com.example.wx;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.HashMap;
import java.util.Set;

public class HttpUtils {
    public static void post(String url, HashMap<String, String> paramsMap){

        OkHttpClient mOkHttpClient = new OkHttpClient();
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Set<String> keySet = paramsMap.keySet();
        for(String key:keySet) {
            String value = paramsMap.get(key);
            formBodyBuilder.add(key, value);
        }
        FormBody formBody = formBodyBuilder.build();
        Request request = new Request
                .Builder()
                .post(formBody)
                .url(url)
                .build();

        try (Response response = mOkHttpClient.newCall(request).execute()) {
            System.out.println(response.body());
        }catch (Exception e){
            System.out.println("-------------------");
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String mm = "{article={name=凹凸数据, cover=https://mmbiz.qpic.cn/mmbiz_jpg/tXYict40xfLgBI5UtlST8f84TCCQcMiaiaicO1G9oMZyObwZCxKAH8fZnFFqZjBC7ORX8l6z7Ir9EOEibWWkt3MJcfg/640?wxtype=jpeg&wxfrom=0, title=对比MySQL学习Pandas的groupby分组聚合, time=2020-02-11 20:20:33, url=http://mp.weixin.qq.com/s?__biz=MzU5Nzg5ODQ3NQ==&mid=2247484480&idx=1&sn=b9e948849d8f520c3fa9b9c8f9428216&chksm=fe4d21c4c93aa8d244529f50e9b3ff4adc752977731454ca05f1eda74ebe942f9cbb05eeb58a&scene=0&xtrack=1#rd}}";
//                HashMap<String,String> params = new HashMap<String, String>() ;
//                params.put("article", mm);
//                post("http://106.14.158.63:8080/wxapi", params);
//            }
//        }).start();
//    }
}