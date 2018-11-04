import java.util.Scanner;

public class Parser {


    static final String PATTERN = " ft| Ft|\\(| feat| Feat|prod|\n";
    // removes the text that isn't needed for
    static final String[] importantWords = {"remix"};// important words that could have been removed by the parsing method that need to get re inserted
    static final char[] notURLCompatibleChars = {'&','#'};
    public static String removeInfoForSearch(String string) {
        Scanner s = new Scanner(string);
        s.useDelimiter(PATTERN);

        String editedString = string;
        if (s.hasNext()){
            editedString = s.next();

        }
        for (String str : importantWords){
            if (string.contains(str) && !editedString.contains(str)){
                editedString+=" " + str;
            }
        }
        for (char c : notURLCompatibleChars){
            editedString = editedString.replace(c,' ');
        }
        return editedString;
    }
}