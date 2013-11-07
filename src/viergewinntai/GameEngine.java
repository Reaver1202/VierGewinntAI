/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package viergewinntai;

/**
 * Contains all game logic
 *
 * @author rusinda
 */
public class GameEngine {

    private boolean[] playerHuman = new boolean[2];
    private int[][] playingField = new int[7][6];  //the field with all positions, 0=empty, 1=first player, 2=second player
    public static boolean lock = true;
    public static boolean gameEnd = true;
    public int playerTurn; //1=first player's turn, 2=second player's turn
    
    public GameEngine(){
        playerHuman[0] = true;
        playerHuman[1] = true;
    }
    
    /**
     * Sets all variables and initializes the game engine
     */
    public void initialize() {
        for (int i = 0; i < playingField.length; i++) {
            for (int j = 0; j < playingField[i].length; j++) {
                playingField[i][j] = 0;
            }
        }
        playerTurn = 1;
        gameEnd = false;

        VierGewinntAi.mainGUI.showPlayerTurnMessage();

        if (!playerHuman[playerTurn - 1]) {
            startAi();
        } else {
            lock = false;
        }
    }

    public void setPlayerOneHuman(boolean set) {
        playerHuman[0] = set;
    }

    public void setPlayerTwoHuman(boolean set) {
        playerHuman[1] = set;
    }

    /**
     * Tries to take a move using the specified column, handles all checks
     *
     * @param column specifies the column where the piece should be placed
     */
    public void tryMove(char column) {
        tryMove((Character.toUpperCase(column) - 65));
    }

    public void tryMove(int column) {
        if (!checkMove(column)) {
            VierGewinntAi.mainGUI.showInvalidMoveMessage();
            System.err.println("Wrong move! Player: " + playerTurn + " Column: " + column);
            lock = false;
        } else {
            int i = 0;
            while (playingField[column][i] != 0) {
                i++;
            }
            int row = i;
            playingField[column][row] = playerTurn;

            VierGewinntAi.mainGUI.showMove(playerTurn, column + 1, row + 1);

        }
    }

    public void endMove(int column, int row) {
        if (checkWin(playingField, column, row)) {
            VierGewinntAi.mainGUI.showWinMessage();
            gameEnd = true;
            return;
        }

        if (checkFull()) {
            VierGewinntAi.mainGUI.showFullMessage();
            gameEnd = true;
            return;
        }

        if(gameEnd){
            return;
        }
        if (playerTurn == 1) {
            playerTurn = 2;
        } else {
            playerTurn = 1;
        }
        VierGewinntAi.mainGUI.showPlayerTurnMessage();

        if (!playerHuman[playerTurn - 1]) {
            startAi();
        } else {
            lock = false;
        }
    }

    /**
     * Checks if a move is valid
     *
     * @param column specifies the column
     * @return true if the move is valid, false otherwise
     */
    public boolean checkMove(char column) {

        return checkMove((Character.toUpperCase(column) - 65));
    }

    public boolean checkMove(int column) {
        if (playingField[column][5] != 0) {
            return false;
        }
        return true;
    }

    private void startAi() {
        VierGewinntAi.AI[playerTurn - 1].startAi();
    }

    private boolean checkFull() {
        int count = 0;
        for (int i = 0; i < 7; i++) {
            if (playingField[i][5] != 0) {
                count++;
            }
        }

        return count >= 7;
    }

    /**
     * Checks if the turn taken resulted in a player winning the game
     *
     * @return
     */
    public boolean checkWin(int[][] field, int column, int row) {

        int sum;
        int row_;
        int column_;

        sum = 1;
        row_ = row;
        column_ = column - 1;
        while (column_ >= 0 && field[column_][row_] == field[column][row]) {
            sum++;
            column_--;
        }
        column_ = column + 1;
        while (column_ < 7 && field[column_][row_] == field[column][row]) {
            sum++;
            column_++;
        }
        if (sum >= 4) {
            return true;
        }

        sum = 1;
        row_ = row - 1;
        column_ = column;
        while (row_ >= 0 && field[column_][row_] == field[column][row]) {
            sum++;
            row_--;
        }
        row_ = row + 1;
        while (row_ < 6 && field[column_][row_] == field[column][row]) {
            sum++;
            row_++;
        }
        if (sum >= 4) {
            return true;
        }

        sum = 1;
        row_ = row - 1;
        column_ = column - 1;
        while (column_ >= 0 && row_ >= 0 && field[column_][row_] == field[column][row]) {
            sum++;
            column_--;
            row_--;
        }
        row_ = row + 1;
        column_ = column + 1;
        while (column_ < 7 && row_ < 6 && field[column_][row_] == field[column][row]) {
            sum++;
            column_++;
            row_++;
        }
        if (sum >= 4) {
            return true;
        }

        sum = 1;
        row_ = row + 1;
        column_ = column - 1;
        while (column_ >= 0 && row_ < 6 && field[column_][row_] == field[column][row]) {
            sum++;
            column_--;
            row_++;
        }
        row_ = row - 1;
        column_ = column + 1;
        while (column_ < 7 && row_ >= 0 && field[column_][row_] == field[column][row]) {
            sum++;
            column_++;
            row_--;
        }
        if (sum >= 4) {
            return true;
        }


        return false;
    }

    public boolean checkWin(int[][] field) {

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] == 0) {
                    break;
                }
                if (checkWin(field, i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Resets the game
     */
    public void resetGame() {
        initialize();
//        VierGewinntAi.mainGUI.resetField();
    }
    
    public int[][] getPlayingField() {
        return VierGewinntAi.cloneArray((playingField));
    }
}
