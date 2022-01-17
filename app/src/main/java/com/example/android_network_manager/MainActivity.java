package com.example.android_network_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private WifiManager wifiManager;
    TextView txt;
    ListView listWifiView;
    ListView listRegisteredWifiView;
    List<ScanResult> results = new ArrayList<>();
    List<WifiConfiguration> listWifiConf = new ArrayList<>();
    String wnetworkSSI = "";
    String wnetworkPass = "";

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listWifiView = (ListView) findViewById(R.id.wifi_list);
        listRegisteredWifiView = (ListView) findViewById(R.id.registred_wifi_list);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        txt = (TextView) findViewById(R.id.text);

        Button forget_wifi_btn = (Button) findViewById(R.id.registred_wifi_btn);
        FloatingActionButton popup_btn = (FloatingActionButton) findViewById(R.id.popup_btn);

        forget_wifi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(i);
            }
        });

        popup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOverlayPermission();
                if (isMyServiceRunning()) {
                    stopService(new Intent(MainActivity.this, ForegroundService.class));
                }
                startService();
            }
        });

        // Check si WiFi actif, sinon activer
        if (wifiManager.isWifiEnabled())
            Toast.makeText(getApplicationContext(), "WiFi already ON...", Toast.LENGTH_LONG).show();
        else {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        listWifiConf = wifiManager.getConfiguredNetworks();

        List<String> listRegisteredSSIDtoRemove = new ArrayList<>();
        for(WifiConfiguration registredWifi : listWifiConf) {
            String currentSSID = registredWifi.SSID.replace("\"", "");
            if(!currentSSID.contains("city"))
                listRegisteredSSIDtoRemove.add(currentSSID);
        }
        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, R.layout.activity_listview, R.id.textView, listRegisteredSSIDtoRemove);
        listRegisteredWifiView.setAdapter(adapter2);

        // Scan des WiFis
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);
        boolean success = wifiManager.startScan();// fonction à changer

        listWifiView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter wifiList = (ArrayAdapter) adapterView.getAdapter();
                showPasswordDialog((String) wifiList.getItem(i));
            }
        });

//        if(!certifIsPresent()){
//            if(isConnectedToWIFI()){
//                Log.i("log", "is connected");
//                try {
//                    downloadFile();
//                    getCertificate();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Log.i("log", "is not connected");
//                String wnetworkSSID = "wifi wpa2";
//                String wnetworkPass = "citypass";
//
//                WifiConfiguration wconf = new WifiConfiguration();
//                wconf.SSID = String.format("\"%s\"", wnetworkSSID);
//                wconf.preSharedKey = String.format("\"%s\"", wnetworkPass);
//
//                int wnetId = wifiManager.addNetwork(wconf);
////                wifiManager.disconnect();
//                if(wifiManager.enableNetwork(wnetId, true)){
//                    try {
//                        downloadFile();
//                        getCertificate();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
////                wifiManager.reconnect();
////                wifiManager.removeNetwork(wnetId);
//            }
//        } else {
//            getCertificate();
//        }

//        String networkSSID = "WIFI prive val";
////        String networkPass = "valentin";
//
//        WifiConfiguration conf = new WifiConfiguration();
//        conf.SSID = String.format("\"%s\"", networkSSID);
//        conf.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.TLS);
//        conf.enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.NONE);
//        conf.enterpriseConfig.setCaCertificate(null);
//        conf.enterpriseConfig.setIdentity("valentin");

//        PrivateKey key = null;
//        X509Certificate cert = null;

//        conf.enterpriseConfig.setClientKeyEntry(key, cert);
//        Log.i("certif", String.valueOf(conf.enterpriseConfig.getClientCertificate()));
//

//        wifiManager.reconnect();
//
//        wifiManager.removeNetwork(netId);
//        wifiManager.removeNonCallerConfiguredNetworks();

//        WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
//                .setSsid(networkSSID)
//                .setWpa2Passphrase(networkPass)
//                .build();

//        Log.i("wifi", wifiNetworkSpecifier.toString());
//
//        NetworkRequest request = new NetworkRequest.Builder()
//                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//                .setNetworkSpecifier(wifiNetworkSpecifier)
//                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
//                .build();
//}

//    @RequiresApi(api = Build.VERSION_CODES.Q)
//    public void requestNetwork(final String ssId) {
//        WifiNetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
//                .setSsid(ssId)
//                .setWpa2Passphrase(parsePassword(ssId))
//                .build();
//        NetworkRequest request = new NetworkRequest.Builder()
//                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//                .setNetworkSpecifier(specifier)
//                .build();
//        requestNetwork(ssId, request);
    }

    private void showPasswordDialog(String SSID) {
        DialogFragment newFragment = ConnectFragment.newInstance("titre", "description", SSID);
        newFragment.show(getSupportFragmentManager(), "dialog");

//        EditText edTxt = findViewById(R.id.editTextPassword);
//        Button btn = findViewById(R.id.connect_to_wifi_btn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i("log", "password is : " + edTxt.getText().toString());
//            }
//        });
    }

    private boolean isConnectedToWIFI() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getActiveNetworkInfo() != null && connManager.getActiveNetworkInfo().isConnected();
    }

    public static void setWifiConf(String ssid, String password) {

    }

     static boolean wpa2Connection(WifiManager wifiManager, String ssid, String password){
//        WifiManager wifiManager1 = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String wnetworkSSID = ssid;
        String wnetworkPass = password;

        WifiConfiguration wconf = new WifiConfiguration();
        wconf.SSID = String.format("\"%s\"", wnetworkSSID);
        wconf.preSharedKey = String.format("\"%s\"", wnetworkPass);

        int wnetId = wifiManager.addNetwork(wconf);
//                wifiManager.disconnect();
        if(wifiManager.enableNetwork(wnetId, true)){
//            try {
//                downloadFile();
//                getCertificate();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
            return true;
        } else {
            return false;
        }
//                wifiManager.reconnect();
    }

    private boolean certifIsPresent() {
        File file = new File(getApplicationContext().getExternalFilesDir("cert").getAbsolutePath(), "valentin.p12");
        if(file.exists()){
            Log.d("log", "le fichier est sur l'appareil");
            return true;
        } else {
            Log.d("log", "le fichier n'est pas sur l'appareil");
            return false;
        }
    }

    public void downloadFile() throws FileNotFoundException {
        String DownloadUrl = "https://code.citypassenger.com/stuff/file/valentin.p12";
        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(DownloadUrl));
        request1.setDescription("Client Cert File");   //appears the same in Notification bar while downloading
        request1.setVisibleInDownloadsUi(true);
        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request1.setDestinationInExternalFilesDir(getApplicationContext(), "cert", "valentin.p12");
//        Log.d("log", Environment.DIRECTORY_DOWNLOADS);
//        request1.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "valentin.p12");

        DownloadManager manager1 = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Objects.requireNonNull(manager1).enqueue(request1);

        if (DownloadManager.STATUS_SUCCESSFUL == 8) {
            Log.d("log", "cert downloaded sucessfully");
        } else {
            Log.d("log", "cert download failed !");
        }
    }

    private void getCertificate() {
        X509Certificate crt = null;
        PrivateKey pKey = null;

        String networkSSID = "WIFI prive val";
//        String networkPass = "valentin";

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = String.format("\"%s\"", networkSSID);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
        conf.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.TLS);
        conf.status = WifiConfiguration.Status.ENABLED;
        conf.enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.NONE);
        conf.enterpriseConfig.setCaCertificate(null);
        conf.enterpriseConfig.setIdentity("valentin");

        try {
            File cert = new File(getApplicationContext().getExternalFilesDir("cert").getAbsolutePath(), "valentin.p12");
            Log.i("log", cert.getAbsolutePath());
            char[] password = "valentin".toCharArray();
            InputStream inputStreamFromDownload = null;
            inputStreamFromDownload = new BufferedInputStream(new FileInputStream(cert));
//            FileInputStream fIn = new FileInputStream("<name of cert>");

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
//            keyStore.load(null, null);
            keyStore.load(inputStreamFromDownload, password);
            Enumeration<String> aliases = keyStore.aliases();
            Log.i("log",inputStreamFromDownload.available() + "");

            while(aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                crt = (X509Certificate) keyStore.getCertificate(alias);
                pKey = (PrivateKey) keyStore.getKey(alias, password);
                Log.i("log", crt.getIssuerDN().getName());
                conf.enterpriseConfig.setClientKeyEntry(pKey, crt);
                Log.d("log", crt.toString() + " & " + pKey.toString());
            }

            int netId = wifiManager.addNetwork(conf);
//        wifiManager.disconnect();
            boolean succ = wifiManager.enableNetwork(netId, true);
            if(!succ)
                Log.i("log", "connexion réussie");
            else
                Log.i("log", "connexion échouée");

        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
    }

    // Récupère données WiFis existants
    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success)
                scanSuccess();
            else
                scanFailure();
        }
    };

    // Si récupération réussie
    private void scanSuccess() {
        List<String> listSSID = new ArrayList<>();
        results = wifiManager.getScanResults();
        for (ScanResult scan : results) {
            if(scan.SSID.toLowerCase().contains("city") && scan.frequency < 5000)
                listSSID.add("🟢"+scan.SSID+"🟢");
            else if(scan.frequency < 5000)
                listSSID.add(scan.SSID);
        }
        ArrayAdapter adapter2 = new ArrayAdapter<>(this, R.layout.activity_listview, R.id.textView, listSSID);
        listWifiView.setAdapter(adapter2);
    }

    // Si récupération échouée
    private void scanFailure() {
        Log.i("log", "echec");
        if(!results.isEmpty())
            return;
        txt.setText("Echec de l'obtention des réseaux Wi-Fi");
    }

    // Check des permissions
    @Override
    public void onResume()
    {
        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 87);
            }
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // method for starting the service
    public void startService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if(Settings.canDrawOverlays(this)) {
                // start the service based on the android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(this, ForegroundService.class));
                } else {
                    startService(new Intent(this, ForegroundService.class));
                }
            }
        }else{
            startService(new Intent(this, ForegroundService.class));
        }
    }

    // method to ask user to grant the Overlay permission
    public void checkOverlayPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
            }
        }
    }
}