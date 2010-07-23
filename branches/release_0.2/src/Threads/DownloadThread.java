package Threads;

import Manager.DownloadManager;
import Model.MovieFileVO;
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
    private List<MovieFileVO> _listaMovie = null;

    public DownloadThread(PluginInterface pluginInterface, List<MovieFileVO> listaMovie) {
        super(pluginInterface);
        _listaMovie = listaMovie;
    }

    public void run() {
        DownloadManager manager = new DownloadManager(_pluginInterface);
        manager.getSubTileForMovie(_listaMovie);
    }
}
