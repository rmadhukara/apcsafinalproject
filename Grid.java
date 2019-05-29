
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class Grid
{

	private Block[][] grid;

	public Grid(){
		grid = new Block[8][7];
                for(int h = 0; h < 7; h++){
                        for(int w = 0; w < 8; w++){
                                grid[w][h] = new Block(5,0);
                        }
                }

		//setting corner blocks to Player 1 and 2 blocks
		grid[7][0] = new Block(5,1);
		grid[0][6] = new Block(5,2);

	}


	public Block[][] getGrid(){
		return grid;
	}



} //end Grid class
