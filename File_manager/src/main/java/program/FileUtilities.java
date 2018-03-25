package program;

import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class FileUtilities {

    public static void open(Path path){
        if(Files.isRegularFile(path)) {
            try {
                Desktop.getDesktop().open(new File(path.toString()));
            } catch (IOException e) {
                System.out.println("Cannot open");
            }
        }
    }

    public static void rename(Path path, String newName) throws Exception{
        Files.move(path, path.resolveSibling(newName));
    }

    public static boolean remove(Path path){
        boolean isOk = true;
        if(Files.isRegularFile(path)){
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.out.println("Cannot delete this file");
                isOk = false;
            }
        }
        else {
            try {
                FileUtils.deleteDirectory(path.toFile());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Cannot delete this directory");
                isOk = false;
            }
        }
        return isOk;
    }
}
