/*
 - Author: otakuchiyan
 - License: GNU GPLv3
 - Description: The impletion and interface class
 */

package io.github.otakuchiyan.dnsman;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public final class NativeCommandUtils implements ValueConstants{
    private NativeCommandUtils(){
    }

	public static int setDNSViaSetprop(String dns1, String dns2) {
		String[] setCommands = {
            SETPROP_COMMAND_PREFIX + "1 \"" + dns1 + '\"',
            SETPROP_COMMAND_PREFIX + "2 \"" + dns2 + '\"'
        };

        Shell.SU.run(setCommands);
        return 0;
    }

	public static int setDNSViaIPtables(String dns, String port){
        List<String> cmds = new ArrayList<>();

        String cmd1 = String.format(SETRULE_COMMAND, "-A", "udp", dns);
        String cmd2 = String.format(SETRULE_COMMAND, "-A", "tcp", dns);
        if(!port.equals("")){
            cmd1 += ":" + port;
            cmd2 += ":" + port;
        }

        cmds.add(cmd1);
        cmds.add(cmd2);

        Log.d("DNSManager.rules", cmd1);
        Log.d("DNSManager.rules", cmd2);

        return Shell.SU.run(cmds).isEmpty() ? 0 : ERROR_UNKNOWN;
    }

    public static int setDNSViaNdc(String netObject, String dns1, String dns2){
        // netObject contained netId and interface name
        List<String> cmds = new ArrayList<>();

        String setdns_cmd;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //>=5.0
            setdns_cmd = String.format(SETNETDNS_COMMAND, netObject, dns1, dns2);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){ //>=4.3
            setdns_cmd = String.format(SETIFDNS_COMMAND, netObject, dns1, dns2);
        }else{ //<=4.2
            setdns_cmd = String.format(SETIFDNS_COMMAND_BELOW_42, netObject, dns1, dns2);
        }

        Log.d("DNSManaget.ndc", setdns_cmd);
        cmds.add(setdns_cmd);

        List<String> result = Shell.SU.run(cmds);

        return result.get(0).equals("") ? 0 : ERROR_UNKNOWN;
    }

	public static boolean isRulesAlivable(String dns, String port){
		String cmd = CHECKRULE_COMMAND_PREFIX + dns;

		if(!port.equals("")){
			cmd += ":" + port;
        }

        Log.d("DNSManager[CMD]", cmd);
		return !Shell.SU.run(cmd).isEmpty();
	}

    public static List<String> deleteRules(String dns, String port){
        List<String> cmds = new ArrayList<>();
        String cmd1 = String.format(SETRULE_COMMAND, "-D", "udp", dns);
        String cmd2 = String.format(SETRULE_COMMAND, "-D", "tcp", dns);
        if(!port.equals("")){
            cmd1 += ":" + port;
            cmd2 += ":" + port;
        }

        cmds.add(cmd1);
        cmds.add(cmd2);

        Log.d("DNSManager.deleteRules", cmd1);
        Log.d("DNSManager.deleteRules", cmd2);
        return Shell.SU.run(cmds);
    }
	
	public static String writeResolvConfig(String dns1, String dns2, String path){
        List<String> cmds = new ArrayList<>();
        boolean isSystem = false;
		
        if(path.substring(0, 7).equals("/system") ||
            path.substring(0, 4).equals("/etc")){
            isSystem = true;
        }
        if(isSystem){
            cmds.add("mount -o remount,rw /system");
        }
		if(!dns1.equals("")){
		    cmds.add("echo nameserver " + dns1 + " > " + path);
		}
		if(!dns2.equals("")){
			cmds.add("echo nameserver " + dns2 + " >> " + path);
		}
        cmds.add("chmod 644 " + path);
        if(isSystem){
            cmds.add("mount -o remount,ro /system");
        }

        StringBuilder sb = new StringBuilder();
		for(String s : Shell.SU.run(cmds)){
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
	}
	
	public static String removeResolvConfig(String path){
        List<String> cmds = new ArrayList<>();
        boolean isSystem = false;

        if(path.substring(0, 7).equals("/system") ||
            path.substring(0, 4).equals("/etc")){
            isSystem = true;
        }

        if(isSystem){
            cmds.add("mount -o remount,rw /system");
        }

		cmds.add("rm " + path);

        if(isSystem){
			cmds.add("mount -o remount,ro /system");
        }

        StringBuilder sb = new StringBuilder();
		for(String s : Shell.SU.run(cmds)){
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
	}
	
	public static List<String> getCurrentPropDNS(){
		return Shell.SH.run(CHECKPROP_COMMANDS);
	}

    public static String getResolvConf(String path){
        StringBuilder sb = new StringBuilder();
        for(String s : Shell.SH.run("cat " + path + " | grep 'nameserver'")){
            sb.append(s.replace("nameserver", ""));
            sb.append("\n");
        }
        return sb.toString();
    }

    
}
