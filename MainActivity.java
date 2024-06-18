package com.cookandroid.minipro2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SudokuSolver sudokuSolver;
    private Handler timerHandler = new Handler();
    private long startTime = 0;
    private TextView timerTextView;
    private GridLayout sudokuGrid;
    private Runnable timerRunnable;
    private RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 루트 레이아웃 생성
        rootLayout = new RelativeLayout(this);
        rootLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        showGameStartScreen();
    }

    private void showGameStartScreen() {
        rootLayout.removeAllViews();

        TextView startTextView = new TextView(this);
        startTextView.setText("GAME START");
        startTextView.setTextSize(32);
        startTextView.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams startTextParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        startTextParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        startTextView.setLayoutParams(startTextParams);

        rootLayout.addView(startTextView);

        LinearLayout buttonLayout = new LinearLayout(this);
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        buttonParams.addRule(RelativeLayout.BELOW, startTextView.getId());
        buttonParams.setMargins(0, 50, 0, 0);
        buttonLayout.setLayoutParams(buttonParams);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);

        Button easyButton = new Button(this);
        easyButton.setText("Easy");
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(3, Color.BLUE, "EASY");
            }
        });

        Button mediumButton = new Button(this);
        mediumButton.setText("Medium");
        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(2, Color.YELLOW, "MEDIUM");
            }
        });

        Button hardButton = new Button(this);
        hardButton.setText("Hard");
        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(1, Color.RED, "HARD");
            }
        });

        buttonLayout.addView(easyButton);
        buttonLayout.addView(mediumButton);
        buttonLayout.addView(hardButton);

        rootLayout.addView(buttonLayout);

        setContentView(rootLayout);
    }

    private void startGame(int difficulty, int bgColor, String difficultyText) {
        rootLayout.removeAllViews();

        // 타이머 텍스트뷰 생성
        timerTextView = new TextView(this);
        timerTextView.setId(View.generateViewId());
        RelativeLayout.LayoutParams timerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        timerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        timerParams.setMargins(0, 50, 0, 50);
        timerTextView.setLayoutParams(timerParams);
        timerTextView.setTextSize(24);
        timerTextView.setText("00:00");

        // 난이도 텍스트뷰 생성
        TextView difficultyTextView = new TextView(this);
        difficultyTextView.setId(View.generateViewId());
        difficultyTextView.setBackgroundColor(bgColor);
        difficultyTextView.setTextColor(Color.WHITE);
        difficultyTextView.setPadding(20, 10, 20, 10);
        RelativeLayout.LayoutParams difficultyParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        difficultyParams.addRule(RelativeLayout.BELOW, timerTextView.getId());
        difficultyParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        difficultyParams.setMargins(0, 20, 0, 20);
        difficultyTextView.setLayoutParams(difficultyParams);
        difficultyTextView.setTextSize(24);
        difficultyTextView.setText(difficultyText);

        rootLayout.addView(timerTextView);
        rootLayout.addView(difficultyTextView);

        // 그리드 레이아웃 생성
        sudokuGrid = new GridLayout(this);
        RelativeLayout.LayoutParams gridParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        gridParams.addRule(RelativeLayout.BELOW, difficultyTextView.getId());
        gridParams.setMargins(16, 0, 16, 0);
        sudokuGrid.setLayoutParams(gridParams);
        sudokuGrid.setRowCount(9);
        sudokuGrid.setColumnCount(9);
        rootLayout.addView(sudokuGrid);

        // 셀 추가
        for (int i = 0; i < 81; i++) {
            EditText cell = new EditText(this);
            GridLayout.LayoutParams cellParams = new GridLayout.LayoutParams();
            cellParams.width = 0;
            cellParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            cellParams.rowSpec = GridLayout.spec(i / 9, 1, 1f);
            cellParams.columnSpec = GridLayout.spec(i % 9, 1, 1f);
            cell.setLayoutParams(cellParams);
            cell.setGravity(Gravity.CENTER);
            cell.setInputType(InputType.TYPE_CLASS_NUMBER);
            cell.setTextSize(18);
            cell.setBackgroundColor(Color.parseColor("#EEEEEE"));
            cell.setSingleLine();
            cell.setBackground(getCellBackground());
            sudokuGrid.addView(cell);
        }

        // 버튼 레이아웃 생성
        LinearLayout buttonLayout = new LinearLayout(this);
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonParams.setMargins(16, 16, 16, 16);
        buttonLayout.setLayoutParams(buttonParams);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);
        rootLayout.addView(buttonLayout);

        // 새 게임 버튼 생성
        Button newGameButton = new Button(this);
        newGameButton.setText("New Game");
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDifficultyOptions();
            }
        });
        buttonLayout.addView(newGameButton);

        // 힌트 버튼 생성
        Button hintButton = new Button(this);
        hintButton.setText("Hint");
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provideHint();
            }
        });
        buttonLayout.addView(hintButton);

        // 완료 버튼 생성
        Button completeButton = new Button(this);
        completeButton.setText("Complete");
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completePuzzle();
            }
        });
        buttonLayout.addView(completeButton);

        setContentView(rootLayout);

        sudokuSolver = new SudokuSolver();
        startNewGame(difficulty);
    }

    private void showDifficultyOptions() {
        final String[] difficulties = {"Easy", "Medium", "Hard"};
        final int[] difficultyLevels = {3, 2, 1}; // 하, 중, 상 순서

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Difficulty");
        builder.setItems(difficulties, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startGame(difficultyLevels[which], which == 0 ? Color.BLUE : which == 1 ? Color.YELLOW : Color.RED, difficulties[which].toUpperCase());
            }
        });
        builder.show();
    }

    private void startNewGame(int difficulty) {
        sudokuSolver.generatePuzzle(difficulty);
        int[][] board = sudokuSolver.getBoard();

        for (int i = 0; i < sudokuGrid.getChildCount(); i++) {
            EditText cell = (EditText) sudokuGrid.getChildAt(i);
            int row = i / 9;
            int col = i % 9;
            if (board[row][col] != 0) {
                cell.setText(String.valueOf(board[row][col]));
                cell.setEnabled(false);
            } else {
                cell.setText("");
                cell.setEnabled(true);
            }
        }

        startTime = System.currentTimeMillis();
        startTimer();
    }

    private void startTimer() {
        if (timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 500);
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void provideHint() {
        int[][] board = sudokuSolver.getBoard();
        while (true) {
            int row = (int) (Math.random() * 9);
            int col = (int) (Math.random() * 9);
            if (board[row][col] == 0) {
                for (int num = 1; num <= 9; num++) {
                    if (sudokuSolver.isValid(row, col, num)) {
                        board[row][col] = num;
                        updateGrid();
                        return;
                    }
                }
            }
        }
    }

    private void completePuzzle() {
        int[][] userBoard = new int[9][9];
        boolean isComplete = true;

        for (int i = 0; i < sudokuGrid.getChildCount(); i++) {
            EditText cell = (EditText) sudokuGrid.getChildAt(i);
            int row = i / 9;
            int col = i % 9;
            String cellText = cell.getText().toString();
            if (cellText.isEmpty()) {
                isComplete = false;
                break;
            } else {
                userBoard[row][col] = Integer.parseInt(cellText);
            }
        }

        if (isComplete && sudokuSolver.solveBoard(userBoard)) {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerHandler.removeCallbacks(timerRunnable);
            Toast.makeText(this, "퍼즐을 모두 완성했습니다. 축하합니다!" + String.format("%02d:%02d", minutes, seconds), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "스도쿠 퍼즐이 완성되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGrid() {
        int[][] board = sudokuSolver.getBoard();
        for (int i = 0; i < sudokuGrid.getChildCount(); i++) {
            EditText cell = (EditText) sudokuGrid.getChildAt(i);
            int r = i / 9;
            int c = i % 9;
            if (board[r][c] != 0) {
                cell.setText(String.valueOf(board[r][c]));
                cell.setEnabled(false);
            } else {
                if (cell.isEnabled() && !cell.getText().toString().isEmpty()) {
                    board[r][c] = Integer.parseInt(cell.getText().toString());
                }
            }
        }
    }

    private GradientDrawable getCellBackground() {
        GradientDrawable cellBackground = new GradientDrawable();
        cellBackground.setColor(Color.parseColor("#EEEEEE"));
        cellBackground.setStroke(2, Color.BLACK);  // 모든 셀에 동일한 두께의 테두리를 적용
        return cellBackground;
    }
}