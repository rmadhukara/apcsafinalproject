
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class Grid extends Canvas
{

	private Block[][] grid;

	public Grid(){
		grid = new Block[8][7];
                for(int h = 0; h < 7; h++){
                        for(int w = 0; w < 8; w++){
                                grid[w][h] = new Block(55,0);
                        }
                }

		//setting corner blocks to Player 1 and 2 blocks
		grid[7][0] = new Block(5,1);
		grid[0][6] = new Block(5,2);

	}


	public void setGrid(Block[][] g){
		grid = g;
	}


	public Block[][] getGrid(){
		return grid;
	}


	public void paint(Graphics window){
		for(int row = 0; row < 8; row++){
			for(int col = 0; col < 7; col++){
				Block one = grid[row][col];
				one.draw(window,270-(5*row),10+(5*col),one.getColor());
			}
		}
		window.drawString("1", 266, 12);
		window.drawString("2", 11, 42);

	}



} //end Grid class
