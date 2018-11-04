import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Converter {

    private static ArrayList<File> allFiles = new ArrayList<>();
    private static final String[] genericTitles = {"intro","introduction","outro","bonus"};
    public static void gui(String path, String out, File ffmpeg, File tempFileFolder, File purchasedAAC, int bitRate)
            throws Exception,
            org.jaudiotagger.audio.exceptions.CannotReadException,
            org.jaudiotagger.tag.FieldDataInvalidException,
            org.jaudiotagger.audio.exceptions.CannotWriteException,
            IOException {
        //FileUtil.clearAllFiles(); May or may not want to keep this. If someone hits convert multiple times it doesn't do the same files again. but user may want to select different files
        FileUtil.getFilesOfType(new File(path), FileUtil.MP3,FileUtil.FLAC);
        allFiles = FileUtil.allFiles;
        for(File file: allFiles) {
            JAudioData song = new JAudioData(file, tempFileFolder);


            String album = song.getAlbum();
            album = FileUtil.makeFileNameSafe(album);

            out+="\\"+album;
            File albumFolder = new File(out);
            albumFolder.mkdir();

            String name = file.getName();

            String m4aOutput = name.substring(0, name.lastIndexOf('.')) + FileUtil.M4A;
            m4aOutput = albumFolder.toString() + "\\" + m4aOutput;

            File m4aOutputFile = new File(m4aOutput);
            File coverArt = new File(out+"\\temp"+allFiles.indexOf(file)+".jpeg");
            JFFMpeg ffmpegConverter = new JFFMpeg(ffmpeg);
            JAudioData m4aFileData;
            boolean hasAlbumArt = ffmpegConverter.convertToM4a(file, m4aOutputFile, coverArt,bitRate);
            String lyrics = getLyricsWithoutErrors(song);
            if(hasAlbumArt) {
                m4aFileData = new JAudioData(m4aOutputFile, tempFileFolder);
                m4aFileData.setArt(coverArt);
                m4aFileData.save();
                Files.delete(coverArt.toPath());
            }
            m4aFileData = new JAudioData(m4aOutputFile, tempFileFolder);

            m4aFileData.setLyrics(lyrics);
            boolean explicit = WebpageParser.isExplicit(lyrics);
            if (explicit){
                m4aFileData.setExplicitItunes(purchasedAAC);

            }
            m4aFileData.save();
        }
    }
    private static String  getLyricsWithoutErrors (JAudioData j) {

        String albumArtist = j.getAlbumArtist();
        String title = j.getTitle();
        String allLyrics = "";

        // if there is no album artist use artist
        if( albumArtist==null || albumArtist.equals("")){
            String artist = j.getArtist();
            if (artist!=null && !artist.equals("")){
                albumArtist = artist;
            }
        }
        boolean useAlbum = false;

        for(String genericTitle : genericTitles){
            if (title==null || title.equals("")){break;}
            if(genericTitle.equalsIgnoreCase(title)){
                useAlbum = true;
            }
        }

        if ( albumArtist!=null && !albumArtist.equals("") && title!=null && !title.equals("") ){
            if (useAlbum) {
                String albumTitle = j.getAlbum();

                try {

                    allLyrics = WebpageParser.getLyrics(albumArtist, Parser.removeInfoForSearch(title), Parser.removeInfoForSearch(albumTitle));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    allLyrics = WebpageParser.getLyrics(Parser.removeInfoForSearch(title), albumArtist);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return allLyrics;
    }

    public static void main(String[] args) throws Exception{
       String lyrics = getLyricsWithoutErrors(new JAudioData(new File("C:\\Users\\Zubair\\Documents\\music\\4th_Quarter\\00 4th Quarter.m4a"),
                new File("C:\\Users\\Zubair\\Videos\\Captures")));
        System.out.println("\n\n\n\t"+lyrics);
    }
}
