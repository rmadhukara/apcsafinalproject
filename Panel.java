import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

<<<<<<< HEAD
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Panel extends JPanel {
  JLabel label = new JLabel();

  public Panel(Color col) {
      setBackground(col);
      setLayout(new GridBagLayout());
      label.setFont(new Font("Arial", Font.BOLD, 40));
      add(label);
  }

  public void setText(char text) {
      label.setForeground(text == 'X' ? Color.BLUE : Color.RED);
      label.setText(text + "");
  }
}
=======
public static class Panel extends JPanel
{

}
>>>>>>> ce843c2179415b5de5bf1ca492e44a05925d210b
