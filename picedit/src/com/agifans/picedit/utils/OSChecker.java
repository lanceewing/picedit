package com.agifans.picedit.utils;

/**
 * Utility class for checking what operating system PICEDIT is running on.
 * 
 * @author Lance Ewing
 */
public class OSChecker {
    
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
 
    public static boolean isMac(){
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }
 
    public static boolean isUnix(){
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }
}
