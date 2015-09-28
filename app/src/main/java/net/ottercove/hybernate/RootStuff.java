package net.ottercove.hybernate;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by ottah on 9/26/15.
 */
public class RootStuff {

    public static Object[] runCommand(Boolean root, String command) {
        Object[] result = new Object[]{-1, ""};
        Process p = null;

        try {
            if(root) {
                p = Runtime.getRuntime().exec("su");
                DataOutputStream outputstream = new DataOutputStream(p.getOutputStream());
                outputstream.writeBytes(command + "\n");
                outputstream.writeBytes("exit\n");
                outputstream.flush();
            } else {
                p = Runtime.getRuntime().exec(command);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }

            result[1] = builder.toString();
            result[0] = p.waitFor();
        } catch (IOException e) {
            Log.println(Log.DEBUG, "HYBERNATE", e.toString());
        } catch (InterruptedException e) {
            Log.println(Log.DEBUG, "HYBERNATE", e.toString());
        }

        return  result;
    }

    public static Object[] runCommand(String command) {
        return  runCommand(true, command);
    }

    public static boolean isProcessRunning(String command) {
        Object[] result = RootStuff.runCommand(false, "ps");
        String text = (String) result[1];

        return text.contains(command);
    }

    public static boolean rootGiven() {
        Object[] result = runCommand("echo I am root.");
        return (int) result[0] == 0;
    }

    public static boolean findBinary(String binaryName) {
        String[] places = { "/sbin/",
                "/system/bin/",
                "/system/xbin/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/" };
        for (String where : places) {
            if (new File(where + binaryName).exists()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRooted() {
        return findBinary("su");
    }
}
