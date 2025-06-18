package com.thebanditofred.utils;

import java.io.*;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages application data directories and file locations.
 * Handles proper separation of application files and user data.
 */
public class AppDataManager {
    private static final Logger logger = Logger.getLogger(AppDataManager.class.getName());
    private static String appName = "Default App"; // Default value, can be changed
    
    /**
     * Sets the application name used for directory paths.
     * This should be called before any other methods to ensure proper directory naming.
     * 
     * @param name The application name to use for directories
     */
    public static void setAppName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            appName = name.trim();
        }
    }
    
    /**
     * Gets the current application name.
     * 
     * @return The current application name
     */
    public static String getAppName() {
        return appName;
    }
    
    /**
     * Gets the user data directory where JSON files and user-modifiable data are stored.
     * Windows: %LOCALAPPDATA%/[AppName]/data
     * macOS: ~/Library/Application Support/[AppName]/data
     * Linux: ~/.config/[AppName]/data
     */
    public static String getUserDataDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        String dataDir;
        
        if (os.contains("win")) {
            dataDir = System.getenv("LOCALAPPDATA") + File.separator + appName + File.separator + "data";
        } else if (os.contains("mac")) {
            dataDir = System.getProperty("user.home") + "/Library/Application Support/" + appName + "/data";
        } else {
            dataDir = System.getProperty("user.home") + "/.config/" + appName + "/data";
        }
        
        // Ensure directory exists
        try {
            Files.createDirectories(Paths.get(dataDir));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to create data directory: " + dataDir, e);
        }
        
        return dataDir;
    }
    
    /**
     * Gets the logs directory.
     */
    public static String getLogsDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        String logsDir;
        
        if (os.contains("win")) {
            logsDir = System.getenv("LOCALAPPDATA") + File.separator + appName + File.separator + "logs";
        } else if (os.contains("mac")) {
            logsDir = System.getProperty("user.home") + "/Library/Application Support/" + appName + "/logs";
        } else {
            logsDir = System.getProperty("user.home") + "/.config/" + appName + "/logs";
        }
        
        // Ensure directory exists
        try {
            Files.createDirectories(Paths.get(logsDir));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to create logs directory: " + logsDir, e);
        }
        
        return logsDir;
    }
    
    /**
    * Initializes user data files by copying from application resources if they don't exist.
    * 
    * @param resourceFiles Resource files to initialize
    */
   public static void initializeUserDataFiles(String... resourceFiles) {
       String userDataDir = getUserDataDirectory();

       for (String resourceFile : resourceFiles) {
           String fileName = Paths.get(resourceFile).getFileName().toString();
           initializeDataFile(resourceFile, userDataDir + File.separator + fileName);
       }

       logger.log(Level.INFO, "User data files initialized in: " + userDataDir);
   }
    
    /**
     * Copies a resource file to user data directory if it doesn't already exist.
     */
    private static void initializeDataFile(String resourcePath, String targetPath) {
        Path targetFilePath = Paths.get(targetPath);
        
        // Only copy if file doesn't exist (preserve user modifications)
        if (!Files.exists(targetFilePath)) {
            try (InputStream inputStream = AppDataManager.class.getResourceAsStream(resourcePath)) {
                if (inputStream != null) {
                    Files.copy(inputStream, targetFilePath);
                    logger.info("Initialized data file: " + targetPath);
                } else {
                    logger.warning("Resource not found: " + resourcePath);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to initialize data file: " + targetPath, e);
            }
        }
    }
    
    /**
     * Gets the full path for a data file in the user data directory.
     */
    public static String getDataFilePath(String fileName) {
        return getUserDataDirectory() + File.separator + fileName;
    }
}