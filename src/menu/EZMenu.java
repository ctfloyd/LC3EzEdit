package menu;

import java.util.HashMap;
import editor.EZEditor;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * Creates and styles the menu
 */
public class EZMenu {

    // The menu bar
    private MenuBar menu;
    // Maps string to MenuItem for easy retrieval of specific items
    private HashMap<String, MenuItem> menuItems;
    // The parent scene
    private Scene parentScene;

    /**
     * Sets up the Menu and its sub-menus
     * @param parentScene The parent scene that this menu will be applied to
     * @param edit The editArea this menu should apply to (save/open/conversion)
     */
    public EZMenu(Scene parentScene, EZEditor edit) {
        this.parentScene = parentScene;
        
        menuItems = new HashMap<String, MenuItem>();
        
        menu = new MenuBar();
        
        Menu menuFile = new Menu("File");
        Menu menuEdit = new Menu("Edit");
        Menu menuHelp = new Menu("Help");
       
        
        MenuItem fileOpen = new MenuItem("Open");
        MenuItem fileSave = new MenuItem("Save");
        MenuItem fileQuit = new MenuItem("Quit");
        MenuItem fileConv2bin = new MenuItem("Convert to Binary");
        MenuItem fileConv2hex = new MenuItem("Convert to Hex");
        
        MenuItem editCopy = new MenuItem("Copy");
        MenuItem editCut = new MenuItem("Cut");
        MenuItem editPaste = new MenuItem("Paste");


        menuFile.getItems().addAll(fileOpen, fileSave, new SeparatorMenuItem(), fileConv2bin, fileConv2hex, new SeparatorMenuItem(), fileQuit);
        menuEdit.getItems().addAll(editCopy, editCut, editPaste);  
        menu.getMenus().addAll(menuFile, menuEdit, menuHelp);
     
        
        menuItems.put("Save", fileSave);
        menuItems.put("Quit", fileQuit);
        menuItems.put("Open", fileOpen);
        menuItems.put("Bin", fileConv2bin);
        menuItems.put("Hex", fileConv2hex);
        menuItems.put("Copy", editCopy);
        menuItems.put("Cut", editCut);
        menuItems.put("Paste", editPaste);
        
        parentScene.getStylesheets().add(getClass().getResource("menu.css").toExternalForm());
        
        new EZMenuController(this, edit);
    }

    /**
     * Getter for menu bar
     * @return menu bar
     */
    public MenuBar getMenuBar() {
        return this.menu;
    }

    /**
     * Getter for the menuItems map
     * @return menuItems hashmap
     */
    public HashMap<String, MenuItem> getMenuItems() {
        return this.menuItems;
    }

    /**
     * Getter for the parent scene
     * @return parent scene
     */
    public Scene getParentScene() {
        return this.parentScene;
    }
}
