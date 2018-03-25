package program;



import java.nio.file.Path;

public class TreeElement {

    TreeElement(String name, Path path, TreeElementType treeElementType){
        this.name = name;
        this.path = path;
        this.treeElementType = treeElementType;
    }

    TreeElement(String name, TreeElementType treeElementType){
        this.name = name;
        this.path = null;
        this.treeElementType = treeElementType;
    }

    @Override
    public String toString(){
        return this.name;
    }

    //Text that appears in TreeItem
    private String name;

    //Path of this element
    private Path path;

    //Which icon will be shown in TreeItem
    //TODO: load system default file icon
    private TreeElementType treeElementType;

    /*
        GETTERS AND SETTERS
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public TreeElementType getTreeElementType() {
        return treeElementType;
    }

    public void setTreeElementType(TreeElementType treeElementType) {
        this.treeElementType = treeElementType;
    }
}
