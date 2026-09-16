# FastCode IDE

A fast, unbloated coding IDE built with Java Swing featuring a classic UI and support for multiple programming languages.

## Features

- **Classic UI Layout**: Menu bar, toolbar, file explorer, code editor, output console, and status bar
- **Multi-Language Support**: C, C++, Java, Rust, C#, Python
- **Fast Performance**: Built with Java Swing for native speed without bloat
- **Essential Features**:
  - New/Open/Save files
  - Auto language detection by file extension
  - Build/Run capabilities with threaded execution
  - 30-second timeout protection for long-running processes
  - Undo/Redo support
  - Tab indentation (4 spaces)
  - File tree explorer
  - Output console with colored text

## Supported Languages

| Language | Extensions | Compiler | Runner |
|----------|-----------|----------|--------|
| C | .c | gcc | ./executable |
| C++ | .cpp, .cc, .h, .hpp | g++ | ./executable |
| Java | .java | javac | java |
| Rust | .rs | rustc | ./executable |
| C# | .cs | csc | mono |
| Python | .py, .pyw | - | python3 |

## Requirements

- Java JDK 8 or higher
- GCC/G++ for C/C++ (optional)
- Java JDK for Java (optional)
- Rust compiler for Rust (optional)
- Mono for C# (optional)
- Python 3 for Python (optional)

## Building

```bash
./build.sh
```

Or manually:
```bash
mkdir -p bin
javac -d bin src/com/fastcode/*.java src/com/fastcode/*/*.java
```

## Running

```bash
java -cp bin com.fastcode.Main
```

## Keyboard Shortcuts

- **Ctrl+N**: New File
- **Ctrl+O**: Open File
- **Ctrl+S**: Save File
- **Ctrl+Shift+S**: Save As
- **Ctrl+X**: Cut
- **Ctrl+C**: Copy
- **Ctrl+V**: Paste
- **Ctrl+Z**: Undo
- **Ctrl+Y**: Redo
- **Ctrl+A**: Select All
- **F5**: Run
- **F6**: Build and Run
- **F7**: Compile
- **Tab**: Insert 4 spaces (or indent selected lines)

## Project Structure

```
fastcode-ide/
├── src/com/fastcode/
│   ├── Main.java              # Application entry point
│   ├── ui/
│   │   └── MainFrame.java     # Main window UI
│   ├── editor/
│   │   └── CodeEditor.java    # Code editor component
│   ├── compiler/
│   │   └── LanguageCompiler.java  # Multi-language compiler
│   └── file/
│       └── FileManager.java   # File operations
├── bin/                       # Compiled classes
├── build.sh                   # Build script
└── README.md
```

## Design Philosophy

FastCode IDE is designed to be:
- **Fast**: Minimal overhead, no heavy frameworks
- **Simple**: Classic UI that developers know and love
- **Practical**: Support for the most popular programming languages
- **Lightweight**: No unnecessary features or bloat

## License

MIT License - See LICENSE file for details
