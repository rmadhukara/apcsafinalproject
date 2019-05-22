
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
	}

	public void createGrid(){
		for(int h = 0; h < 7; h++){
			for(int w = 0; w < 8; w++){
				grid[w][h] = new Block();
			}
		}
	}





} //end Grid class
