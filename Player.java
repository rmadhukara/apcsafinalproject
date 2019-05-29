import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Player implements Playerable, Runnable
{
  private int winCount;   //The total amount wins a player has
  private int controlCount;   //The total blocks the user controls in current game
  private Color color;

  //For server stuff - Rachana
  private char mark;
  private Player opponent;
  private Socket socket;
  private Scanner input;
  private PrintWriter output;
  
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

  //SERVER
  public Player(Socket socket, char mark) 
  {
    this.socket = socket;
    this.mark = mark;
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

  public void run()
  {
    try 
  }
}