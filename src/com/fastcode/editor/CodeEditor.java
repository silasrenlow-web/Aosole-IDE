package com.fastcode.editor;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

/**
 * High-performance code editor with undo/redo support and line numbers
 * Optimized for speed without heavy syntax highlighting
 */
public class CodeEditor extends JTextArea {
    
    private Map<String, LanguageConfig> languageConfigs;
    private String currentLanguage = "Plain Text";
    private UndoManager undoManager;
    
    public CodeEditor() {
        // Performance optimizations
        setFont(new Font("Monospaced", Font.PLAIN, 14));
        setLineWrap(false);
        setWrapStyleWord(false);
        setCaretPosition(0);
        
        // Setup undo manager
        undoManager = new UndoManager();
        getDocument().addUndoableEditListener(undoManager);
        
        // Setup tab handling
        setupTabKey();
        
        // Initialize language configurations
        initializeLanguages();
    }
    
    private void setupTabKey() {
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "insertTab");
        getActionMap().put("insertTab", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int start = getSelectionStart();
                int end = getSelectionEnd();
                
                if (start != end) {
                    // Indent selected lines
                    try {
                        int startLine = getLineOfOffset(start);
                        int endLine = getLineOfOffset(end);
                        
                        for (int i = startLine; i <= endLine; i++) {
                            int lineStart = getLineStartOffset(i);
                            getDocument().insertString(lineStart, "    ", null);
                        }
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    // Insert 4 spaces
                    try {
                        int pos = getCaretPosition();
                        getDocument().insertString(pos, "    ", null);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    
    public void undo() {
        try {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        } catch (CannotUndoException e) {
            e.printStackTrace();
        }
    }
    
    public void redo() {
        try {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        } catch (CannotRedoException e) {
            e.printStackTrace();
        }
    }
    
    private void initializeLanguages() {
        languageConfigs = new HashMap<>();
        
        // C/C++
        LanguageConfig cppConfig = new LanguageConfig();
        cppConfig.name = "C++";
        cppConfig.extensions = Arrays.asList(".cpp", ".c", ".h", ".hpp", ".cc");
        cppConfig.singleLineComment = "//";
        cppConfig.multiLineCommentStart = "/*";
        cppConfig.multiLineCommentEnd = "*/";
        languageConfigs.put("C++", cppConfig);
        
        // Java
        LanguageConfig javaConfig = new LanguageConfig();
        javaConfig.name = "Java";
        javaConfig.extensions = Arrays.asList(".java");
        javaConfig.singleLineComment = "//";
        javaConfig.multiLineCommentStart = "/*";
        javaConfig.multiLineCommentEnd = "*/";
        languageConfigs.put("Java", javaConfig);
        
        // Python
        LanguageConfig pythonConfig = new LanguageConfig();
        pythonConfig.name = "Python";
        pythonConfig.extensions = Arrays.asList(".py", ".pyw");
        pythonConfig.singleLineComment = "#";
        languageConfigs.put("Python", pythonConfig);
        
        // Rust
        LanguageConfig rustConfig = new LanguageConfig();
        rustConfig.name = "Rust";
        rustConfig.extensions = Arrays.asList(".rs");
        rustConfig.singleLineComment = "//";
        rustConfig.multiLineCommentStart = "/*";
        rustConfig.multiLineCommentEnd = "*/";
        languageConfigs.put("Rust", rustConfig);
        
        // C#
        LanguageConfig csharpConfig = new LanguageConfig();
        csharpConfig.name = "C#";
        csharpConfig.extensions = Arrays.asList(".cs");
        csharpConfig.singleLineComment = "//";
        csharpConfig.multiLineCommentStart = "/*";
        csharpConfig.multiLineCommentEnd = "*/";
        languageConfigs.put("C#", csharpConfig);
    }
    
    public void setLanguageByExtension(String fileName) {
        if (fileName == null) {
            setLanguage("Plain Text");
            return;
        }
        
        for (LanguageConfig config : languageConfigs.values()) {
            for (String ext : config.extensions) {
                if (fileName.toLowerCase().endsWith(ext)) {
                    setLanguage(config.name);
                    return;
                }
            }
        }
        
        setLanguage("Plain Text");
    }
    
    public void setLanguage(String language) {
        currentLanguage = language;
    }
    
    public String getCurrentLanguage() {
        return currentLanguage;
    }
    
    public LanguageConfig getCurrentLanguageConfig() {
        return languageConfigs.get(currentLanguage);
    }
    
    public int getLineOfOffset(int offset) throws BadLocationException {
        int line = 0;
        int len = getDocument().getLength();
        if (offset >= len) {
            offset = len - 1;
        }
        
        for (int i = 0; i < offset; i++) {
            if (getText(i, 1).equals("\n")) {
                line++;
            }
        }
        return line;
    }
    
    public int getLineStartOffset(int line) throws BadLocationException {
        int offset = 0;
        int currentLine = 0;
        int len = getDocument().getLength();
        
        while (currentLine < line && offset < len) {
            if (getText(offset, 1).equals("\n")) {
                currentLine++;
                if (currentLine < line) {
                    offset++;
                }
            } else {
                offset++;
            }
        }
        return offset;
    }
    
    public static class LanguageConfig {
        public String name;
        public java.util.List<String> extensions;
        public String singleLineComment;
        public String multiLineCommentStart;
        public String multiLineCommentEnd;
    }
}
