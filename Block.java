
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Math;

public class Block extends Rectangle
{

	private Color color;
	private int status;

	public Block(){
		super();
		color = Color.BLACK;
		status = 0;
	}

	public Block(int size, Color col, int stat){
		super(size);
		col = Color.BLACK;
		stat = 0;
	}

	public Color randColor(){
		int rand = (int)(Math.random() * 6 + 1);
		if(rand == 1)
			color = Color.RED;
		if(rand == 2)
			color = Color.GREEN;
		if(rand == 3)
			color = Color.YELLOW;
		if(rand == 4)
			color = Color.BLUE;
		if(rand == 5)
			color = Color.PURPLE;
		if(rand == 6)
			color = Color.BLACK;

	}



} //end Block class
