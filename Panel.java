import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

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