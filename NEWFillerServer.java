import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Code based on: https://cs.lmu.edu/~ray/notes/javanetexamples/
 * 
 * A server for a multi-player Filler game. Loosely based on an example in
 * Deitel and Deitel's "Java How to Program" book.
 * 
 * Messages:
 *
 * Client -> Server
 *     MOVE <n>
 *     QUIT
 *     
 *     USER <text>
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
 *     
 *     BOARD_UPDATE <text>
 *     UPDATE_USER <text>
 *     UPDATE_SCORE <text>
 */
public class NEWFillerServer {
    private static int height = NEWFillerClient.HEIGHT;
    private static int width = NEWFillerClient.WIDTH;
    
    private static int[][] setColorNum() {
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
      
      for(int row = 1; row < height-1; row++)
      {
        colorNum[row][0] = (int)(Math.random()*6);
        while(colorNum[row][0] == colorNum[row-1][0])
        {
          colorNum[row][0] = (int)(Math.random()*6);
        }
      }
      
      //Make sure players start with different initial colors
      while(colorNum[height-1][0] == colorNum[0][width-1] || colorNum[height-1][0] == colorNum[height-2][0])
      {
        colorNum[height-1][0] = (int)(Math.random()*6);
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
      
      return colorNum;
    }
    
    private static int[][] setStatusNum() {
      int[][] statusNum = new int[height][width];
      statusNum[height-1][0] = 1;
      statusNum[0][width-1] = 2;
      
      return statusNum;
    }
    
    public static void main(String[] args) throws Exception 
    {
        try (ServerSocket listener = new ServerSocket(58901)) 
        {
            System.out.println("Filler Server is Running...");
            ExecutorService pool = Executors.newFixedThreadPool(200);
            while (true) 
            {
                //Initialize random game board colors
                int[][] colorNum = setColorNum();

                //Initialize status in grid
                int[][] statusNum = setStatusNum();
                
                // Initialize game
                Game game = new Game(colorNum, statusNum);
                pool.execute(game.new Player(listener.accept(), '1'));
                pool.execute(game.new Player(listener.accept(), '2'));
            }
        }
    }
} // end NEWFillerServer class