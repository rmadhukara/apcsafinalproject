import java.awt.*;

public class Player implements Playererable
{
  private int winCount;   //The total amount wins a player has
  private int controlCount;   //The total blocks the user controls in current game
  private Color color;
  
  public Player(Color c)
  {
    winCount = 0;
    controlCount = 0;
    color = c;
  }
  public Player(int wins)
  {
    winCount = wins;
    controlCount = 0;
  }

  public int getWinCount()
  {
    return winCount;
  }
  public int getControlCount()
  {
    return controlCount;
  }
  public Color getColor()
  {
    return color;
  }

  public void setColor(Color x)
  {
    color = x;
  }
  public void addWin()
  {
    winCount ++;
  }
  public void setControlCount(int x)
  {
    controlCount = x;
  }
}