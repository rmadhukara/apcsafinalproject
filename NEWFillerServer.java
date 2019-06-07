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
                
                // Initialize game
                Game game = new Game(colorNum, statusNum);
                pool.execute(game.new Player(listener.accept(), '1'));
                pool.execute(game.new Player(listener.accept(), '2'));
            }
        }
    }
} // end NEWFillerServer class