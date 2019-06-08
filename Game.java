import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class Game extends GameLogic
{
    private static int height = NEWFillerClient.HEIGHT;
    private static int width = NEWFillerClient.WIDTH;

    private int[][] colorNum;
    private int[][] statusNum;
    private String boardColorInts;
    private String boardStatusInts;
    
    private boolean setUp = true;
    
    // Board cells numbered 0-55, top to bottom, left to right; null if empty
    private Player[] playerButtons = new Player[6];

    Player currentPlayer;
    
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
    
    public boolean hasWinner() {
		if(!boardFilledUp())
			return false;
		int player1 = 0;
		int player2 = 0;
		for(row = 0; row < statusNum.length; row++)
		{
			for(col = 0; col < statusNum[0].length; col++)
			{
				if(statusNum[row][col] == 1)
					player1++;
				else
					player2++;
			}
		}
		if(player1 != player2)
			return true;
    }

    public boolean boardFilledUp() {
      //FIX
      //return Arrays.stream(playerBoard).allMatch(p -> p != null);
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

      if (count > 0)
      {
        return false;
      }
      return true;
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
        
        //Switch Turns
        // currentPlayer = currentPlayer.opponent;

        //CALCULATE SCORE HERE (temp values - set score1 and score 2 in a method)
        int score1 = 0;
        int score2 = 0;

        for (int row = 0; row < statusNum.length; row++)
        {
          for (int col = 0; col < statusNum[0].length; col++)
          {
            if (statusNum[row][col] == 1)
            {
              score1++;
            }
            else if (statusNum[row][col] == 2)
            {
              score2++;
            }
          } 
        }
        // System.out.println(score1 + " " + score2);
        
        player.setScore(score1);
        player.opponent.setScore(score2);

        //Switch Turns
        currentPlayer = currentPlayer.opponent;
        
        return boardColorInts + "-" + boardStatusInts;
    }

    // public synchronized void scoreUpdate(Player player)
    // {
    //   int theCurrentPlayerStatus = 0;
    //   if (player.getMark() == '1')
    //   {
    //     theCurrentPlayerStatus = 1;
    //   }
    //   else
    //   {
    //     theCurrentPlayerStatus = 2;
    //   }

    //   //CALCULATE SCORE HERE (temp values - set score1 and score 2 in a method)
    //   int score1 = 0;
    //   int score2 = 0;

    //   for (int row = 0; row < statusNum.length; row++)
    //   {
    //     for (int col = 0; col < statusNum[0].length; col++)
    //     {
    //       if (statusNum[row][col] == theCurrentPlayerStatus)
    //       {
    //         if (theCurrentPlayerStatus == 1)
    //         {
    //           score1++;
    //         }
    //         else if (theCurrentPlayerStatus == 2)
    //         {
    //           score2++;
    //         }
    //       }
    //     } 
    //   }
      
    //   player.setScore(score1);
    //   player.opponent.setScore(score2);

    //   //Switch Turns
    //   currentPlayer = currentPlayer.opponent;
    // }

    public class Player implements java.lang.Runnable
    {
      //For server stuff - Rachana
      private char mark;
      private Player opponent;
      private Socket socket;
      private Scanner input;
      private PrintWriter output;
      
      private String username;
      private int score;

      //SERVER
      public Player(Socket socket, char mark) 
      {
        this.socket = socket;
        this.mark = mark;
      }

      public char getMark()
      {
        return mark;
      }
      
      public String getUsername() {
        return username;
      }
      
      public int getScore() {
        return score;
      }
      
      public void setUsername(String user) {
        username = user;
      }
      
      public void setScore(int s) {
        score = s;
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
            processMoveCommand(colorToChangeTo);
          }
          else if (command.startsWith("USER"))
          {
            String username = command.substring(5);
            setUsername(username);
            
            if (setUp && getMark() == '2') {
              String user1 = opponent.getUsername();
              String user2 = getUsername();
              opponent.processUsernameCommand(user1 + "-" + user2);
              processUsernameCommand(user1 + "-" + user2);
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

          // scoreUpdate(this);
          
          // Update score: score1-score2
          output.println("UPDATE_SCORE " + getScore() + "-" + opponent.getScore());
          opponent.output.println("UPDATE_SCORE " + getScore() + "-" + opponent.getScore());
          
          opponent.output.println("OPPONENT_MOVED " + color);
          
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
      
      private void processUsernameCommand(String userMessage)
      {
        try 
        {
          output.println("UPDATE_USER " + userMessage);
        } 
        catch (IllegalStateException e) 
        {
          output.println("MESSAGE " + e.getMessage());
        }
      }
    } // end Player class
} // end Game class