
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;


public class Rectangle
{

	private int size;

	public Rectangle(){
		size = 5;
	}

	public Rectangle(int s){
		size = s;
	}


	public void setSize(int s){
		size = s;
	}

	public int getSize(){
		return size;
	}


	public void draw(Graphics window, int xPos, int yPos, Color col){
		window.setColor(col);
		window.fillRect(xPos, yPos, size, size);
	}





} // end Rectangle class
