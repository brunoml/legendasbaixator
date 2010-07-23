package Model;

/**
 * Created by IntelliJ IDEA.
 * User: Brunol
 * Date: 17/05/2010
 * Time: 11:48:47
 * To change this template use File | Settings | File Templates.
 */
public class DownloadHandlerException extends Exception {
    private String _resource;
    public String getResource() {
        return _resource;
    }

    private String[] _args;
    public String[] getArgs() {
        return _args;
    }

    public DownloadHandlerException(String message) {
        super(message);
    }

    public DownloadHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DownloadHandlerException(String resource, String[] args) {
        super();
        _resource = resource;
        _args = args;
    }

    public DownloadHandlerException(String resource, String[] args,  String message) {
        super(message);
        _resource = resource;
        _args = args;
    }

    public DownloadHandlerException(String resource, String[] args,  String message, Throwable cause) {
        super(message, cause);
        _resource = resource;
        _args = args;
    }
}
