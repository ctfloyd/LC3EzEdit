package menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import editor.EZEditor;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Defines the behavior for the menu
 */
public class EZMenuController {
    // Location of the saved file
    private File saveLocation;

    // Binary representation of LC-3 Instructions, will be used to convert from ASM to BIN
    private final String[] INSTRUCTIONS = new String[]{
            "0001", "0101", "0000", "1100", "0100", "0100", "0010", "1010", "0110" ,"1110", "1001", "1100",
            "1000", "0011", "1011", "0111", "1111"
    };

    /**
     * Sets up the behavior for a menu.
     * @param menu The menu that this controller will define
     * @param edit The edit area this controller will define
     */
    public EZMenuController(EZMenu menu, EZEditor edit) {
        // Get items from the hash map to add behavior to
        HashMap<String, MenuItem> items = menu.getMenuItems();
        MenuItem saveItem = items.get("Save");
        MenuItem openItem = items.get("Open");
        MenuItem quitItem = items.get("Quit");
        MenuItem cutItem = items.get("Cut");
        MenuItem copyItem = items.get("Copy");
        MenuItem pasteItem = items.get("Paste");
        MenuItem convToHex = items.get("Hex");
        MenuItem convToBin = items.get("Bin");

        // Open save dialog if file not selected, otherwise just save
        saveItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save...");
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Assembly", "*.asm"),
                    new ExtensionFilter("All Files", "*.*"));


            if(saveLocation == null)
                saveLocation = fileChooser.showSaveDialog(menu.getParentScene().getWindow());
            
            byte[] strToBytes = edit.getEditArea().getText().getBytes();
            try {
                if(saveLocation == null) return; // Didn't select a file
                Files.write(Paths.get(saveLocation.getPath()), strToBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        // Open 'open' dialog and read file into edit area
        openItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open...");
            fileChooser.getExtensionFilters().addAll(
                            new ExtensionFilter("Assembly", "*.asm"));
            File selectedFile = fileChooser.showOpenDialog(menu.getParentScene().getWindow());
            saveLocation = selectedFile;
            
            String inString = "";
            Scanner readIn;
            try {
                if(selectedFile == null) return; // Didn't select a file
                readIn = new Scanner(selectedFile);
                while(readIn.hasNextLine()) {
                    inString += readIn.nextLine() + "\n";
                }
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            edit.getEditArea().insertText(0, inString);
             
        });

        // Exits the javaFX application
        quitItem.setOnAction(e -> {
            Platform.exit();
        });

        // Cuts selected text
        cutItem.setOnAction(e -> {
            edit.getEditArea().cut();
        });

        // Copies selected text
        copyItem.setOnAction(e -> {
            edit.getEditArea().copy();
        });

        // Pastes selected text
        pasteItem.setOnAction(e -> {
            edit.getEditArea().paste();
        });

//        convToBin.setOnAction(e -> {
//            String asm = edit.getEditArea().getText();
//        });
//
//        private String parseAsmToBin(String asm){
//            StringBuilder binString = new StringBuilder();
//            String[] lines = asm.split("\n");
//            ArrayList<String[]> tokens = new ArrayList<>();
//            for(String line: lines){
//                tokens.add(line.split(" "));
//            }
//            for(String[] lineToken: tokens){
//                for(String token: lineToken){
//                    for(int i = 0; i < INSTRUCTIONS.length; i++){
//                        if(INSTRUCTIONS[i].equals(token)){
//                            binString.append(INSTRUCTIONS[I])
//                        }
//                    }
//                }
//            }
//        }
    }
    
}
