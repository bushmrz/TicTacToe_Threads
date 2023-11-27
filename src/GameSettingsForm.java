import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GameSettingsForm extends JFrame {
    GameSettingsForm gameSettingsForm = this;
    public GameSettingsForm() {
        setTitle("Настройки игры");
        setBounds(450, 450, 240, 190);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JLabel jLabelMode = new JLabel("Режим игры:");
        add(jLabelMode);
        JRadioButton radioButtonModeAgainstAI = new JRadioButton("Автоматический");
        add(radioButtonModeAgainstAI);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(radioButtonModeAgainstAI);
        JLabel jLabelLinesCount = new JLabel("Размер поля (по умолчанию 3 на 3): ");
        add(jLabelLinesCount);
        JTextField jTextFieldLinesCount = new JTextField();
        jTextFieldLinesCount.setMaximumSize(new Dimension(100, 20));
        add(jTextFieldLinesCount);
        JButton jButtonSetSettings = new JButton("Начать игру!");
        add(jButtonSetSettings);
        setVisible(true);

        // Задаём размер поля, если размер поля не указан, то по умолчанию он равен - 3
        jButtonSetSettings.addActionListener((ActionEvent e) -> {
            MainGameField gameField = MainGameField.getInstance();
            if (jTextFieldLinesCount.getText().isEmpty()) {
                gameField.linesCount = 3;
            }
            else {
                try {
                    gameField.linesCount = Integer.parseInt(jTextFieldLinesCount.getText());
                }
                catch (NumberFormatException ex) {
                    System.out.println("Необходимо ввести целое число!");
                }
            }
            // Запускаем игру
            gameField.startNewGame();
            if (radioButtonModeAgainstAI.isSelected()) {
                gameField.gameMode = 1;
            }
            gameSettingsForm.setVisible(false);
        });
    }
}