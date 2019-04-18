package editor;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import javafx.scene.Scene;

import java.io.File;

/**
 * This class is responsible for the creation and styling of the editing area.
 */
public class EZEditor {
    
    CodeArea editArea;
    Scene parentScene;
    VirtualizedScrollPane<CodeArea> pane;

    /**
     * Creates an editing area and applies the appropriate styles to it.
     * @param parentScene The scene that the editing object will be applied to
     */
    public EZEditor (Scene parentScene) {
        this.parentScene = parentScene;
        editArea = new CodeArea();
        editArea.setId("editor");
        pane = new VirtualizedScrollPane<>(editArea);
        parentScene.getStylesheets().add(getClass().getResource("editor.css").toExternalForm());

        File colorFile = new File("colors.css");
        if(colorFile != null && colorFile.isFile()) {
            parentScene.getStylesheets().add("file:///" + colorFile.getAbsolutePath().replace("\\", "/"));
        } else {
            parentScene.getStylesheets().add(getClass().getResource("colors.css").toExternalForm());
        }
        new EZEditorController(this);
    }

    /**
     * Getter for editor's parent scene
     * @return parentScene
     */
    public Scene getScene() {
        return this.parentScene;
    }

    /**
     * Getter for scroll pane that the edit area is contained in
     * @return
     */
    public VirtualizedScrollPane<CodeArea> getScrollPane() {
        return this.pane;
    }

    /**
     * Getter for the edit area
     * @return
     */
    public CodeArea getEditArea() {
        return this.editArea;
    }

}
