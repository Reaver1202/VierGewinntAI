/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package viergewinntai;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author rusinda
 */
public class GUI {

    private final int FRAMEWIDTH = 1200;
    private final int FRAMEHEIGHT = 700;
    private final int PLAYINGFIELDX = 50;
    private final int PLAYINGFIELDY = 100;
    private final int ROWBUTTONOFFSETX = 30;
    private final int ROWBUTTONWIDTH = 93;
    private final int ROWBUTTONHEIGHT = 525;
    private final boolean SHOWROWBUTTONS = false;
    private final String PLAYERONEPIC = "graphics\\playerone.png";
    private final String PLAYERTWOPIC = "graphics\\playertwo.png";
    private final String PLAYINGFIELDPIC = "graphics\\playingfield.png";
    private final int PIECEOFFSETXLEFT = 6;
    private final int PIECEOFFSETXBETWEEN = 7;
    private final int PIECEOFFSETYUP = 16;
    private final int ANIMATIONYOFFSET = 20;
    private final int ANIMATIONSPEED = 6;
    private JLabel field;
    private JPanel mainPanel = new JPanel();
    private JPanel menuPanel = new JPanel();
    private JPanel displayPanel = new JPanel();
    private JFrame mainFrame = new JFrame();
    private BufferedImage playerOnePic;
    private BufferedImage playerTwoPic;
    private LinkedList<JLabel> pieces = new LinkedList();
    private JLabel player = new JLabel();
    private JButton restart = new JButton("Restart");

    public void initialize() {
        showField();
        createMenu();
        createMessagePanel();
        mainPanel.repaint();
    }

    /**
     * Builds and displays the playing field
     */
    private void showField() {
        mainFrame.setSize(FRAMEWIDTH, FRAMEHEIGHT);
        BufferedImage myPicture = null;
        try {
            myPicture = ImageIO.read(new File(PLAYINGFIELDPIC));
        } catch (IOException ex) {
        }
        field = new JLabel(new ImageIcon(myPicture));
        mainFrame.setLayout(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(mainPanel);
        mainPanel.setBounds(0, 0, FRAMEWIDTH, FRAMEHEIGHT);
        mainPanel.setLayout(null);
        mainPanel.add(field);
        field.setBounds(PLAYINGFIELDX, PLAYINGFIELDY, myPicture.getWidth(), myPicture.getHeight());
        field.setVisible(true);


        RowButton[] buttons = new RowButton[7];

        for (int i = 0; i < 7; i++) {
            buttons[i] = new RowButton(PLAYINGFIELDX + ROWBUTTONOFFSETX + i * ROWBUTTONWIDTH, PLAYINGFIELDY, ROWBUTTONWIDTH, ROWBUTTONHEIGHT, (char) (i + 65));
            mainPanel.add(buttons[i]);
        }

        mainFrame.setVisible(true);
        try {
            playerOnePic = ImageIO.read(new File(PLAYERONEPIC));
            playerTwoPic = ImageIO.read(new File(PLAYERTWOPIC));
        } catch (IOException ex) {
        }

    }

    /**
     * Displays the move taken, will be called from the game engine after
     * checking for a valid move
     *
     * @param column specifies the column
     * @param row specifies the row
     */
    public void showMove(int player, int column, int row) {
        if (player > 1) {
            pieces.add(new JLabel(new ImageIcon(playerTwoPic)));
        } else {
            pieces.add(new JLabel(new ImageIcon(playerOnePic)));
        }

        mainPanel.add(pieces.getLast());
        animate(pieces.getLast(), PLAYINGFIELDX + ROWBUTTONOFFSETX + PIECEOFFSETXLEFT + ((PIECEOFFSETXBETWEEN + playerOnePic.getHeight()) * (column - 1)), PLAYINGFIELDY + PIECEOFFSETYUP + ((6 - row) * playerOnePic.getHeight()), playerOnePic.getHeight(), playerOnePic.getWidth(), column - 1, row - 1);
        //pieces.getLast().setBounds(PLAYINGFIELDX + ROWBUTTONOFFSETX + PIECEOFFSETXLEFT + ((PIECEOFFSETXBETWEEN + playerOnePic.getHeight()) * (column-1)), PLAYINGFIELDY + PIECEOFFSETYUP + ((6 - row) * playerOnePic.getHeight()), playerOnePic.getHeight(), playerOnePic.getWidth());
    }

    private void animate(final JLabel piece, int x, final int lastY, int width, int height, final int column, final int row) {

        piece.setBounds(x, ANIMATIONYOFFSET - ((lastY - ANIMATIONYOFFSET) % ANIMATIONSPEED), width, height);

        javax.swing.Timer t = new javax.swing.Timer(1, new ActionListener() {
            int counter = ANIMATIONYOFFSET - ((lastY - ANIMATIONYOFFSET) % ANIMATIONSPEED);

            @Override
            public void actionPerformed(ActionEvent e) {

                counter += ANIMATIONSPEED;
                piece.setBounds(piece.getX(), counter, piece.getWidth(), piece.getHeight());
                mainPanel.repaint();
                if (counter >= lastY) {
                    piece.setBounds(piece.getX(), lastY, piece.getWidth(), piece.getHeight());
                    ((Timer) e.getSource()).stop();
                    VierGewinntAi.mainGameEngine.endMove(column, row);
                }
            }
        });
        t.start();

    }

    public void showFullMessage() {
        player.setText("<html><div align=\"center\">Draw!</div></html>");
    }

    public void createMessagePanel() {

        displayPanel.setBounds(800, 0, 390, 700);

        player.setBounds(50, 250, 300, 80);
        player.setVisible(true);
        player.setFont(new Font("Arial", Font.BOLD, 33));
        displayPanel.setLayout(null);
        displayPanel.add(player);
        restart.setBounds(95, 550, 200, 50);
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameEngine.lock = true;
                GameEngine.gameEnd = true;
                menuPanel.setVisible(true);
            }
        });
        displayPanel.add(restart);
        restart.setVisible(true);

        // displayPanel.setVisible(true);

        mainPanel.add(displayPanel);


    }

    /**
     * Shows which players turn it is, called from game engine
     */
    public void showPlayerTurnMessage() {

        if (VierGewinntAi.mainGameEngine.playerTurn == 1) {
            player.setText("<html><div align=\"center\">Player 1's turn</div></html>");
        } else {
            player.setText("<html><div align=\"center\">Player 2's turn</div></html>");
        }

//        PlayerTurnDisplay.setText("Spieler " + Integer.toString(VierGewinntAi.mainGameEngine.playerTurn) + " ist am Zug");
    }

    /**
     * Shows that the move taken by the player was invalid, called from game
     * engine
     */
    public void showInvalidMoveMessage() {
        if (VierGewinntAi.mainGameEngine.playerTurn == 1) {
            player.setText("<html><div align=\"center\">Inavalid move!<br>Player 1's turn</div></html>");
        } else {
            player.setText("<html><div align=\"center\">Inavalid move!<br>Player 2's turn</html>");
        }
    }

    /**
     * Shows a game over message with the winner
     */
    public void showWinMessage() {

        if (VierGewinntAi.mainGameEngine.playerTurn == 1) {
            player.setText("<html><div align=\"center\">Player 1<br>wins!</div></html>");
        } else {
            player.setText("<html><div align=\"center\">Player 2<br>wins!</div></html>");
        }
        restart.setVisible(true);



        // JOptionPane.showMessageDialog(null, "Sie haben gewonnen!", "Glückwunsch!!", JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * Shows the Menu before start
     *
     */
    private void createMenu() {


        menuPanel.setBounds(800, 0, 390, 700);

        menuPanel.setLayout(null);

        //left Side
        final JRadioButton human1 = new JRadioButton("Human");
        human1.setFont(new Font("Arial", Font.PLAIN, 20));
        final JRadioButton AI1 = new JRadioButton("Computer");
        AI1.setFont(new Font("Arial", Font.PLAIN, 20));
        human1.setSelected(true);
        JLabel player1label = new JLabel("Player 1");
        ButtonGroup player1 = new ButtonGroup();

        player1.add(human1);
        player1.add(AI1);
        player1label.setFont(new Font("Arial", Font.BOLD, 25));
        player1label.setBounds(10, 80, 185, 80);
        human1.setBounds(10, 160, 185, 80);
        AI1.setBounds(10, 240, 185, 80);
        final JSlider difficulty1 = new JSlider(1, 4);
        difficulty1.setBounds(10, 320, 160, 60);

        final JLabel label1 = new JLabel();
        label1.setBounds(10, 360, 100, 80);
        label1.setText("normal");

        difficulty1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                int value = difficulty1.getValue();
                String str;
                str = "";

                switch (value) {
                    case 1: {
                        str = "easy";
                        VierGewinntAi.AI[0].random = true;
                        break;
                    }
                    case 2: {
                        str = "normal";
                        VierGewinntAi.AI[0].minMaxDepth = 3;
                        VierGewinntAi.AI[0].random = false;
                        break;
                    }
                    case 3: {
                        str = "hard";
                        VierGewinntAi.AI[0].minMaxDepth = 5;
                        VierGewinntAi.AI[0].random = false;
                        break;
                    }

                    case 4: {

                        str = "very hard";
                        VierGewinntAi.AI[0].minMaxDepth = 7;
                        VierGewinntAi.AI[0].random = false;
                        break;
                    }

                }

                label1.setText(str);
            }
        });

        AI1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (AI1.isSelected()) {
                    difficulty1.setVisible(true);
                    label1.setVisible(true);
                } else {
                    difficulty1.setVisible(false);
                    label1.setVisible(false);
                }

            }
        });

        difficulty1.setVisible(false);
        label1.setVisible(false);

        menuPanel.add(label1);
        menuPanel.add(difficulty1);
        menuPanel.add(human1);
        menuPanel.add(AI1);
        menuPanel.add(player1label);

        //right Site
        final JRadioButton human2 = new JRadioButton("Human");
        human2.setFont(new Font("Arial", Font.PLAIN, 20));
        final JRadioButton AI2 = new JRadioButton("Computer");
        AI2.setFont(new Font("Arial", Font.PLAIN, 20));
        JLabel player2label = new JLabel("Player 2");

        human2.setSelected(true);
        ButtonGroup player2 = new ButtonGroup();
        player2.add(human2);
        player2.add(AI2);
        player2label.setBounds(195, 80, 185, 80);
        player2label.setFont(new Font("Arial", Font.BOLD, 25));
        human2.setBounds(195, 160, 185, 80);
        AI2.setBounds(195, 240, 185, 80);
        final JSlider difficulty2 = new JSlider(1, 4);
        difficulty2.setBounds(195, 320, 160, 60);

        final JLabel label2 = new JLabel();
        label2.setBounds(195, 360, 100, 80);
        label2.setText("normal");

        difficulty2.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                int value = difficulty2.getValue();
                String str;
                str = "";

                switch (value) {
                    case 1: {
                        str = "easy";
                        VierGewinntAi.AI[1].random = true;
                        break;
                    }
                    case 2: {
                        str = "normal";
                        VierGewinntAi.AI[1].minMaxDepth = 3;
                        VierGewinntAi.AI[1].random = false;
                        break;
                    }
                    case 3: {
                        str = "hard";
                        VierGewinntAi.AI[1].minMaxDepth = 5;
                        VierGewinntAi.AI[1].random = false;
                        break;
                    }
                    case 4: {

                        str = "very hard";
                        VierGewinntAi.AI[1].minMaxDepth = 7;
                        VierGewinntAi.AI[1].random = false;
                        break;
                    }

                }

                label2.setText(str);
            }
        });

        AI2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (AI2.isSelected()) {
                    difficulty2.setVisible(true);
                    label2.setVisible(true);
                } else {
                    difficulty2.setVisible(false);
                    label2.setVisible(false);
                }

            }
        });

        difficulty2.setVisible(false);
        label2.setVisible(false);

        menuPanel.add(label2);

        menuPanel.add(difficulty2);

        menuPanel.add(human2);
        menuPanel.add(AI2);
        menuPanel.add(player2label);

        JButton startButton = new JButton("Start");
        startButton.setBounds(95, 550, 200, 50);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (human1.isSelected()) {
                    VierGewinntAi.mainGameEngine.setPlayerOneHuman(true);
                } else {
                    VierGewinntAi.mainGameEngine.setPlayerOneHuman(false);
                }
                if (human2.isSelected()) {
                    VierGewinntAi.mainGameEngine.setPlayerTwoHuman(true);
                } else {
                    VierGewinntAi.mainGameEngine.setPlayerTwoHuman(false);
                }
                resetField();
                VierGewinntAi.mainGameEngine.initialize();

                menuPanel.setVisible(false);
                displayPanel.setVisible(true);
                mainPanel.repaint();
//                mainPanel.remove(menuPanel);
            }
        });
        menuPanel.add(startButton);
        mainPanel.add(menuPanel);
        menuPanel.setVisible(true);


    }

    /**
     * Empties the field, called from game engine
     */
    public void resetField() {
        for (int i = 0; i < pieces.size(); i++) {
            mainPanel.remove(pieces.get(i));
        }
        pieces = new LinkedList<>();
    }

    private class RowButton extends JButton {

        public RowButton(int xfield, int yfield, int xwidth, int ywidth, final char column) {
            this.setOpaque(SHOWROWBUTTONS);
            this.setContentAreaFilled(SHOWROWBUTTONS);
            this.setBorderPainted(SHOWROWBUTTONS);
            this.setBounds(xfield, yfield, xwidth, ywidth);
            this.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {

                    if (!GameEngine.lock) {
                        GameEngine.lock = true;
                        VierGewinntAi.mainGameEngine.tryMove(column);
                    }
//                    JOptionPane.showMessageDialog(null, Character.toString(column));

                }
            });
        }
    }
}
