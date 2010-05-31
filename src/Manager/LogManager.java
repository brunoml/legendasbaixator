package Manager;

import Utils.FileUtils;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.logging.LoggerChannel;
import org.gudy.azureus2.plugins.utils.LocaleUtilities;

/**
 * Created by IntelliJ IDEA.
 * User: Brunol
 * Date: 26/03/2010
 * Time: 15:24:07
 * To change this template use File | Settings | File Templates.
 */
public class LogManager {
    private LoggerChannel _LoggerChannel;
    private LocaleUtilities _localeUtil;

    public LogManager(PluginInterface pluginInterface) {
        _LoggerChannel = pluginInterface.getLogger().getChannel("LegendasBaixator");
        _localeUtil = pluginInterface.getUtilities().getLocaleUtilities();
    }

    public void info(String value) {
        _LoggerChannel.logAlert(LoggerChannel.LT_INFORMATION, value);
    }

    public void warning(String value) {
        _LoggerChannel.logAlert(LoggerChannel.LT_WARNING, value);
    }

    public void error(String value) {
        _LoggerChannel.logAlert(LoggerChannel.LT_ERROR, value);
    }

    public void fatal(String value, Throwable e) {
        _LoggerChannel.logAlertRepeatable(value, e);
    }

    public void debug(String value) {
        _LoggerChannel.log(value);
    }

    public void NoSubTitle(String fileName, String torrentName) {
        if (fileName.equalsIgnoreCase(torrentName))
            warning(_localeUtil.getLocalisedMessageText(ConfigManager.BaseName + ".NoSubtitle", new String[]{ fileName }));
        else
            warning(_localeUtil.getLocalisedMessageText(ConfigManager.BaseName + ".NoSubtitleTorrent", new String[]{ fileName, torrentName }));
    }

    public void SavedSubTitle(String subFileName, String movieFileName, String handlerDescription) {
        if (FileUtils.getFileNameWithoutExtension(subFileName).equalsIgnoreCase(FileUtils.getFileNameWithoutExtension(movieFileName)))
            info(_localeUtil.getLocalisedMessageText(ConfigManager.BaseName + ".SavedSubtitle", new String[]{subFileName, handlerDescription}));
        else
            info(_localeUtil.getLocalisedMessageText(ConfigManager.BaseName + ".SavedSubtitleFileName", new String[]{subFileName, movieFileName, handlerDescription}));
    }

    public void ServerError(String serverDescription, String errorMessage, Exception e) {
        fatal(_localeUtil.getLocalisedMessageText(ConfigManager.BaseName + ".ServerError", new String[]{serverDescription, errorMessage}), e);
    }

    public void InitiateAllDownloads() {
        info(_localeUtil.getLocalisedMessageText(ConfigManager.BaseName + ".InitiateAllDownloads", new String[]{}));
    }
}
