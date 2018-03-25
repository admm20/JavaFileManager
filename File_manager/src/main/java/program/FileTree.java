package program;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileTree{

    //Root element of tree
    private TreeItem<TreeElement> myComputer;

    //Icons
    Image iconPC =  new Image(getClass().getResourceAsStream("/pc.png"));
    Image iconDrive =  new Image(getClass().getResourceAsStream("/drive.png"));
    Image iconFile =  new Image(getClass().getResourceAsStream("/file.png"));
    Image iconCatalog =  new Image(getClass().getResourceAsStream("/catalog.png"));

    //When new object is made, add My Computer element and search for drives
    FileTree(){
        addRoot();
    }

    public TreeItem<TreeElement> getRoot(){
        return myComputer;
    }

    //Adds 'My Computer' item to the tree
    private void addRoot(){
        //create My Computer element
        TreeElement computerElem = new TreeElement(
                "My Computer",
                TreeElementType.PC);

        //add it to Treeitem
        myComputer = new TreeItem<>(
                computerElem,
                new ImageView(iconPC));

        //find drives and add it to root as children
        addDrives();
    }

    private void addDrives(){

        //get list of drives
        Iterable<Path> dir = FileSystems.getDefault().getRootDirectories();

        //add every drive to My Computer (root) item
        for(Path path : dir){
            TreeElement drive = new TreeElement(
                    path.toString(),
                    path,
                    TreeElementType.DRIVE
            );

            //if(!path.toString().equals("C:\\")) {
                myComputer.getChildren().add(createNode(drive));
            //}
        }
    }

    // source: https://docs.oracle.com/javafx/2/api/javafx/scene/control/TreeItem.html
    // changed from 'io' to 'nio'
    public TreeItem<TreeElement> createNode(TreeElement te){

        return new TreeItem<TreeElement>(te, getIcon(te.getTreeElementType())){
            // if it's file, then don't make it expandable
            public boolean isLeaf;

            public boolean isFirstTimeChildren = true;
            public boolean isFirstTimeLeaf = true;

            @Override public ObservableList<TreeItem<TreeElement>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;
                    // First getChildren() call, so we actually go off and
                    // determine the children of the File contained in this TreeItem.
                    super.getChildren().setAll(buildChildren(this));
                }
                return super.getChildren();
            }

            @Override public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    //isLeaf = Files.isRegularFile(te.getPath());
                    isLeaf = !Files.isDirectory(te.getPath());
                }

                return isLeaf;
            }

            private ObservableList<TreeItem<TreeElement>> buildChildren(TreeItem<TreeElement> TreeItem) {

                //get path of current file
                Path filePath = TreeItem.getValue().getPath();

                //if it's directory, then check files inside it
                if(filePath != null && Files.isDirectory(filePath)){

                    try {
                        //content of directory
                        DirectoryStream<Path> ds = Files.newDirectoryStream(filePath);

                        if(!ds.equals(null)){
                            ObservableList<TreeItem<TreeElement>> children = FXCollections.observableArrayList();

                            for (Path pth : ds) {
                                if(Files.isDirectory(pth)){
                                    children.add(createNode(new TreeElement(pth.getFileName().toString(), pth, TreeElementType.DIRECTORY)));
                                }
                                else{
                                    children.add(createNode(new TreeElement(pth.getFileName().toString(), pth, TreeElementType.FILE)));
                                }
                            }

                            return children;
                        }

                    } catch (IOException e) {
                        System.out.println("No permission to file " + filePath.toString());
                    }

                }

                return FXCollections.emptyObservableList();
            }
        };
    }

    //Reset tree. Please use getRoot() after using refresh()
    public void refresh(){
        addRoot();
    }

    private ImageView getIcon(TreeElementType type){
        ImageView retVal = null;
        switch(type){

            case DIRECTORY:
                retVal = new ImageView(iconCatalog);
                break;
            case DRIVE:
                retVal = new ImageView(iconDrive);
                break;
            case FILE:
                retVal = new ImageView(iconFile);
                break;
            case PC:
                retVal = new ImageView(iconPC);
                break;
        }
        return retVal;
    }
}
