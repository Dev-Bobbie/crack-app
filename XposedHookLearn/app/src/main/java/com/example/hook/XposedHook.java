package com.example.hook;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedHook implements IXposedHookLoadPackage{
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable{
        if (loadPackageParam.packageName.equals("com.example.hook")){
            XposedBridge.log("hooking...");
            // 导入需要hook的类
            Class cls = loadPackageParam.classLoader.loadClass("com.example.hook.MainActivity");
            // 找到目标方法并hook
            XposedHelpers.findAndHookMethod(cls, "message", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object obj = param.getResult();
                    XposedBridge.log(obj.toString());
                    param.setResult("biubiubiu");
                }
            });
        }
    }
}
