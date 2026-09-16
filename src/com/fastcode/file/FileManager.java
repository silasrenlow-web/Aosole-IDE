package com.fastcode.file;

import com.fastcode.ui.MainFrame;
import javax.swing.*;
import java.io.*;

/**
 * File management operations for the IDE
 */
public class FileManager {
    
    private MainFrame mainFrame;
    private JFileChooser fileChooser;
    
    public FileManager(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
    }
    
    public void newFile() {
        // Check for unsaved changes
        if (mainFrame.getEditor().getDocument().getLength() > 0) {
            int result = JOptionPane.showConfirmDialog(
                mainFrame,
                "Create new file? Unsaved changes will be lost.",
                "New File",
                JOptionPane.YES_NO_OPTION
            );
            
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        mainFrame.getEditor().setText("");
        mainFrame.setCurrentFilePath(null);
        mainFrame.setStatus("New file created");
        mainFrame.getEditor().setLanguageByExtension(null);
    }
    
    public void openFile() {
        int returnValue = fileChooser.showOpenDialog(mainFrame);
        
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            openFile(selectedFile.getAbsolutePath());
        }
    }
    
    public void openFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "File not found: " + filePath,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            mainFrame.getEditor().setText(content.toString());
            mainFrame.setCurrentFilePath(filePath);
            mainFrame.getEditor().setLanguageByExtension(file.getName());
            mainFrame.setStatus("Opened: " + filePath);
            
            // Update file tree if needed
            updateFileTree(file.getParent());
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame,
                "Error opening file: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public void saveFile(String filePath) {
        if (filePath == null) {
            saveFileAs();
            return;
        }
        
        try {
            File file = new File(filePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(mainFrame.getEditor().getText());
            }
            
            mainFrame.setStatus("Saved: " + filePath);
            
            // Update file tree
            updateFileTree(file.getParent());
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame,
                "Error saving file: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public void saveFileAs() {
        int returnValue = fileChooser.showSaveDialog(mainFrame);
        
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            saveFile(selectedFile.getAbsolutePath());
        }
    }
    
    private void updateFileTree(String directoryPath) {
        if (directoryPath == null) {
            return;
        }
        
        try {
            File dir = new File(directoryPath);
            if (!dir.isDirectory()) {
                return;
            }
            
            // Build tree structure
            javax.swing.tree.DefaultMutableTreeNode root = 
                new javax.swing.tree.DefaultMutableTreeNode(dir.getName());
            buildFileTree(dir, root);
            
            mainFrame.getFileTree().setModel(new javax.swing.tree.DefaultTreeModel(root));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void buildFileTree(File folder, javax.swing.tree.DefaultMutableTreeNode node) {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        
        // Sort: directories first, then files
        java.util.Arrays.sort(files, (f1, f2) -> {
            if (f1.isDirectory() && !f2.isDirectory()) {
                return -1;
            } else if (!f1.isDirectory() && f2.isDirectory()) {
                return 1;
            } else {
                return f1.getName().compareToIgnoreCase(f2.getName());
            }
        });
        
        for (File file : files) {
            // Skip hidden files and common IDE folders
            if (file.isHidden() || 
                file.getName().startsWith(".") ||
                file.getName().equals("__pycache__") ||
                file.getName().equals("bin") ||
                file.getName().equals("obj")) {
                continue;
            }
            
            javax.swing.tree.DefaultMutableTreeNode fileNode = 
                new javax.swing.tree.DefaultMutableTreeNode(file.getAbsolutePath());
            
            if (file.isDirectory()) {
                javax.swing.tree.DefaultMutableTreeNode dirNode = 
                    new javax.swing.tree.DefaultMutableTreeNode(file.getName());
                buildFileTree(file, dirNode);
                node.add(dirNode);
            } else {
                node.add(fileNode);
            }
        }
    }
}
