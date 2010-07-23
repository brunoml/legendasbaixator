package Manager;

import Implementation.OpenSubtitlesOrgHandler;
import Implementation.SubDBHandler;
import Interface.IDownloadHandler;
import Model.Description;
import Model.DownloadHandlerVO;
import Model.SubTitleLanguage;
import Threads.DownloadAllThread;
import Utils.TorrentUtils;
import org.gudy.azureus2.plugins.PluginConfig;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.ui.config.*;
import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
import org.gudy.azureus2.plugins.utils.LocaleUtilities;

import java.io.File;
import java.util.*;
import java.lang.reflect.*;


/**
 * Created by IntelliJ IDEA.
 * User: Brunol
 * Date: 26/03/2010
 * Time: 14:44:23
 * To change this template use File | Settings | File Templates.
 */
public class ConfigManager {
    private PluginConfig pconfig;
    private PluginInterface _pluginInterface;

    public ConfigManager(PluginInterface pluginInterface) {
        pconfig = pluginInterface.getPluginconfig();
        _pluginInterface = pluginInterface;
    }

    public static final String BaseName = "VuzeLegendasBaixator";
    private static final String _BaseConfigName = BaseName + ".config";
    private static final String _PluginActive = "Active";
    private static final String _SubTitleWithMovieName = "SubTitleWithMovieName";
    private static final String _CategoryList = "CategoryList";
    private static final String _CategoryAll = "CategoryAll";
    private static final String _CheckedFiles = "CheckedFiles";
    private static final String _ExcludeFilesRegex = "ExcludeFilesRegex";

    public static void addLocalisedMessage(LocaleUtilities localeUtil, String name, String value) {
        Properties propsMsg = new Properties();
        propsMsg.put(name, value);
        localeUtil.integrateLocalisedMessageBundle(propsMsg);
    }

    public boolean getPluginActive() {
        return pconfig.getPluginBooleanParameter(_PluginActive, false);
    }

    public boolean getSubTitleWithMovieName() {
        return pconfig.getPluginBooleanParameter(_SubTitleWithMovieName, true);
    }

    public String[] getCheckedFiles() {
        return pconfig.getPluginStringListParameter(_CheckedFiles);
    }
    public void setCheckedFiles(String[] checkedFiles) {
        pconfig.setPluginStringListParameter(_CheckedFiles, checkedFiles);
    }
    public void addCheckedFile(String checkedFile) {
        String[] checkedFiles = getCheckedFiles();
        String[] newCheckedFiles = new String[checkedFiles.length+1];
        System.arraycopy(checkedFiles, 0, newCheckedFiles, 0, checkedFiles.length);
        newCheckedFiles[newCheckedFiles.length-1] = checkedFile;
        setCheckedFiles(newCheckedFiles);
    }

    public String getExcludeFilesRegex() {
        return pconfig.getPluginStringParameter(_ExcludeFilesRegex);
    }

    public HashMap<IDownloadHandler, DownloadHandlerVO> getDownloadHandlers() {
        List<IDownloadHandler> handlersList = getExistingHandlers();     
        HashMap<IDownloadHandler, DownloadHandlerVO> handlers = new HashMap<IDownloadHandler, DownloadHandlerVO>();

        for (IDownloadHandler handler : handlersList) {
            // Se estiver marcado para ser usado busco as informações do VO
            if (pconfig.getPluginBooleanParameter(handler.getClass().getSimpleName(), false)) {
                try {
                    DownloadHandlerVO handlerVO = (DownloadHandlerVO)handler.getHandlerVOType().newInstance();
                    Method[] metodos = handlerVO.getClass().getMethods();
                    for (Method metodo : metodos) {
                        if (metodo.getName().startsWith("set")) {
                            Class<?> typeParam = metodo.getParameterTypes()[0];
                            String nomeProp = metodo.getName().substring(3);
                            String nomePropPlugin = handler.getClass().getSimpleName() + "." + nomeProp;
                            if (typeParam == int.class) {
                                metodo.invoke(handlerVO, pconfig.getPluginIntParameter(nomePropPlugin, 0));
                            } else if (typeParam == String.class) {
                                metodo.invoke(handlerVO, pconfig.getPluginStringParameter(nomePropPlugin, ""));
                            } else if (typeParam.isEnum()) {
                                String valor = pconfig.getPluginStringParameter(nomePropPlugin, "");
                                Object[] enumValores = typeParam.getEnumConstants();
                                for (Object objEnum : enumValores)
                                    if (objEnum.toString().equalsIgnoreCase(valor)) {
                                        metodo.invoke(handlerVO, objEnum);
                                        break;
                                    }
                            }
                        }
                    }
                    handlers.put(handler, handlerVO);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return handlers;
    }

    public boolean getCategoryAll() {
        return pconfig.getPluginBooleanParameter(_CategoryAll, true);
    }

    public String[] getCategoryList() {
        if (getCategoryAll())
            return null;

        String[] existingCategories = getExistingCategories(_pluginInterface);
        String categorias = "";
        for (String category : existingCategories) {
            String nomeCfgCategory = _CategoryList + "." + category;
            if (pconfig.getPluginBooleanParameter(nomeCfgCategory, false))
                categorias += category + ";";
        }
        if (categorias.indexOf(";") > 0)
            return categorias.substring(0, categorias.length() - 1).split(";");
        else
            return new String[0];
    }

    public static List<IDownloadHandler> getExistingHandlers() {
        List<IDownloadHandler> handlersList = new ArrayList<IDownloadHandler>();
        handlersList.add(new OpenSubtitlesOrgHandler());
        handlersList.add(new SubDBHandler());
        return handlersList;
    }

    public static String[] getExistingCategories(PluginInterface pluginInterface) {
        Download[] torrents = pluginInterface.getDownloadManager().getDownloads();
        String categorias = "";
        TorrentAttribute ta = TorrentUtils.getCategoryAttr(pluginInterface);
        for (Download torrent : torrents) {
            String categoria = torrent.getAttribute(ta);
            if (categoria == null)
                continue;
            if ((!categoria.equalsIgnoreCase("")) && (categorias.indexOf(categoria) < 0))            
                categorias += categoria + ";";
        }
        // Retiro o último caractere que será sempre ;
        if (categorias.indexOf(";") > 0)
            return categorias.substring(0, categorias.length()-1).split(";");
        else
            return new String[0];
    }

    public static void initializeConfigPage(final PluginInterface pluginInterface) {
        final BasicPluginConfigModel cfg = pluginInterface.getUIManager().createBasicPluginConfigModel(_BaseConfigName);
        LocaleUtilities localeUtil = pluginInterface.getUtilities().getLocaleUtilities();

        cfg.addBooleanParameter2(_PluginActive, _BaseConfigName + "." + _PluginActive, false);
        cfg.addBooleanParameter2(_SubTitleWithMovieName, _BaseConfigName + "." + _SubTitleWithMovieName, true);
        cfg.addStringParameter2(_ExcludeFilesRegex, _BaseConfigName + "." + _ExcludeFilesRegex, "");

        // Configuração dosHandlers
        final List<IDownloadHandler> handlersList = getExistingHandlers();
        for (IDownloadHandler handler : handlersList) {
            try {
                DownloadHandlerVO handlerVO = (DownloadHandlerVO) handler.getHandlerVOType().newInstance();
                Method[] metodos = handlerVO.getClass().getMethods();

                int paramCount = 2;
                for (Method metodo : metodos)
                    if (metodo.getName().startsWith("set"))
                        paramCount++;

                String nomeHandle = handler.getClass().getSimpleName();
                addLocalisedMessage(localeUtil, nomeHandle, handler.getDescription());
                addLocalisedMessage(localeUtil, handler.getSiteUrl(), handler.getSiteUrl());

                Parameter[] parametros = new Parameter[paramCount];
                parametros[0] = cfg.addHyperlinkParameter2(handler.getSiteUrl(), handler.getSiteUrl());
                parametros[1] = cfg.addBooleanParameter2(nomeHandle, _BaseConfigName + ".Active", false);

                paramCount = 2;
                for (Method metodo : metodos) {
                    if (metodo.getName().startsWith("set")) {
                        Class<?> typeParam = metodo.getParameterTypes()[0];
                        String nomeProp = metodo.getName().substring(3);
                        String nomePropPlugin = handler.getClass().getSimpleName() + "." + nomeProp;
                        String resourceMessage = _BaseConfigName + "." + handlerVO.getClass().getSimpleName() + "." + nomeProp;

                        if ((handler.getLogonType() == IDownloadHandler.LogonType.None) &
                            ((metodo.getName().equals("setUserName")) || (metodo.getName().equals("setPassword"))))
                            continue;
                        
                        if (typeParam == SubTitleLanguage.class) {
                            // Na lista de idiomas coloco só os que o Handler suporta
                            SubTitleLanguage[] enumValores = handler.getSupportedLanguages();
                            String[] valores = new String[enumValores.length];
                            String[] labels = new String[enumValores.length];
                            for (int j = 0; j < enumValores.length; j++) {
                                valores[j] = enumValores[j].toString();
                                labels[j] = SubTitleLanguage.class.getField(enumValores[j].name()).getAnnotation(Description.class).value();
                            }
                            parametros[paramCount] = cfg.addStringListParameter2(nomePropPlugin, resourceMessage, valores, labels, "");
                        } else if (typeParam == int.class) {
                            parametros[paramCount] = cfg.addIntParameter2(nomePropPlugin, resourceMessage, 0);
                        } else if (typeParam == String.class) {
                            if (nomeProp.equalsIgnoreCase("Password"))
                                parametros[paramCount] = cfg.addPasswordParameter2(nomePropPlugin, resourceMessage, PasswordParameter.ET_PLAIN, new byte[0]);
                            else
                                parametros[paramCount] = cfg.addStringParameter2(nomePropPlugin, resourceMessage, "");
                        } else if (typeParam.isEnum()) {
                            Object[] enumValores = typeParam.getEnumConstants();
                            String[] valores = new String[enumValores.length];
                            for (int j = 0; j < enumValores.length; j++)
                                valores[j] = enumValores[j].toString();
                            parametros[paramCount] = cfg.addStringListParameter2(nomePropPlugin, resourceMessage, valores, "");
                        }
                        ((BooleanParameter)parametros[1]).addEnabledOnSelection(parametros[paramCount]);
                        paramCount++;
                    }
                }
                cfg.createGroup(nomeHandle, parametros);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // Configuração das Categorias
        String[] existingCategories = getExistingCategories(pluginInterface);
        Parameter[] parametros = new Parameter[existingCategories.length];
        BooleanParameter paramCatAll = cfg.addBooleanParameter2(_CategoryAll, _BaseConfigName + "." + _CategoryAll, true);
        int paramCount = 0;
        for (String category : existingCategories) {
            String nomeCfgCategory = _CategoryList + "." + category;
            String nomeResMessage = _BaseConfigName + "." + nomeCfgCategory;
            addLocalisedMessage(localeUtil, nomeResMessage, category);
            parametros[paramCount] = cfg.addBooleanParameter2(nomeCfgCategory, nomeResMessage, false);
            paramCatAll.addDisabledOnSelection(parametros[paramCount]);
            paramCount++;
        }
        cfg.createGroup(_BaseConfigName + "." + _CategoryList, parametros);

        // Botão para buscar em todos os arquivos já terminados
        ActionParameter btnDownloadAll = cfg.addActionParameter2(_BaseConfigName + ".btnDownloadAll.label", _BaseConfigName + ".btnDownloadAll");
        btnDownloadAll.addListener(new ParameterListener() {
            public void parameterChanged(Parameter p) {
                DownloadAllThread downAllThread = new DownloadAllThread(pluginInterface);
                new Thread(downAllThread).start();        
           }
       });
    }
}
