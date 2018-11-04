import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Scanner;
import java.io.IOException;

public class WebpageParser {
    private static final String GENIUS_URL = "https://genius.com/search?q=";// maybe use az lyrics instead
    private static final String BING_URL= "https://bing.com/search?q=";
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
        query+=" genius lyrics";
        query = query.replace(" ",SPACE_CHARACTER);
        System.out.println(query);
        Document doc = Jsoup.connect(BING_URL+query).get();

        Elements elements = doc.select("a");

        for(Element element : elements) {
            String str = element.absUrl("href");
            System.out.println(element.absUrl("href"));
            if(str.contains("genius.com") && !str.contains("genius.com/albums") && !str.contains("genius.com/artists") && !str.contains("genius.com/a/")){
                return str;}
        }
        return null;
    }
    public static String getLyrics(String... info) throws IOException{
        return getLyrics(getGeniusUrl(info));
    }
    private  static int attempts = 0;
    private static String getLyrics(String url) throws  IOException {
        Document doc = Jsoup.connect(url).get();
        String text = doc.body().text(); // this doesn't keep formatting of webpage, removes \n and \t, hopefully can be fixed

        int startIndex = text.indexOf("Lyrics") + "Lyrics".length();
        int endIndex = text.indexOf("More on Genius");
        String lyrics = text.substring(startIndex,endIndex);
        /*
        Yo I'm retarded
        wrote a million lines of code instead of just doing these easy 3 lines of code above
        I'm finna keep this here to remind me of how stupid I am
        Scanner s = new Scanner(text);
        String lyrics = "";
        String temp = s.next();
        boolean addToLyrics = false;
        int count = 0;
        while (s.hasNext()){
            System.out.print(temp+" ");
            //if(temp.equals(find a keyword that will only come up in the error case)){
                //if (attempts<=3) {
                  //  attempts++;
                   // return getLyrics(url);
                //}
               // return "";
            //}
            if((temp.equals("Lyrics")) && (count <= 0)){
                count++;
                addToLyrics = true;
                temp = s.next();
                continue;
            }
            if(temp.equals("More"))
            {
                String temp1 = s.next();
                if(temp1.equals("on")){
                    String temp2 = s.next();
                    if(temp2.equals("Genius")){
                        addToLyrics = false;
                        break;
                    }
                    temp = "More on";
                }
            }
            if(addToLyrics){
                lyrics+=temp+" ";
                temp = s.next();
                continue;
            }
            temp = s.next();
        }
        attempts = 0;
        */
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
