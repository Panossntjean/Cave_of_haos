package rogue.ui;

import javax.swing.*;
import java.awt.*;

public class GameLogPanel extends JPanel {
    private JTextArea logArea;

    public GameLogPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 300));
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addMessage(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}