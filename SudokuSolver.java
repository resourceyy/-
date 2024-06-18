package com.cookandroid.minipro2;

public class SudokuSolver {
    private int[][] board;

    public SudokuSolver() {
        board = new int[9][9];
    }

    public void generatePuzzle(int difficulty) {
        board = new int[9][9];
        generateFullBoard();
        removeNumbers(difficulty);
    }

    private void generateFullBoard() {
        solve();
    }

    private void removeNumbers(int difficulty) {
        int cellsToRemove = 0;
        switch (difficulty) {
            case 1:
                cellsToRemove = 50;
                break;
            case 2:
                cellsToRemove = 40;
                break;
            case 3:
                cellsToRemove = 30;
                break;
        }

        while (cellsToRemove > 0) {
            int row = (int) (Math.random() * 9);
            int col = (int) (Math.random() * 9);
            if (board[row][col] != 0) {
                board[row][col] = 0;
                cellsToRemove--;
            }
        }
    }

    private void fillBoard() {
        solve();
    }

    public boolean isValid(int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }

        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean solveBoard(int[][] board) {
        this.board = board;
        return solve();
    }

    private boolean solve() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(row, col, num)) {
                            board[row][col] = num;
                            if (solve()) {
                                return true;
                            } else {
                                board[row][col] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] getBoard() {
        return board;
    }
}