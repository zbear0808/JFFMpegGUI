import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Window;

import java.io.File;

public class GUI extends Application implements EventHandler<ActionEvent>{

    private TabPane tabPane;
    private Tab pathsTab, convertTab;
    private Button selectFFMpeg, selectMusicInput, selectMusicOutput, selectTempPath, selectPurchasedFile, convert;
    private TextField ffmpegText, inputText, outputText, tempText, purchasedText, bitRateText;
    private Slider bitRate;
    private File ffmpegBinPath, musicInputPath, musicOutputPath, tempPath, purchasedFile;
    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Music converter");

        tabPane = new TabPane();
        pathsTab = new Tab("Set Paths");
        convertTab = new Tab("Conversion settings");

        selectFFMpeg = new Button("ffmpeg\\bin");
        selectFFMpeg.setOnAction(this);
        ffmpegText = new TextField();
        ffmpegText.setPromptText("Select ffmpeg bin folder");

        selectMusicInput = new Button("input");
        selectMusicInput.setOnAction(this);
        inputText = new TextField();
        inputText.setPromptText("Select input music folder");

        selectMusicOutput = new Button("output");
        selectMusicOutput.setOnAction(this);
        outputText = new TextField();
        outputText.setPromptText("Select output music folder");

        selectTempPath = new Button("temp");
        selectTempPath.setOnAction(this);
        tempText = new TextField();
        tempText.setPromptText("Select an empty temporary folder");

        selectPurchasedFile = new Button("Purchased File");
        selectPurchasedFile.setOnAction(this);
        purchasedText = new TextField();
        purchasedText.setPromptText("Select the purchased Itunes file");

        bitRate = new Slider(0,400,320);
        bitRateText = new TextField(); bitRateText.setPromptText("Bit Rate in kb/s");

        bitRate.valueProperty().addListener( e ->
                bitRateText.textProperty().setValue("Bit Rate: " + (int)bitRate.getValue() + "kb/s")
            );
        /*bitRateText.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("textfield changed from " + oldValue + " to " + newValue);
        });*/
        convert = new Button("convert");
        convert.setOnAction(this);



        VBox pathsLayout = new VBox();
        pathsLayout.getChildren().addAll(
                selectFFMpeg,ffmpegText,selectMusicInput,inputText,selectMusicOutput,outputText,selectTempPath,tempText,selectPurchasedFile,purchasedText);
        pathsTab.setContent(pathsLayout);


        VBox conversionLayout = new VBox();
        HBox sliderAndText = new HBox();

        sliderAndText.getChildren().addAll(bitRate,bitRateText);

        conversionLayout.getChildren().addAll(sliderAndText,convert);
        convertTab.setContent(conversionLayout);

        tabPane.getTabs().addAll(pathsTab,convertTab);

        Scene scene = new Scene(tabPane,500,500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @Override
    public void handle(ActionEvent event)  {
        Button source = ((Button)event.getSource());
        Window primaryStage = source.getScene().getWindow();

        if(source.equals(selectFFMpeg)) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Open ffmpeg\\bin library");
            File temp = chooser.showDialog(primaryStage);
            if(temp!=null){ffmpegBinPath = temp;
            ffmpegText.setText(ffmpegBinPath.toString());}
        }
        else if(source.equals(selectMusicInput)) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Open Folder With Music");
            File temp = chooser.showDialog(primaryStage);
            if(temp!=null){musicInputPath = temp;
            inputText.setText(musicInputPath.toString());}
        }
        else if(source.equals(selectMusicOutput)){
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Open Folder where you want music");
            File temp = chooser.showDialog(primaryStage);
            if(temp!=null){musicOutputPath = temp;
            outputText.setText(musicOutputPath.toString());}
        }
        else if(source.equals(selectTempPath)){
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Open an empty folder for temporary files");
            File temp = chooser.showDialog(primaryStage);
            if(temp!=null){tempPath = temp;
            tempText.setText(tempPath.toString());}
        }
        else if (source.equals(selectPurchasedFile)){
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open the Purchased AAC File");
            File temp = chooser.showOpenDialog(primaryStage);
            if(temp!=null){purchasedFile = temp;
                purchasedText.setText(purchasedFile.toString());}
        }
        else if(source.equals(convert)){
            try{JAudioData.guiTest(inputText.getText(),outputText.getText(),new File(ffmpegText.getText()), new File(tempText.getText()), new File(purchasedText.getText()) , (int)bitRate.getValue());
                //Converter.gui(inputText.getText(),outputText.getText(),new File(ffmpegText.getText()), new File(tempText.getText()), new File(purchasedText.getText()) , (int)bitRate.getValue());
            }//code works with first line but not with 2nd line
            catch(Exception e){e.printStackTrace();}
        }

    }



    public static void main(String[] args) {
        launch(args);
    }

}
