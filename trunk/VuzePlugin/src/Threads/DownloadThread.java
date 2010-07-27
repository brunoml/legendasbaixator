package Threads;

import TorrentManager.DownloadManager;
import Model.VideoFileVO;
import org.gudy.azureus2.plugins.PluginInterface;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Brunol
 * Date: 07/05/2010
 * Time: 09:39:38
 * To change this template use File | Settings | File Templates.
 */
public class DownloadThread extends BaseThread implements Runnable {
    private List<VideoFileVO> _listaMovie = null;

    public DownloadThread(PluginInterface pluginInterface, List<VideoFileVO> listaMovie) {
        super(pluginInterface);
        _listaMovie = listaMovie;
    }

    public void run() {
        DownloadManager manager = new DownloadManager(_pluginInterface);
        manager.getSubTileForMovie(_listaMovie);
    }
}
