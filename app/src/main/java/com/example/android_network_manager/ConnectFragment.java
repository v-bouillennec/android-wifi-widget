package com.example.android_network_manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link ConnectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "title";
    private static final String ARG_PARAM2 = "description";
    private static final String ARG_PARAM3 = "SSID";

    // TODO: Rename and change types of parameters
    private String title;
    private String description;
    private String SSID;

    public ConnectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @param description Parameter 2.
     * @param SSID Parameter 3.
     * @return A new instance of fragment ConnectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConnectFragment newInstance(String title, String description, String SSID) {
        ConnectFragment fragment = new ConnectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putString(ARG_PARAM2, description);
        args.putString(ARG_PARAM3, SSID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
            description = getArguments().getString(ARG_PARAM2);
            SSID = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText edTxt = view.findViewById(R.id.editTextPassword);
        Button btn = view.findViewById(R.id.connect_to_wifi_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.setWifiConf(SSID, edTxt.getText().toString());
                WifiManager wifiManager1 = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
                String wnetworkSSID = SSID.replace("🟢", "");
                String wnetworkPass = edTxt.getText().toString();
                Log.i("log", "SSID is : " + wnetworkSSID + "\npassword is : " + wnetworkPass);

                WifiConfiguration wconf = new WifiConfiguration();
                wconf.SSID = String.format("\"%s\"", wnetworkSSID);
                wconf.preSharedKey = String.format("\"%s\"", wnetworkPass);

                ConnectivityManager connManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                int wnetId = wifiManager1.addNetwork(wconf);
//                wifiManager1.disconnect();
                wifiManager1.enableNetwork(wnetId, true);
//                wifiManager1.reconnect();

                dismiss();
            }
        });
    }
}