import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class NEWFillerClient {
  
  private JFrame frame = new JFrame("Filler");
  private JLabel messageLabel = new JLabel("...");
  
  private Socket socket;
  private Scanner in;
  private PrintWriter out;
  
  private GameBoard board;
  private Panel[] buttons;
  private Panel current;
  private String username;
  
  public static final Color[] COLORS = {Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.MAGENTA, Color.BLACK};
  public static final int HEIGHT = 7;
  public static final int WIDTH = 8;
  
  public NEWFillerClient(String serverAddress, String user) throws Exception 
  {
      board = new GameBoard();
      buttons = new Panel[6];
      username = user;
      
      socket = new Socket(serverAddress, 58901);
      in = new Scanner(socket.getInputStream());
      out = new PrintWriter(socket.getOutputStream(), true);

      messageLabel.setBackground(Color.lightGray);
      frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);
      
      out.println("USER " + username);
      
      //Display Game Board
      board.setBounds(0,0,500,440);
      frame.getContentPane().add(board);

      //Display Buttons
      JPanel boardPanel = new JPanel();
      
      boardPanel.setLayout(new GridLayout(1, 6, 10, 1));
      boardPanel.setBackground(Color.WHITE);
      
      for (var i = 0; i < buttons.length; i++) 
      {
          final int j = i;
          buttons[i] = new Panel(COLORS[i]);
          buttons[i].addMouseListener(new MouseAdapter() 
          {
              public void mousePressed(MouseEvent e) 
              {
                  current = buttons[j];
                  out.println("MOVE " + j);
              }
          });
          boardPanel.add(buttons[i]);
      }
      
      frame.getContentPane().add(boardPanel, BorderLayout.CENTER);
  }

  /**
   * Code based on: https://cs.lmu.edu/~ray/notes/javanetexamples/
   * 
   * The main thread of the client will listen for messages from the server.
   * The first message will be a "WELCOME" message in which we receive our
   * mark. Then we go into a loop listening for any of the other messages,
   * and handling each message appropriately. The "VICTORY", "DEFEAT", "TIE",
   *  and "OTHER_PLAYER_LEFhT" messages will ask the user whether or not to
   * play another game. If the answer is no, the loop is exited and the server
   * is sent a "QUIT" message.
   */
  public void play() throws Exception {
      try 
      {
          String response = in.nextLine();
          char mark = response.charAt(8);
          char opponentMark = mark == '1' ? '2' : '1';
          frame.setTitle("FILLER: Player " + mark + " - " + username);
          
          if (mark == '1') {
            board.setUser1(username);
          }
          else {
            board.setUser2(username);
          }
          
          while (in.hasNextLine()) 
          {
              response = in.nextLine();
              if (response.startsWith("VALID_MOVE")) 
              {
                  messageLabel.setText("Valid move, please wait");
                  //current.setText(mark);
                  //current.repaint();
              } 
              else if (response.startsWith("OPPONENT_MOVED")) 
              {
                  int loc = Integer.parseInt(response.substring(15));
                  //buttons[loc].setText(opponentMark);
                  //buttons[loc].repaint();
                  messageLabel.setText("Opponent moved, your turn");
              } 
              else if (response.startsWith("MESSAGE")) 
              {
                  messageLabel.setText(response.substring(8));
              } 
              else if (response.startsWith("VICTORY")) 
              {
                  JOptionPane.showMessageDialog(frame, "Winner Winner");
                  break;
              } 
              else if (response.startsWith("DEFEAT")) 
              {
                  JOptionPane.showMessageDialog(frame, "Sorry you lost");
                  break;
              } 
              else if (response.startsWith("TIE")) 
              {
                  JOptionPane.showMessageDialog(frame, "Tie");
                  break;
              } 
              else if (response.startsWith("OTHER_PLAYER_LEFT")) 
              {
                  JOptionPane.showMessageDialog(frame, "Other player left");
                  break;
              } 
              else if (response.startsWith("BOARD_UPDATE")) 
              {
                //FOR COLORS
                String colorTurnToArray = response.substring(13, response.indexOf("-"));
                String[] colorToSplit = colorTurnToArray.split(" ");

                int[] colorBoardInts = new int[colorToSplit.length];
                for (int i = 0; i < colorToSplit.length; i++)
                {
                  colorBoardInts[i] = Integer.parseInt(colorToSplit[i]);
                }
                
                board.setColorInGrid(colorBoardInts);
                
                //FOR STATUS
                String statusTurnToArray = response.substring(response.indexOf("-") + 2);
                String[] statusToSplit = statusTurnToArray.split(" ");

                int[] statusBoardInts = new int[statusToSplit.length];
                for (int i = 0; i < statusToSplit.length; i++)
                {
                  statusBoardInts[i] = Integer.parseInt(statusToSplit[i]);
                }

                board.setStatusInGrid(statusBoardInts);
                board.run();
              }
              else if (response.startsWith("UPDATE_USER"))
              {
                String user1 = response.substring(12, response.indexOf("-"));
                board.setUser1(user1);
                String user2 = response.substring(response.indexOf("-") + 1);
                board.setUser2(user2);
                board.run();
              }
          }
          out.println("QUIT");
      } 
      catch (Exception e) {
          e.printStackTrace();
      }
      finally 
      {
          socket.close();
          frame.dispose();
      }
  }

  public static void main(String[] args) throws Exception {
      if (args.length != 1) {
          System.err.println("Pass the server IP as the sole command line argument");
          return;
      }
      
      Scanner keyboard = new Scanner(System.in);
      String username = "";
      System.out.print("Input username :: ");
      username = keyboard.next();
              
      NEWFillerClient client = new NEWFillerClient(args[0], username);
      client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      client.frame.setSize(500, 550);
      client.frame.setVisible(true);
      client.frame.setResizable(false);
      client.play();
  }
}
