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
    
    public static final String VERSION_NUMBER = "0.2";
    public static final String SYSTEM_NAME = "VuzeLegendasBaixator";
    public static final String URL = "http://legendasbaixator.googlecode.com";

    public enum LogonType {
        None,
        Optional,
        Required
    }

    String getDescription();
    String getSiteUrl();
    Class getHandlerVOType();
    LogonType getLogonType();
    SubTitleLanguage[] getSupportedLanguages();
    void doLogin(DownloadHandlerVO handlerVO) throws DownloadHandlerException;
    List<SubTitleVO> getSubTitleList(VideoFileVO movieFile);
    SubTitleVO chooseOneSubTitle(VideoFileVO movieFile, List<SubTitleVO> subList);
    InputStream getSubTitleFile(SubTitleVO subTitleVO);
    void doLogout();
}
