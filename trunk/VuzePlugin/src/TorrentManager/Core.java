package TorrentManager;

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
    
    public void unload() throws PluginException {

    }

    public void initialize(PluginInterface pluginInterface) throws PluginException {
        pluginInterface.getDownloadManager().addListener(new TorrentListener(pluginInterface));
        ConfigManager.initializeConfigPage(pluginInterface);
    }
}
