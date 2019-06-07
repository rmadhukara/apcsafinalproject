
import static java.lang.System.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;
import java.awt.Font;
import java.awt.Canvas;

public class GameBoard extends Canvas
{
    private Block[][] grid;
    private static int height = NEWFillerClient.HEIGHT;
    private static int width = NEWFillerClient.WIDTH;
    private static Color[] colors = NEWFillerClient.COLORS;
    

    public GameBoard(){
        grid = new Block[height][width];
        for(int h = 0; h < height; h++)
        {
            for(int w = 0; w < width; w++)
            {
                grid[h][w] = new Block(50,0);
            }
        }

        //setting corner blocks to Player 1 and 2 blocks
        grid[height-1][0] = new Block(50,1);
        grid[0][width-1] = new Block(50,2);

    }


    public void setGrid(Block[][] g)
    {
        grid = g;
    }


    public Block[][] getGrid()
    {
        return grid;
    }

    public void setColorInGrid(int[] colorInts)
    {
        int counter = 0;
        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                grid[row][col].setColor(colors[colorInts[counter]]);
                counter++;
            }
        }
    }

    public void setStatusInGrid(int[] status)
    {
        int counter = 0;
        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                grid[row][col].setStatus(status[counter]);
                counter++;
            }
        }
    }

    public void paint(Graphics window)
    {
      window.setColor(Color.WHITE);
      window.fillRect(0,0,500,440);
        for(int row = 0; row < height; row++)
        {
            for(int col = 0; col < width; col++)
            {
                Block one = grid[row][col];
                one.draw(window,50+(50*col),60+(50*row),one.getColor());
            }
        }
        window.setColor(Color.BLACK);
        window.setFont(new Font("Courier", Font.PLAIN, 40));
        window.drawString("1", 22, 400);
        window.drawString("2", 455, 98);

    	window.setFont(new Font("Courier", Font.PLAIN, 15));
    	window.drawString("USER 1: ", 50, 20);
    	window.drawString("USER 2: ", 50, 40);
    	window.drawString("GAMES WON: ", 250, 20);
    	window.drawString("GAMES WON: ", 250, 40);
    }
    
    public void run() {
      repaint();
    }



} //end GameBoard class
