import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Scanner;
import java.io.IOException;

public class WebpageParser {
    private static final String GENIUS_URL = "https://genius.com/search?q=";// maybe use az lyrics instead
    private static final String BING_URL= "https://www.bing.com/search?q=site%3Agenius.com+";
    private static final String SPACE_CHARACTER = "+";
    private static final String[] EXPLICIT_CONTAINS = {"shit","fuck","bitch","nigger","nigga","asshole","fag","dick","pussy"};
    private static final String[] EXPLICIT_EQUALS = {"ass"};

    public static boolean isExplicit(String... info) throws IOException{

        return isExplicit(getLyrics(getGeniusUrl(info)));
    }

    public static boolean isExplicit(String lyrics){

        Scanner s = new Scanner(lyrics);
        while(s.hasNext()){

            String lyric = s.next();
            for(String word : EXPLICIT_CONTAINS){
                if(lyric.contains(word)){ return true;}
            }
            for(String word : EXPLICIT_EQUALS) {
                if (lyric.equals(word)) { return true;}
            }
        }
        return false;
    }

    private static String getGeniusUrl(String... songInfo) throws IOException {
        String query = "";
        for(String string:songInfo){ query+=(SPACE_CHARACTER+string);}
        query = query.replace(" ",SPACE_CHARACTER);
        System.out.println(query);
        Document doc = Jsoup.connect(BING_URL+query).get();
        System.out.println(BING_URL+query);
        Elements elements = doc.select("a");

        for(Element element : elements) {
            String str = element.absUrl("href");
            System.out.println(element.absUrl("href"));
            if(str.contains("//genius.com") && !str.contains("genius.com/albums") && !str.contains("genius.com/artists")
                    && !str.contains("genius.com/a/") && !str.contains("genius.com/discussions/") && !str.contains("genius.com/songs")
                    && !str.contains("genius.com/videos/")){
                return str;}
        }
        return null;
    }
    public static String getLyrics(String... info) throws IOException{
        return getLyrics(getGeniusUrl(info));
    }
    private static String getLyrics(String url) throws  IOException {
        Document doc = Jsoup.connect(url).get();
        doc.outputSettings().prettyPrint(false);
        String text = doc.body().wholeText();

        int startIndex = text.indexOf("Lyrics") + "Lyrics".length() + 60;// Don't judge me for these imaginary numbers
        int endIndex = text.indexOf("More on Genius") - 83;// numbers represent blank space between the lyrics and the text that notes when the lyrics start and when they end
        String lyrics = text.substring(startIndex,endIndex);

        return lyrics;

    }
    public static void main(String[] args) throws Exception {
        String lyrics = getLyrics(getGeniusUrl("In Tune","Metro boomin","double or nothing"));
        System.out.println(lyrics);
        System.out.println(isExplicit(lyrics));
        lyrics = getLyrics(getGeniusUrl("intro","kod","j. cole"));

        System.out.println(lyrics);
        System.out.println(isExplicit(lyrics));

    }

}
