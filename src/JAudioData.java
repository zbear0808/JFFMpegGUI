import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.flac.FlacTagReader;
import org.jaudiotagger.audio.flac.FlacTagWriter;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.audio.mp4.Mp4TagReader;
import org.jaudiotagger.audio.mp4.Mp4TagWriter;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;
import org.jaudiotagger.tag.mp4.Mp4FieldKey;
import org.jaudiotagger.tag.mp4.Mp4Tag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.io.File;
import java.io.RandomAccessFile;

public class JAudioData{
    private static final String PATH = "C:\\Users\\Zubair\\music";
    //private static final String PATH = "C:\\Users\\Zubair\\Documents\\m4a musics\\Jay-Z & Kanye West - Watch The Throne [Explicit]_CD";
    private static final String OUTPUT = "C:\\Users\\Zubair\\Documents\\outputMusic";
    private static final String PURCHASED_AAC = "C:\\Users\\Zubair\\Documents\\Purchased AAC.m4a";
    private static final String TEMP = OUTPUT+"\\temp";
    private static final String M4A = ".m4a";
    private static final String MP4 = ".mp4";
    private static final String MP3 = ".mp3";
    private static final String FLAC = ".flac";

    private static char[] ca = {'.',',',':',';','\"','\'','|','\t','\n','*','?','!','=','/','\\', ' '};
    private static final String[] EXTENSIONS ={M4A,MP3,FLAC};
    private static final String EXPLICIT = "1";
    private static ArrayList<File> allFiles = new ArrayList<>();
    private static final Mp4FieldKey[] basicKeys =
            {Mp4FieldKey.TITLE,
            Mp4FieldKey.TRACK,
            Mp4FieldKey.DISCNUMBER,
            Mp4FieldKey.GENRE,
            Mp4FieldKey.ALBUM,
            Mp4FieldKey.ALBUM_ARTIST,
            Mp4FieldKey.ARTIST,
            Mp4FieldKey.COMPOSER,
            Mp4FieldKey.DAY, Mp4FieldKey.MM_ORIGINAL_YEAR, Mp4FieldKey.COPYRIGHT};
    private static final FieldKey[] fieldKeys =
    {FieldKey.TITLE,
    FieldKey.TRACK,
    FieldKey.DISC_NO,
    FieldKey.GENRE,
    FieldKey.ALBUM,
    FieldKey.ALBUM_ARTIST,
    FieldKey.ARTIST,
    FieldKey.COMPOSER, FieldKey.YEAR, FieldKey.COPYRIGHT};
    private static final String[] genericTitles = {"intro","introduction","outro","bonus"};
    private Tag tag;
    private String EXTENSION;
    private File tempFileFolder;
    private File songFile;

    public JAudioData(File song, File temp) throws Exception{
        EXTENSION = getExtension(song);
        if(EXTENSION == null){
            System.err.println(song.toString()+" Is not a valid audio file ");return;
        }
        if (temp.isDirectory()) {
            tempFileFolder = temp;
        }
        songFile = song;
        if(EXTENSION.equals(M4A)){tag = getMp4Tag(songFile);}
        else if (EXTENSION.equals(MP3)){tag = getMp3Tag(songFile);}
        else if (EXTENSION.equals(FLAC)){tag = getFlacTag(songFile);}

    }
    public static String getExtension(File song){
        String ext=null;
        for(String extension:EXTENSIONS){
            if(isAudioType(song,extension)){
                ext = extension;}
        }
        return ext;
    }
    public static Mp4Tag getMp4Tag(File song) throws
            org.jaudiotagger.audio.exceptions.CannotReadException,
            IOException{
        Mp4TagReader reader = new Mp4TagReader();
        RandomAccessFile raf = new RandomAccessFile(song,"rw");
        return reader.read(raf);
    }
    public static Tag getMp3Tag(File song) throws Exception{
        MP3File f = (MP3File) AudioFileIO.read(song);
        return f.getTag();
    }
    public static FlacTag getFlacTag(File song) throws Exception{
        FlacTagReader reader = new FlacTagReader();
        return reader.read(song.toPath());
    }

    public void save() throws Exception{
        String temp = tempFileFolder.toString()+"\\"+"temp"+EXTENSION;
        if(EXTENSION.equals(M4A) || EXTENSION.equals(MP4)){
            Mp4TagWriter writer = new Mp4TagWriter();
            Files.deleteIfExists(new File(temp).toPath());
            RandomAccessFile tempRaf = new RandomAccessFile(temp,"rw");

            writer.write(tag,new RandomAccessFile(songFile,"rw"),tempRaf);
            Files.deleteIfExists(songFile.toPath());
            Files.copy(new File(temp).toPath(),songFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(new File(temp).toPath());
        }
        else if (EXTENSION.equals(MP3)){
            AudioFile audioFile = AudioFileIO.read(songFile);
            audioFile.setTag(tag);
            audioFile.setFile(songFile);
        }
        else if (EXTENSION.equals(FLAC)){
            FlacTagWriter writer = new FlacTagWriter();
            writer.write(tag,songFile.toPath());
        }
    }
    public String getArtist(){
        return tag.getFirst(FieldKey.ARTIST);
    }
    public String getTitle(){
        return tag.getFirst(FieldKey.TITLE);
    }
    public String getTrack(){
        return tag.getFirst(FieldKey.TRACK);
    }
    public String getDisc(){
        return tag.getFirst(FieldKey.DISC_NO);
    }
    public String getAlbum(){
        return tag.getFirst(FieldKey.ALBUM);
    }
    public String getAlbumArtist(){
        return tag.getFirst(FieldKey.ALBUM_ARTIST);
    }
    public String getComposer(){
        return tag.getFirst(FieldKey.COMPOSER);
    }
    public String getLyrics() { return tag.getFirst(FieldKey.LYRICS); }

    public void setArt(File art)throws Exception{
        Artwork artwork = ArtworkFactory.createArtworkFromFile(art);
        tag.deleteArtworkField();
        tag.addField(artwork);
        tag.setField(artwork);
    }
    public void setLyrics(String lyrics) throws Exception{
        tag.addField(FieldKey.LYRICS, lyrics);
        tag.setField(FieldKey.LYRICS, lyrics);
    }

    public static void setArtwork(File art, File m4a) throws
            org.jaudiotagger.audio.exceptions.CannotReadException,
            org.jaudiotagger.tag.FieldDataInvalidException,
            org.jaudiotagger.audio.exceptions.CannotWriteException,
            IOException{
        Mp4TagReader reader = new Mp4TagReader();
        Mp4TagWriter writer = new Mp4TagWriter();
        RandomAccessFile song = new RandomAccessFile(m4a.toString(),"rw");
        Mp4Tag mp4tag = reader.read(song);

        Artwork artwork = ArtworkFactory.createArtworkFromFile(art);
        mp4tag.addField(artwork);
        mp4tag.setField(artwork);

        Files.deleteIfExists(new File("temp.m4a").toPath());
        RandomAccessFile temp = new RandomAccessFile("temp.m4a","rw");
        writer.write(mp4tag,song,temp);

        Files.deleteIfExists(m4a.toPath());
        Files.copy(new File("temp.m4a").toPath(),m4a.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.deleteIfExists(new File("temp.m4a").toPath());
    }
    public static String getAlbumMp3(File mp3) throws Exception{

        MP3File f = (MP3File) AudioFileIO.read(mp3);
        Tag tag = f.getTag();
        try{
        String album = tag.getFirst(FieldKey.ALBUM);

        return album;}
        catch (NullPointerException e){return "no Album";}
    }

    private static boolean isAudioType(File file, String extension){
        String str = file.toString();
        return str.substring(str.length()-extension.length()) //checks to see if file type is the same
                .equals(extension);
    }

    private static void addFiles(File startPath, String extension){
        if(isAudioType(startPath,extension)){allFiles.add(startPath);}
        else if(startPath.isDirectory()) {
            File[] files = startPath.listFiles();
            for(File file:files){
                addFiles(file,extension);
            }
        }
    }

    public void setExplicitItunes(File iTunesSong) throws Exception{
        Mp4Tag purchasedTag = getMp4Tag(iTunesSong);
        //purchasedTag.deleteArtworkField();
        for(FieldKey key:FieldKey.values()){
            try {
                String data = tag.getFirst(key);
                if (data != null && !(data.equals(""))) {
                    purchasedTag.setField(key, data);
                } else {
                    purchasedTag.deleteField(key);
                }
            }
            catch (org.jaudiotagger.tag.KeyNotFoundException e){}
        }
        purchasedTag.setField(Mp4FieldKey.RATING,EXPLICIT);
        tag = purchasedTag;
    }
    public static void setItunesExplicit(File testFile) throws Exception {
        Mp4TagReader reader = new Mp4TagReader();
        Mp4TagWriter writer = new Mp4TagWriter();

        RandomAccessFile test = new RandomAccessFile(testFile.toString(), "rw");
        RandomAccessFile purchasedAAC = new RandomAccessFile(
                PURCHASED_AAC, "rw");
        Mp4Tag testTag = reader.read(test);
        Mp4Tag purchasedAacTag = reader.read(purchasedAAC);
        purchasedAacTag.deleteArtworkField();

        for(FieldKey key:FieldKey.values()){
            try{
            String data = testTag.getFirst(key);
            if(data!=null && !(data.equals(""))) {
                purchasedAacTag.setField(key,data);}
            else{purchasedAacTag.deleteField(key);}
            }
            catch (org.jaudiotagger.tag.KeyNotFoundException e){}
        }
        purchasedAacTag.setField(Mp4FieldKey.RATING,"1");


        Files.deleteIfExists(new File("temp.m4a").toPath());
        RandomAccessFile temp = new RandomAccessFile("temp.m4a","rw");

        writer.write(purchasedAacTag,test,temp);
        Files.deleteIfExists(testFile.toPath());
        Files.copy(new File("temp.m4a").toPath(),testFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.deleteIfExists(new File("temp.m4a").toPath());

    }
    public void printAllData(){
        for(FieldKey key:FieldKey.values()){
            try {
                String data = tag.getFirst(key);
                if (data != null && !(data.equals(""))) {
                    System.out.println(key.toString()+":\t"+data);
                }
            }
            catch (org.jaudiotagger.tag.KeyNotFoundException e){}
        }
    }



    public static void guiTest(String path, String out, File ffmpeg, File tempFileFolder, File purchasedAAC, int bitRate)
            throws Exception,
            org.jaudiotagger.audio.exceptions.CannotReadException,
            org.jaudiotagger.tag.FieldDataInvalidException,
            org.jaudiotagger.audio.exceptions.CannotWriteException,
            IOException {

        addFiles(new File(path),MP3);

        for(File file: allFiles) {
            //for(int i=0;i<10;i++){
            String output = out;
            String album = getAlbumMp3(file);
            for (char c : ca) {
                album = album.replace(""+c, "_");
            }
            output+="\\"+album;
            File directory = new File(output);
            directory.mkdir();

            JAudioData j = new JAudioData(file, new File("temp.m4a"));
            String albumArtist = j.getAlbumArtist();
            String title = j.getTitle();
            boolean explicit = false;

            // if there is no album artist use artist
            if( albumArtist==null || albumArtist.equals("")){
                String artist = j.getArtist();
                if (artist!=null && !artist.equals("")){
                    albumArtist = artist;
                }
            }
            boolean useAlbum = false;
            for(String str : genericTitles){
                if (title==null || title.equals("")){break;}
                if(str.equalsIgnoreCase(title)){
                    useAlbum = true;
                }
            }

            if ( albumArtist!=null && !albumArtist.equals("") && title!=null && !title.equals("") ){
                if (useAlbum) {
                    String albumTitle = j.getAlbum();

                    try {

                        explicit = WebpageParser.isExplicit(albumArtist, Parser.removeInfoForSearch(title), Parser.removeInfoForSearch(albumTitle));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        explicit = WebpageParser.isExplicit(Parser.removeInfoForSearch(title), albumArtist);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            JFFMpeg.mp3Tom4a(file,new File(output),new File(out+"\\temp"+allFiles.indexOf(file)+".jpeg"),
                    ffmpeg,bitRate, explicit);

            //JFFMpeg.mp3Tom4a(allFiles.get(i),new File(output),new File(TEMP+i+".jpeg"));

        }
        //allFiles = new ArrayList<>();
        //addFiles(new File(OUTPUT),M4A);
        //setAllExplicit();

    }

    public static void main(String[] args)
            throws Exception,
            org.jaudiotagger.audio.exceptions.CannotReadException,
            org.jaudiotagger.tag.FieldDataInvalidException,
            org.jaudiotagger.audio.exceptions.CannotWriteException,
            IOException {

        addFiles(new File(PATH),MP3);

        //for(File file: allFiles) {
            for(int i=0;i<10;i++){
            String output = OUTPUT;
            String album = getAlbumMp3(allFiles.get(i));
            //String album = getAlbumMp3(file);
            char[] ca = {'.',',',':',';','\"','\'','|','\t','\n',' ','*','?','!','=','/','\\'};
            for (char c : ca) {
                album = album.replace(""+c, "_");
            }
            output+="\\"+album;
            File directory = new File(output);
            directory.mkdir();
            //JFFMpeg.mp3Tom4a(file,new File(output),new File(TEMP+allFiles.indexOf(file)+".jpeg")
            //        ,new File ("C:\\Users\\Zubair\\Desktop\\JFFMpeg\\ffmpeg\\bin"),300);
            JFFMpeg.mp3Tom4a(allFiles.get(i),new File(output),new File(TEMP+i+".jpeg"),
                    new File ("C:\\Users\\Zubair\\Desktop\\JFFMpeg\\ffmpeg\\bin"),300, true);


        }

    }
}

