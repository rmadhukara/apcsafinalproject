
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

    // private Rectangle box;

    // public GameBoard(){
    //  box = new Rectangle();
    // }


    // public void drawScoreBoard(Graphics window){
    //  Scanner keyboard = new Scanner(System.in);

    //  out.println("Input username of first player.");
    //  String userOne = keyboard.next();
    //  window.drawString("Username: " + userOne, 10, 10);

    //  window.drawString("Score: ", 40, 10);

    //  out.println("Input username of second player.");
    //  String userTwo = keyboard.next();
 //                window.drawString("Username: " + userTwo, 10, 100);

 //                window.drawString("Score: ", 40, 100);
    // }

    private Block[][] grid;

    public GameBoard(){
        grid = new Block[7][8];
        for(int h = 0; h < 7; h++)
        {
            for(int w = 0; w < 8; w++)
            {
                grid[h][w] = new Block(50,0);
            }
        }

        //setting corner blocks to Player 1 and 2 blocks
        grid[6][0] = new Block(50,1);
        grid[0][7] = new Block(50,2);

    }


    public void setGrid(Block[][] g)
    {
        grid = g;
    }


    public Block[][] getGrid()
    {
        return grid;
    }

    public void setColorInGrid(int[] colors)
    {
        int counter = 0;
        for (int row = 0; row < 7; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                grid[row][col].setColor(NEWFillerClient.colors[colors[counter]]);
                // PRINT TEST
                //System.out.print(colors[counter] + " ");
                counter++;
            }
            // PRINT TEST
            //System.out.println();
        }
        repaint();
    }

    // public boolean checkAdjacencies(int color)
    // {
        
    // }

    public void paint(Graphics window)
    {
        for(int row = 0; row < 7; row++)
        {
            for(int col = 0; col < 8; col++)
            {
                Block one = grid[row][col];
                one.draw(window,50+(50*col),20+(50*row),one.getColor());
            }
        }
        window.setColor(Color.BLACK);
        window.setFont(new Font("TimesRoman", Font.PLAIN, 40));
        window.drawString("1", 25, 360);
        window.drawString("2", 458, 58);

    }



} //end Gameboard class
