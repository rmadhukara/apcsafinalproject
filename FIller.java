import java.awt.*;
import java.util.*;

//GAME LOGIC   Includes
//determining if a player won
//Adding Wins to specific player
//Runnign the Game

public class Filler implements KeyListener, Runnable
{

  //Variables
  //Two objects of Players
  private Player player1;   //Controlling - 1
  private Player player2;   //Controlling - 2
  //One Grid - Includes the Blocks for each Color and UI
  private GameBoard board;   //Automatically generates the orig Grid

  private int total_Count;    //Amount of claimed spots
  public boolean playing = true;   //True Allows for game to run
  private int currentPlayer;    //1 - P1   2 - P2
  private int color;    //The Current Color to be used

  private Player[] players;   //Stores Players Here
  private int[][] grid;
  private boolean[] keys;   //The 1-6 BUTTONS

  //SET VARIABLES WON'T CHANGE
  private static final int HEIGHT = 600;
  private static final int WIDTH = 800;
  private static final Color[] COLORS = {Color.RED, 
                                         Color.GREEN,
                                         Color.YELLOW,
                                         Color.BLUE,
                                         Color.MAGENTA,
                                         Color.BLACK};


  //STARTING FOR THE BEG, LATER INTEGRATING WITH USERS
  public Filler()
  {
    player1 = new Player();
    player2 = new Player();
    Gameboard board = new GameBoard();
    keys = new boolean[6];
    
    
    grid = board.getBoard();    //Need to Change this, grid values
    players = new Player[2];
    players[0] = new Player(grid[7][0].getColor());
    player[1] = new Player(grid[0][6].getColor());
    total_Count = 2;
    currentPlayer = 0;

    this.addKeyListener(this);
    new Thread(this).start();
  }

  public void game()
  {
    if(currentPlayer > 1)
      currentPlayer = 0;
    //Make sure game is still running
    if(playing && total_Count<56)
    {
      grid = board.getBoard();    //GETTING BOARD VALUES

      //DETERMINE WHAT COLOR THE USER SELECTED
      do
      {
        for(int i = 0; i < keys.length; i++)
        {
          if(keys[i])
            color = i;
        }
      }while(COLORS[i].equals(players[0].getColor()) &&
             COLORS[i].equals(players[1].getColor()));

      players[currentPlayer].setColor(COLORS[color]);

      for(int r = 0; r < grid.length; r++)
      {
        for(int c = 0; c < grid[0].length; c++)
        {
          if(grid[r][c].getControl == 0)
          {
            if(grid[r][c].getColor.equals(
                players[currentPlayer].getColor()))
            {
              //CHECKING IF ADJACENT COLOR IS SAME
              if((grid[r-1][c].getControl == (currentPlayer+1)
              ||grid[r-1][c-1].getControl == (currentPlayer+1)
              ||grid[r-1][c+1].getControl == (currentPlayer+1)
              ||grid[r][c-1].getControl == (currentPlayer+1)
              ||grid[r][c+1].getControl == (currentPlayer+1)
              ||grid[r+1][c-1].getControl == (currentPlayer+1)
              ||grid[r+1][c].getControl == (currentPlayer+1)
              ||grid[r+1][c+1].getControl == (currentPlayer+1))
              {
                grid[r][c].setControl(currentPlayer+1);
                total_Count ++;
                players[currentPlayer].setControlCount(players[currentPlayer].getControlCount() + 1);
              }
            }
          }
        }
      }

      //UPDATING LOCAL GRID
      for(int row = 0; row < grid.length; row++)
      {
        for(int col = 0; col < grid[0].length; col++)
        {
          if(grid[row][col].getControl == (currentPlayer+1))
          {
            grid.setColor(players[currentPlayer].getColor());
          }
        }
      }

      //SENDING UPDATES TO GAMEBOARD
      board.update(grid);
      

      //CHECKING THAT IT ISN'T OVER BOARD
      if(total_Count >=56)
        playing = false;
      currentPlayer ++;
    }//END PLAYING STATEMENT


  }//END OF GAME METHOD


}