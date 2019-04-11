package editor;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import javafx.scene.Scene;

public class EZEditor {
    
    CodeArea editArea;
    Scene parentScene;
    VirtualizedScrollPane<CodeArea> pane;
    
    public EZEditor (Scene parentScene) {
        this.parentScene = parentScene;
        editArea = new CodeArea();
        editArea.setId("editor");
        pane = new VirtualizedScrollPane<>(editArea);
        parentScene.getStylesheets().add(getClass().getResource("editor.css").toExternalForm());
        parentScene.getStylesheets().add(getClass().getResource("colors.css").toExternalForm());
        new EZEditorController(this);
    }
    
    public Scene getScene() {
        return this.parentScene;
    }
    
    public VirtualizedScrollPane<CodeArea> getScrollPane() {
        return this.pane;
    }
    
    public CodeArea getEditArea() {
        return this.editArea;
    }

}
