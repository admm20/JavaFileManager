package program;

import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.plaf.synth.SynthOptionPaneUI;

public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        /*
            Load layout from fxml file
         */
        VBox view = FXMLLoader.load(getClass().getResource("/view.fxml"));

        Scene scene = new Scene(view);

        primaryStage.setScene(scene);
        primaryStage.setTitle("File Manager");

        //min and max size of window
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);

        primaryStage.setOnCloseRequest(event -> Platform.exit()); //close all windows

        primaryStage.show();
    }
}
