package com.fastcode;

import com.fastcode.ui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * FastCode IDE - Main Entry Point
 * A fast, unbloated coding IDE with classic UI
 * Supporting: C/C++/Java/Rust/C#/Python
 */
public class Main {
    
    public static void main(String[] args) {
        // Set system look and feel for native performance
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else if (os.contains("mac")) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {
                // Linux - use GTK or Metal for speed
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch IDE on EDT for thread safety
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
