package program;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/*
    Public methods for menu bar
 */
public class MenuOption {

    /*
        Closes only one window
     */
    public static void closeWindow(Stage stage){
        stage.close();
    }

    /*
        Exit application
     */
    public static void exitApp(){
        Platform.exit();
    }

    /*
        Show new window with information about program
     */
    public static void aboutAuthor(){

        /*
            Stage
         */
        Stage authorWindow = new Stage();
        authorWindow.setTitle("Author");

        /*
            Button
         */
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> authorWindow.close());

        /*
            Label
         */

        Label labelAuthor = new Label("File Manager made by Adam Prusak");
        labelAuthor.setPadding(new Insets(20, 0, 20,0));

        /*
            Create VBox and insert button and label
         */
        VBox authorPane = new VBox();
        authorPane.setAlignment(Pos.CENTER);
        authorPane.getChildren().addAll(
                labelAuthor,
                exitButton
        );


        /*
            Set created scene to stage and show it as window
         */
        authorWindow.setResizable(false);
        authorWindow.setScene(new Scene(authorPane, 250, 150));
        authorWindow.show();
    }
}
