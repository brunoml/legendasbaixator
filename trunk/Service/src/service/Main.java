/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package service;

import ServiceManager.ConfigManager;
import ServiceManager.DownloadManager;
import ServiceManager.FilesManager;
import ServiceManager.LogManager;

/**
 *
 * @author Bruno
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ConfigManager configManager = new ConfigManager();
        LogManager logManager = new LogManager(configManager);
        FilesManager filesManager = new FilesManager(configManager);

        DownloadManager downloadManager = new DownloadManager(configManager, filesManager, logManager);
        downloadManager.downloadSubTitles();

        // TODO code application logic here
    }

}
