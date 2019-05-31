
import static java.lang.System.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;

public class GameBoard extends Grid
{

	private Rectangle box;

	public GameBoard(){
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

		box.setSize(15);
		box.draw(window, 10, 300, Color.RED);
		box.draw(window, 20, 300, Color.GREEN);
		box.draw(window, 30, 300, Color.YELLOW);
		box.draw(window, 40, 300, Color.BLUE);
		box.draw(window, 50, 300, Color.MAGENTA);
		box.draw(window, 60, 300, Color.BLACK);

		Block[][] statGrid = getGrid();
		Color oneCol = statGrid[7][0].getColor();
		Color twoCol = statGrid[0][6].getColor();

		if(oneCol != Color.RED || oneCol != Color.RED)
			box.setSize(2);

		if(oneCol != Color.GREEN || twoCol != Color.GREEN)
			box.setSize(2);

                if(oneCol != Color.YELLOW || twoCol != Color.YELLOW)
                        box.setSize(2);

                if(oneCol != Color.BLUE || twoCol != Color.BLUE)
                        box.setSize(2);

                if(oneCol != Color.MAGENTA || twoCol != Color.MAGENTA)
                        box.setSize(2);

                if(oneCol != Color.BLACK || twoCol != Color.BLACK)
                        box.setSize(2);


	}


} //end GameBoard class
