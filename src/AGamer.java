import java.util.Random;

public abstract class AGamer {
    // метка
    protected String sign;

    // выстрел в координаты x и y
    abstract boolean shot(int x, int y);

    // проверка победы
    abstract boolean win();
}

