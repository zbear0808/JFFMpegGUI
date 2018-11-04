import java.io.File;
import java.util.ArrayList;

public class FileUtil {

    public static final String M4A = ".m4a";
    public static final String MP4 = ".mp4";
    public static final String MP3 = ".mp3";
    public static final String FLAC = ".flac";
    public static final String WAV = ".wav";
    public static ArrayList<File> allFiles = new ArrayList<>();

    public static final String[] EXTENSIONS ={M4A,MP4,MP3,FLAC,WAV};


    public static final char[] ILLEGAL_CHARS = {'.',',',':',';','\"','\'','|','\t','\n','*','?','!','=','/','\\', ' '};



    public static boolean isAudioType(File file, String extension){
        String str = file.toString();
        return str.substring(str.length()-extension.length()) //checks to see if file type is the same
                .equals(extension);
    }

    public static void getFilesOfType(File startPath, String... extensions){
        boolean isType = false;
        for (String extension: extensions){
            if (isAudioType(startPath,extension)){isType = true;}
        }
        if(isType){allFiles.add(startPath);}
        else if(startPath.isDirectory()) {
            File[] files = startPath.listFiles();
            for(File path : files) {
                getFilesOfType(path, extensions);
            }
        }
    }
    public static void clearAllFiles(){
        allFiles = new ArrayList<>();
    }

    public static String getExtension(File song){
        String ext=null;
        for(String extension:EXTENSIONS){
            if(isAudioType(song,extension)){
                ext = extension;}
        }
        return ext;
    }
    public static String makeFileNameSafe(String str){
        for (char c : ILLEGAL_CHARS) {
            str = str.replace(c, '_');
        }
        return str;
    }
}
