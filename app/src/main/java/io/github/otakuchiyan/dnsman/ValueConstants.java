package io.github.otakuchiyan.dnsman;

import android.net.NetworkInfo;

import java.util.HashMap;
import java.lang.Integer;

/**
 * Created by 西行寺幽玄 on 3/2/2016.
 */
public interface ValueConstants {
    String[] DEFAULT_DNS_LIST = {
            "127.0.0.1",
            "192.168.0.1",
            "192.168.100.1",
            "8.8.8.8",
            "8.8.4.4",
            "208.67.222.222",
            "208.67.220.220"
    };

    String KEY_DNS_LIST = "dns_list";
    String KEY_PREF_AUTO_SETTING = "pref_auto_setting";
    String KEY_PREF_FULL_KEYBOARD = "pref_full_keyboard";
    String KEY_PREF_INDIVIDUAL_MODE = "pref_individual_mode";

    String KEY_PREF_METHOD = "pref_method";
    String METHOD_VPN = "vpn";
    String METHOD_ACCESSIBILITY = "accessibility";
    String METHOD_NDC = "ndc";
    String METHOD_IPTABLES = "iptables";
    String METHOD_MODULE = "module";
    String METHOD_SETPROP = "setprop";

    String EXTRA_DNS1 = "extra.DNS1";
    String EXTRA_DNS2 = "extra.DNS2";

    //prop
    String SETPROP_COMMAND_PREFIX = "setprop net.dns";
    String GETPROP_COMMAND_PREFIX = "getprop net.dns";

    String[] CHECKPROP_COMMANDS = {
            GETPROP_COMMAND_PREFIX + "1",
            GETPROP_COMMAND_PREFIX + "2"
    };

    //IPTABLES mode
    String SETRULE_COMMAND = "iptables -t nat %s OUTPUT -p %s --dport 53 -j DNAT --to-destination %s\n";
    String CHECKRULE_COMMAND_PREFIX = "iptables -t nat -L OUTPUT | grep ";

    //NDC mode
    String NDC_COMMAND_PREFIX = "ndc resolver";
    String SETIFDNS_COMMAND_BELOW_42 = NDC_COMMAND_PREFIX + " setifdns %s %s %s\n";
    String SETIFDNS_COMMAND = NDC_COMMAND_PREFIX + " setifdns %s '' %s %s\n";
    String SETNETDNS_COMMAND = NDC_COMMAND_PREFIX + " setnetdns %s '' %s %s\n";
    String SETDEFAULTIF_COMMAND = NDC_COMMAND_PREFIX + " setdefaultif";

    String FLUSHNET_COMMAND = NDC_COMMAND_PREFIX + " flushnet %s\n";
    String FLUSHDEFAULTIF_COMMAND = NDC_COMMAND_PREFIX + " flushdefaultif\n";

    //0 is no error
    int ERROR_SETPROP_FAILED = 1;
    int ERROR_UNKNOWN = 9999;
    int ERROR_NO_DNS = 2;

    int REQUEST_DNS_CHANGE = 0x00;
    int REQUEST_VPN = 0x01;
}
