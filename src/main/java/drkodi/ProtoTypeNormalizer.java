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
            result = Pattern.compile(ex, Pattern.LITERAL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(result)
                    .replaceAll(Matcher.quoteReplacement(" "));
        }
        for(String ex : getConfiguration().getDelete()){
            result = Pattern.compile(ex, Pattern.LITERAL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(result)
                    .replaceAll(Matcher.quoteReplacement(""));

        }
        result = result.replaceAll("\\d{2,}", "");
        result = result.replaceAll("\\s{2,}", " ");
        return result;
    }


}
