package io.github.otakuchiyan.dnsman;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;

public class DnsmanCore implements ValueConstants{
    public static ArrayList<NetworkInfo> supportedNetInfoList = new ArrayList<>();

    //Don't use NetworkInfo as key because it's complex structure
    public static HashMap<String, Integer> info2resMap = new HashMap<>();
    public static HashMap<String, String> info2interfaceMap = new HashMap<>();
    public static HashMap<String, Integer> method2resMap = new HashMap<>();
    public static HashMap<Integer, Integer> code2resMap = new HashMap<>();
    public static HashMap<String, String[]> server2ipMap = new HashMap<>();

    private SharedPreferences preferences;
    private SharedPreferences.Editor preferenceEditor;

    public DnsmanCore(Context c){
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
        preferenceEditor = preferences.edit();
    }

    public static void refreshInfo2InterfaceMap(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        for(int i = 0; i != ValueConstants.NET_TYPE_LIST.length; i++){
            NetworkInfo info = manager.getNetworkInfo(ValueConstants.NET_TYPE_LIST[i]);
            if(info != null) {
                info2interfaceMap.put(info.getTypeName(),
                        preferences.getString(
                                ValueConstants.KEY_CUSTOM_INTERFACES[i],
                                ValueConstants.NETWORK_INTERFACES[i]));
            }
        }

    }

    public static void initResourcesMap(){
        for(int i = 0; i != METHODS.length; i++){
            method2resMap.put(METHODS[i], METHOD_RESOURCES[i]);
        }
        for(int i = 0; i != RESPONSE_CODES.length; i++){
            code2resMap.put(RESPONSE_CODES[i], RESPONSE_RESOURCES[i]);
        }
    }

    private static void initServerMap(Context c){
        String[] serverNames = c.getResources().getStringArray(R.array.server_strings);
        String[] serverIp = c.getResources().getStringArray(R.array.server_values);
        for(int i = 0; i != serverNames.length; i++){
            String[] ips = serverIp[i].split("\\|", 2);
            server2ipMap.put(serverNames[i], ips);
        }
    }


    public static void initDnsMap(Context context){
        //Keep build one time
        if(supportedNetInfoList.size() == 0) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            for (int i = 0; i != ValueConstants.NET_TYPE_LIST.length; i++) {
                NetworkInfo info = manager.getNetworkInfo(ValueConstants.NET_TYPE_LIST[i]);
                if (info != null) {
                    info2resMap.put(info.getTypeName(), ValueConstants.NET_TYPE_RESOURCES[i]);
                    supportedNetInfoList.add(info);
                }
            }
            initServerMap(context);
            refreshInfo2InterfaceMap(context);
        }
    }


    public String[] getDnsByNetInfo(NetworkInfo info){
        return getDnsByKeyPrefix(info.getTypeName());
    }

    public String[] getGlobalDns(){
        return getDnsByKeyPrefix("g");
    }

    //To compatible old version
    public String[] getDnsByKeyPrefix(String keyPrefix){
        String[] dnsEntry = new String[2];
        for(int i = 0; i != 2; i++) {
            dnsEntry[i] = preferences.getString(keyPrefix + Integer.toString(i), "");
        }
        return dnsEntry;
    }

    public void putGlobalDns(String[] dnsEntry){
        putDnsByKeyPrefix("g", dnsEntry);
    }

    public void putDnsByKeyPrefix(String keyPrefix, String[] dnsEntry){
        for(int i = 0; i != 2; i++){
            preferenceEditor.putString(keyPrefix + Integer.toString(i),
                    dnsEntry[i]);
        }
        preferenceEditor.apply();
    }
}