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

    public static void main(String[] args) throws Exception {
        try (ServerSocket listener = new ServerSocket(58901)) {
            System.out.println("Filler Server is Running...");
            ExecutorService pool = Executors.newFixedThreadPool(200);
            while (true) {
                //set random game board once
                String boardInts = "";
                for (int i = 0; i < 56; i++) {
                  boardInts += " " + (int)(Math.random() * 6);
                }
                
                Game game = new Game(boardInts);
                pool.execute(game.new Player(listener.accept(), '1'));
                pool.execute(game.new Player(listener.accept(), '2'));
            }
        }
    }
}

class Game {
    private String boardInts;
    
    // Board cells numbered 0-55, top to bottom, left to right; null if empty
    private Player[] board = new Player[56];

    Player currentPlayer;
    
    public Game(String nums) {
      boardInts = nums;
    }
    
    public boolean hasWinner() {
      //fix
        return (board[0] != null && board[0] == board[1] && board[0] == board[2])
            || (board[3] != null && board[3] == board[4] && board[3] == board[5])
            || (board[6] != null && board[6] == board[7] && board[6] == board[8])
            || (board[0] != null && board[0] == board[3] && board[0] == board[6])
            || (board[1] != null && board[1] == board[4] && board[1] == board[7])
            || (board[2] != null && board[2] == board[5] && board[2] == board[8])
            || (board[0] != null && board[0] == board[4] && board[0] == board[8])
            || (board[2] != null && board[2] == board[4] && board[2] == board[6]
        );
    }

    public boolean boardFilledUp() {
      //fix
        return Arrays.stream(board).allMatch(p -> p != null);
    }

    public synchronized void move(int location, Player player) 
    {
        if (player != currentPlayer) {
            throw new IllegalStateException("Not your turn");
        } 
        else if (player.opponent == null) {
            throw new IllegalStateException("You don't have an opponent yet");
        } 
        else if (board[location] != null) {
            throw new IllegalStateException("Cell already occupied");
        }
        board[location] = currentPlayer;
        currentPlayer = currentPlayer.opponent;
    }

    public class Player implements Playerable, java.lang.Runnable
    {
      private int winCount;   //The total amount wins a player has
      private int controlCount;   //The total blocks the user controls in current game
      private Color color;
      // private ArrayList<Block> ownership;

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

        // ownership = new ArrayList<Integer>();
      }
      public Player(int wins)
      {
        winCount = wins;
        controlCount = 0;

        // ownership = new ArrayList<Integer>();
      }

      //SERVER
      public Player(Socket socket, char mark) 
      {
        this.socket = socket;
        this.mark = mark;

        // ownership = new ArrayList<Integer>();
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

      // public void add(int blockPosition)
      // {
      //   ownership.add(blockPosition);
      // }

      //The following code is adapted from https://cs.lmu.edu/~ray/notes/javanetexamples/#tictactoe

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
        output.println("BOARD_UPDATE" + boardInts);
        
        if (mark == '1') 
        {
          currentPlayer = this;
          // currentPlayer.add(48);
          output.println("MESSAGE Waiting for opponent to connect");
        } 
        else 
        {          
          opponent = currentPlayer;
          opponent.opponent = this;
          // opponent.add(7);
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
            // System.out.println(test);
            processMoveCommand(colorToChangeTo);
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
}