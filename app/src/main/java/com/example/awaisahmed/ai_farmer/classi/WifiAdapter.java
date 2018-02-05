package com.example.awaisahmed.ai_farmer.classi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.awaisahmed.ai_farmer.R;

import java.util.List;

/**
 * Created by Awais Ahmed on 11/01/2018.
 */

public class WifiAdapter extends BaseAdapter {
    private Context context;
    private List<ScanResult> wifilist;

    public WifiAdapter(Context context, List<ScanResult> scanlist) {
        this.context = context;
        this.wifilist = scanlist;
    }
    @Override
    public int getCount() {
        int size;
        size = wifilist.size();
        return size;
    }
    //-->
    @Override
    public Object getItem(int position) {
        return wifilist.get(position).SSID;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    //<--
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View wifiView = inflater.inflate(R.layout.list_row_view, viewGroup, false);
        TextView title = wifiView.findViewById(R.id.title);
        //TextView subtitle = wifiView.findViewById(R.id.subtitle);
        ScanResult result = wifilist.get(position);
        String ssid = result.SSID;
        //String bssid = result.BSSID;
        title.setText(ssid);
        //subtitle.setText(bssid);
        return wifiView;
    }
}