# LC3 EZ Edit
LC-3 Assembly editor featuring syntax highlighting and rudimentary auto-complete.

## Why?
I was frustrated over having to write LC-3 Assembly instructions without syntax highlighting and constantly needing to reference other  sources for the syntax and use cases for instructions. 

For an alternative syntax highlighting in VSCode see: https://marketplace.visualstudio.com/itemdetails?itemName=PaperFanz.lc3-assembly

## Use:
  Download repository and compile into jar, or run from Main.java located in /src/application/Main.java.
  Optionally: Use compiled jar in repository.
  
  Keyboard Shortcut:
    CTRL + Space: Opens an autcomplete dialog if cursor is over an LC-3 ISA keyword. where you can then use numbers to choose autocomplete      options.

![alt text](https://github.com/ctfloyd/LC3EzEdit/blob/master/Screen%20Shot%202019-04-10%20at%2010.20.18%20PM.png "Image of LC3 EZ Edit")

**NOTE**
This software is very experimental and very rudimental. See TODO list below.

    
## TODO:
  * Add ability to change colors and font scheme once compiled (can currentnly be changed in respective .css files)
  * Add ability to remap keybinds
  * Implement change code to binary/hex menu options
  * Fix some autocomplete bugs with directives
  * Fix save dialog having to overwrite file (opens dialog everytime)
  * DOCUMENTATION
