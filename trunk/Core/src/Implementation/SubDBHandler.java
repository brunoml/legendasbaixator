package Implementation;

import Hash.SubDBHasher;
import Interface.IDownloadHandler;
import Model.*;
import Utils.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectMax;

/**
 * Created by IntelliJ IDEA.
 * User: Brunol
 * Date: 20/05/2010
 * Time: 10:18:39
 * To change this template use File | Settings | File Templates.
 */
public class SubDBHandler implements IDownloadHandler {
    private static final String _UserAgent = "SubDB/1.0 (" + SYSTEM_NAME + "/" + VERSION_NUMBER + "; " + URL + ")";
    private static final String _BaseUrl = "http://api.thesubdb.com/";
    private DefaultHttpClient httpclient = null;
    private DownloadHandlerVO _handlerVO = null;

    private String getURLForDownload(String hash) {
        return _BaseUrl + "?action=download&language=" + getCodeLanguage() + "&hash=" + hash;
    }

    private String getCodeLanguage() {
        switch (_handlerVO.getLanguage()) {
            case pt_BR: return "pt";
            case en_US: return "en";
            case nl_NL: return "nl";
            default: return null;
        }
    }

    public String getDescription() {
        return "SubDB";
    }

    public String getSiteUrl() {
        return "http://blog.thesubdb.com";
    }

    public Class getHandlerVOType() {
        return DownloadHandlerVO.class;
    }

    public LogonType getLogonType() {
        return LogonType.None;
    }

    public SubTitleLanguage[] getSupportedLanguages() {
        SubTitleLanguage[] langs = new SubTitleLanguage[3];
        langs[0] = SubTitleLanguage.pt_BR;
        langs[1] = SubTitleLanguage.en_US;
        langs[2] = SubTitleLanguage.nl_NL;
        return langs;
    }

    public void doLogin(DownloadHandlerVO handlerVO) throws DownloadHandlerException {
        httpclient = new DefaultHttpClient();
        // Não tem login nesse site
        _handlerVO = handlerVO;
    }

    public List<SubTitleVO> getSubTitleList(VideoFileVO movieFile) {
        String movieHash = SubDBHasher.computeHash(movieFile.getFile());
        HttpGet httpGet = new HttpGet(getURLForDownload(movieHash));
        httpGet.addHeader("User-Agent", _UserAgent);
        try {
            HttpResponse response = httpclient.execute(httpGet);

            List<SubTitleVO> subTitleList = new ArrayList<SubTitleVO>();

            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity ent = response.getEntity();

                String contentDisp = response.getFirstHeader("Content-Disposition").getValue();
                contentDisp = contentDisp.replace(" attachment; filename=", "");

                InputStream entStream = ent.getContent();
                
                SubTitleVO subTitle = new SubTitleVO();
                subTitle.setID(movieHash);
                subTitle.setFileName(contentDisp);
                // Deixo a legenda aqui já porque não tenho que pegar de novo depois
                subTitle.setDescricao(FileUtils.InputToString(entStream, "ISO-8859-1"));
                subTitleList.add(subTitle);

                ent.consumeContent();
            } else
            if (response.getStatusLine().getStatusCode() != 404) {
               throw new RuntimeException("Status Response Error: " + response.getStatusLine());
            }

            return subTitleList;            
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public SubTitleVO chooseOneSubTitle(VideoFileVO movieFile, List<SubTitleVO> subList) {
        // Se houver mais de uma legenda para o filme, pega a que tem mais downloads no site, deve ser a melhor...
        return selectMax(subList, on(SubTitleVO.class).getDownloads());
    }

    public InputStream getSubTitleFile(SubTitleVO subTitleVO) {
        return FileUtils.StringToInput(subTitleVO.getDescricao());
    }

    public void doLogout() {
        // Não tem login nesse site
    }
}
