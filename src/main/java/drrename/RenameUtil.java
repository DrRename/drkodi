package drrename;

public class RenameUtil {

    public static char iterateLetter(char inputchar){
        int numValue = Character.getNumericValue(inputchar);
        System.out.println(numValue);
        return Character.forDigit(numValue, 10);
    }
}
