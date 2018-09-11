import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.io.InputStreamReader;

public class JFFMpeg {
    private static  final String MP3 = ".mp3";
    private static final String M4A = ".m4a";
    private static final String FLAC = ".flac";
    private static final String TEMP = "C:\\Users\\Zubair\\Documents\\music\\temp.m4a";
    private File ffmpegPath;

    public JFFMpeg(File ffmpeg) {
        if(ffmpeg.isDirectory()){ffmpegPath = ffmpeg;}
        else {
            System.err.println("The path given isn't a directory");
            throw new IllegalArgumentException();
        }
    }


    public static void mp3Tom4a(File mp3File, File m4aFolder, File coverArt, File ffmpegBin, int bitRate, boolean explicit) throws Exception {

        String input = mp3File.toString();
        String name = mp3File.getName();

        String output = name.substring(0, name.indexOf(MP3)) + M4A;
        output = m4aFolder.toString() + "\\" + output;
        //cd ffmpeg\bin && ffmpeg -y -i input.mp3 -an -vcodec copy cover.jpg && ffmpeg -y -i input.mp3 -c:a aac -b:a 192k -vn output.m4a
        System.out.println("ffmpeg input:"+input);
        System.out.println("ffmpeg output:"+output);
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd "+ffmpegBin+" && " +
                "ffmpeg -y -i \"" + input + "\" -c:a aac -b:a "+bitRate+"k -cutoff 20000 -vn \"" + output +
                "\" && ffmpeg -y -i \""
                + input + "\" -an -vcodec copy \"" + coverArt + "\"");
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((reader.readLine()) != null) {System.out.println(reader.readLine());}
        p.waitFor();
        System.out.println(explicit);
        if (explicit) {
            JAudioData.setItunesExplicit(new File(output));
        }
        if(coverArt.exists()){
            JAudioData.setArtwork(coverArt, new File(output));
        }
        Files.deleteIfExists(coverArt.toPath());
    }


    /**
     *
     * @param file The original file. can be mp3 or flac
     * @param fileExtension the file extension of the original file
     * @param m4aFolder the location for the new file to be placed. By default the file gets placed in a folder of the album name inside the given directory
     * @param coverArt the file for the cover art. should be a jpeg
     * @param bitRate the bit rate in kbps. For example a high bit rate is 320kbps. It is recommended to use a higher bitrate when converting from mp3 to m4a than you would use when converting from flac.
     * @return a boolean that represents whether or not the song has album art that needs to be written
     * @throws Exception
     */
    public boolean convertToM4a(File file,String fileExtension, File m4aFolder, File coverArt, int bitRate) throws Exception{
        String input = file.toString();
        String name = file.getName();

        String output = name.substring(0, name.indexOf(MP3)) + M4A;
        output = m4aFolder.toString() + "\\" + output;
        //cd ffmpeg\bin && ffmpeg -y -i input.mp3 -an -vcodec copy cover.jpg && ffmpeg -y -i input.mp3 -c:a aac -b:a 192k -vn output.m4a
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "cd "+ffmpegPath+" && " +
                "ffmpeg -y -i \"" + input + "\" -c:a aac -b:a "+bitRate+"k -cutoff 20000 -vn \"" + output +
                "\" && ffmpeg -y -i \""
                + input + "\" -an -vcodec copy \"" + coverArt + "\"");
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((reader.readLine()) != null) {System.out.println(reader.readLine());}
        p.waitFor();

        if(coverArt.exists()){ return true; }
        return false;
    }
    public static void main(String[] args) throws Exception {
        mp3Tom4a(new File("C:\\Users\\Zubair\\Documents\\music\\song.mp3"),
                new File("C:\\Users\\Zubair\\Documents\\music"),
                new File("C:\\Users\\Zubair\\Documents\\music\\coverArt.jpeg"),
                new File ("C:\\Users\\Zubair\\Desktop\\JFFMpeg\\ffmpeg\\bin"),320, true);
    }
}
