import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.*;
import javax.swing.text.*;

public class NotepadClone extends JFrame {
    private JTextPane textPane;
    private String currentFilePath;
    private Timer timer;
    private Clip clip;

    public NotepadClone() {
        setTitle("Notepad Clone");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        textPane = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(textPane);
        add(scrollPane, BorderLayout.CENTER);

        createMenuBar();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopRingtone();
                dispose();
            }
        });

        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        newMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newFile();
            }
        });
        fileMenu.add(newMenuItem);

        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        openMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        fileMenu.add(openMenuItem);

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        fileMenu.add(saveMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        saveAsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFileAs();
            }
        });
        fileMenu.add(saveAsMenuItem);

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopRingtone();
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        // Timer menu
        JMenu timerMenu = new JMenu("Timer");

        JMenuItem setTimerMenuItem = new JMenuItem("Set Timer");
        setTimerMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));
        setTimerMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setTimer();
            }
        });
        timerMenu.add(setTimerMenuItem);

        menuBar.add(timerMenu);

        // Format menu
        JMenu formatMenu = new JMenu("Format");

        JMenuItem italicMenuItem = new JMenuItem("Italic");
        italicMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
        italicMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyItalic();
            }
        });
        formatMenu.add(italicMenuItem);

        JMenuItem unitalicMenuItem = new JMenuItem("Unitalic");
        unitalicMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        unitalicMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeItalic();
            }
        });
        formatMenu.add(unitalicMenuItem);

        JMenuItem boldMenuItem = new JMenuItem("Bold");
        boldMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK));
        boldMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyBold();
            }
        });
        formatMenu.add(boldMenuItem);

        JMenuItem unboldMenuItem = new JMenuItem("Unbold");
        unboldMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        unboldMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeBold();
            }
        });
        formatMenu.add(unboldMenuItem);

        JMenuItem fontSizeMenuItem = new JMenuItem("Font Size");
        fontSizeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        fontSizeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setFontSize();
            }
        });
        formatMenu.add(fontSizeMenuItem);

        JMenuItem underlineMenuItem = new JMenuItem("Underline");
        underlineMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
        underlineMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyUnderline();
            }
        });
        formatMenu.add(underlineMenuItem);

        JMenuItem ununderlineMenuItem = new JMenuItem("Ununderline");
        ununderlineMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        ununderlineMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeUnderline();
            }
        });
        formatMenu.add(ununderlineMenuItem);

        menuBar.add(formatMenu);

        setJMenuBar(menuBar);
    }

    private void newFile() {
        textPane.setText("");
        setTitle("Notepad Clone");
        currentFilePath = null;
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            currentFilePath = selectedFile.getPath();
            setTitle("Notepad Clone - " + currentFilePath);
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                textPane.setText(content.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveFile() {
        if (currentFilePath == null) {
            saveFileAs();
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFilePath))) {
                writer.write(textPane.getText());
                setTitle(getTitle().replace("*", ""));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveFileAs() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getPath();
            if (!filePath.endsWith(".txt")) {
                filePath += ".txt";
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(textPane.getText());
                currentFilePath = filePath;
                setTitle("Notepad Clone - " + currentFilePath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setTimer() {
        JTextField timerField = new JTextField(10);
        Object[] message = {"Enter the timer in seconds:", timerField};

        int option = JOptionPane.showConfirmDialog(this, message, "Set Timer", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String timerText = timerField.getText();
            try {
                int timerSeconds = Integer.parseInt(timerText);
                startTimer(timerSeconds);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid timer value. Please enter a valid number.");
            }
        }
    }

    private void startTimer(int seconds) {
        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                timerComplete();
            }
        }, seconds * 1000);
    }

    private void timerComplete() {
        playRingtone();
        JOptionPane.showMessageDialog(this, "Timer complete!", "Timer", JOptionPane.INFORMATION_MESSAGE);
        stopRingtone();
    }

    private void playRingtone() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    File soundFile = new File("src/1.wav"); // Replace with your WAV ringtone file name
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                    clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void stopRingtone() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    private void applyFontToSelectedText(AttributeSet attr) {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        StyledDocument doc = textPane.getStyledDocument();
        doc.setCharacterAttributes(start, end - start, attr, false);
    }

    private void applyItalic() {
        MutableAttributeSet attr = textPane.getInputAttributes();
        StyleConstants.setItalic(attr, !StyleConstants.isItalic(attr));
        applyFontToSelectedText(attr);
    }

    private void removeItalic() {
        MutableAttributeSet attr = textPane.getInputAttributes();
        StyleConstants.setItalic(attr, false);
        applyFontToSelectedText(attr);
    }

    private void applyBold() {
        MutableAttributeSet attr = textPane.getInputAttributes();
        StyleConstants.setBold(attr, !StyleConstants.isBold(attr));
        applyFontToSelectedText(attr);
    }

    private void removeBold() {
        MutableAttributeSet attr = textPane.getInputAttributes();
        StyleConstants.setBold(attr, false);
        applyFontToSelectedText(attr);
    }

    private void setFontSize() {
        String fontSize = JOptionPane.showInputDialog(this, "Enter font size:");
        try {
            int size = Integer.parseInt(fontSize);
            if (size > 0) {
                MutableAttributeSet attr = textPane.getInputAttributes();
                StyleConstants.setFontSize(attr, size);
                applyFontToSelectedText(attr);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid font size. Please enter a positive number.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid font size. Please enter a valid number.");
        }
    }

    private void applyUnderline() {
        MutableAttributeSet attr = textPane.getInputAttributes();
        StyleConstants.setUnderline(attr, true);
        applyFontToSelectedText(attr);
    }

    private void removeUnderline() {
        MutableAttributeSet attr = textPane.getInputAttributes();
        StyleConstants.setUnderline(attr, false);
        applyFontToSelectedText(attr);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new NotepadClone();
            }
        });
    }
}
