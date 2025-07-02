import java.util.Scanner;

public class TicTacToe {

    // Represents the Tic-Tac-Toe board
    private char[][] board;
    // Current player, starts with 'X'
    private char currentPlayer;
    // Scanner for reading user input
    private Scanner scanner;

    /**
     * Constructor to initialize the game board and current player.
     */
    public TicTacToe() {
        // Initialize a 3x3 board with empty spaces
        board = new char[3][3];
        // Set all cells to empty (space character)
        initializeBoard();
        // 'X' always starts
        currentPlayer = 'X';
        // Initialize the scanner
        scanner = new Scanner(System.in);
    }

    /**
     * Initializes the 3x3 board with empty spaces.
     */
    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    /**
     * Displays the current state of the Tic-Tac-Toe board to the console.
     * It shows row and column numbers for user convenience.
     */
    private void displayBoard() {
        System.out.println("\n-------------");
        System.out.println("  0   1   2"); // Column numbers
        for (int i = 0; i < 3; i++) {
            System.out.print(i + " "); // Row number
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + (j == 2 ? "" : " | ")); // Print cell content with separator
            }
            System.out.println();
            if (i < 2) {
                System.out.println("  ---------"); // Separator between rows
            }
        }
        System.out.println("-------------\n");
    }

    /**
     * Handles a player's turn. Prompts the player for row and column,
     * validates the input, and places their mark on the board.
     *
     * @return true if the move was successful, false otherwise (e.g., invalid input or occupied cell)
     */
    private boolean makeMove() {
        int row, col;
        // Loop until a valid move is made
        while (true) {
            System.out.println("Player " + currentPlayer + ", enter your move (row and column, e.g., 0 1): ");
            // Read row
            if (scanner.hasNextInt()) {
                row = scanner.nextInt();
            } else {
                System.out.println("Invalid input. Please enter numbers for row and column.");
                scanner.next(); // Consume the invalid input
                continue;
            }

            // Read column
            if (scanner.hasNextInt()) {
                col = scanner.nextInt();
            } else {
                System.out.println("Invalid input. Please enter numbers for row and column.");
                scanner.next(); // Consume the invalid input
                continue;
            }

            // Validate if the chosen cell is within board boundaries
            if (row < 0 || row >= 3 || col < 0 || col >= 3) {
                System.out.println("Invalid move. Row and column must be between 0 and 2.");
            }
            // Validate if the chosen cell is empty
            else if (board[row][col] != ' ') {
                System.out.println("Invalid move. That cell is already occupied.");
            }
            // If valid, place the mark and break the loop
            else {
                board[row][col] = currentPlayer;
                return true;
            }
        }
    }

    /**
     * Checks if the current player has won the game.
     * Checks rows, columns, and both diagonals.
     *
     * @return true if the current player has won, false otherwise
     */
    private boolean checkWin() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (board[0][j] == currentPlayer && board[1][j] == currentPlayer && board[2][j] == currentPlayer) {
                return true;
            }
        }

        // Check main diagonal (top-left to bottom-right)
        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) {
            return true;
        }

        // Check anti-diagonal (top-right to bottom-left)
        if (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer) {
            return true;
        }

        return false; // No win detected
    }

    /**
     * Checks if the game is a draw. A draw occurs if all cells are filled
     * and no player has won.
     *
     * @return true if the game is a draw, false otherwise
     */
    private boolean checkDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false; // Found an empty cell, so it's not a draw yet
                }
            }
        }
        return true; // All cells are filled and no winner (checked before calling this)
    }

    /**
     * Switches the current player from 'X' to 'O' or 'O' to 'X'.
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    /**
     * The main game loop. Runs the Tic-Tac-Toe game until a win or draw occurs.
     */
    public void startGame() {
        boolean gameWon = false;
        boolean gameDraw = false;

        // Loop until the game is won or drawn
        while (!gameWon && !gameDraw) {
            displayBoard(); // Show the current board
            makeMove();     // Let the current player make a move

            gameWon = checkWin(); // Check if the current player won
            if (gameWon) {
                displayBoard(); // Display final board
                System.out.println("Player " + currentPlayer + " wins! Congratulations!");
                break; // End game
            }

            gameDraw = checkDraw(); // Check for a draw if no one won
            if (gameDraw) {
                displayBoard(); // Display final board
                System.out.println("It's a draw! No one wins.");
                break; // End game
            }

            switchPlayer(); // Switch to the next player
        }
        scanner.close(); // Close the scanner when the game ends
    }

    /**
     * Main method to run the Tic-Tac-Toe game.
     *
     * @param args Command line arguments (not used here)
     */
    public static void main(String[] args) {
        System.out.println("Welcome to Tic-Tac-Toe!");
        TicTacToe game = new TicTacToe();
        game.startGame();
    }
}
