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

  //Override from Runnable
  public void run() 
  {
    try 
    {
      setup();
      processCommands();
    } 
    catch (Exception e) 
    {
      e.printStackTrace();
    } 
    finally 
    {
      if (opponent != null && opponent.output != null) 
      {
          opponent.output.println("OTHER_PLAYER_LEFT");
      }
      try 
      {
        socket.close();
      } 
      catch (IOException e) {}
    }
  }

  private void setup() throws IOException 
  {
    input = new Scanner(socket.getInputStream());
    output = new PrintWriter(socket.getOutputStream(), true);
    output.println("WELCOME " + mark);
    if (mark == 'X') {
        currentPlayer = this;
        output.println("MESSAGE Waiting for opponent to connect");
    } else {
        opponent = currentPlayer;
        opponent.opponent = this;
        opponent.output.println("MESSAGE Your move");
    }
  }

  private void processCommands() 
  {
    while (input.hasNextLine()) 
    {
      String command = input.nextLine();
      if (command.startsWith("QUIT")) {
          return;
      } else if (command.startsWith("MOVE")) {
          processMoveCommand(Integer.parseInt(command.substring(5)));
      }
    }
  }

  private void processMoveCommand(int location) 
  {
    try 
    {
      move(location, this);
      output.println("VALID_MOVE");
      opponent.output.println("OPPONENT_MOVED " + location);
      if (hasWinner()) 
      {
        output.println("VICTORY");
        opponent.output.println("DEFEAT");
      } 
      else if (boardFilledUp()) 
      {
        output.println("TIE");
        opponent.output.println("TIE");
      }
    } 
    catch (IllegalStateException e) 
    {
      output.println("MESSAGE " + e.getMessage());
    }
  }
}