package program;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    private MenuBar menuBar;

    public TreeView<TreeElement> getTreeView() {
        return treeView;
    }

    public void setTreeView(TreeView<TreeElement> treeView) {
        this.treeView = treeView;
    }

    @FXML
    private TreeView<TreeElement> treeView;

    @FXML
    private TextField pathTextField;

    @FXML
    private Button buttonRefresh;

    private FileTree fileTree = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Create FileTree object and give it a reference to TreeView
        fileTree = new FileTree();

        treeView.setRoot(fileTree.getRoot());

        //Load icon to refresh button and remove default text
        ImageView iconRefresh = new ImageView(new Image(getClass().getResourceAsStream("/refresh.png")));
        buttonRefresh.setGraphic(iconRefresh);
        buttonRefresh.setText("");

        /*
            TREE ITEM SELECTED ACTION
        */
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {

            //Get path of selected item
            Path p = null;
            try{
                p = newVal.getValue().getPath();
            }
            catch(Exception e){
                //some small error with selecting
                pathTextField.setText("");
            }


            if(p!=null){
                //If it's not 'My Computer', set path into text field
                pathTextField.setText(p.toString());
            }
            else{
                //otherwise, set it to null
                pathTextField.setText("");
            }
        });

    }

    /*
        MENU BAR OPTIONS
     */

    @FXML
    void menuAboutPressed(ActionEvent event) {
        MenuOption.aboutAuthor();
    }

    @FXML
    void menuClosePressed(ActionEvent event) {
        MenuOption.exitApp();
    }

    /*
        TREE VIEW MOUSE EVENTS
     */
    @FXML
    void treeViewClicked(MouseEvent event) {
        if(event.getClickCount() == 2){
            Path p = getSelectedTreeItemPath();
            if(p!=null && Files.isRegularFile(p)){
                FileUtilities.open(p);
            }
        }
    }

    /*
        BUTTON (REFRESH)
     */

    @FXML
    void RefreshPressed(ActionEvent event) {
        fileTree.refresh();
        treeView.setRoot(fileTree.getRoot());
        pathTextField.setText("");
    }



    /*
        CONTEXT MENU FOR TREE VIEW
     */
    private Path srcPath;
    private boolean removeOnPaste;
    private TreeItem<TreeElement> itemPasteMemory;

    @FXML
    void treeMenuCopy(ActionEvent event) {
        srcPath = getSelectedTreeItemPath();
        removeOnPaste = false;
        itemPasteMemory = getSelectedTreeItem();
    }

    @FXML
    void treeMenuCut(ActionEvent event) {
        srcPath = getSelectedTreeItemPath();
        removeOnPaste = true;
        itemPasteMemory = getSelectedTreeItem();
    }

    @FXML
    void treeMenuDelete(ActionEvent event) {

        //check if user is trying to delete disk or 'My Computer'
        Path p = getSelectedTreeItemPath();
        if(p.toString().equals("") || p.getNameCount()<1){
            System.out.println("You can't delete this");
        }
        else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete warning");
            alert.setHeaderText("Warning");
            alert.setContentText("Do you want to delete '" + p.toString() + "'?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                //delete file
                if(FileUtilities.remove(p)){
                    //remove it from tree if delete procedure was succeeded
                    getSelectedTreeItem().getParent().getChildren().remove(getSelectedTreeItem());
                }

            }
            else{
                //do nothing
            }
        }

    }

    @FXML
    void treeMenuNewDirectory(ActionEvent event) {
        Path path = getSelectedTreeItemPath();
        if(!path.toString().equals("")){

            //Show new window
            TextInputDialog dialog = new TextInputDialog("New Folder");
            dialog.setTitle("Create a new folder");
            dialog.setHeaderText("Creating new folder");
            dialog.setContentText("Please enter name:");

            //Wait for response
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                Path newDirectoryPath;

                //path points to directory
                if(Files.isDirectory(path)){
                    newDirectoryPath = Paths.get(getSelectedTreeItemPath().toString() + "\\" + result.get());
                    getSelectedTreeItem().setExpanded(true);
                }
                else{ //points to file
                    newDirectoryPath = Paths.get(getSelectedTreeItemPath().getParent().toString() + "\\" + result.get());
                }

                try {
                    Files.createDirectory(newDirectoryPath);
                    getSelectedTreeItem().getChildren().add(fileTree.createNode(
                            new TreeElement(
                                    result.get(),
                                    newDirectoryPath,
                                    TreeElementType.DIRECTORY)
                            )
                    );
                } catch (IOException e) {
                    System.out.println("Cannot create directory");
                }
                //getSelectedTreeItem().getChildren().add(LeftTreeView.createNode(new TreeElement(result.get(), p)));
            }
        }
        else{
            //do nothing
        }
    }

    @FXML
    void treeMenuOpen(ActionEvent event) {
        Path p = getSelectedTreeItemPath();
        if(p!=null && Files.isRegularFile(p)){
            FileUtilities.open(p);
        }
    }

    @FXML
    void treeMenuPaste(ActionEvent event) {
        if(!srcPath.toString().equals("")){
            if(Files.isRegularFile(srcPath)){
                Path dest = getSelectedTreeItemPath();

                //path points to directory
                if(Files.isDirectory(dest)){
                    dest = Paths.get(dest.toString() + "\\" + srcPath.getFileName());
                    getSelectedTreeItem().setExpanded(true);
                }
                else{ //points to file
                    dest = Paths.get(dest.getParent().toString() + "\\" + srcPath.getFileName());
                }
                if(removeOnPaste){
                    try {
                        Files.move(srcPath, dest);
                        itemPasteMemory.getParent().getChildren().remove(itemPasteMemory);
                        getSelectedTreeItem().getChildren().add(fileTree.createNode(new TreeElement(dest.getFileName().toString(), dest, TreeElementType.FILE)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    try {
                        Files.copy(srcPath, dest);
                        getSelectedTreeItem().getChildren().add(fileTree.createNode(new TreeElement(dest.getFileName().toString(), dest, TreeElementType.FILE)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                System.out.println("You can only move files (not directories)");
            }
        }
    }

    @FXML
    void treeMenuRename(ActionEvent event) {
        //check if user is trying to rename disk or 'My Computer'
        Path p = getSelectedTreeItemPath();
        if(p.toString().equals("") || p.getNameCount()<1){
            System.out.println("You can't rename this");
        }
        else {
            //Show new window
            TextInputDialog dialog = new TextInputDialog("New Folder");
            dialog.setTitle("Create a new folder");
            dialog.setHeaderText("Creating new folder");
            dialog.setContentText("Please enter name:");

            //Wait for response
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                try {
                    FileUtilities.rename(p, result.get()); //rename file
                    Path renamedFilePath = p.resolveSibling(result.get()); //get path of new renamed file

                    //add renamed file to tree
                    getSelectedTreeItem().getParent().getChildren().add(fileTree.createNode(
                            new TreeElement(
                                    result.get(),
                                    renamedFilePath,
                                    Files.isDirectory(renamedFilePath) ? TreeElementType.DIRECTORY : TreeElementType.FILE)));

                    //delete file with old name
                    getSelectedTreeItem().getParent().getChildren().remove(getSelectedTreeItem());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    

    //Return path of selected item.
    public Path getSelectedTreeItemPath(){
        //return treeView.getSelectionModel().getSelectedItem().getValue().getPath();
        String text = pathTextField.getText();
        Path path = Paths.get(text);
        return path;
    }

    //Return selected item
    public TreeItem<TreeElement> getSelectedTreeItem(){
        return treeView.getSelectionModel().getSelectedItem();
    }
}
