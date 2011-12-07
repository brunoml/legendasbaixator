/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceManager;

import Interface.ILogManager;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Bruno
 */
public class LogManager implements ILogManager {
    private static Logger logger;
    private ConfigManager configManager;

    public LogManager(ConfigManager configManager) {
        this.configManager = configManager;

        FileHandler fh;
        try {
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy_MM_dd");
            String logFileName = df.format(date) + ".log";
            fh = new FileHandler(configManager.getCurrentPath() + File.pathSeparator + logFileName, true);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } catch (SecurityException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        fh.setFormatter(new SimpleFormatter());
        logger = Logger.getLogger("LegendasBaixator");
        logger.addHandler(fh);
    }

    public void info(String value) {
        if (configManager.getRecordLog())
            logger.info(value);
    }

    public void warning(String value) {
        if (configManager.getRecordLog())
            logger.warning(value);
    }

    public void error(String value) {
        logger.severe(value);
    }

    public void fatal(String value, Throwable e) {
        logger.severe(value + " - Error Message: " + e.getMessage());
    }

    public void debug(String value) {
        if (configManager.getRecordDebug())
            logger.info(value);
    }

    public void initiateDownloading() {
        this.info("Download Starting...");
    }

    public void finishDownloading() {
        this.info("Download Finished.");
    }

    public void ServerError(String serverDescription, Exception e) {
        this.info(String.format("%s: Server Error - %s", serverDescription, e.getMessage()));
    }

    public void SavedSubTitle(String videoName, String subtitleName, String handler) {
        this.info(String.format("%s: Save subtitle %s from %s", videoName, subtitleName, handler));
    }

    public void NoSubTitle(String videoName) {
        this.info(String.format("%s: Subtitle not found", videoName));
    }

}
