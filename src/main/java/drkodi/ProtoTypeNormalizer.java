package drkodi;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class ProtoTypeNormalizer {

    public interface ProtoTypeNormalizerConfiguration {

        List<String> getReplaceWithSpace();

        List<String> getDelete();
    }

    protected abstract ProtoTypeNormalizerConfiguration getConfiguration();

    public ProtoTypeNormalizer(){

    }

    public String normalize(String movieTitle){
        if(movieTitle == null)
            return null;
        String result = movieTitle;
        for(String ex : getConfiguration().getReplaceWithSpace()){
            result = Pattern.compile("\\B"+Pattern.quote(ex)+"\\B",  Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(result)
                    .replaceAll(" ");
        }
        for(String ex : getConfiguration().getDelete()){
            result = Pattern.compile("(?!\\B\\w)"+Pattern.quote(ex)+"(?<!\\w\\B)",  Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(result)
                    .replaceAll("");

        }
        // remove 'years', like '1998'
        result = result.replaceAll("\\d{4,4}", "");
        // remove multi whitespaces
        result = result.replaceAll("\\s{2,}", " ");
        return result.trim();
    }


}
