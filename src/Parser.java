import java.util.Scanner;

public class Parser {

   static final String PATTERN = "ft|\\(|feat|prod|\n";

    public static String removeUnneededInfo(String string) {
        Scanner s = new Scanner(string);
        s.useDelimiter(PATTERN);
        if (s.hasNext()){
            return s.next();
        }
        return string;
    }

    public static void main(String[] args) {
        System.out.println(
        removeUnneededInfo("Saturday night"));
    }
}