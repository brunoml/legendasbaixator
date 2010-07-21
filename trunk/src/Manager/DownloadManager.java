package Manager;

import Interface.IDownloadHandler;
import Model.DownloadHandlerException;
import Model.DownloadHandlerVO;
import Model.MovieFileVO;
import Model.SubTitleVO;
import Utils.FileUtils;
import Utils.TorrentUtils;
import org.apache.log4j.lf5.util.StreamUtils;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.utils.LocaleUtilities;
import org.hamcrest.Matchers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.lambdaj.Lambda.*;

/**
 * Created by IntelliJ IDEA.
 * User: Bruno
 * Date: 24/03/2010
 * Time: 15:32:31
 * To change this template use File | Settings | File Templates.
 */
public class DownloadManager {
    private PluginInterface _pluginInterface;
    private ConfigManager _config;
    private LogManager _log;
    private LocaleUtilities _localeUtil;

    public DownloadManager(PluginInterface pluginInterface) {
        _pluginInterface = pluginInterface;
        _config = new ConfigManager(_pluginInterface);
        _log = new LogManager(_pluginInterface);
        _localeUtil = _pluginInterface.getUtilities().getLocaleUtilities();
    }

    private List<MovieFileVO> filterBasic(List<MovieFileVO> listaMovies) {
        // Tira os que já tem legenda
        listaMovies = filter(having(on(MovieFileVO.class).getHasSubTitle(), Matchers.equalTo(false)), listaMovies);
        // Tira os que não devem ser baixados de acordo com a Categoria, se precisar
        if (!_config.getCategoryAll()) {
            String[] categoryList = _config.getCategoryList();
            // Filtra por Categoria, se houver alguma
            if (categoryList != null)
                for (int i = listaMovies.size()-1; i >= 0; i--) {
                    Boolean naoTem = true;
                    for (String aCategoryList : categoryList) {
                        if (aCategoryList.equalsIgnoreCase(listaMovies.get(i).getCategory())) {
                            naoTem = false;
                            break;
                        }
                    }
                    if (naoTem)
                        listaMovies.remove(i);
                }
        }
        // Tira o que estão de acordo com a Regex configurada
        String excludeRegex = _config.getExcludeFilesRegex();
        if ((excludeRegex != null) || (excludeRegex.trim().equals(""))) {
            Pattern patternExcludeFiles = Pattern.compile(excludeRegex, Pattern.CASE_INSENSITIVE);

            List<MovieFileVO> listaTemp = listaMovies;
            listaMovies = new ArrayList<MovieFileVO>();

            for (MovieFileVO movieVO : listaTemp) {
                if (!patternExcludeFiles.matcher(movieVO.getFileName()).find())
                    listaMovies.add(movieVO);
            }
        }
        // Retorna a lista filtrada
        return listaMovies;
    }

    public void getSubTileForMovie(List<MovieFileVO> listaMovies) {
        if (!_config.getPluginActive())
            return;
        // Filtros básicos
        listaMovies = filterBasic(listaMovies);
        // Pega os Handlers
        HashMap<IDownloadHandler, DownloadHandlerVO> downloadHandlers = _config.getDownloadHandlers();
        boolean anyWasOk = false;
        // Vê se acha a bagaça nos Handlers
        for (Map.Entry<IDownloadHandler, DownloadHandlerVO> handler : downloadHandlers.entrySet()) {
            // Pego as legendas para os filmes
            try {
                downloadSubTitles(listaMovies, handler.getKey(), handler.getValue());
                anyWasOk = true;
            } catch (Exception e) {
                _log.ServerError(handler.getKey().getDescription(), e.getMessage(), e);
            }
            // Tiro da lista os que achei legenda
            listaMovies = filter(having(on(MovieFileVO.class).getHasSubTitle(), Matchers.equalTo(false)), listaMovies);
            // Se a lista está vazia, já saio, não tenho porque ficar aqui
            if (listaMovies.size() == 0)
                break;
        }
        // Agora mostra a mensagem pra quem sobrou, que foram os sem legenda
        if (anyWasOk)
            for (MovieFileVO movie : listaMovies)
                _log.NoSubTitle(movie.getFileName(), movie.getTorrentName());
    }

    public void getSubTitleForAllCompletedMovies() {
        _log.InitiateAllDownloads();
        // Pega todos os torrents que são video
        List<MovieFileVO> listaMovies = TorrentUtils.getMovieFiles(_pluginInterface);
        // Filtros necessários
        listaMovies = filterBasic(listaMovies);
        if (listaMovies.size() == 0)
            return;
        boolean anyWasOk = false;
        // Pega os Handlers
        HashMap<IDownloadHandler, DownloadHandlerVO> downloadHandlers = _config.getDownloadHandlers();
        // Vê se acha a bagaça nos Handlers
        for (Map.Entry<IDownloadHandler, DownloadHandlerVO> handler : downloadHandlers.entrySet()) {
            if (listaMovies.size() == 0)
                break;
            // Pego as legendas para os filmes
            try {
                downloadSubTitles(listaMovies, handler.getKey(), handler.getValue());
                anyWasOk = true;
            } catch (Exception e) {
                _log.ServerError(handler.getKey().getDescription(), e.getMessage(), e);
            }
            // Tira os que consegui pegar a legenda, os que sobraram vão para o próximo loop no próximo handler
            listaMovies = filter(having(on(MovieFileVO.class).getHasSubTitle(), Matchers.equalTo(false)), listaMovies);
        }
        // Log dos que não consegui legenda
        if (anyWasOk)
            for (MovieFileVO movie : listaMovies)
                _log.NoSubTitle(movie.getFileName(), movie.getTorrentName());

        _log.FinishAllDownloads();
    }

    private void downloadSubTitles(List<MovieFileVO> listaMovies, IDownloadHandler downloadHandler, DownloadHandlerVO handlerVO) throws Exception {
        if ((listaMovies == null) || (listaMovies.size() == 0))
            return;

        try {
            downloadHandler.doLogin(handlerVO);

            for (MovieFileVO movieFileVO : listaMovies) {
                List<SubTitleVO> listaSubTitle = downloadHandler.getSubTitleList(movieFileVO);

                if (listaSubTitle.size() == 0) {
                    _log.debug(String.format("%s: SubTitle not found on %s", movieFileVO.getFileName(), downloadHandler.getDescription()));
                    continue;
                }
                _log.debug(String.format("%s: Found %s SubTitles on %s", movieFileVO.getFileName(), listaSubTitle.size(), downloadHandler.getDescription()));
                
                // Escolhe a melhor legenda nesse handler
                SubTitleVO chosenSubTitle = downloadHandler.chooseOneSubTitle(movieFileVO, listaSubTitle);

                _log.debug(String.format("%s: Downloading %s...", movieFileVO.getFileName(), chosenSubTitle.getRelease()));

                InputStream subTitleStream = null;
                try {
                    subTitleStream = downloadHandler.getSubTitleFile(chosenSubTitle);
                } catch (Exception e) {
                    _log.fatal(_localeUtil.getLocalisedMessageText(Core.SYSTEM_NAME + ".DownloadingSubtitleError",
                        new String[]{chosenSubTitle.getFileName(), downloadHandler.getDescription(), e.getMessage()}), e);
                }

                if (subTitleStream != null) {
                    String subtitleFileName = chosenSubTitle.getFileName();
                    if (_config.getSubTitleWithMovieName()) {
                        subtitleFileName = FileUtils.changeExtension(movieFileVO.getFileName(), FileUtils.getExtension(chosenSubTitle.getFileName()));
                    }
                    File file = new File(movieFileVO.getPathDir(), subtitleFileName);
                    FileOutputStream stream = new FileOutputStream(file);
                    StreamUtils.copyThenClose(subTitleStream, stream);

                    _log.SavedSubTitle(subtitleFileName, movieFileVO.getFileName(), downloadHandler.getDescription());

                    movieFileVO.setHasSubTitle(true);
                }
            }
        } catch (DownloadHandlerException e) {
            _log.warning(_localeUtil.getLocalisedMessageText(ConfigManager.BaseName + "." + e.getResource(), e.getArgs()));
        } finally {
            downloadHandler.doLogout();
        }
    }
}
