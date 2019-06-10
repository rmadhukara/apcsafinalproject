import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.awt.Font;
import java.awt.Canvas;

public class GameBoard extends Canvas
{
    private Block[][] grid;
    private static int height = FillerClient.HEIGHT;
    private static int width = FillerClient.WIDTH;
    private static Color[] colors = FillerClient.COLORS;
    
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
    
    public void setWins1(int w1) {
      wins1 = w1;
    }
    
    public void setWins2(int w2) {
      wins2 = w2;
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
    	window.drawString("1: " + user1, 50, 20);
    	window.drawString("2: " + user2, 50, 40);
    	
    	window.drawString("SCORE: " + score1, 203, 20);
    	window.drawString("SCORE: " + score2, 203, 40);

    	window.drawString("GAMES WON: " + wins1, 336, 20);
    	window.drawString("GAMES WON: " + wins2, 336, 40);
    }
    
    public void run() {
      repaint();
    }
    
    public void save() throws IOException {
      String filename = "UserScore.dat";
      
      //Format: username #
      Scanner file =  new Scanner(new File(filename));
      
      //Put file into ArrayList
      ArrayList<String> users = new ArrayList<String>();      
      while(file.hasNextLine()) {
        users.add(file.nextLine());
      }
      
      file.close();
      
      //Edit ArrayList
      boolean user1found = false;
      boolean user2found = false;
      
      for (int i = 0; i < users.size(); i++) {
        if (!user1found && users.get(i).indexOf(user1) > -1) {
          users.set(i, user1 + " " + wins1);
          user1found = true;
        }
        
        if (!user2found && users.get(i).indexOf(user2) > -1) {
          users.set(i, user2 + " " + wins2);
          user2found = true;
        }
      }
      
      //If username not found, add to list
      if(!user1found){        
        users.add(user1 + " " + wins1);
      }
    
      if(!user2found){
        users.add(user2 + " " + wins2);
      }
      
      System.out.println("--- Other Players ---");
      for (String item : users) {
        System.out.println(item);
      }
      
      //Write to file
      FileWriter writer = new FileWriter(filename); 
      for(String str: users) {
        writer.write(str);
        writer.write(System.getProperty("line.separator"));
      }
      writer.close();
    }


} //end GameBoard class
