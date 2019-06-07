import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;

/**
 * A server for a multi-player tic tac toe game. Loosely based on an example in
 * Deitel and Deitel's "Java How to Program" book. For this project I created a
 * new application-level protocol called TTTP (for Tic Tac Toe Protocol), which
 * is entirely plain text. The messages of TTTP are:
 *
 * Client -> Server
 *     MOVE <n>
 *     QUIT
 *
 * Server -> Client
 *     WELCOME <char>
 *     VALID_MOVE
 *     OTHER_PLAYER_MOVED <n>
 *     OTHER_PLAYER_LEFT
 *     VICTORY
 *     DEFEAT
 *     TIE
 *     MESSAGE <text>
 */
public class NEWFillerServer {
    private static int height = NEWFillerClient.HEIGHT;
    private static int width = NEWFillerClient.WIDTH;
    
    public static void main(String[] args) throws Exception 
    {
        try (ServerSocket listener = new ServerSocket(58901)) 
        {
            System.out.println("Filler Server is Running...");
            ExecutorService pool = Executors.newFixedThreadPool(200);
            while (true) 
            {
                //Initialize random game board colors
              
                int[][] colorNum = new int[height][width];
                
                colorNum[0][0] = (int)(Math.random()*6);
                for(int col = 1; col < width; col++)
                {
                  colorNum[0][col] = (int)(Math.random()*6);
                  while(colorNum[0][col-1] == colorNum[0][col])
                  {
                    colorNum[0][col] = (int)(Math.random()*6);
                  }
                }
                
                for(int row = 1; row < height; row++)
                {
                  colorNum[row][0] = (int)(Math.random()*6);
                  while(colorNum[row][0] == colorNum[row-1][0])
                  {
                    colorNum[row][0] = (int)(Math.random()*6);
                  }
                }
                
                for(int row = 1; row < height; row++)
                {
                  for(int col = 1; col < width; col++)
                  {
                    colorNum[row][col] = (int)(Math.random()*6);
                    while(colorNum[row][col] == colorNum[row-1][col] || colorNum[row][col] == colorNum[row][col-1])
                    {
                      colorNum[row][col] = (int)(Math.random()*6);
                    }
                  }
                }

                //Initialize status in grid

                int[][] statusNum = new int[height][width];
                
                statusNum[height-1][0] = 1;
                statusNum[0][width-1] = 2;
                
                Game game = new Game(colorNum, statusNum);
                pool.execute(game.new Player(listener.accept(), '1'));
                pool.execute(game.new Player(listener.accept(), '2'));
            }
        }
    }
} // end NEWFillerServer class

class Game {
  //extends GameLogic
    private static int height = NEWFillerClient.HEIGHT;
    private static int width = NEWFillerClient.WIDTH;

    private int[][] colorNum;
    private int[][] statusNum;
    private String boardColorInts;
    private String boardStatusInts;
    
    // Board cells numbered 0-55, top to bottom, left to right; null if empty
    //fix hasWinner? don't need playerBoard
    private Player[] playerBoard = new Player[56];

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
      //fix
        return (playerBoard[0] != null && playerBoard[0] == playerBoard[1] && playerBoard[0] == playerBoard[2])
            || (playerBoard[3] != null && playerBoard[3] == playerBoard[4] && playerBoard[3] == playerBoard[5])
            || (playerBoard[6] != null && playerBoard[6] == playerBoard[7] && playerBoard[6] == playerBoard[8])
            || (playerBoard[0] != null && playerBoard[0] == playerBoard[3] && playerBoard[0] == playerBoard[6])
            || (playerBoard[1] != null && playerBoard[1] == playerBoard[4] && playerBoard[1] == playerBoard[7])
            || (playerBoard[2] != null && playerBoard[2] == playerBoard[5] && playerBoard[2] == playerBoard[8])
            || (playerBoard[0] != null && playerBoard[0] == playerBoard[4] && playerBoard[0] == playerBoard[8])
            || (playerBoard[2] != null && playerBoard[2] == playerBoard[4] && playerBoard[2] == playerBoard[6]
        );
    }

    public boolean boardFilledUp() {
      //fix
        return Arrays.stream(playerBoard).allMatch(p -> p != null);
    }

    public synchronized String move(int color, Player player) 
    {
        if (player != currentPlayer) {
            throw new IllegalStateException("Not your turn");
        } 
        else if (player.opponent == null) {
            throw new IllegalStateException("You don't have an opponent yet");
        } 
        else if (playerBoard[color] != null) {
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

        // Change status of blocks after player selects color
        int[][] newStatusNum = statusNum;

        for (int row = 0; row < height; row++)
        {
          for (int col = 0; col < width; col++)
          {
            if (colorNum[row][col] == color && statusNum[row][col] == 0)
            {
              //testing the block above
              if (row-1 >= 0 && statusNum[row-1][col] == theCurrentPlayerStatus)
              {
                newStatusNum[row][col] = theCurrentPlayerStatus;
              }
              //testing the block below
              else if (row+1 < height && statusNum[row+1][col] == theCurrentPlayerStatus)
              {
                newStatusNum[row][col] = theCurrentPlayerStatus;
              }
              //testing the block left
              else if (col-1 >= 0 && statusNum[row][col-1] == theCurrentPlayerStatus)
              {
                newStatusNum[row][col] = theCurrentPlayerStatus;
              }
              //testing the block right
              else if (col+1 < width && statusNum[row][col+1] == theCurrentPlayerStatus)
              {
                newStatusNum[row][col] = theCurrentPlayerStatus;
              }
            }
          }
        }

        statusNum = newStatusNum;

        for (int row = 0; row < statusNum.length; row++)
        {
          for (int col = 0; col < statusNum[0].length; col++)
          {
            if (statusNum[row][col] == theCurrentPlayerStatus)
            {
              colorNum[row][col] = color;
            }
          } 
        }

        turnArrayToString();
        
        currentPlayer = currentPlayer.opponent;   
        
        return boardColorInts + "-" + boardStatusInts;
    }

    public class Player implements java.lang.Runnable
    {
      //For server stuff - Rachana
      private char mark;
      private Player opponent;
      private Socket socket;
      private Scanner input;
      private PrintWriter output;

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
        }
      }

      private void processMoveCommand(int color) 
      {
        try 
        {
          String updateMessage = move(color, this);
          
          output.println("VALID_MOVE");
          output.println("BOARD_UPDATE" + updateMessage);
          opponent.output.println("BOARD_UPDATE" + updateMessage);
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
    } // end Player class
} // end Game class
