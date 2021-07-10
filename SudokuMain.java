

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;

public class SudokuMain extends JFrame implements ActionListener {

    int[][] feld;
    JTextField[][] textfelder;
    // der vordere Index adressiert die Zeile (y-Koordinate)
    // und der hintere Index die Spalte (x-Koordinate)

    JTextField info;
    JPanel tfPanel;
    JPanel bPanel;
    JButton findLoes, test, clear;
    Font f = Font.decode(Font.MONOSPACED);
    

    SudokuMain() {
        this.setTitle("Sudoku Solver");
        this.setSize(300, 350);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        tfPanel = new JPanel();
        tfPanel.setLayout(new GridLayout(9, 9));
        feld = new int[9][];
        textfelder = new JTextField[9][];
        for (int i = 0; i < 9; i++) {
            textfelder[i] = new JTextField[9];
            feld[i] = new int[9];
            for (int j = 0; j < 9; j++) {
                feld[i][j] = 0;
                JTextField jtf = new JTextField("", 1);
                textfelder[i][j] = jtf;
                jtf.setFont(f);
                jtf.setDocument(new TDoc(2));
                tfPanel.add(textfelder[i][j]);
            }
        }
        bPanel = new JPanel();
        bPanel.setLayout(new GridLayout(2, 2));
        findLoes = new JButton("findeLösung");
        test = new JButton("test");
        clear = new JButton("clear");
        findLoes.addActionListener(this);
        findLoes.setActionCommand("solve");
        test.addActionListener(this);
        test.setActionCommand("test");
        clear.addActionListener(this);
        clear.setActionCommand("clear");
        info = new JTextField("bereit", 15);
        info.setFont(f);
        info.setEditable(false);
        bPanel.add(info);
        bPanel.add(findLoes);
        bPanel.add(test);
        bPanel.add(clear);

        this.add(tfPanel, BorderLayout.CENTER);
        this.add(bPanel, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new SudokuMain();

    }

    void printFeld() {
        for (int i = 0; i < 9; i++) {
            System.out.println();
            for (int j = 0; j < 9; j++)
                System.out.print(feld[i][j]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("solve")) {
            read();
            if (testBoard(feld)) {
                solve(feld);
                write();
            } else
                info.setText("nicht lösbar");
        }
        if (s.equals("test")) {
            read();

            if (testBoard(feld))
                info.setText("lösbar");
            else
                info.setText("nicht lösbar");
        }
        if (s.equals("clear")) {
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++) {
                    feld[i][j] = 0;
                    textfelder[i][j].setText("");
                }
            info.setText("bereit");
        }

    }

    void write() {
        info.setText("");
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                textfelder[i][j].setText(feld[i][j] + "");
            }
    }

    void read() {
        info.setText("");
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                try {
                    if (textfelder[i][j].getText().trim().length() > 0) {
                        String s = textfelder[i][j].getText().trim().substring(0, 1);
                        int z = Integer.parseInt(s);
                        feld[i][j] = z;
                    } else
                        feld[i][j] = 0;
                } catch (NumberFormatException n) {
                }
            }
        write();
    }

    boolean testBoard(int[][] inBoard) {
        for (int i = 0; i < 9; i++) {
            boolean[] testCardH = new boolean[10];
            boolean[] testCardV = new boolean[10];
            for (int j = 0; j < 9; j++) {
                if (inBoard[i][j] > 0) if (!testCardV[inBoard[i][j]])
                    testCardV[inBoard[i][j]] = true;
                else
                    return false;
                if (inBoard[j][i] > 0) if (!testCardH[inBoard[j][i]])
                    testCardH[inBoard[j][i]] = true;
                else
                    return false;
            }
        }
        for (int yU = 0; yU < 7; yU += 3)
            for (int xU = 0; xU < 7; xU += 3) {
                boolean[] testCard = new boolean[10];
                for (int y = yU; y < (yU + 3); y++)
                    for (int x = xU; x < (xU + 3); x++) {
                        if (inBoard[y][x] > 0) if (!testCard[inBoard[y][x]])
                            testCard[inBoard[y][x]] = true;
                        else
                            return false;
                    }
            }
        return true;
    }

    boolean solve(int[][] inBoard) {
        for (int zeile = 0; zeile < 9; zeile++)
            for (int spalte = 0; spalte < 9; spalte++)
                if (inBoard[zeile][spalte] == 0) {
                    for (int zahl = 1; zahl <= 9; zahl++) {
                        inBoard[zeile][spalte] = zahl;
                        if (testBoard(inBoard) && solve(inBoard)) return true;
                        inBoard[zeile][spalte] = 0;
                    }
                    return false;
                }
        return true;
    }
}

class TDoc extends PlainDocument {
    int maxLength;

    TDoc(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str.length() == 0) return;
        char c = str.charAt(0);
        if(!Character.isDigit(c) || c == '0') return;
        if (getLength() + str.length() < maxLength) super.insertString(offs, str, a);
    }
}