package Implementation;

import Hash.SubDBHasher;
import Interface.IDownloadHandler;
import Manager.Core;
import Model.*;
import Utils.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.lf5.util.StreamUtils;

import java.io.ByteArrayInputStream;
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
    private DefaultHttpClient httpclient = null;
    private DownloadHandlerVO _handlerVO = null;
    private static final String _BaseUrl = "http://api.thesubdb.com/";

    private String getURLForDownload(String hash) {
        return _BaseUrl + "?action=download&language=" + getCodeLanguage() + "&hash=" + hash;
    }

    private String getCodeLanguage() {
        switch (_handlerVO.getLanguage()) {
            case pt_BR: return "pt";
            case en_US: return "en";
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

    public SubTitleLanguage[] getSupportedLanguages() {
        SubTitleLanguage[] langs = new SubTitleLanguage[2];
        langs[0] = SubTitleLanguage.en_US;
        langs[1] = SubTitleLanguage.pt_BR;
        return langs;
    }

    public void doLogin(DownloadHandlerVO handlerVO) throws DownloadHandlerException {
        httpclient = new DefaultHttpClient();
        // Não tem login nesse site
        _handlerVO = handlerVO;
    }

    public List<SubTitleVO> getSubTitleList(MovieFileVO movieFile) {
        String hash = SubDBHasher.computeHash(movieFile.getFile());
        HttpPost httpost = new HttpPost(getURLForDownload(hash));
        httpost.addHeader("User-Agent", Core.USER_AGENT);
        try {
            HttpResponse response = httpclient.execute(httpost);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Status Response Error: " + response.getStatusLine());
            }

            HttpEntity ent = response.getEntity();

            List<SubTitleVO> subTitleList = new ArrayList<SubTitleVO>();
            SubTitleVO subTitle = new SubTitleVO();
            subTitle.setID(hash);
            subTitle.setFileName(response.getFirstHeader("Content-Disposition").getValue());
            subTitleList.add(subTitle);

            ent.consumeContent();

            return subTitleList;            
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public SubTitleVO chooseOneSubTitle(MovieFileVO movieFile, List<SubTitleVO> subList) {
        // Se houver mais de uma legenda para o filme, pega a que tem mais downloads no site, deve ser a melhor...
        return selectMax(subList, on(SubTitleVO.class).getDownloads());
    }

    public InputStream getSubTitleFile(SubTitleVO subTitleVO) {
        HttpPost httpost = new HttpPost(getURLForDownload(subTitleVO.getID()));
        httpost.addHeader("User-Agent", Core.USER_AGENT);
        try {
            HttpResponse response = httpclient.execute(httpost);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Status Response Error: " + response.getStatusLine());
            }

            HttpEntity ent = response.getEntity();
            InputStream entStream = ent.getContent();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(FileUtils.InputToByte(entStream));
            ent.consumeContent();

            return inputStream;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void doLogout() {
        // Não tem login nesse site
    }
}
