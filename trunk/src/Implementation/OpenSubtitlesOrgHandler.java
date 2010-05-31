package Implementation;

import Interface.IDownloadHandler;
import Manager.Core;
import Model.*;
import Utils.FileUtils;
import Hash.OpenSubtitlesHasher;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.hamcrest.Matchers;

import java.io.*;
import java.net.URL;
import java.util.*;

import static ch.lambdaj.Lambda.*;
import static ch.lambdaj.Lambda.on;

/**
 * Created by IntelliJ IDEA.
 * User: Brunol
 * Date: 12/05/2010
 * Time: 11:08:36
 * To change this template use File | Settings | File Templates.
 */
public class OpenSubtitlesOrgHandler implements IDownloadHandler {
    private static final String _UrlXmlRpc = "http://api.opensubtitles.org/xml-rpc";
    private XmlRpcClient xmlRpcClient;
    private DownloadHandlerVO _handlerVO;
    private String _tokenConnection;

    private String getCodeLanguage(SubTitleLanguage language) {
        switch (language) {
            case pt_BR: return "pb";
            case pt_PT: return "pt";
            case en_US: return "en";
            case es_ES: return "es";
            case sq_AL: return "sq";
            case ar_AE: return "ar";
            case ar_BH: return "ar";
            case ar_DZ: return "ar";
            case ar_EG: return "ar";
            case ar_IQ: return "ar";
            case ar_JO: return "ar";
            case ar_KW: return "ar";
            case ar_LB: return "ar";
            case ar_LY: return "ar";
            case ar_MA: return "ar";
            case ar_OM: return "ar";
            case ar_QA: return "ar";
            case ar_SA: return "ar";
            case ar_SY: return "ar";
            case ar_TN: return "ar";
            case ar_YE: return "ar";
            case bg_BG: return "bg";
            case ca_ES: return "ca";
            case zh_CHS: return "zh";
            case zh_CHT: return "zh";
            case zh_CN: return "zh";
            case zh_HK: return "zh";
            case zh_MO: return "zh";
            case zh_SG: return "zh";
            case zh_TW: return "zh";
            case cs_CZ: return "cs";
            case da_DK: return "da";
            case nl_NL: return "nl";
            case et_EE: return "et";
            case fi_FI: return "fi";            
            case fr_BE: return "fr";
            case fr_CA: return "fr";
            case fr_CH: return "fr";
            case fr_FR: return "fr";
            case fr_LU: return "fr";
            case fr_MC: return "fr";
            case ka_GE: return "ka";
            case de_DE: return "de";
            case de_AT: return "de";
            case de_CH: return "de";
            case de_LI: return "de";
            case de_LU: return "de";
            case gl_ES: return "gl";
            case el_GR: return "el";
            case he_IL: return "he";
            case hr_HR: return "hr";
            case hu_HU: return "hu";
            case id_ID: return "id";
            case it_IT: return "it";
            case it_CH: return "it";
            case ja_JP: return "ja";
            case ko_KR: return "ko";
            case mk_MK: return "mk";
            case ms_BN: return "ms";
            case ms_MY: return "ms";
            case nb_NO: return "no";
            case oc_FR: return "oc";
            case oc_IT: return "oc";
            case oc_ES: return "oc";
            case oc_MC: return "oc";
            case fa_IR: return "fa";
            case pl_PL: return "pl";
            case ru_RU: return "ru";
            case sr_SP: return "sr";
            case si_LK: return "si";
            case sk_SK: return "sk";
            case sl_SI: return "sl";
            case sv_SE: return "sv";
            case sv_FI: return "sv";
            case tr_TR: return "tr";
            case uk_UA: return "uk";
            case vi_VN: return "vi";
            case ro_RO: return "ro";
            default: return null;
        }
    }

    private void validarRespStatus(String status, String method) {
        if ((!status.equalsIgnoreCase("200 OK")) && (!status.equalsIgnoreCase("206 Partial content; message")))
            throw new RuntimeException("Error on " + method + ": " + status);
    }

    private Object executeRpcMethod(String method, Object[] params) {
        try {
            return xmlRpcClient.execute(method, params);
        } catch (XmlRpcException e) {
            throw new RuntimeException("Erro on calling method " + method, e);
        }
    }

    private Object executeRpcMethod(String method, List params) {
        try {
            return xmlRpcClient.execute(method, params);
        } catch (XmlRpcException e) {
            throw new RuntimeException("Erro on calling method " + method, e);
        }
    }

    public String getDescription() {
        return "OpenSubTitles.org";
    }

    public String getSiteUrl() {
        return "http://www.opensubtitles.org";
    }

    public Class getHandlerVOType() {
        return DownloadHandlerVO.class;
    }

    public SubTitleLanguage[] getSupportedLanguages() {
        SubTitleLanguage[] langs = new SubTitleLanguage[78];
        langs[0] = SubTitleLanguage.pt_BR;
        langs[1] = SubTitleLanguage.pt_PT;
        langs[2] = SubTitleLanguage.en_US;
        langs[3] = SubTitleLanguage.es_ES;
        langs[4] = SubTitleLanguage.sq_AL;
        langs[5] = SubTitleLanguage.ar_AE;
        langs[6] = SubTitleLanguage.ar_BH;
        langs[7] = SubTitleLanguage.ar_DZ;
        langs[8] = SubTitleLanguage.ar_EG;
        langs[9] = SubTitleLanguage.ar_IQ;
        langs[10] = SubTitleLanguage.ar_JO;
        langs[11] = SubTitleLanguage.ar_KW;
        langs[12] = SubTitleLanguage.ar_LB;
        langs[13] = SubTitleLanguage.ar_LY;
        langs[14] = SubTitleLanguage.ar_MA;
        langs[15] = SubTitleLanguage.ar_OM;
        langs[16] = SubTitleLanguage.ar_QA;
        langs[17] = SubTitleLanguage.ar_SA;
        langs[18] = SubTitleLanguage.ar_SY;
        langs[19] = SubTitleLanguage.ar_TN;
        langs[20] = SubTitleLanguage.ar_YE;
        langs[21] = SubTitleLanguage.bg_BG;
        langs[22] = SubTitleLanguage.ca_ES;
        langs[23] = SubTitleLanguage.zh_CHS;
        langs[24] = SubTitleLanguage.zh_CHT;
        langs[25] = SubTitleLanguage.zh_CN;
        langs[26] = SubTitleLanguage.zh_HK;
        langs[27] = SubTitleLanguage.zh_MO;
        langs[28] = SubTitleLanguage.zh_SG;
        langs[29] = SubTitleLanguage.zh_TW;
        langs[30] = SubTitleLanguage.cs_CZ;
        langs[31] = SubTitleLanguage.da_DK;
        langs[32] = SubTitleLanguage.nl_NL;
        langs[33] = SubTitleLanguage.et_EE;
        langs[34] = SubTitleLanguage.fi_FI;
        langs[35] = SubTitleLanguage.fr_BE;
        langs[36] = SubTitleLanguage.fr_CA;
        langs[37] = SubTitleLanguage.fr_CH;
        langs[38] = SubTitleLanguage.fr_FR;
        langs[39] = SubTitleLanguage.fr_LU;
        langs[40] = SubTitleLanguage.fr_MC;
        langs[41] = SubTitleLanguage.ka_GE;
        langs[42] = SubTitleLanguage.de_DE;
        langs[43] = SubTitleLanguage.de_AT;
        langs[44] = SubTitleLanguage.de_CH;
        langs[45] = SubTitleLanguage.de_LI;
        langs[46] = SubTitleLanguage.de_LU;
        langs[47] = SubTitleLanguage.gl_ES;
        langs[48] = SubTitleLanguage.el_GR;
        langs[49] = SubTitleLanguage.he_IL;
        langs[50] = SubTitleLanguage.hr_HR;
        langs[51] = SubTitleLanguage.hu_HU;
        langs[52] = SubTitleLanguage.id_ID;
        langs[53] = SubTitleLanguage.it_IT;
        langs[54] = SubTitleLanguage.it_CH;
        langs[55] = SubTitleLanguage.ja_JP;
        langs[56] = SubTitleLanguage.ko_KR;
        langs[57] = SubTitleLanguage.mk_MK;
        langs[58] = SubTitleLanguage.ms_BN;
        langs[59] = SubTitleLanguage.ms_MY;
        langs[60] = SubTitleLanguage.nb_NO;
        langs[61] = SubTitleLanguage.oc_FR;
        langs[62] = SubTitleLanguage.oc_IT;
        langs[63] = SubTitleLanguage.oc_ES;
        langs[64] = SubTitleLanguage.oc_MC;
        langs[65] = SubTitleLanguage.fa_IR;
        langs[66] = SubTitleLanguage.pl_PL;
        langs[67] = SubTitleLanguage.ru_RU;
        langs[68] = SubTitleLanguage.sr_SP;
        langs[69] = SubTitleLanguage.si_LK;
        langs[70] = SubTitleLanguage.sk_SK;
        langs[71] = SubTitleLanguage.sl_SI;
        langs[72] = SubTitleLanguage.sv_SE;
        langs[73] = SubTitleLanguage.sv_FI;
        langs[74] = SubTitleLanguage.tr_TR;
        langs[75] = SubTitleLanguage.uk_UA;
        langs[76] = SubTitleLanguage.vi_VN;
        langs[77] = SubTitleLanguage.ro_RO;
        return langs;
    }

    public void doLogin(DownloadHandlerVO handlerVO) throws DownloadHandlerException {
        try {
            XmlRpcClientConfigImpl xmlRpcConfig = new XmlRpcClientConfigImpl();
            xmlRpcConfig.setServerURL(new URL(_UrlXmlRpc));
            xmlRpcConfig.setEnabledForExtensions(true);
            xmlRpcConfig.setUserAgent(Core.USER_AGENT);
            xmlRpcClient = new XmlRpcClient();
            xmlRpcClient.setConfig(xmlRpcConfig);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        _handlerVO = handlerVO;

        Vector<String> params = new Vector<String>();
        params.add(_handlerVO.getUserName());
        params.add(_handlerVO.getPassword());
        params.add(getCodeLanguage(_handlerVO.getLanguage()));
        params.add(Core.USER_AGENT);

        Map resp = (Map)executeRpcMethod("LogIn", params);

        validarRespStatus((String)resp.get("status"), "LogIn");
        
        _tokenConnection = (String)resp.get("token");
    }

    @SuppressWarnings({"unchecked", "unsafe"})
    public List<SubTitleVO> getSubTitleList(MovieFileVO movieFile) {
        List<SubTitleVO> subTitleList = new ArrayList<SubTitleVO>();

        HashMap<String, Object> movieMap = new HashMap<String, Object>();
        //movieMap.put("sublanguageid", getCodeLanguage(_handlerVO.getLanguage()));
        movieMap.put("sublanguageid", "all");
        movieMap.put("moviehash", OpenSubtitlesHasher.computeHash(movieFile.getFile()));
        movieMap.put("moviebytesize", movieFile.getSize());
        Vector<Serializable> params = new Vector<Serializable>();
        params.add(_tokenConnection);
        params.add(new Object[] { movieMap });

        Map resp = (Map)executeRpcMethod("SearchSubtitles", params);

        Object respData = resp.get("data");

        if ((respData.getClass() == Boolean.class) && (!(Boolean)respData))
            return subTitleList;

        Map[] subtitlesResp = (Map[])respData;

        for(Map subtitleResp : subtitlesResp) {
            SubTitleVO subTitleVO = new SubTitleVO();
            subTitleVO.setID((String)subtitleResp.get("IDSubtitleFile"));
            subTitleVO.setDescricao((String)subtitleResp.get("MovieName"));
            subTitleVO.setFileName((String)subtitleResp.get("SubFileName"));
            subTitleVO.setCds(Integer.parseInt((String)subtitleResp.get("SubSumCD")));
            subTitleVO.setDownloads(Integer.parseInt((String)subtitleResp.get("SubDownloadsCnt")));
            subTitleVO.setMovieSize(Long.parseLong((String)subtitleResp.get("SubDownloadsCnt")));
            subTitleVO.setRelease((String)subtitleResp.get("MovieReleaseName"));
            subTitleList.add(subTitleVO);
        }
        return subTitleList;
    }

    public SubTitleVO chooseOneSubTitle(MovieFileVO movieFile, List<SubTitleVO> subList) {
        // Filtra pelo tamanho do arquivo, se não sobrar nada ignora, isso nem sempre é confiável
        List<SubTitleVO> possibleSubTitle1 = filter(having(on(SubTitleVO.class).getMovieSize(), Matchers.equalTo(movieFile.getSize())), subList);
        if (possibleSubTitle1.size() == 0)
            possibleSubTitle1 = subList;

        // Tento filtar pela extensão da legenda, podem haver vários tipos
        List<SubTitleVO> possibleSubTitle2 = filter(having(FileUtils.getExtension(on(SubTitleVO.class).getFileName()), Matchers.equalToIgnoringCase(_handlerVO.getPreferedExtSubTitle().toString())), possibleSubTitle1);
        if (possibleSubTitle2.size() == 0)
            possibleSubTitle2 = possibleSubTitle1;

        // Se houver mais de uma legenda para o filme, pega a que tem mais downloads no site
        return selectMax(possibleSubTitle2, on(SubTitleVO.class).getDownloads());
    }

    public InputStream getSubTitleFile(SubTitleVO subTitleVO) {
        Vector params = new Vector();
        params.add(_tokenConnection);
        params.add(new int[] { Integer.parseInt(subTitleVO.getID()) });

        Map resp = (Map)executeRpcMethod("DownloadSubtitles", params);

        validarRespStatus((String)resp.get("status"), "DownloadSubtitles");

        Map[] subtitlesResp = (Map[])resp.get("data");

        if (subtitlesResp.length == 0)
            throw new RuntimeException("No subtitles returned from server");

        return FileUtils.inflateFromGZip(FileUtils.decodeBase64((String)subtitlesResp[0].get("data")));
    }

    public void doLogout() {
        if (xmlRpcClient == null)
            return;

        Vector<String> params = new Vector<String>();
        params.add(_tokenConnection);

        Map resp = (Map)executeRpcMethod("LogOut", params);

        validarRespStatus((String)resp.get("status"), "LogOut");
    }
}
