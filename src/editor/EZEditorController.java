package editor;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;

/**
 * Handles the action and behavior of the edit area.
 */
public class EZEditorController {

    // LC-3 ASM Keywords
    private static final String[] KEYWORDS = new String[] {"ADD", "AND", "BR", "JMP", "JSR", "JSRR",
                    "LD", "LDI", "LDR", "LEA", "NOT", "RET", "RTI", "ST", "STI", "STR", "TRAP",
                    "ORIG", "FILL", "BLKW", "STRINGZ", "END"};

    // LC-3 ASM Keyword descriptions (how-to use)
    private static final String[] DESCRIPTIONS = new String[] {
                    "1: ADD DR, SR1, SR2\n2: ADD DR, SR1, imm5\n\n"
                                    + "Adds the values in SR1 and SR2/imm5 and sets DR to that value.",
                    "1: AND DR, SR1, SR2\n2: AND DR, SR1, imm5\n\n"
                                    + "Performs a bitwise and on the values in SR1 and SR2/imm5 and sets DR to the result.",
                    "1: BR(n/z/p) LABEL\n\n"
                                    + "Branch to the code section indicated by LABEL, if the bit indicated by (n/z/p) has been set by a previous instructionn. n: negative bit, z: zero bit, p: positive bit. Note that some instrutions do not set condition code bits.",
                    "1: JMP SR1\n\n" + "Unconditionally jump to the instruction based upon the adress in SR1.",
                    "1: JSR LABEL\n\n" + "Put the address of th next instruciton after the JSR instruction into R7 and jump to the subroutine inidcated by LABEL.",
                    "1: JSRR SR1\n\n" + "Similar to JSR except the address stored in SR1 is used instead of using a LABEL.",
                    "1: LD DR, LABEL\n\n" + "Load the value inidcated by LABEL into the DR register.",
                    "1: LDI DR, LABEL\n\n"
                                    + "Load the value indicated by the address at LABEL's memory location into the DR register.",
                    "1: LDR LDR DR, SR1, offset6\n\n"
                                    + "Load the value from the memory location found by adding the value of SR1 to offset6 into DR.",
                    "1: LEA DR, LABEL\n\n" + "Load the address of LABEL into DR.",
                    "1: NOT DR, SR1\n\n"
                                    + "Performs a bitwise not on SR1 and stores the result in DR.",
                    "1: RET\n\n" + "Return from a subroutine using the value in R7 as the base address.",
                    "1: RTI\n\n" + "Return from an  interrupt to the code that was interrupted. The address to return to is obtained by popping it off the supervisor stack, which is automatically done by RTI.",
                    "1: ST STR1, LABEL\n\n"
                                    + "Store the value in SR1 into the memory location indicated by LABEL",
                    "1: STI SR1, LABEL\n\n"
                                    + "Store the value in SR1 into the memory location indicated by the value that LABEL's memory location contains.",
                    "1: STR SR1, SR2, offset6\n\n"
                                    + "The value in SR1 is stored in the memory location found by adding SR2 and offset6 togther.",
                    "1: TRAP trapvector8\n\n"
                                    + "Performs the trap service specified by trapvector8. Each trapvector8 service has its own assembly isntruction that can replace the trap instruction.\n\n"
                                    + "x20 GETC\n Read one input character from the keyboard and store it into R0 without echoing the character to the console.\n\n"
                                    + "x21 OUT\n Output character in R0 to the console.\n\n"
                                    + "x22 PUTS\n Output null terminating string to the console starting at address contained in R0.\n\n"
                                    + "x23 IN\n Read one input character from the keyboard and store it into R0 and echo the character to the console.\n\n"
                                    + "x24 PUTSP\n Same as PUTS except that it outputs null temrinated strings with two ASCII characters packed into a single memeory location, with the low 8 bits outputted first then the high 8 bits.\n\n"
                                    + "x25 HALT\n End's a user's program.",
                    "1: .ORIG #\n\n"
                                    + "Tells the LC-3 simulator where it should place the segment of code starting at address #.",
                    "1: .FILL #\n\n"
                                    + "Place value # at the code line.",
                    "1: .BLKW #\n\n"
                                    + "Reserve # memeory locations for data at that line of code.",
                    "1: .STRINGZ \"<String>\"\n\n"
                                    + "Place a null terminating string <String> starting at that location.",
                    "1: .END\n\n"
                                    + "Tells the LC-3 assembler to stop assembling your code."};

    // Tokens that need to be changed after autocomplete
    private static final String[] INCOMPLETE = new String[] {
                    "SR1", "SR2", "DR", "imm5", "trapvector8", "offset6", "LABEL", 
                    "BR(n/z/p)", "#", "<String>"
    };

    // LC-3 ISA Register
    private static final String[] REGISTERS = new String[] {
                    "R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "PC", "IR", "PSR", "CC"
    };

    // Regex filter for LC-3 ISA specific highlighting
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String COMMENT_PATTERN = ";.*";
    private static final String INCOMPLETE_PATTERN = "\\b(" + String.join("|", INCOMPLETE) + ")\\b";
    private static final String REGISTER_PATTERN = "\\b(" + String.join("|", REGISTERS) + ")\\b";

    // Compile the Regex filters into one location
    private static final Pattern PATTERN = Pattern.compile(
                    "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<INCOMPLETE>" + INCOMPLETE_PATTERN + ")"
                    + "|(?<REGISTER>" + REGISTER_PATTERN + ")");

    /**
     * Setup the behavior of the controller
     * @param editor The editor object that's going to be controlled
     */
    public EZEditorController(EZEditor editor) {
        CodeArea edit = editor.getEditArea();
        // Key combination to open auto-complete
        // TODO: Make this changleable at runtime
        KeyCombination toolTipkeys = new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN);
        Runnable displayToolTip = () -> {
            int[] info = findKeyWordInCaret(edit);
            if(info != null) {
                String toolTipText = DESCRIPTIONS[info[0]];
                
                Popup popup = new Popup();
                Label popupMsg = new Label(toolTipText);
                ScrollPane popupPane = new ScrollPane(popupMsg);
                popupPane.setId("popup");
               
                popupMsg.minHeightProperty().bind(popup.heightProperty());
                popupMsg.setWrapText(true);                 
                popupMsg.setId("popupMessage");
               
                popup.getContent().add(popupPane);
                Bounds caretBounds = edit.getCaretBounds().get();
                popup.show(edit, caretBounds.getMaxX(), caretBounds.getMaxY());
                popupPane.requestFocus();                    

                // Auto-complete upon presisng selection option
                popupPane.addEventFilter(KeyEvent.ANY, e -> {
                    if(e.getCode() == KeyCode.DIGIT1 || e.getCode() == KeyCode.DIGIT2 || e.getCharacter().equals("1") || e.getCharacter().equals("2")) {
                      e.consume();
                      if(e.getEventType() == KeyEvent.KEY_PRESSED) {
                          int keyPressed = !e.getText().equals("") ? Integer.parseInt(e.getText()) : Integer.parseInt(e.getCharacter());
                          String definition = DESCRIPTIONS[info[0]].split("\n")[keyPressed - 1];
                          definition = definition.replace(keyPressed + ": ", "");
                          definition = definition.replace(".", "");
                          edit.replaceText(info[1], info[2], definition);
                          edit.setStyleSpans(0, computeHighlighting(edit.getText()));
                      } else {
                          popup.hide();  
                      }
                  } else if (!e.isControlDown() && !(e.getCode() == KeyCode.SPACE) && !(e.getCode() == KeyCode.CONTROL)) {
                      popup.hide();
                  }
                });

            }
        };

        // Add keycombination to scene
        editor.getScene().getAccelerators().put(toolTipkeys, displayToolTip);

        // Add line numbers to edit area
        edit.setParagraphGraphicFactory(LineNumberFactory.get(edit));
        
        // Compute highlighting each time a key is pressed
        edit.setOnKeyPressed(e -> {
              edit.setStyleSpans(0, computeHighlighting(edit.getText()));
        });
        

        // Tab behavior
        edit.addEventFilter(KeyEvent.ANY, e -> {
            // Listener that makes Tabs into 4 space because we're civilized
            if(!e.isShiftDown() && e.getCode() == KeyCode.TAB || e.getCharacter().equals("\t")) {
                e.consume();
                if(e.getEventType() == KeyEvent.KEY_PRESSED)
                    edit.insertText(edit.getCaretPosition(), "    ");
                edit.setStyleSpans(0, computeHighlighting(edit.getText()));
            }
            
            // Listener that tab matches the previous line
            if(e.getCode() == KeyCode.ENTER || e.getCharacter().equals("\n")) {
                e.consume();
                if(e.getEventType() == KeyEvent.KEY_PRESSED) {
                   String lastParagraph = edit.getParagraph(edit.getCurrentParagraph()).getText();
                   edit.insertText(edit.getCaretPosition(), "\n");
                   for(int i = 0; i < lastParagraph.length(); i++) {
                       if(lastParagraph.charAt(i) == ' ') {
                           edit.insertText(edit.getCaretPosition(), " ");
                       } else {
                           break;
                       }
                   }
                }
                edit.setStyleSpans(0, computeHighlighting(edit.getText()));
            }
            
            // Shift tab removes 4 spaces, once again we're civilized
            if(e.isShiftDown() && e.getCode() == KeyCode.TAB) {
                e.consume();
                if(e.getEventType() == KeyEvent.KEY_PRESSED) {
                    Boolean proceed = true;
                    String currentParagraph = edit.getText();
                    try {
                        for(int i = 1; i < 5; i++) {
                            System.out.println(currentParagraph.charAt(edit.getCaretPosition() - i));
                            if(currentParagraph.charAt(edit.getCaretPosition() - i) != ' ') {
                                proceed = false;
                            }
                        }
                    } catch (StringIndexOutOfBoundsException f) {
                        // PROCEED SHOULD JUST KEEP ITS VALUE
                        // This only happens on first line with less than 4 chars
                        // Checking rules still apply, just don't throw error
                    }
                    if(proceed)
                        edit.replaceText(edit.getCaretPosition() - 4, edit.getCaretPosition(), "");
                }
                edit.setStyleSpans(0, computeHighlighting(edit.getText()));
            }
        });
    }

    /**
     * Takes in non-styled text each keystroke and updates syntax highlighting
     * TODO: don't recompute highlighting, only on changed text
     * @param text Text to apply highlighting to
     * @return The styled text
     */
    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = 
                matcher.group("KEYWORD") != null ? "keyword" : 
                matcher.group("COMMENT") != null ? "comment" : 
                matcher.group("INCOMPLETE") != null ? "incomplete" :
                matcher.group("REGISTER") != null ? "register" : null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    /**
     * If they caret is currently on top or directly after an LC-3 ISA keyword @see KEYWORDS
     * then return an array containing the index of the keyword in @see KEYWORDS as well as
     * the start and end position of the keyword.
     * @param edit The edit area
     * @return Caret position and keyword hovered
     */
    private int[] findKeyWordInCaret(CodeArea edit) {
        int caretPos = edit.getCaretPosition();
        // No keyword can ever be more than 6 characters from caret position
        // e.g. .STRINGZ
        int scanTo = edit.getText().length() > caretPos + 6 ? caretPos + 6
                        : edit.getText().length();
        String keyword = edit.getText().substring(0, scanTo);
        Matcher matcher = PATTERN.matcher(keyword);
        while (matcher.find()) {
            int keywordIndex = -1;
            if (caretPos >= matcher.start() && caretPos <= matcher.end()) {
                for(int i = 0; i < KEYWORDS.length; i++) {
                    if(KEYWORDS[i].equals(matcher.group())){
                        keywordIndex = i;
                    }
                }
                return new int[] { keywordIndex, matcher.start(), matcher.end() };
            }
        }
        return null;
    }
}
