import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Scanner;

public class MainGameField extends JPanel {
    private static MainGameField instance = null;
    // Размер игрового поля
    public static final int FIELD_SIZE = 450;
    // Начальное значение ячеек поля
    public final String NOT_SIGN = "*";
    // Признак, что игра закончилась
    boolean gameOver = false;
    // Сообщение, которое появится при завершении игры
    String gameOverMessage = "";
    static int linesCount = 3;
    int cellSize;
    int x, y;

    // Чей ход
    boolean nextTurn = false;

    Player player1;
    Player player2;

    private final Object playerOneLock = new Object();
    private final Object playerTwoLock = new Object();

    int gameMode = 1;
    // Уровень AI
    int aiLevel = 2;
    // Наше поле
    public String[][] cell;

    // Получение экземпляра MainGameField
    public static synchronized MainGameField getInstance() {
        if (instance == null)
            instance = new MainGameField();
        return instance;
    }

    // Запуск новой игры
    void startNewGame() {
        gameOver = false;
        gameOverMessage = "";
        // Размер одной ячейки
        cellSize = FIELD_SIZE / linesCount;
        cell = new String[linesCount][linesCount];
        // Перерисовка поля
        repaint();
        // Инициализация поля
        for (int i = 0; i < linesCount; i++) {
            for (int j = 0; j < linesCount; j++) {
                cell[i][j] = NOT_SIGN;
            }
        }
        gameMode = 1;
        aiLevel = 0;
        setVisible(true);
    }

    // Конструктор
    private MainGameField() {
        setVisible(false);
        player1 = new Player("X");
        player2 = new Player("O");

        // Считываем координаты клика мышью
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                x = e.getX() / cellSize;
                y = e.getY() / cellSize;
                System.out.println("Mouse clicked on " + e.getX() + " " + e.getY());

                if (!gameOver) {
                    if (gameMode == 1) {
                        // Старт нового потока для каждого игрока
                        new Thread(() -> modeAgainstAI(player1, playerOneLock)).start();
                        new Thread(() -> modeAgainstAI(player2, playerTwoLock)).start();
                    }
                }
            }
        });
    }

    void modeAgainstAI(Player player, Object lock) {
        synchronized (lock) {
            AI aiPlayer = new AI(player.getSign(), aiLevel, player.getSign());

            if (!gameOver) {
                if (aiPlayer.shot(x, y)) {
                    if (aiPlayer.win()) {
                        System.out.println("Игрок " + player.getSign() + " выиграл в потоке " + Thread.currentThread().getName() + "(+1) !!!");
                        gameOver = true;
                        gameOverMessage = "Игрок " + player.getSign() + " выиграл!!!";
                        repaint();
                    }
                    if (isFieldFull()) {
                        gameOver = true;
                        gameOverMessage = "Ничья!!!";
                        repaint();
                    }
                }
                repaint();
            }
        }
    }

    // Проверка ячейки на занятость
    boolean isCellBusy(int x, int y) {
        if (x < 0 || y < 0 || x > linesCount - 1 || y > linesCount - 1) {
            return false;
        }
        return cell[x][y] != NOT_SIGN;
    }

    // Проверка поля на заполнение
    public boolean isFieldFull() {
        for (int i = 0; i < linesCount; i++) {
            for (int j = 0; j < linesCount; j++) {
                if (cell[i][j] == NOT_SIGN)
                    return false;
            }
        }
        return true;
    }

    // Проверяем линию на равенство значений
    public boolean checkLine(int start_x, int start_y, int dx, int dy, String sign) {
        for (int i = 0; i < linesCount; i++) {
            if (cell[start_x + i * dx][start_y + i * dy] != sign)
                return false;
        }
        return true;
    }

    // Проверка победы
    public boolean checkWin(String sign) {
        for (int i = 0; i < linesCount; i++) {
            // проверяем строки
            if (checkLine(i, 0, 0, 1, sign)) return true;
            // проверяем столбцы
            if (checkLine(0, i, 1, 0, sign)) return true;
        }
        // проверяем диагонали
        if (checkLine(0, 0, 1, 1, sign)) return true;
        if (checkLine(0, linesCount - 1, 1, -1, sign)) return true;
        return false;
    }


    // Метод, который занимается отрисовкой всей графики на форме
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Рисуем линии, которые представляют собой сетку
        for (int i = 0; i <= this.linesCount; i++) {
            g.drawLine(0, i * this.cellSize, FIELD_SIZE, i * this.cellSize);
            g.drawLine(i * this.cellSize, 0, i * this.cellSize, FIELD_SIZE);
        }
        for (int i = 0; i < linesCount; i++) {
            for (int j = 0; j < linesCount; j++) {
                if (cell[i][j] != NOT_SIGN) {
                    if (cell[i][j] == "X") {

                        // Рисуем крестик
                        g.setColor(Color.RED);
                        g.drawLine((i * cellSize), (j * cellSize), (i + 1) * cellSize, (j + 1) * cellSize);
                        g.drawLine((i + 1) * cellSize, (j * cellSize), (i * cellSize), (j + 1) * cellSize);
                    }
                    if (cell[i][j] == "O") {

                        // Рисуем нолик
                        g.setColor(Color.BLUE);
                        g.drawOval((i * cellSize), (j * cellSize), cellSize, cellSize);
                    }
                }
            }
        }

        if (gameOver) {

            // Отрисовка сообщения при завершении игры
            g.setColor(Color.BLACK);
            g.fillRect(0, FIELD_SIZE / 2, FIELD_SIZE, FIELD_SIZE / 8);
            g.setColor(Color.RED);
            g.setFont(new Font("Tahoma", 10, 40));
            g.drawString(gameOverMessage, 0, 19 * FIELD_SIZE / 32);
        }
    }
}

