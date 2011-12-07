/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceManager;

import Interface.IConfigManager;
import Interface.IDownloadHandler;
import Manager.SubTitleManager;
import Model.DownloadHandlerVO;
import Model.SubTitleLanguage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruno
 */
public class ConfigManager implements IConfigManager {
    private static final String propertyFileName = "LegendasBaixator.properties";
    private static final String propertyFilesUpload = "UploadFiles.properties";
    private Properties propertyFile = new Properties();
    private Properties propertyUploadFile = new Properties();
    private String _currentPath;

    public ConfigManager() {
        try {
            propertyFile.load(new FileInputStream(propertyFileName));
            propertyUploadFile.load(new FileInputStream(propertyFilesUpload));
        } catch (Exception e) {
            throw new RuntimeException(String.format("Property file not found %s", propertyFileName), e);
        }
        java.io.File currentDir = new java.io.File("");
        _currentPath = currentDir.getAbsolutePath();
    }

    private static final String _SubTitleWithMovieName = "SubTitleWithMovieName";
    private static final String _IntervalSearch = "IntervalSearch";
    private static final String _ExcludeFilesRegex = "ExcludeFilesRegex";
    private static final String _Language = "Language";
    private static final String _DirectoryScan = "DirectoryScan";
    private static final String _DirectoryIgnore = "DirectoryIgnore";
    private static final String _FileIgnore = "FileIgnore";
    private static final String _RecordLog = "RecordLog";
    private static final String _DebugLog = "DebugLog";

    public String getCurrentPath() {
        return _currentPath;
    }

    public boolean getSubTitleWithMovieName() {
        return propertyFile.getProperty(_SubTitleWithMovieName).equalsIgnoreCase("S");
    }

    public String getExcludeFilesRegex() {
        return propertyFile.getProperty(_ExcludeFilesRegex);
    }

    public int getIntervalSearch() {
        return Integer.parseInt(propertyFile.getProperty(_IntervalSearch));
    }

    public SubTitleLanguage getLanguage() {
        String valor = propertyFile.getProperty(_Language, "");
        try {
            return SubTitleLanguage.valueOf(valor);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(String.format("Invalid Language: %s", e.getMessage()));
        }
    }

    public String getDirectoryScan() {
        String valor = propertyFile.getProperty(_DirectoryScan, "");
        File folderExisting = new File(valor);
        if (!folderExisting.exists())
            throw new RuntimeException(String.format("Folder not exists: %s", valor));
        return valor;
    }

    public List<String> getDirectoryIgnore() {
        String valor = propertyFile.getProperty(_DirectoryIgnore, "");
        return Arrays.asList(valor.split(";"));
    }

    public List<String> getFileIgnore() {
        String valor = propertyFile.getProperty(_FileIgnore, "");
        return Arrays.asList(valor.split(";"));
    }

    public boolean getRecordLog() {
        String valor = propertyFile.getProperty(_RecordLog, "");
        return valor.equalsIgnoreCase("Y");
    }

    public boolean getRecordDebug() {
        String valor = propertyFile.getProperty(_DebugLog, "");
        return valor.equalsIgnoreCase("Y");
    }

    public HashMap<IDownloadHandler, DownloadHandlerVO> getDownloadHandlers() {
        List<IDownloadHandler> handlersList = SubTitleManager.getExistingHandlers();
        HashMap<IDownloadHandler, DownloadHandlerVO> handlers = new HashMap<IDownloadHandler, DownloadHandlerVO>();

        for (IDownloadHandler handler : handlersList) {
            try {
                DownloadHandlerVO handlerVO = (DownloadHandlerVO) handler.getHandlerVOType().newInstance();
                Method[] metodos = handlerVO.getClass().getMethods();
                for (Method metodo : metodos) {
                    if (metodo.getName().startsWith("set")) {
                        Class<?> typeParam = metodo.getParameterTypes()[0];
                        String nomeProp = metodo.getName().substring(3);
                        String nomePropFile = handler.getClass().getSimpleName() + "." + nomeProp;
                        if (typeParam == int.class) {
                            metodo.invoke(handlerVO, Integer.parseInt(propertyFile.getProperty(nomePropFile, "0")));
                        } else if (typeParam == String.class) {
                            metodo.invoke(handlerVO, propertyFile.getProperty(nomePropFile, ""));
                        } else if (typeParam.isEnum()) {
                            String valor = propertyFile.getProperty(nomePropFile, "");
                            Object[] enumValores = typeParam.getEnumConstants();
                            for (Object objEnum : enumValores) {
                                if (objEnum.toString().equalsIgnoreCase(valor)) {
                                    metodo.invoke(handlerVO, objEnum);
                                    break;
                                }
                            }
                        }
                    }
                }
                handlers.put(handler, handlerVO);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return handlers;
    }

    public boolean getHandlerDownloadActive(IDownloadHandler handler) {
        return propertyFile.getProperty(handler.getClass().getSimpleName() + ".Download").equalsIgnoreCase("Y");
    }

    public boolean getHandlerUploadActive(IDownloadHandler handler) {
        return propertyFile.getProperty(handler.getClass().getSimpleName() + ".Upload").equalsIgnoreCase("Y");
    }

    public boolean getHasUploaded(IDownloadHandler handler, String fileName) {
        return propertyUploadFile.containsKey(handler.getClass().getSimpleName() + "." + fileName);
    }

    public void saveUploadedFile(IDownloadHandler handler, String fileName) {
        propertyUploadFile.setProperty(handler.getClass().getSimpleName() + "." + fileName, "Uploaded");
        FileOutputStream fileStream;
        try {
            fileStream = new FileOutputStream(propertyFilesUpload);
            try {
                propertyUploadFile.store(fileStream, "Files already uploaded to determined hanled");
            } finally {
                fileStream.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

}
