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

/* TEST FOR LATER
public class Panel extends Canvas{
  private Color color;
  private int xPos;
  private int yPos;
  
  public Panel(Color col, int x, int y) {
      super();
      color = col;
      xPos = x;
      yPos = y;
  }
  
  public void paint(Graphics window) {
    Rectangle box = new Rectangle();
    box.setSize(20);
    box.draw(window, xPos, yPos, color);
  }
}
*/