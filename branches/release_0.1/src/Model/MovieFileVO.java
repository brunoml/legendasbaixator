package Model;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Brunol
 * Date: 11/03/2010
 * Time: 17:09:58
 * To change this template use File | Settings | File Templates.
 */
public class MovieFileVO {
    private String _fileName;
    public String getFileName() {
        return _fileName;
    }
    public void setFileName(String value) {
        _fileName = value;
    }

    private String _pathDir;
    public String getPathDir() {
        return _pathDir;
    }
    public void setPathDir(String value) {
        _pathDir = value;
    }

    private String _category;
    public String getCategory() {
        return _category;
    }
    public void setCategory(String value) {
        _category = value;
    }

    private long _size;
    public long getSize() {
        return _size;
    }
    public void setSize(long value) {
        _size = value;
    }

    private Boolean _hasSubTitle;
    public Boolean getHasSubTitle() {
        return _hasSubTitle;
    }
    public void setHasSubTitle(Boolean value) {
        _hasSubTitle = value;
    }

    private String _torrentName;
    public String getTorrentName() {
        return _torrentName;
    }
    public void setTorrentName(String value) {
        _torrentName = value;
    }

    private File _file;
    public File getFile() {
        return _file;
    }
    public void setFile(File value) {
        _file = value;
    }
}
