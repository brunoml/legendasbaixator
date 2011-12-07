/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceManager;

import org.hamcrest.Matchers;
import Interface.IDownloadHandler;
import Manager.SubTitleManager;
import Model.DownloadHandlerVO;
import Model.VideoFileVO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static ch.lambdaj.Lambda.*;

/**
 *
 * @author Bruno
 */
public class DownloadManager {
    private ConfigManager _config;
    private FilesManager _filesManager;
    private LogManager _log;

    public DownloadManager(ConfigManager configManager, FilesManager filesManager, LogManager logManager) {
        _config = configManager;
        _filesManager = filesManager;
        _log = logManager;
    }

    public void downloadSubTitles() {
        SubTitleManager subManager = new SubTitleManager(_config, _log);

        _log.initiateDownloading();
        try {
            List<VideoFileVO> listVideo = _filesManager.getVideosWithoutSubTitles();
            // Pega os Handlers
            HashMap<IDownloadHandler, DownloadHandlerVO> downloadHandlers = _config.getDownloadHandlers();
            // Vê se acha a bagaça nos Handlers
            for (Map.Entry<IDownloadHandler, DownloadHandlerVO> handler : downloadHandlers.entrySet()) {
                if (listVideo.isEmpty()) {
                    break;
                }
                // Pego as legendas para os filmes
                try {
                    subManager.setListaVideo(listVideo);
                    subManager.downloadSubTitles(handler.getKey(), handler.getValue());
                    for (VideoFileVO videoVO : subManager.getListaVideoSaved())
                        _log.SavedSubTitle(videoVO.getFileName(), videoVO.getSubTitleVO().getFileName(), handler.getKey().getDescription());
                } catch (Exception e) {
                    _log.ServerError(handler.getKey().getDescription(), e);
                }
                // Tira os que consegui pegar a legenda, os que sobraram vão para o próximo loop no próximo handler
                listVideo = filter(having(on(VideoFileVO.class).getHasSubTitle(), Matchers.equalTo(false)), listVideo);
            }
            // Log dos que não consegui legenda, ou seja, dos que sobraram na lista
            for (VideoFileVO movie : listVideo) {
                 _log.NoSubTitle(movie.getFileName());
            }
        } finally {
            _log.finishDownloading();
        }

    }

}
