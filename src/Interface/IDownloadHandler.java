package Interface;

import Model.*;

import java.io.InputStream;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Brunol
 * Date: 25/02/2010
 * Time: 18:53:23
 * To change this template use File | Settings | File Templates.
 */
public interface IDownloadHandler {
    String getDescription();
    String getSiteUrl();
    Class getHandlerVOType();
    SubTitleLanguage[] getSupportedLanguages();
    void doLogin(DownloadHandlerVO handlerVO) throws DownloadHandlerException;
    List<SubTitleVO> getSubTitleList(MovieFileVO movieFile);
    SubTitleVO chooseOneSubTitle(MovieFileVO movieFile, List<SubTitleVO> subList);
    InputStream getSubTitleFile(SubTitleVO subTitleVO);
    void doLogout();
}
