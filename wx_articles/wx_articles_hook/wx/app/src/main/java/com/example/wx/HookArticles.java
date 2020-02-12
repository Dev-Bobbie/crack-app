package com.example.wx;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HookArticles implements IXposedHookLoadPackage{
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable{
        try {
            if (loadPackageParam.packageName.equals("com.tencent.mm")){
                XposedBridge.log("hooking...");
                // 导入需要hook的类
                Class cls = loadPackageParam.classLoader.loadClass("com.tencent.mm.sdk.platformtools.az");
                // 找到目标方法并hook
                XposedHelpers.findAndHookMethod(cls, "Yd",  String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("wx_article: hook begin!");
                        HashMap<String, String> map1 = new HashMap<String, String>();
                        HashMap<String, String> map2 = new HashMap<String, String>();
                        HashMap<String, String> map3 = new HashMap<String, String>();
                        HashMap<String, String> map4 = new HashMap<String, String>();
                        HashMap<String, String> map5 = new HashMap<String, String>();
                        List<HashMap<String, String>> article_sets =new ArrayList<HashMap<String, String>>();
                        article_sets.add(map1);
                        article_sets.add(map2);
                        article_sets.add(map3);
                        article_sets.add(map4);
                        article_sets.add(map5);
                        Map<String,String> cond = (Map<String, String>) param.getResult();
                        for (String key : cond.keySet()) {
                            int article_idx = 0;
                            try{
                                String key_find = key.split("\\.")[5];
                                String article_idx_str = key_find.substring(key_find.length()-1,key_find.length());
                                article_idx = Integer.parseInt(article_idx_str);
                            }catch (Exception e){
                            }
                            String article_type = "";
                            String article_value = "";
                            if (key.contains("item") && key.contains("source.name")){
                                article_type = "name";
                                article_value = cond.get(key);
                            }else if (key.contains("item") && key.contains("pub_time")) {
                                article_type = "time";
                                article_value = cond.get(key);
                                if (article_value != null && !article_value.equals("")){
                                    article_value = stampToDate(article_value);
                                }
                            } else if (key.contains("item") && key.contains("title")){
                                article_type = "title";
                                article_value = cond.get(key);
                            }
                            else if (key.contains("item") && key.contains("url")){
                                article_type = "url";
                                article_value = cond.get(key);
                            } else if (key.contains("item") && key.contains("cover")){
                                article_type = "cover";
                                article_value = cond.get(key);
                            }
                            if (article_type != null && article_value != null && !article_type.equals("") && !article_value.equals("") && !article_value.isEmpty()){
                                article_sets.get(article_idx).put(article_type, article_value);
                            }
                        }
                        for (int i=0; i < article_sets.size(); i++){
                            HashMap<String, String> map =  article_sets.get(i);
                            if (map.isEmpty()){
                                continue;
                            }
                            for (String key: map.keySet()){
                                String value = map.get(key);
                                if(key.equals("title")){
                                    XposedBridge.log(String.format("wx_article[文章标题]: %s", value));
                                }else if (key.equals("name")){
                                    XposedBridge.log(String.format("wx_article[文章作者]: %s", value));
                                }else if(key.equals("time")){
                                    XposedBridge.log(String.format("wx_article[发布时间]: %s", value));
                                }else if (key.equals("url")){
                                    XposedBridge.log(String.format("wx_article[文章链接]: %s", value));
                                }else if(key.equals("cover")){
                                    XposedBridge.log(String.format("wx_article[文章封面]: %s", value));
                                }
                            }
                            HashMap<String, String> params = new HashMap<String, String>() ;
                            XposedBridge.log(String.format("wx_article: %s", map.toString()));
                            params.put("article", map.toString());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    HttpUtils.post("http://106.14.158.63:8080/wxapi", params);
                                }
                            }).start();
                        }
                        XposedBridge.log("wx_article: hook end!");
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String stampToDate(String s){
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(s+"000")));
    }
}