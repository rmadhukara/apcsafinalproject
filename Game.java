import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class Game extends GameLogic
{
    private int[][] colorNum;
    private int[][] statusNum;
    private String boardColorInts;
    private String boardStatusInts;
    
    private boolean setUp = true;
    
    // Board cells numbered 0-55, top to bottom, left to right; null if empty
    private Player[] playerButtons = new Player[6];

    Player currentPlayer;
    
    private static int height = NEWFillerClient.HEIGHT;
    private static int width = NEWFillerClient.WIDTH;
    
    public Game(int[][] colors, int[][] status) 
    {
      colorNum = colors;
      statusNum = status;
      boardColorInts = "";
      boardStatusInts = "";

      turnArrayToString();
    }

    public void turnArrayToString()
    {
      boardColorInts = "";
      for (int[] row : colorNum) 
      {
        for (int col : row) 
        {
          boardColorInts += " " + col;
        }
      }
      
      boardStatusInts = "";
      for (int[] row : statusNum) 
      {
        for (int col : row) 
        {
          boardStatusInts += " " + col;
        }
      }
    }
    
    public boolean hasWinner()
    {
      return boardFilledUp() && (currentPlayer.getScore() < currentPlayer.opponent.getScore() || 
          currentPlayer.getScore() > currentPlayer.opponent.getScore());
    }
    
    public boolean isTie() {
      return boardFilledUp() && currentPlayer.getScore() == currentPlayer.opponent.getScore();
    }

    public boolean boardFilledUp() {
      int count = 0;

      for (int row = 0; row < statusNum.length; row++)
      {
        for (int col = 0; col < statusNum[0].length; col++)
        {
          if (statusNum[row][col] == 0)
          {
            count++;
          }
        } 
      }
      
      return !(count > 0);
    }

    public synchronized String move(int color, Player player) 
    {
        if (player != currentPlayer) {
            throw new IllegalStateException("Not your turn");
        } 
        else if (player.opponent == null) {
            throw new IllegalStateException("You don't have an opponent yet");
        } 
        else if (playerButtons[color] != null) {
            throw new IllegalStateException("Cell already occupied");
        }

        player.setCurrentColor(color);
        
        int theCurrentPlayerStatus = 0;
        if (player.getMark() == '1')
        {
          theCurrentPlayerStatus = 1;
        }
        else
        {
          theCurrentPlayerStatus = 2;
        }

        logic(colorNum, statusNum, theCurrentPlayerStatus, color);

        turnArrayToString();
        
        //Calculate scores
        int score1 = countScore(statusNum, 1);
        int score2 = countScore(statusNum, 2);
        player.setScore(score1);
        player.opponent.setScore(score2);
        
        //Switch Turns
        currentPlayer = currentPlayer.opponent;
        
        return boardColorInts + "-" + boardStatusInts;
    }

    public class Player implements java.lang.Runnable
    {
      private char mark;
      private Player opponent;
      private Socket socket;
      private Scanner input;
      private PrintWriter output;
      
      private String username;
      private int score;
      private int wins;
      private int currentColor;
      
      public Player(Socket socket, char mark) 
      {
        this.socket = socket;
        this.mark = mark;
      }

      public char getMark()
      {
        return mark;
      }
      
      public String getUsername() 
      {
        return username;
      }
      
      public int getScore() 
      {
        return score;
      }
      
      public int getWins() 
      {
        return wins;
      }
      
      public void setUsername(String user) 
      {
        username = user;
      }
      
      public void setScore(int s) 
      {
        score = s;
      }
      
      public void setWins(int w) 
      {
        wins = w;
      }
      
      public void addWin()
      {
        wins++;
      }

      public void setCurrentColor(int color)
      {
        currentColor = color;
      }

      public int getCurrentColor()
      {
        return currentColor;
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
        output.println("BOARD_UPDATE" + boardColorInts + "-" + boardStatusInts);
        
        if (mark == '1') 
        {
          currentPlayer = this;
          output.println("MESSAGE Waiting for opponent to connect");
        } 
        else 
        {
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
          if (command.startsWith("QUIT")) 
          {
            return;
          } 
          else if (command.startsWith("MOVE")) 
          {
            int colorToChangeTo = Integer.parseInt(command.substring(5));

            if (colorToChangeTo == currentPlayer.getCurrentColor() || colorToChangeTo == currentPlayer.opponent.getCurrentColor())
            {
              output.println("INVALID");
            }
            else
            {
              processMoveCommand(colorToChangeTo);
            }
          }
          else if (command.startsWith("USER"))
          {
            String username = command.substring(5, command.indexOf("-"));
            setUsername(username);
            
            int wins = Integer.parseInt(command.substring(command.indexOf("-") + 1));
            setWins(wins);
            
            if (setUp && getMark() == '2') {
              String user1 = opponent.getUsername();
              String user2 = getUsername();
              
              int wins1 = opponent.getWins();
              int wins2 = getWins();
              
              //Set usernames
              opponent.processUsernameCommand(user1 + "-" + user2);
              processUsernameCommand(user1 + "-" + user2);
              
              //Set wins
              opponent.processWinsCommand(wins1 + "-" + wins2);
              processWinsCommand(wins1 + "-" + wins2);
              
              //Set current color
              opponent.setCurrentColor(colorNum[height-1][0]);
              setCurrentColor(colorNum[0][width-1]);
              
              //finish setup
              setUp = false;
            }
          }
        }
      }

      private void processMoveCommand(int color) 
      {
        try 
        {
          String updateMessage = move(color, this);
          
          output.println("VALID_MOVE");
          
          // Update grid
          output.println("BOARD_UPDATE" + updateMessage);
          opponent.output.println("BOARD_UPDATE" + updateMessage);
          
          // Update score: score1-score2
          output.println("UPDATE_SCORE " + getScore() + "-" + opponent.getScore());
          opponent.output.println("UPDATE_SCORE " + getScore() + "-" + opponent.getScore());
          
          // Test for winner          
          if (hasWinner()) {
            currentPlayer.addWin();
            setWins();
            currentPlayer.processWinner();
          }
          else if (isTie()) 
          {
            currentPlayer.output.println("REPAINT");
            currentPlayer.opponent.output.println("REPAINT");
            
            currentPlayer.output.println("TIE");
            currentPlayer.opponent.output.println("TIE");
          }
          
          opponent.output.println("OPPONENT_MOVED " + color);
        }
        
        catch (IllegalStateException e) 
        {
          output.println("MESSAGE " + e.getMessage());
        }
      }
      
      private void setWins() {
        int wins1 = 0;
        int wins2 = 0;
        
        if (getMark() == '1') {
          wins1 = getWins();
          wins2 = opponent.getWins();
        }
        else {
          wins1 = opponent.getWins();
          wins2 = getWins();
        }
        processWinsCommand(wins1 + "-" + wins2);
        opponent.processWinsCommand(wins1 + "-" + wins2);
      }
      
      private void processUsernameCommand(String message)
      {
        try 
        {
          output.println("UPDATE_USER " + message);
        } 
        catch (IllegalStateException e) 
        {
          output.println("MESSAGE " + e.getMessage());
        }
      }
      
      private void processWinsCommand(String message)
      {
        try 
        {
          output.println("UPDATE_WINS " + message);
        } 
        catch (IllegalStateException e) 
        {
          output.println("MESSAGE " + e.getMessage());
        }
      }
      
      private void processWinner()
      {
        try 
        {
          output.println("VICTORY");
          opponent.output.println("DEFEAT"); 
        } 
        catch (IllegalStateException e) 
        {
          output.println("MESSAGE " + e.getMessage());
        }
      }
    } // end Player class
} // end Game class