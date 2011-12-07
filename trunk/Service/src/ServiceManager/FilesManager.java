/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ServiceManager;

import Model.VideoFileVO;
import Utils.FileUtils;
import Utils.VoUtils;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Bruno
 */
public class FilesManager {
    private ConfigManager configManager;
    private List<String> dirsIgnore;
    private List<String> filesIgnore;

    public FilesManager(ConfigManager configManager) {
        this.configManager = configManager;
        dirsIgnore = configManager.getDirectoryIgnore();
        filesIgnore = configManager.getFileIgnore();
    }

    private List<File> getFilesWithoutSubTitles(String directory) {
        File dirFiles = new File(directory);

        final List<File> listFiles = new ArrayList<File>();

        File[] files = dirFiles.listFiles(new FilenameFilter() {
                public boolean accept(File file, String name) {
                    if ((file.isDirectory()) && (dirsIgnore.contains(file.getName())))
                        return false;
                    if ((file.isFile()) && (filesIgnore.contains(file.getName())))
                        return false;

                    if (file.isDirectory()) {
                        listFiles.addAll(getFilesWithoutSubTitles(file.getAbsolutePath()));
                        return false;
                    } else {
                        // Pega o que for Video e não tem legenda
                        return FileUtils.isMovieFile(file.getName()) && !FileUtils.hasSubTitleFile(file.getAbsolutePath(), file.getName());
                    }
                }
        });
        listFiles.addAll(Arrays.asList(files));
        return listFiles;
    }

    public List<VideoFileVO> getVideosWithoutSubTitles() {
        List<File> listFiles = getFilesWithoutSubTitles(configManager.getDirectoryScan());

        List<VideoFileVO> listVideo = new ArrayList<VideoFileVO>();
        for (File file : listFiles) {
            listVideo.add(VoUtils.fileToMovieVO(file));
        }
        return listVideo;
    }
}
