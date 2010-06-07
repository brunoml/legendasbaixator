package Threads;

import Manager.DownloadManager;
import org.gudy.azureus2.plugins.PluginInterface;

/**
 * Created by IntelliJ IDEA.
 * User: Brunol
 * Date: 07/05/2010
 * Time: 09:41:04
 * To change this template use File | Settings | File Templates.
 */
public class DownloadAllThread extends BaseThread implements Runnable {

    public DownloadAllThread(PluginInterface pluginInterface) {
        super(pluginInterface);
    }

    public void run() {
        DownloadManager manager = new DownloadManager(_pluginInterface);
        manager.getSubTitleForAllCompletedMovies();
    }
}
