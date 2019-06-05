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
  
  public static final Color[] colors = {Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.MAGENTA, Color.BLACK};
  
  public NEWFillerClient(String serverAddress) throws Exception {
      board = new GameBoard();
      buttons = new Panel[6];
      
      socket = new Socket(serverAddress, 58901);
      in = new Scanner(socket.getInputStream());
      out = new PrintWriter(socket.getOutputStream(), true);

      messageLabel.setBackground(Color.lightGray);
      frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);
      
      //Display Game Board
      board.setBounds(0,0,500,390);
      frame.getContentPane().add(board);

      //Display Buttons
      JPanel boardPanel = new JPanel();
      
      boardPanel.setLayout(new GridLayout(1, 6, 10, 1));
      
      for (var i = 0; i < buttons.length; i++) {
          final int j = i;
          buttons[i] = new Panel(colors[i]);
          buttons[i].addMouseListener(new MouseAdapter() {
              public void mousePressed(MouseEvent e) {
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
      try {
          String response = in.nextLine();
          char mark = response.charAt(8);
          char opponentMark = mark == '1' ? '2' : '1';
          frame.setTitle("Filler: Player " + mark);
          while (in.hasNextLine()) {
              response = in.nextLine();
              if (response.startsWith("VALID_MOVE")) {
                  messageLabel.setText("Valid move, please wait");
                  //current.setText(mark);
                  //current.repaint();
              } else if (response.startsWith("OPPONENT_MOVED")) {
                  int loc = Integer.parseInt(response.substring(15));
                  //buttons[loc].setText(opponentMark);
                  //buttons[loc].repaint();
                  messageLabel.setText("Opponent moved, your turn");
              } else if (response.startsWith("MESSAGE")) {
                  messageLabel.setText(response.substring(8));
              } else if (response.startsWith("VICTORY")) {
                  JOptionPane.showMessageDialog(frame, "Winner Winner");
                  break;
              } else if (response.startsWith("DEFEAT")) {
                  JOptionPane.showMessageDialog(frame, "Sorry you lost");
                  break;
              } else if (response.startsWith("TIE")) {
                  JOptionPane.showMessageDialog(frame, "Tie");
                  break;
              } else if (response.startsWith("OTHER_PLAYER_LEFT")) {
                  JOptionPane.showMessageDialog(frame, "Other player left");
                  break;
              } else if (response.startsWith("BOARD_UPDATE")) {
                  // JOptionPane.showMessageDialog(frame, "Other player left");
                // response = in.nextLine().trim();
                String turnToArray = response.substring(13);
                String[] toSplit = turnToArray.split(" ");

                int[] boardInts = new int[toSplit.length];
                for (int i = 0; i < toSplit.length; i++)
                {
                  boardInts[i] = Integer.parseInt(toSplit[i]);
                }
                // int[] boardInts = Integer.parseInt(turnToArray.split(" "));
                board.setColorInGrid(boardInts);
                  //break;
              }
          }
          out.println("QUIT");
      } catch (Exception e) {
          e.printStackTrace();
      }
      finally {
          socket.close();
          frame.dispose();
      }
  }

  public static void main(String[] args) throws Exception {
      if (args.length != 1) {
          System.err.println("Pass the server IP as the sole command line argument");
          return;
      }
      NEWFillerClient client = new NEWFillerClient(args[0]);
      client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      client.frame.setSize(500, 500);
      client.frame.setVisible(true);
      client.frame.setResizable(false);
      client.play();
  }
}