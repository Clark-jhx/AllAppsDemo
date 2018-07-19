package com.clark.allapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApplicationsModel applicationsModel;
    private ApplicationsAdapter adapter;
    private ProgressBar progressBar;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApplicationsAdapter();
        recyclerView.setAdapter(adapter);
        applicationsModel = new ApplicationsModel(this);

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme("package");
        receiver = new Receiver();
        registerReceiver(receiver, intentFilter);

        start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void start() {
        applicationsModel.start();
    }

    //更新ui
    public void updateUI(boolean b){
        if (b){
            adapter.setData(applicationsModel.apps);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }


    class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationsAdapter.MyViewHolder>{

        private List<ApplicationsModel.AppInfo> apps = new ArrayList<>();

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ApplicationsModel.AppInfo appInfo = apps.get(position);
            holder.imageView.setImageDrawable(appInfo.icon);
            holder.textView_name.setText(appInfo.name);
            holder.textView_version.setText(appInfo.version);
            holder.textView_last_update_time.setVisibility(View.GONE);
            holder.textView_last_update_time.setText(String.valueOf(appInfo.last_update_time));
        }

        @Override
        public int getItemCount() {
            return apps.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            private ImageView imageView;
            private TextView textView_name;
            private TextView textView_version;
            private TextView textView_last_update_time;

            public MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.app_image);
                textView_name = itemView.findViewById(R.id.app_name);
                textView_version = itemView.findViewById(R.id.app_version);
                textView_last_update_time = itemView.findViewById(R.id.app_last_update_time);
            }
        }

        public void setData(List<ApplicationsModel.AppInfo> apps){
            //this.apps = apps;
            this.apps.clear();
            this.apps.addAll(apps);
        }

    }

    //应用更新|卸载..广播接收
    class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String packageName = intent.getDataString();
            ApplicationsModel.AppInfo appInfo = new ApplicationsModel.AppInfo();
            appInfo.package_name = packageName;

            if (Intent.ACTION_PACKAGE_ADDED.equals(action)){//安装
                Log.i("clark", "安装");

            }else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)){//卸载
                Log.i("clark", "卸载");

            }
//            else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)){//更新
//
//            }
            applicationsModel.start();
        }
    }

}
