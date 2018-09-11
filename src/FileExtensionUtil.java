import java.io.File;
import java.util.ArrayList;

public class FileExtensionUtil {

    private static final String M4A = ".m4a";
    private static final String MP3 = ".mp3";
    private static final String FLAC = ".flac";
    private static final String[] EXTENSIONS ={M4A,MP3,FLAC};
    private static ArrayList<File> allFiles = new ArrayList<>();

    private static final char[] ca = {'.',',',':',';','\"','\'','|','\t','\n','*','?','!','=','/','\\', ' '};



    private static boolean isAudioType(File file, String extension){
        String str = file.toString();
        return str.substring(str.length()-extension.length()) //checks to see if file type is the same
                .equals(extension);
    }

    private static ArrayList<File> getFilesOfType(File startPath, String extension){
        allFiles = new ArrayList<>();
        if(isAudioType(startPath,extension)){allFiles.add(startPath);}
        else if(startPath.isDirectory()) {
            File[] files = startPath.listFiles();
            for(File file:files){
                getFilesOfType(file,extension);
            }
        }
        return allFiles;
    }

    public static String getExtension(File song){
        String ext=null;
        for(String extension:EXTENSIONS){
            if(isAudioType(song,extension)){
                ext = extension;}
        }
        return ext;
    }
}
