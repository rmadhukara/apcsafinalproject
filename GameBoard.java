
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
    
    private String user1;
    private String user2;
    private int score1;
    private int score2;
    private int wins1;
    private int wins2;

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
        
        user1 = "";
        user2 = "";
        score1 = 1;
        score2 = 1;
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
    
    public void setUser1(String u1) {
        user1 = u1;
    }
    
    public void setUser2(String u2) {
      user2 = u2;
    }
    
    public void setScore1(int s1) {
        score1 = s1;
    }
    
    public void setScore2(int s2) {
      score2 = s2;
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


/* The persistence part (doesn't work right now)
	Scanner file =  new Scanner(new File("UserScore.dat"));
	String read = file.nextLine();
	int index1 = read.indexOf(user1);
	if(index1 != -1){
		char num1 = read.charAt(index1 + user1.length() + 1);
		wins1 = (int)num1;
	}

	int index2 = read.indexOf(user2);
        if(index2 != -1){
                char num2 = read.charAt(index2 + user2.length() + 1);
                wins2 = (int)num2;
        }

	if(index1 == -1){
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(user1 + " " + wins1 + ", ");
	}

        if(index2 == -1){
                BufferedWriter out2 = new BufferedWriter(new FileWriter(file));
                out2.write(user2 + " " + wins2 + ", ");
        }
*/

        window.setColor(Color.BLACK);
        window.setFont(new Font("Courier", Font.PLAIN, 40));
        window.drawString("1", 22, 400);
        window.drawString("2", 455, 98);

    	window.setFont(new Font("Courier", Font.PLAIN, 15));
    	window.drawString("1: " + user1, 50, 20);
    	window.drawString("2: " + user2, 50, 40);
    	//PUT DRAW STRING SCORE HERE
    	window.drawString("SCORE: " + score1, 203, 20);
    	window.drawString("SCORE: " + score2, 203, 40);

    	window.drawString("GAMES WON: " + wins1, 336, 20);
    	window.drawString("GAMES WON: " + wins2, 336, 40);
    }
    
    public void run() {
      repaint();
    }



} //end GameBoard class
