import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.undo.UndoManager;

class t_edit{
    public static JFrame frame;
    public static JTextArea textArea;
    public static String currentFilePath;
    public static UndoManager undoRedoManager;
    public static boolean mode;
    t_edit(){
        frame = new JFrame("My Personal Notepad");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        frame.setLayout(new BorderLayout(0, 0));
    }

    public void createTextArea(){
        //Create text area
        textArea = new JTextArea();
        //Adjust the font of the text in text Area
        textArea.setFont(new Font("Verdana", Font.PLAIN, 20));
        //Create a scollpane and add the text area to it.
        JScrollPane scrollPane = new JScrollPane(textArea);
        //Add the scrollpane to the main frame
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
    }


    public void addMenuBar(){
        JMenuBar menuBar = new JMenuBar();

        //Contents to add to the menu bar
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu formatMenu = new JMenu("Format");
        JMenu customizeMenu = new JMenu("Customize");

        //Contents to add to the menu elements
        JMenuItem open = new JMenuItem("Open");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem saveAs = new JMenuItem("Save As");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem cut = new JMenuItem("Cut");
        JMenuItem copy = new JMenuItem("Copy");
        JMenuItem paste = new JMenuItem("Paste");
        JMenuItem undo = new JMenuItem("Undo");
        JMenuItem redo = new JMenuItem("Redo");
        JMenuItem find = new JMenuItem("Find");
        JMenuItem findReplace = new JMenuItem("Find and Replace");
        JMenuItem findReplaceAll = new JMenuItem("Find and Repalce All");
        JMenuItem nightMode = new JMenuItem("Apply Night Mode");
        JMenuItem dayMode = new JMenuItem("Apply Day Mode");
        JMenuItem bg = new JMenuItem("Background Color");
        JMenuItem size = new JMenuItem("Size");

        //Adding action listeners
        //Open Action listener
        open.addActionListener(e -> {
            JFileChooser fChooser = new JFileChooser();
            int option = fChooser.showOpenDialog(frame);
            if(option == JFileChooser.APPROVE_OPTION){
                String filePath = fChooser.getSelectedFile().getPath();
                displayFileContents(filePath);
                currentFilePath = filePath;
            }
        });

        //Save Action listener
        save.addActionListener(e -> {
            if(currentFilePath != null)
                saveFileContents(currentFilePath);
            else
                saveAsFile();
        });

        //SaveAs Action listener
        saveAs.addActionListener(e -> {
            saveAsFile();
        });

        //Exit Action Listener
        exit.addActionListener(e -> {
            System.exit(0);
        });

        //Cut Action Listener
        cut.addActionListener(e -> {
            cutText();
        });

        //Copy Action Listener
        copy.addActionListener(e -> {
            copyText();
        });

        //Paste Action Listener
        paste.addActionListener(e -> {
            pasteText();
        });

        //Undo Action Listener
        undo.addActionListener(e -> {
            if(undoRedoManager.canUndo())
                undoRedoManager.undo();
        });

        //Redo Action Listener
        redo.addActionListener(e -> {
            if(undoRedoManager.canRedo())
                undoRedoManager.redo();
        });

        //Find Action Listener
        find.addActionListener(e -> {
            String searchItem = JOptionPane.showInputDialog(frame,"Enter search item: ");
            if(searchItem != null && !searchItem.isEmpty()){
                String text = textArea.getText();
                int currentPosition = textArea.getCaretPosition();
                String lowercaseText = text.toLowerCase();
                String search = searchItem.toLowerCase();
                int startIndex = lowercaseText.indexOf(search, currentPosition);

                if(startIndex != -1){
                    int endIndex = startIndex + searchItem.length();
                    textArea.setSelectionStart(startIndex);
                    textArea.setSelectionEnd(endIndex);
                    while (true) {
                        startIndex = lowercaseText.indexOf(search, endIndex);
                        if (startIndex != -1) {
                            endIndex = startIndex + searchItem.length();
                            textArea.setSelectionStart(startIndex);
                            textArea.setSelectionEnd(endIndex);
                        } else {
                            break;
                        }
                    }
                }
                else
                    JOptionPane.showMessageDialog(frame, "No matches found.", "Find", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        //Background Color Action Listener
        bg.addActionListener(e -> {
            Color color = JColorChooser.showDialog(frame, "Choose BackgroundColor", textArea.getBackground());
            if(color != null)
                textArea.setBackground(color);
        });

        //Size Action Listener
        size.addActionListener(e -> {
            createFontSizeSpinner();
        });

        nightMode.addActionListener(e -> {
            //Set the background color to dark grey
            textArea.setBackground(new Color(40, 40, 40));
            //Set the font color to white
            textArea.setForeground(Color.WHITE);
            //Set the selection darker background color
            textArea.setSelectionColor(new Color(20, 20, 20));
            //Set the Selcted text color
            textArea.setSelectedTextColor(new Color(200, 200, 200));
        });

        dayMode.addActionListener(e -> {
            //Set the background color to white
            textArea.setBackground(Color.WHITE);
            //Set the font color to white
            textArea.setForeground(Color.BLACK);
            //Set the selection darker background color
            textArea.setSelectionColor(new Color(128, 128, 245));
            //Set the Selcted text color
            textArea.setSelectedTextColor(Color.BLACK);
        });


        //Add items to the menu elements
        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.add(exit);
        editMenu.add(cut);
        editMenu.add(copy);
        editMenu.add(paste);
        editMenu.add(undo);
        editMenu.add(redo);
        formatMenu.add(find);
        formatMenu.add(findReplace);
        formatMenu.add(findReplaceAll);
        customizeMenu.add(bg);
        customizeMenu.add(size);
        formatMenu.add(nightMode);
        formatMenu.add(dayMode);

        //Add menu elements to the menu
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(customizeMenu);

        //Add menuBar to the frame
        frame.setJMenuBar(menuBar);
    }

    public static void displayFileContents(String filePath){
        JFrame new_frame = new JFrame("File Contents");
        new_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        new_frame.setSize(500,500);
        new_frame.setLayout(new BorderLayout());

        //Customize the font ans size of the text in textArea
        textArea.setFont(new Font("Lucida", Font.PLAIN, 20));
        //Add a scrollPane to the textArea
        JScrollPane scrollPane = new JScrollPane(textArea);
        //Add the scrollPane to the frame with borderlayout
        new_frame.add(scrollPane, BorderLayout.CENTER);

        try{
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder fileContent = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
                fileContent.append(line).append("\n");
            reader.close();
            textArea.setText(fileContent.toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        new_frame.setVisible(true);
    } 

    public static void saveFileContents(String filePath){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(textArea.getText());
            writer.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void saveAsFile(){
        JFileChooser fChooser = new JFileChooser();
        int option = fChooser.showSaveDialog(frame);
        if(option == JFileChooser.APPROVE_OPTION){
            String filePath = fChooser.getSelectedFile().getPath();
            saveFileContents(filePath);
        }
    }

    public static void cutText() {
        String selectedText = textArea.getSelectedText();
        if (selectedText != null && !selectedText.isEmpty()) {
            StringSelection selection = new StringSelection(selectedText);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            textArea.replaceSelection("");
        }
    }

    public static void copyText() {
        String selectedText = textArea.getSelectedText();
        if (selectedText != null && !selectedText.isEmpty()) {
            StringSelection selection = new StringSelection(selectedText);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        }
    }

    public static void pasteText() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String text = (String) contents.getTransferData(DataFlavor.stringFlavor);
                textArea.replaceSelection(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createFontComboBox(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();

        JComboBox<String> fontComboBox = new JComboBox<>(fontNames);
        fontComboBox.addActionListener(e -> {
            String selectedFont = (String) fontComboBox.getSelectedItem();
            if(selectedFont != null){
                Font font = new Font(selectedFont, Font.PLAIN, 20);
                textArea.setFont(font);
            }
        });
        frame.add(fontComboBox, BorderLayout.SOUTH);
    }

    public void createFontSizeSpinner(){
        SpinnerNumberModel sModel = new SpinnerNumberModel(14, 10,30,1);
        JSpinner fontSizeSpinner = new JSpinner(sModel);
        int result = JOptionPane.showOptionDialog(frame, fontSizeSpinner, "Select FontSize", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        if(result == JOptionPane.OK_OPTION){
            int fontSize = (int) fontSizeSpinner.getValue();
            Font font = textArea.getFont().deriveFont((float) fontSize);
            textArea.setFont(font);
        }
    }

    public Color adjustBrightness(Color color, float brightnessFactor){
        float[] hsbValues = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float adjustedBrightness = Math.max(0f, Math.min(hsbValues[2] * brightnessFactor, 1f));
        return new Color(Color.HSBtoRGB(hsbValues[0], hsbValues[1], adjustedBrightness));
    }

    public static void main(String args[]){
        t_edit edit = new t_edit();
        edit.createTextArea();
        edit.addMenuBar();
        edit.createFontComboBox();
        frame.setVisible(true);
        //Initialize the undoRedoManager
        undoRedoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoRedoManager);
    }
}


