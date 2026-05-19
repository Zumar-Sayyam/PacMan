import java.util.Random;
import java.util.Scanner;

interface PacManActions {
    void moveUp();
    void moveDown();
    void moveLeft();
    void moveRight();
    void displayBoard();
}

// ================= RANDOM GHOST =================
class RandomWalker {
    protected int x, y;
    protected Random rand = new Random();

    public RandomWalker(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void walk(Window w) {

        int nx = x;
        int ny = y;

        int d = rand.nextInt(4);

        switch (d) {
            case 0 -> nx++;
            case 1 -> nx--;
            case 2 -> ny++;
            case 3 -> ny--;
        }

        if (nx > 0 && ny > 0 &&
                nx < w.cols - 1 &&
                ny < w.rows - 1 &&
                w.maze[ny][nx] != '#') {

            x = nx;
            y = ny;
        }
    }
}

// ================= PACMAN =================
class DirectedWalker extends RandomWalker {

    public DirectedWalker(int x, int y) {
        super(x, y);
    }

    public void walk(char dir, Window w) {

        int nx = x;
        int ny = y;

        switch (dir) {
            case 'W' -> ny--;
            case 'S' -> ny++;
            case 'A' -> nx--;
            case 'D' -> nx++;
        }

        if (nx > 0 && ny > 0 &&
                nx < w.cols - 1 &&
                ny < w.rows - 1 &&
                w.maze[ny][nx] != '#') {

            x = nx;
            y = ny;
        }
    }
}

// ================= WINDOW / MAZE =================
class Window {

    public char[][] maze;
    public int rows, cols;

    public Window(int r, int c) {

        rows = r;
        cols = c;

        maze = new char[rows][cols];

        setMaze();
    }

    public void setMaze() {

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {

                // ===== OUTER WALL =====
                if (i == 0 || i == rows - 1 ||
                        j == 0 || j == cols - 1) {

                    maze[i][j] = '#';
                }

                // ===== TOP & BOTTOM WALLS =====
                else if (i == 3 && j > 5 && j < 25) {

                    maze[i][j] = '#';

                } else if (i == 16 && j > 5 && j < 25) {

                    maze[i][j] = '#';
                }

                // ===== SMALL CORNERS =====
                else if ((i == 2 || i == 17) &&
                        (j == 6 || j == 24)) {

                    maze[i][j] = '#';
                }

                // ===== SIDE WALLS =====
                else if (i > 3 && i < 16 &&
                        (j == 2 || j == 27)) {

                    maze[i][j] = '#';
                }

                // ===== INNER SIDE WALLS =====
                else if (i > 5 && i < 14 &&
                        (j == 5 || j == 25)) {

                    maze[i][j] = '#';
                }

                // ===== LEFT STRUCTURE =====
                else if (i == 7 && j > 6 && j < 11) {

                    maze[i][j] = '#';

                } else if (i > 7 && i < 12 && j == 10) {

                    maze[i][j] = '#';

                } else if (i == 11 && j > 10 && j < 16) {

                    maze[i][j] = '#';

                } else if (i == 14 && j > 6 && j < 10) {

                    maze[i][j] = '#';

                } else if (i == 13 && j == 9) {

                    maze[i][j] = '#';

                } else if (j == 7 && i > 8 && i < 13) {

                    maze[i][j] = '#';
                }

                // ===== CENTER STRUCTURE =====
                else if (j == 13 && i > 5 && i < 9) {

                    maze[i][j] = '#';

                } else if (i == 8 && j > 13 && j < 18) {

                    maze[i][j] = '#';
                }

                // ===== RIGHT STRUCTURE =====
                else if (j == 17 && i > 8 && i < 15) {

                    maze[i][j] = '#';

                } else if (i == 14 && j > 17 && j < 23) {

                    maze[i][j] = '#';

                } else if (j == 19 && i > 8 && i < 12) {

                    maze[i][j] = '#';

                } else if (i == 6 && j > 19 && j < 25) {

                    maze[i][j] = '#';

                } else if (i == 5 && j > 14 && j < 19) {

                    maze[i][j] = '#';
                }

                // ===== FOOD =====
                else {

                    maze[i][j] = '.';
                }
            }
        }
    }
}

// ================= GAME =================
class PacManGame implements PacManActions {

    private Window window;

    private DirectedWalker pacman;

    private RandomWalker ghost1;
    private RandomWalker ghost2;

    private int score = 0;

    private int remainingDots;

    public PacManGame() {

        window = new Window(21, 31);

        pacman = new DirectedWalker(1, 1);

        ghost1 = new RandomWalker(10, 10);
        ghost2 = new RandomWalker(20, 10);

        remainingDots = 0;

        for (int i = 0; i < window.rows; i++) {

            for (int j = 0; j < window.cols; j++) {

                if (window.maze[i][j] == '.') {

                    remainingDots++;
                }
            }
        }

        clearStartPosition(pacman.x, pacman.y);
        clearStartPosition(ghost1.x, ghost1.y);
        clearStartPosition(ghost2.x, ghost2.y);
    }

    private void clearStartPosition(int x, int y) {

        if (window.maze[y][x] == '.') {

            window.maze[y][x] = ' ';
            remainingDots--;
        }
    }

    @Override
    public void moveUp() {
        movePac('W');
    }

    @Override
    public void moveDown() {
        movePac('S');
    }

    @Override
    public void moveLeft() {
        movePac('A');
    }

    @Override
    public void moveRight() {
        movePac('D');
    }

    private void movePac(char dir) {

        pacman.walk(dir, window);

        if (window.maze[pacman.y][pacman.x] == '.') {

            score += 5;

            remainingDots--;

            window.maze[pacman.y][pacman.x] = ' ';
        }

        ghost1.walk(window);
        ghost2.walk(window);

        if (checkCollision()) {

            displayBoard();

            System.out.println("\nGAME OVER! Ghost caught Pac-Man!");

            System.exit(0);
        }

        if (remainingDots == 0) {

            displayBoard();

            System.out.println("\nYOU WIN! All dots eaten!");

            System.exit(0);
        }
    }

    private boolean checkCollision() {

        return (pacman.x == ghost1.x && pacman.y == ghost1.y)
                || (pacman.x == ghost2.x && pacman.y == ghost2.y);
    }

    @Override
    public void displayBoard() {

        System.out.println("\nScore: " + score +
                " | Remaining Dots: " + remainingDots);

        for (int i = 0; i < window.rows; i++) {

            for (int j = 0; j < window.cols; j++) {

                if (i == pacman.y && j == pacman.x) {

                    System.out.print("P ");

                } else if ((i == ghost1.y && j == ghost1.x)
                        || (i == ghost2.y && j == ghost2.x)) {

                    System.out.print("G ");

                } else {

                    System.out.print(window.maze[i][j] + " ");
                }
            }

            System.out.println();
        }
    }
}

// ================= MAIN =================
public class Main {

    public static void main(String[] args) {

        PacManGame game = new PacManGame();

        Scanner sc = new Scanner(System.in);

        System.out.println("===== PAC-MAN GAME STARTED =====");

        while (true) {

            game.displayBoard();

            System.out.print("\nMove (W/A/S/D/Q): ");

            char c = sc.next().toUpperCase().charAt(0);

            if (c == 'Q') {

                System.out.println("Game Quit!");

                break;
            }

            switch (c) {

                case 'W' -> game.moveUp();

                case 'S' -> game.moveDown();

                case 'A' -> game.moveLeft();

                case 'D' -> game.moveRight();
            }
        }
    }
}