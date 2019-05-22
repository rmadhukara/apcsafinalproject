
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;


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





} //end Block class
