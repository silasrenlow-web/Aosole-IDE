package com.fastcode.ui;

import com.fastcode.editor.CodeEditor;
import com.fastcode.compiler.LanguageCompiler;
import com.fastcode.file.FileManager;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Main application window with classic IDE layout
 */
public class MainFrame extends JFrame {
    
    private CodeEditor editor;
    private JTree fileTree;
    private JTextArea outputConsole;
    private JLabel statusLabel;
    private FileManager fileManager;
    private LanguageCompiler compiler;
    private String currentFilePath;
    private boolean isModified = false;
    
    public MainFrame() {
        super("FastCode IDE");
        
        fileManager = new FileManager(this);
        compiler = new LanguageCompiler(this);
        
        initializeUI();
        setupMenuBar();
        setupToolBar();
        setupStatusBar();
        
        // Set default size and position
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Window listener for unsaved changes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (isModified) {
                    int result = JOptionPane.showConfirmDialog(
                        MainFrame.this,
                        "You have unsaved changes. Save before closing?",
                        "Unsaved Changes",
                        JOptionPane.YES_NO_CANCEL_OPTION
                    );
                    if (result == JOptionPane.YES_OPTION) {
                        fileManager.saveFile(currentFilePath);
                    } else if (result == JOptionPane.CANCEL_OPTION) {
                        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    }
                }
            }
        });
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create main content panel with splitter
        JSplitPane mainSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitter.setResizeWeight(0.2);
        
        // File explorer panel
        JPanel fileExplorerPanel = new JPanel(new BorderLayout());
        fileExplorerPanel.setBorder(BorderFactory.createTitledBorder("Project Files"));
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Project");
        fileTree = new JTree(root);
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedFile();
                }
            }
        });
        
        JScrollPane treeScrollPane = new JScrollPane(fileTree);
        fileExplorerPanel.add(treeScrollPane, BorderLayout.CENTER);
        
        // Editor panel
        editor = new CodeEditor();
        JScrollPane editorScrollPane = new JScrollPane(editor);
        
        // Add text change listener
        editor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { markModified(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { markModified(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { markModified(); }
        });
        
        // Console panel at bottom
        JPanel consolePanel = new JPanel(new BorderLayout());
        consolePanel.setPreferredSize(new Dimension(getWidth(), 150));
        consolePanel.setBorder(BorderFactory.createTitledBorder("Output"));
        
        outputConsole = new JTextArea();
        outputConsole.setEditable(false);
        outputConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputConsole.setBackground(Color.BLACK);
        outputConsole.setForeground(Color.GREEN);
        JScrollPane consoleScrollPane = new JScrollPane(outputConsole);
        
        consolePanel.add(consoleScrollPane, BorderLayout.CENTER);
        
        // Vertical splitter for editor and console
        JSplitPane verticalSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
                                                      editorScrollPane, consolePanel);
        verticalSplitter.setResizeWeight(0.7);
        
        mainSplitter.setLeftComponent(fileExplorerPanel);
        mainSplitter.setRightComponent(verticalSplitter);
        
        add(mainSplitter, BorderLayout.CENTER);
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem newItem = new JMenuItem("New", KeyEvent.VK_N);
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(e -> fileManager.newFile());
        
        JMenuItem openItem = new JMenuItem("Open...", KeyEvent.VK_O);
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(e -> fileManager.openFile());
        
        JMenuItem saveItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> fileManager.saveFile(currentFilePath));
        
        JMenuItem saveAsItem = new JMenuItem("Save As...", KeyEvent.VK_A);
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
                                                          InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveAsItem.addActionListener(e -> fileManager.saveFileAs());
        
        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        JMenuItem cutItem = new JMenuItem("Cut", KeyEvent.VK_T);
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        cutItem.addActionListener(e -> editor.cut());
        
        JMenuItem copyItem = new JMenuItem("Copy", KeyEvent.VK_C);
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.addActionListener(e -> editor.copy());
        
        JMenuItem pasteItem = new JMenuItem("Paste", KeyEvent.VK_P);
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.addActionListener(e -> editor.paste());
        
        JMenuItem undoItem = new JMenuItem("Undo", KeyEvent.VK_U);
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoItem.addActionListener(e -> editor.undo());
        
        JMenuItem redoItem = new JMenuItem("Redo", KeyEvent.VK_R);
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        redoItem.addActionListener(e -> editor.redo());
        
        JMenuItem selectAllItem = new JMenuItem("Select All", KeyEvent.VK_A);
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        selectAllItem.addActionListener(e -> editor.selectAll());
        
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(selectAllItem);
        
        // Build menu
        JMenu buildMenu = new JMenu("Build");
        buildMenu.setMnemonic(KeyEvent.VK_B);
        
        JMenuItem compileItem = new JMenuItem("Compile", KeyEvent.VK_C);
        compileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
        compileItem.addActionListener(e -> compiler.compileCurrentFile());
        
        JMenuItem runItem = new JMenuItem("Run", KeyEvent.VK_R);
        runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        runItem.addActionListener(e -> compiler.runCurrentFile());
        
        JMenuItem buildRunItem = new JMenuItem("Build and Run", KeyEvent.VK_B);
        buildRunItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
        buildRunItem.addActionListener(e -> {
            compiler.compileCurrentFile();
            compiler.runCurrentFile();
        });
        
        buildMenu.add(compileItem);
        buildMenu.add(runItem);
        buildMenu.add(buildRunItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        JMenuItem aboutItem = new JMenuItem("About", KeyEvent.VK_A);
        aboutItem.addActionListener(e -> showAboutDialog());
        
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(buildMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void setupToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(true);
        
        JButton newButton = new JButton("New");
        newButton.addActionListener(e -> fileManager.newFile());
        
        JButton openButton = new JButton("Open");
        openButton.addActionListener(e -> fileManager.openFile());
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> fileManager.saveFile(currentFilePath));
        
        JButton compileButton = new JButton("Compile");
        compileButton.addActionListener(e -> compiler.compileCurrentFile());
        
        JButton runButton = new JButton("Run");
        runButton.addActionListener(e -> compiler.runCurrentFile());
        
        toolBar.add(newButton);
        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.addSeparator();
        toolBar.add(compileButton);
        toolBar.add(runButton);
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    private void setupStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        
        statusLabel = new JLabel("Ready");
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        JLabel languageLabel = new JLabel("Plain Text");
        statusPanel.add(languageLabel, BorderLayout.EAST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void openSelectedFile() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
        if (node != null && !node.isRoot()) {
            String filePath = node.getUserObject().toString();
            fileManager.openFile(filePath);
        }
    }
    
    private void markModified() {
        isModified = true;
        String title = "FastCode IDE";
        if (currentFilePath != null) {
            title += " - " + new File(currentFilePath).getName() + " *";
        } else {
            title += " - Untitled *";
        }
        setTitle(title);
    }
    
    public void setStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }
    
    public void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputConsole.append(text + "\n");
            outputConsole.setCaretPosition(outputConsole.getDocument().getLength());
        });
    }
    
    public void clearOutput() {
        SwingUtilities.invokeLater(() -> outputConsole.setText(""));
    }
    
    public CodeEditor getEditor() {
        return editor;
    }
    
    public void setCurrentFilePath(String path) {
        currentFilePath = path;
        isModified = false;
        String title = "FastCode IDE";
        if (path != null) {
            title += " - " + new File(path).getName();
        }
        setTitle(title);
    }
    
    public String getCurrentFilePath() {
        return currentFilePath;
    }
    
    public JTree getFileTree() {
        return fileTree;
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "FastCode IDE v1.0\n" +
            "A fast, unbloated coding IDE\n" +
            "Supporting: C/C++/Java/Rust/C#/Python\n\n" +
            "Built with Java Swing for maximum performance",
            "About FastCode IDE",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
