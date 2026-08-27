import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReplaceScript {
    public static void main(String[] args) throws Exception {
        Files.walk(Paths.get("d:/dihadi/src/main/java/com/dihadi/view"))
             .filter(Files::isRegularFile)
             .filter(p -> p.toString().endsWith(".java"))
             .forEach(p -> {
                 try {
                     String content = Files.readString(p);
                     boolean changed = false;
                     
                     if (content.contains(".getProfileScene(() -> stage.setScene(currentScene)))")) {
                         content = content.replace(".getProfileScene(() -> stage.setScene(currentScene)))", ".getProfileScene(() -> stage.setScene(currentScene), currentScene))");
                         changed = true;
                     }
                     if (content.contains(".getScene(() -> stage.setScene(currentScene)))")) {
                         content = content.replace(".getScene(() -> stage.setScene(currentScene)))", ".getScene(() -> stage.setScene(currentScene), currentScene))");
                         changed = true;
                     }
                     
                     if (changed) {
                         Files.writeString(p, content);
                         System.out.println("Updated " + p);
                     }
                 } catch (Exception e) {}
             });
    }
}
