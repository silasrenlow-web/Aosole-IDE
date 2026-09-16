package com.fastcode.compiler;

import com.fastcode.ui.MainFrame;
import javax.swing.*;
import java.io.*;
import java.util.concurrent.*;

/**
 * Multi-language compiler and runner with timeout protection
 * Supports: C/C++/Java/Rust/C#/Python
 */
public class LanguageCompiler {
    
    private MainFrame mainFrame;
    private ExecutorService executor;
    
    public LanguageCompiler(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        executor = Executors.newCachedThreadPool();
    }
    
    public void compileCurrentFile() {
        String filePath = mainFrame.getCurrentFilePath();
        
        if (filePath == null) {
            JOptionPane.showMessageDialog(mainFrame,
                "Please save the file first",
                "No File",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        File file = new File(filePath);
        String fileName = file.getName();
        String fileDir = file.getParent();
        
        mainFrame.clearOutput();
        mainFrame.setStatus("Compiling...");
        
        try {
            ProcessBuilder pb = getCompileProcess(fileName, fileDir);
            if (pb == null) {
                mainFrame.appendOutput("Language not supported for compilation");
                mainFrame.setStatus("Ready");
                return;
            }
            
            pb.directory(new File(fileDir));
            Process process = pb.start();
            
            // Read output with timeout
            Future<String> outputFuture = executor.submit(() -> readStream(process.getInputStream()));
            Future<String> errorFuture = executor.submit(() -> readStream(process.getErrorStream()));
            
            boolean completed = process.waitFor(30, TimeUnit.SECONDS);
            
            if (completed) {
                String output = outputFuture.get(2, TimeUnit.SECONDS);
                String errors = errorFuture.get(2, TimeUnit.SECONDS);
                
                if (!output.isEmpty()) {
                    mainFrame.appendOutput(output);
                }
                if (!errors.isEmpty()) {
                    mainFrame.appendOutput(errors);
                }
                
                if (process.exitValue() == 0) {
                    mainFrame.appendOutput("Compilation successful!");
                    mainFrame.setStatus("Compiled successfully");
                } else {
                    mainFrame.setStatus("Compilation failed");
                }
            } else {
                process.destroyForcibly();
                mainFrame.appendOutput("Compilation timed out (30s limit)");
                mainFrame.setStatus("Compilation timeout");
            }
            
        } catch (Exception e) {
            mainFrame.appendOutput("Error: " + e.getMessage());
            mainFrame.setStatus("Compilation error");
            e.printStackTrace();
        }
    }
    
    public void runCurrentFile() {
        String filePath = mainFrame.getCurrentFilePath();
        
        if (filePath == null) {
            JOptionPane.showMessageDialog(mainFrame,
                "Please save the file first",
                "No File",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        File file = new File(filePath);
        String fileName = file.getName();
        String fileDir = file.getParent();
        
        mainFrame.clearOutput();
        mainFrame.setStatus("Running...");
        
        try {
            ProcessBuilder pb = getRunProcess(fileName, fileDir);
            if (pb == null) {
                mainFrame.appendOutput("Language not supported for running");
                mainFrame.setStatus("Ready");
                return;
            }
            
            pb.directory(new File(fileDir));
            Process process = pb.start();
            
            // Read output with timeout
            Future<String> outputFuture = executor.submit(() -> readStream(process.getInputStream()));
            Future<String> errorFuture = executor.submit(() -> readStream(process.getErrorStream()));
            
            boolean completed = process.waitFor(30, TimeUnit.SECONDS);
            
            if (completed) {
                String output = outputFuture.get(2, TimeUnit.SECONDS);
                String errors = errorFuture.get(2, TimeUnit.SECONDS);
                
                if (!output.isEmpty()) {
                    mainFrame.appendOutput(output);
                }
                if (!errors.isEmpty()) {
                    mainFrame.appendOutput(errors);
                }
                
                mainFrame.setStatus("Execution finished");
            } else {
                process.destroyForcibly();
                mainFrame.appendOutput("Execution timed out (30s limit)");
                mainFrame.setStatus("Execution timeout");
            }
            
        } catch (Exception e) {
            mainFrame.appendOutput("Error: " + e.getMessage());
            mainFrame.setStatus("Execution error");
            e.printStackTrace();
        }
    }
    
    private ProcessBuilder getCompileProcess(String fileName, String fileDir) {
        if (fileName.endsWith(".java")) {
            return new ProcessBuilder("javac", fileName);
        } else if (fileName.endsWith(".c")) {
            return new ProcessBuilder("gcc", "-o", fileName.replace(".c", ""), fileName);
        } else if (fileName.endsWith(".cpp") || fileName.endsWith(".cc")) {
            return new ProcessBuilder("g++", "-o", fileName.substring(0, fileName.lastIndexOf('.')), fileName);
        } else if (fileName.endsWith(".rs")) {
            return new ProcessBuilder("rustc", fileName);
        } else if (fileName.endsWith(".cs")) {
            return new ProcessBuilder("csc", "/out:" + fileName.replace(".cs", ".exe"), fileName);
        }
        return null;
    }
    
    private ProcessBuilder getRunProcess(String fileName, String fileDir) {
        if (fileName.endsWith(".java")) {
            String className = fileName.replace(".java", "");
            return new ProcessBuilder("java", className);
        } else if (fileName.endsWith(".c") || fileName.endsWith(".cpp") || fileName.endsWith(".cc")) {
            String exeName = fileName.substring(0, fileName.lastIndexOf('.'));
            // Handle Windows vs Unix executable names
            String exePath = System.getProperty("os.name").toLowerCase().contains("win") 
                ? exeName + ".exe" : exeName;
            return new ProcessBuilder("./" + exePath);
        } else if (fileName.endsWith(".rs")) {
            String exeName = fileName.replace(".rs", "");
            return new ProcessBuilder("./" + exeName);
        } else if (fileName.endsWith(".cs")) {
            String exeName = fileName.replace(".cs", ".exe");
            return new ProcessBuilder("mono", exeName);
        } else if (fileName.endsWith(".py") || fileName.endsWith(".pyw")) {
            return new ProcessBuilder("python3", fileName);
        }
        return null;
    }
    
    private String readStream(InputStream stream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }
    
    public void shutdown() {
        executor.shutdownNow();
    }
}
