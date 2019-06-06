
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;

public class Block extends Rectangle
{

	private Color color;
	private int status;
	private boolean isFlickering;

	public Block()
	{
		super();
		color = Color.BLACK;
		status = 0;
		isFlickering = false;
	}

	public Block(int size, int stat)
	{
		super(size);
		randColor();
		setStatus(stat);
		isFlickering = false;
	}

	public void setStatus(int stat)
	{
		status = stat;
	}

	public void setColor(Color col)
	{
		color = col;
	}

	public int getStatus()
	{
		return status;
	}

	public Color getColor()
	{
		return color;
	}

	public void setIsFlickering(boolean flick)
	{
		isFlickering = flick;
	}

	public void randColor()
	{
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
			color = Color.MAGENTA;
		if(rand == 6)
			color = Color.BLACK;

	}



} //end Block class
