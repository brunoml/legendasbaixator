package Manager;

import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.UnloadablePlugin;

/**
 * Created by IntelliJ IDEA.
 * User: Brunol
 * Date: 15/03/2010
 * Time: 14:05:19
 * To change this template use File | Settings | File Templates.
 */
public class Core implements UnloadablePlugin {

    public static final String VERSION_NUMBER = "0.1";
    public static final String SYSTEM_NAME = "VuzeLegendasBaixator";
    public static final String USER_AGENT = SYSTEM_NAME + " v" + VERSION_NUMBER; 

    public void unload() throws PluginException {

    }

    public void initialize(PluginInterface pluginInterface) throws PluginException {
        pluginInterface.getDownloadManager().addListener(new TorrentListener(pluginInterface));
        ConfigManager.initializeConfigPage(pluginInterface);
    }
}
