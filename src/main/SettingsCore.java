package main;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SettingsCore {
    public static UIManager.LookAndFeelInfo[] allLooksInfo;
    public static boolean noVerifyNeeded = false;
    public static boolean confirmExit = true;
    public static boolean userType = true;//Not used yet
    public static String backgroundName = Globals.NONE;
    public static String lookName = UIManager.getLookAndFeel() == null ? "Metal" : UIManager.getLookAndFeel().getName();
    private static Date serializationTime;//All serializations are done at same time, so this property needs not be relative


    public static String[] getLookNames(){
        final String[] array = new String[allLooksInfo.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = allLooksInfo[i].getName();
        }
        return array;
    }

    public static Color currentBackground(){
        final LinkedHashMap<String, Color> hm = backgroundsMap();
        for (String s : hm.keySet()) {
            if (s.equals(backgroundName)) {
                return hm.get(s);
            }
        }
        return null;
    }

    public static String currentBackgroundName(){
        return backgroundName;
    }

    private static LinkedHashMap<String, Color> backgroundsMap(){
        final LinkedHashMap<String, Color> map = new LinkedHashMap<>();
        map.put("Default", null);
        map.put("White", Color.WHITE);
        map.put("Cyan", new Color(102, 255,255));
        map.put("Pink", Color.PINK);
        map.put("Green", Color.GREEN);
        map.put("Yellow", Color.YELLOW);
//        map.put("Black", Color.BLACK);
        return map;
    }

    public static String[] allBackgroundNames(){
        final Object[] bgs = backgroundsMap().keySet().toArray();
        final String[] names = new String[bgs.length];
        for (int i = 0; i < bgs.length; i++) {
            names[i] = (String) bgs[i];
        }
        return names;
    }

    public static String currentLookName(){
        return lookName;
    }

    public static Date getSerializationTime(){
        return serializationTime;
    }

    public static void serialize(){
        System.out.print("Serializing Settings... ");
        final HashMap<String, Object> coreMap = new HashMap<>();
        coreMap.put("verificationUnneeded",noVerifyNeeded);
        coreMap.put("directLeave", confirmExit);
        coreMap.put("uType",userType);
        coreMap.put("tipInitialDelay",ToolTipManager.sharedInstance().getInitialDelay());
        coreMap.put("tipDismissDelay",ToolTipManager.sharedInstance().getDismissDelay());
        coreMap.put("lafName",lookName);
        coreMap.put("bgName",backgroundName);
        coreMap.put("dateSerialized", new Date());
        Serializer.toDisk(coreMap, "settings.ser");
        System.out.println("Completed.");
    }

    public static void deSerialize() {
        System.out.print("Deserializing Settings... ");
        final HashMap<String, Object> coreMap = (HashMap<String, Object>) Serializer.fromDisk("settings.ser");
        if (coreMap == null) {
            System.err.println("Unsuccessful.");
            return;
        }
        serializationTime = (Date) coreMap.get("dateSerialized");
        noVerifyNeeded = Boolean.parseBoolean(String.valueOf(coreMap.get("verificationUnneeded")));
        confirmExit = Boolean.parseBoolean(String.valueOf(coreMap.get("directLeave")));
        userType = Boolean.parseBoolean(String.valueOf(coreMap.get("uType")));
        backgroundName = String.valueOf(coreMap.get("bgName"));
        lookName = String.valueOf(coreMap.get("lafName"));
        ToolTipManager.sharedInstance().setInitialDelay(Integer.parseInt(coreMap.get("tipInitialDelay").toString()));
        ToolTipManager.sharedInstance().setDismissDelay(Integer.parseInt(coreMap.get("tipDismissDelay").toString()));
        System.out.println("Completed successfully.");
    }

}
