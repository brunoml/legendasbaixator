/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utils;

import java.io.File;
import java.util.regex.Pattern;

/**
 *
 * @author Brunol
 */
public class VoUtils {
    
    public MovieVO fileToMovieVO(File file) {
        String regexTvShow = "(.*)\\.[sS](\\d{2})[eExX](\\d{2,4}).*(xvid|x264|h.264).(.[^\\.]*).*";
        Pattern patternTvShow = Pattern.compile(regexTvShow, Pattern.CASE_INSENSITIVE);



    }

}
