# Java Application Utils

[![Java Version](https://img.shields.io/badge/Java-8%2B-brightgreen)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-blue)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-BSD_3--Clause-yellow.svg)](LICENSE)

A collection of utility classes for Java applications providing internationalization, cross-platform data management, and comprehensive logging capabilities.

## Table of Contents
- [Quick Start](#quick-start)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Directory Structure](#directory-structure)
- [License](#license)

## Quick Start

```java
// 1. Set up your application
AppDataManager.setAppName("MyApplication");
LoggingManager.initializeLogging();

// 2. Initialize internationalization
ResourceManager.setLocale("en-US");

// 3. Start using the utilities
Logger logger = Logger.getLogger(MyClass.class.getName());
logger.info("Application started successfully");

String welcomeMsg = ResourceManager.getString("welcome.message");
System.out.println(welcomeMsg);
```

## Features

### 🌍 ResourceManager - Internationalization
- **Multi-language Support**: Manage localized strings and messages from `lang/messages.properties` files
- **Runtime Language Switching**: Change application locale dynamically with string or Locale objects
- **Flexible Language Tags**: Support for "en", "en_US", "en-US", and Locale objects
- **Parameterized Messages**: Support for MessageFormat placeholders (`{0}`, `{1}`, etc.)
- **Graceful Fallback**: Returns descriptive error messages for missing translation keys
- **Automatic Initialization**: Uses system default locale on startup with English fallback
- **Thread-Safe**: Safe for concurrent access

### 💾 AppDataManager - Cross-Platform Data Management
- **Platform-Aware**: Proper application data directory handling for Windows, macOS, and Linux
- **User Data Separation**: Separates application files from user-modifiable data
- **Automatic Setup**: Creates necessary directories on first use
- **Resource Initialization**: Copies default files from JAR resources to user data directory
- **Path Resolution**: Easy access to platform-appropriate file paths

### 📝 LoggingManager - Advanced Logging
- **Dual Output**: File and console logging with different levels (INFO+ to file, WARNING+ to console)
- **Log Rotation**: Automatic daily log files with size-based rotation (10MB per file, 5 files max)
- **Rich Formatting**: Detailed file logs with timestamps, thread info, class names, and exception stack traces
- **Simple Console Output**: Clean console messages for warnings and errors only
- **Platform Storage**: Uses platform-appropriate log file locations
- **Fallback Safety**: Graceful fallback to console-only logging if file logging fails

## Installation
1. Download the JAR from [releases](https://github.com/TheBanditOfRed/Java-Application-Utils/releases)
2. Add to your classpath
3. Ensure Java 24+ is installed

## Usage

### Complete Application Setup

```java
public class MyApplication {
    private static final Logger logger = Logger.getLogger(MyApplication.class.getName());
    
    public static void main(String[] args) {
        try {
            // 1. Set application name (must be done first)
            AppDataManager.setAppName("MyAwesomeApp");
            
            // 2. Initialize logging
            LoggingManager.initializeLogging();
            logger.info("Logging initialized");
            
            // 3. Set up user data files (copy from JAR resources if needed)
            AppDataManager.initializeUserDataFiles(
                "/data/config.json", 
                "/data/settings.json"
            );
            
            // 4. ResourceManager auto-initializes with system locale
            // But you can change it:
            ResourceManager.setLocale("en-US");
            
            logger.info("Application setup complete");
            
        } catch (Exception e) {
            logger.severe("Failed to initialize application: " + e.getMessage());
            System.exit(1);
        }
    }
}
```

### Internationalization Examples

```java
// Basic usage - ResourceManager auto-initializes with system locale
String message = ResourceManager.getString("welcome.message");

// Change locale with string
ResourceManager.setLocale("en-US");  // English (US)
ResourceManager.setLocale("en_US");  // Also works
ResourceManager.setLocale("en");     // Just language code
ResourceManager.setLocale("pt-PT");  // Portuguese (Portugal)

// Using Locale objects
ResourceManager.setLocale(Locale.FRENCH);
ResourceManager.setLocale(new Locale("pt", "PT")); // Portuguese (Portugal)

// Parameterized messages
String greeting = ResourceManager.getString("user.greeting", "John", 5);
// With messages.properties: user.greeting=Hello {0}, you have {1} new messages
// Result: "Hello John, you have 5 new messages"

// Handle missing keys gracefully
String missing = ResourceManager.getString("nonexistent.key");
// Result: "Missing translation for key: nonexistent.key"

// Runtime language switching
public void switchLanguage(String languageCode) {
    ResourceManager.setLocale(languageCode);
    updateUI(); // Refresh your UI with new translations
    logger.info("Language switched to: " + languageCode);
}
```

### Data Management Examples

```java
// Get platform-specific directories
String userDataDir = AppDataManager.getUserDataDirectory();
String logsDir = AppDataManager.getLogsDirectory();
String currentAppName = AppDataManager.getAppName();

// Work with configuration files
String configPath = AppDataManager.getDataFilePath("config.json");
File configFile = new File(configPath);

// Read user data
try {
    String config = Files.readString(Paths.get(configPath));
    // Process configuration...
} catch (IOException e) {
    logger.warning("Could not read config file: " + e.getMessage());
}

// Initialize multiple data files from JAR resources
AppDataManager.initializeUserDataFiles(
    "/data/config.json",
    "/data/settings.json", 
    "/data/preferences.json"
);
// Files are copied from JAR resources to user data directory
// Only copied if they don't already exist (preserves user modifications)
```

### Advanced Logging

```java
public class DatabaseService {
    private static final Logger logger = Logger.getLogger(DatabaseService.class.getName());
    
    public void connectToDatabase() {
        logger.info("Attempting database connection...");  // Goes to file
        
        try {
            // Database connection logic
            logger.info("Database connected successfully");  // Goes to file
        } catch (SQLException e) {
            // This goes to both file AND console (WARNING level)
            logger.warning("Database connection failed: " + e.getMessage());
            
            // For severe errors (goes to both file and console)
            logger.severe("Critical database error - cannot continue");
            throw new RuntimeException("Cannot proceed without database", e);
        }
    }
    
    public void performMaintenance() {
        logger.info("Starting database maintenance");  // File only
        // ... maintenance operations
        logger.info("Database maintenance completed");   // File only
        
        // Force log flush for critical operations
        LoggingManager.flushLogs();
        
        // Get log directory for debugging
        String logDir = LoggingManager.getLogDirectory();
        logger.info("Logs available at: " + logDir);
    }
}
```

## Configuration

### Resource Bundle Setup
Create your internationalization files in `src/main/resources/lang/`:

```
src/main/resources/lang/
├── messages.properties         # Default (English)
├── messages_es.properties     # Spanish
├── messages_fr.properties     # French
├── messages_de.properties     # German
└── messages_pt_PT.properties  # Portuguese (Portugal)
```

**Example `messages.properties`:**
```properties
# Application messages
welcome.message=Welcome to the application
app.title=My Awesome Application

# User interactions
user.greeting=Hello {0}, you have {1} new messages
user.login.success=Login successful for user: {0}

# Error messages
error.file.notfound=File not found: {0}
error.permission.denied=Permission denied accessing: {0}
error.network.timeout=Network timeout after {0} seconds

# Validation messages
validation.email.invalid=Please enter a valid email address
validation.password.weak=Password must be at least 8 characters
```

### Log File Format Examples

**File Log Format** (detailed):
```
2025-06-18 14:32:15.123 INFO    [main           ] MyApplication        - Application started successfully
2025-06-18 14:32:15.456 WARNING [worker-thread-1] DatabaseService      - Connection retry attempt 2
2025-06-18 14:32:16.789 SEVERE  [main           ] ConfigManager        - Configuration file corrupted
Exception: JsonParseException: Unexpected character at line 5
    at com.example.ConfigManager.loadConfig(ConfigManager.java:45)
    at com.example.MyApp.initialize(MyApp.java:23)
    ... 3 more lines
```

**Console Log Format** (simplified, WARNING+ only):
```
WARNING DatabaseService: Connection retry attempt 2
SEVERE ConfigManager: Configuration file corrupted
```

### Logging Configuration
The logging system uses these defaults:

- **File Log Level**: `INFO` and above (includes INFO, WARNING, SEVERE)
- **Console Log Level**: `WARNING` and above (WARNING and SEVERE only)
- **Log File Naming**: `app_YYYY-MM-DD_X.log` (daily files with rotation number)
- **Max File Size**: 10MB before rotation to next file
- **Max Log Files**: 5 files kept per day before oldest is deleted
- **File Log Format**: `[TIMESTAMP] [LEVEL] [THREAD] [CLASS] MESSAGE` + stack traces
- **Console Log Format**: `LEVEL CLASS: MESSAGE` (simplified)

## Directory Structure

### Windows
```
%LOCALAPPDATA%/[AppName]/
├── data/
│   ├── config.json
│   └── settings.json
└── logs/
    ├── app_2025-06-18_0.log
    ├── app_2025-06-18_1.log
    └── app_2025-06-17_0.log
```

### macOS
```
~/Library/Application Support/[AppName]/
├── data/
│   ├── config.json
│   └── settings.json
└── logs/
    ├── app_2025-06-18_0.log
    ├── app_2025-06-18_1.log
    └── app_2025-06-17_0.log
```

### Linux
```
~/.config/[AppName]/
├── data/
│   ├── config.json
│   └── settings.json
└── logs/
    ├── app_2025-06-18_0.log
    ├── app_2025-06-18_1.log
    └── app_2025-06-17_0.log
```

## Error Handling & Best Practices

### Robust Error Handling
```java
// ResourceManager returns descriptive error messages, never throws
String message = ResourceManager.getString("missing.key");
// Returns: "Missing translation for key: missing.key"

// Handle data directory issues gracefully
try {
    AppDataManager.initializeUserDataFiles("/data/config.json");
} catch (Exception e) {
    // This shouldn't happen as initializeUserDataFiles logs errors but doesn't throw
    logger.warning("Configuration initialization issue: " + e.getMessage());
    useDefaultConfiguration();
}

// LoggingManager has built-in fallback
LoggingManager.initializeLogging(); // Never throws - falls back to console if needed
```

### Thread Safety Notes
- **All utility classes use thread-safe static methods**
- **ResourceManager**: Thread-safe static access, ResourceBundle is thread-safe
- **LoggingManager**: Synchronized initialization, Java logging is thread-safe
- **AppDataManager**: Thread-safe static methods, directory creation is synchronized
- **Initialization**: Should happen once at startup, but safe if called multiple times

### Performance Tips
- **Initialize logging early** in your application lifecycle
- **ResourceManager auto-initializes** with system locale - no manual setup needed
- **Resource bundle loading is cached** - first access may be slower, subsequent calls are fast
- **Use `LoggingManager.flushLogs()` sparingly** - only for critical operations before shutdown
- **Consider the overhead** of parameterized messages in tight loops
- **Log file rotation is automatic** - daily files with size-based splitting
- **Missing translation keys are logged once** per key to avoid log spam

## Requirements

- **Java**: 8 or higher (uses standard Java logging, NIO.2, ResourceBundle APIs)
- **Maven**: 3.6+ (for building from source)
- **Operating System**: Windows 10+, macOS 10.10+, or Linux (any modern distribution)
- **JAR Resources**: Your application JAR should include resource files in the expected paths

## License

This project is licensed under the BSD 3-Clause License. See the [LICENSE](LICENSE) file for details.
