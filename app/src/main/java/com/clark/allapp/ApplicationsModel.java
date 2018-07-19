package com.clark.allapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by clark on 2018/7/18.
 */

public class ApplicationsModel {
    private static String TAG = ApplicationsModel.class.getSimpleName();
    private boolean debug = false;
    public Context mContext;
    public List<AppInfo> apps = new ArrayList<>();
    public PackageManager pm;

    public ApplicationsModel(Context context){
        mContext = context;
        pm = mContext.getPackageManager();
    }

    public void start(){
        apps.clear();
        new LoadAppsInfoTask().execute();
    }

    public AppInfo getAppInfo(String packageName) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        return inflateAppInfo(packageInfo);
    }

    public AppInfo inflateAppInfo(PackageInfo packageInfo){
        AppInfo appInfo = new AppInfo();
        appInfo.icon = packageInfo.applicationInfo.loadIcon(mContext.getPackageManager());
        appInfo.name = packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
        appInfo.version = packageInfo.versionName;
        appInfo.package_name = packageInfo.packageName;
        appInfo.last_update_time = packageInfo.lastUpdateTime;
        return appInfo;
    }

    public void getAllApplications(){
        if(apps.size()>0){
            apps.clear();
        }
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages){
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                //非系统应用
                apps.add(inflateAppInfo(packageInfo));
            }else{
                // 系统应用
            }
        }
        //排序
        sort(apps);
    }

    public void sort(List<AppInfo> apps){
        Collections.sort(apps, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo appInfo1, AppInfo appInfo2) {
                if (appInfo1.last_update_time > appInfo2.last_update_time){
                    return -1;
                }else if (appInfo1.last_update_time < appInfo2.last_update_time){
                    return 1;
                }else{
                    return 0;
                }
            }
        });
    }

    class LoadAppsInfoTask extends AsyncTask<Object, Object, List<AppInfo>>{
        private boolean ing = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((MainActivity)mContext).updateUI(false);
        }

        @Override
        protected List<AppInfo> doInBackground(Object[] objects) {
            getAllApplications();
            //打印
            if (debug){
                for (AppInfo appInfo : apps){
                    Log.i(TAG, "doInBackground: time " + appInfo.last_update_time);
                }
            }
            return apps;
        }

        @Override
        protected void onPostExecute(List<AppInfo> appInfos) {
            super.onPostExecute(appInfos);
            Log.i(TAG, "onPostExecute: size " + appInfos.size());
            ((MainActivity)mContext).updateUI(true);
        }
    }

    static class AppInfo {
        public String package_name;
        public String name;
        public Drawable icon;
        public String version;
        public long last_update_time;

        @Override
        public boolean equals(Object obj) {
            if (this.package_name.equals(((AppInfo)obj).package_name)){
                return true;
            }
            return super.equals(obj);
        }
    }
}
