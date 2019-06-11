import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Panel extends JPanel {
  
  public Panel(Color col) {
      setBackground(col);
      setLayout(new GridBagLayout());
  }
}