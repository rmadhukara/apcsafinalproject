import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
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

    public static void main(String[] args) throws Exception 
    {
        try (ServerSocket listener = new ServerSocket(58901)) 
        {
            System.out.println("Filler Server is Running...");
            ExecutorService pool = Executors.newFixedThreadPool(200);
            while (true) 
            {
                //set random game board once
                int[][] colorNum = new int[7][8];
                colorNum[0][0] = (int)(Math.random()*6);
                for(int col = 1; col < 8; col++)
                {
                  colorNum[0][col] = (int)(Math.random()*6);
                  while(colorNum[0][col-1] == colorNum[0][col])
                  {
                    colorNum[0][col] = (int)(Math.random()*6);
                  }
                }
                
                for(int row = 1; row < 7; row++)
                {
                  colorNum[row][0] = (int)(Math.random()*6);
                  while(colorNum[row][0] == colorNum[row-1][0])
                  {
                    colorNum[row][0] = (int)(Math.random()*6);
                  }
                }
                
                for(int row = 1; row < 7; row++)
                {
                  for(int col = 1; col < 8; col++)
                  {
                    colorNum[row][col] = (int)(Math.random()*6);
                    while(colorNum[row][col] == colorNum[row-1][col] || colorNum[row][col] == colorNum[row][col-1])
                    {
                      colorNum[row][col] = (int)(Math.random()*6);
                    }
                  }
                }

                //Initialize status in grid

                int[][] statusNum = new int[7][8];
                statusNum[6][0] = 1;
                statusNum[0][7] = 2;
                
                Game game = new Game(colorNum, statusNum);
                pool.execute(game.new Player(listener.accept(), '1'));
                pool.execute(game.new Player(listener.accept(), '2'));
            }
        }
    }
}

class Game {
    private int[][] colorNum;
    private int[][] statusNum;
    private String boardInts;
    private String boardStatusInts;
    
    // Board cells numbered 0-55, top to bottom, left to right; null if empty
    private Player[] playerBoard = new Player[56];

    Player currentPlayer;
    
    public Game(int[][] colors, int[][] status) 
    {
      colorNum = colors;
      statusNum = status;
      boardInts = "";
      boardStatusInts = "";

      turnArrayToString();
    }

    public void turnArrayToString()
    {
      for (int[] row : colorNum) 
      {
        for (int col : row) 
        {
          boardInts += " " + col;
        }
      }

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

    public synchronized void move(int color, Player player) 
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

        for (int row = 0; row < 7; row++)
        {
          for (int col = 0; col < 8; col++)
          {
            if (colorNum[row][col] == color && statusNum[row][col] == 0)
            {
              if (row-1 >= 0) 
              {
                if (statusNum[row-1][col] == theCurrentPlayerStatus)
                {
                  colorNum[row][col] = color;
                  statusNum[row][col] = theCurrentPlayerStatus;
                }
              }
              else if (row+1 < 7) 
              {
                if (statusNum[row+1][col] == theCurrentPlayerStatus)
                {
                  colorNum[row][col] = color;
                  statusNum[row][col] = theCurrentPlayerStatus;
                }
              }
              else if (col-1 >= 0) 
              {
                if (statusNum[row][col-1] == theCurrentPlayerStatus)
                {
                  colorNum[row][col] = color;
                  statusNum[row][col] = theCurrentPlayerStatus;
                }
              }
              else if (col+1 < 8) 
              {
                if (statusNum[row][col+1] == theCurrentPlayerStatus)
                {
                  colorNum[row][col] = color;
                  statusNum[row][col] = theCurrentPlayerStatus;
                }
              }
            }
          }
        }

        turnArrayToString();

        //System.out.println("COLORS: " + boardInts);
        for (int r = 0; r < 7; r++) {
          for (int c = 0; c < 8; c++) {
            System.out.print(colorNum[r][c] + " ");
          }
          System.out.println();
        }
        System.out.println();
        //System.out.println("STATUS: " + boardStatusInts);
        for (int r = 0; r < 7; r++) {
          for (int c = 0; c < 8; c++) {
            System.out.print(statusNum[r][c] + " ");
          }
          System.out.println();
        }
        System.out.println();
        System.out.println();
        
//move down
        currentPlayer = currentPlayer.opponent;
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
        output.println("BOARD_UPDATE" + boardInts + "-" + boardStatusInts);
        
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
          move(color, this);
          output.println("BOARD_UPDATE" + boardInts + "-" + boardStatusInts);
          // System.out.println("COLORS: " + boardInts);
          // System.out.println("STATUS: " + boardStatusInts);
          opponent.output.println("BOARD_UPDATE" + boardInts + "-" + boardStatusInts);
          opponent.output.println("OPPONENT_MOVED " + color);

          //currentPlayer = currentPlayer.opponent;
          
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
}
