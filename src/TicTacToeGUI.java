import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TicTacToeGUI extends JFrame implements ActionListener {
	private static final int BOARD_SIZE = 3;
	private char[][] board = new char[BOARD_SIZE][BOARD_SIZE];
	private JButton[][] boardButtons = new JButton[BOARD_SIZE][BOARD_SIZE];

	private Player currentPlayer;
	private Player xPlayer = new Player("X");
	private Player oPlayer = new Player("O");
	private JLabel statusLabel = new JLabel("X's turn");

	private JButton resetButton = new JButton("Reset");
	private JButton restartButton = new JButton("Restart");

	private JDialog dialog;

	private int xScore = 0;
	private int oScore = 0;
	private JLabel xLabel = new JLabel("  X Score: ");
	private JLabel xScoreValueLabel = new JLabel("0");
	private JLabel oLabel = new JLabel("  O Score: ");
	private JLabel oScoreValueLabel = new JLabel("0");
	private Font boldFont = new Font(Font.SANS_SERIF, Font.BOLD, 18);

	public TicTacToeGUI() {
		initializeBoard();

		setTitle("Tic-Tac-Toe");
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				boardButtons[row][col] = new JButton();
				boardButtons[row][col].setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));
				boardButtons[row][col].addActionListener(this);
				boardPanel.add(boardButtons[row][col]);
			}
		}
		add(boardPanel, BorderLayout.CENTER);

		xScoreValueLabel.setFont(boldFont);
		oScoreValueLabel.setFont(boldFont);
		resetButton.addActionListener(this);

		JPanel controlPanel = new JPanel();
		controlPanel.add(resetButton);
		controlPanel.add(xLabel);
		controlPanel.add(xScoreValueLabel);
		controlPanel.add(oLabel);
		controlPanel.add(oScoreValueLabel);
		add(controlPanel, BorderLayout.NORTH);

		add(statusLabel, BorderLayout.SOUTH);

		setVisible(true);
	}

	private void initializeBoard() {
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				board[i][j] = '-';
			}
		}
		currentPlayer = xPlayer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton clickedButton = (JButton) e.getSource();
			if (clickedButton == resetButton) {
				handleReset();
			} else if (clickedButton == restartButton) {
				dialog.dispose();
				resetGame();
			} else {
				if (makeMove(clickedButton)) {
					checkForWin();
					checkForDraw();
					switchPlayer();
				}
			}
		}
	}

	private boolean makeMove(JButton clickedButton) {
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				if (boardButtons[row][col] == clickedButton) {
					if (board[row][col] == '-') {
						board[row][col] = currentPlayer.getMark();
						clickedButton.setText(String.valueOf(currentPlayer.getMark()));
						return true;
					}
				}
			}
		}
		return false;
	}

	private void checkForWin() {
		for (int row = 0; row < BOARD_SIZE; row++) {
			if (checkCharsEqual(board[row][0], board[row][1], board[row][2])) {
				declareWinnerAndEndGame(false);
				return;
			}
		}

		for (int col = 0; col < BOARD_SIZE; col++) {
			if (checkCharsEqual(board[0][col], board[1][col], board[2][col])) {
				declareWinnerAndEndGame(false);
				return;
			}
		}

		if (checkCharsEqual(board[0][0], board[1][1], board[2][2])
				|| checkCharsEqual(board[0][2], board[1][1], board[2][0])) {
			declareWinnerAndEndGame(false);
			return;
		}
	}

	private void checkForDraw() {
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				if (board[row][col] == '-') {
					return;
				}
			}
		}
		declareWinnerAndEndGame(true);
	}

	private void switchPlayer() {
		currentPlayer = (currentPlayer.getMark() == 'X') ? oPlayer : xPlayer;
		statusLabel.setText(currentPlayer.getMark() + "'s turn");
	}

	private boolean checkCharsEqual(char a, char b, char c) {
		return a != '-' && a == b && b == c;
	}

	private void declareWinnerAndEndGame(boolean isDraw) {
		if (!isDraw) {
			statusLabel.setText(currentPlayer.getMark() + " wins!");
			if (currentPlayer.getMark() == 'X') {
				xScore++;
			} else {
				oScore++;
			}
			updateScoreLabels();
			showRestartDialog(isDraw);
		} else {
			statusLabel.setText("It's a draw!");
			showRestartDialog(isDraw);
		}
	}

	private void disableButtons() {
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				boardButtons[row][col].setEnabled(false);
			}
		}
	}

	private void resetGame() {
		initializeBoard();
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				boardButtons[row][col].setText("");
				boardButtons[row][col].setEnabled(true);
			}
		}
		statusLabel.setText("X's turn");
	}

	private void restartGame() {
		resetGame();
		xScore = 0;
		oScore = 0;
		updateScoreLabels();
	}

	private void updateScoreLabels() {
		xScoreValueLabel.setText("" + xScore);
		oScoreValueLabel.setText("" + oScore);
	}

	private void handleReset() {
		if (isBoardEmpty()) {
			restartGame();
		} else {
			int choice = JOptionPane.showConfirmDialog(this,
					"Are you sure you want to reset the board? This will not reset scores.", "Reset Confirmation",
					JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				resetGame();
			}
		}
	}

	private boolean isBoardEmpty() {
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				if (board[row][col] != '-') {
					return false;
				}
			}
		}
		return true;
	}

	private void showRestartDialog(boolean isDraw) {
		disableButtons();

		dialog = new JDialog(this, "Game Over", true);
		dialog.setLayout(new FlowLayout());

		if (!isDraw) {
			dialog.add(new JLabel(currentPlayer.getMark() + " wins!"));
		} else {
			dialog.add(new JLabel("It's a draw!"));
		}
		restartButton.addActionListener(this);
		dialog.add(restartButton);

		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

}
