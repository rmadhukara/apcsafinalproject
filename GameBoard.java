
import static java.lang.System.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;

public class GameBoard
{

	private Grid grid;
	private Rectangle box;

	public GameBoard(){

		grid = new Grid();
		box = new Rectangle();
	}

	public void drawScoreBoard(Graphics window){
		Scanner keyboard = new Scanner(System.in);

		out.println("Input username of first player.");
		String userOne = keyboard.next();
		window.drawString("Username: " + userOne, 10, 10);

		window.drawString("Score: ", 40, 10);

		out.println("Input username of second player.");
		String userTwo = keyboard.next();
                window.drawString("Username: " + userTwo, 10, 100);

                window.drawString("Score: ", 40, 100);
	}

	public void drawPanel(Graphics window){
		box.draw(window, 10, 300, Color.RED);
		box.draw(window, 20, 300, Color.GREEN);
		box.draw(window, 30, 300, Color.YELLOW);
		box.draw(window, 40, 300, Color.BLUE);
		box.draw(window, 50, 300, Color.MAGENTA);
		box.draw(window, 60, 300, Color.BLACK);
	}


} //end GameBoard class
