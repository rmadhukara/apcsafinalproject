import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FillerClient {
  private JFrame frame = new JFrame("Filler");
  private JLabel messageLabel = new JLabel("...");

  //private Square[] board = new Square[9];
  //private Square currentSquare;
  
  private GameBoard board;
  
  private Socket socket;
  private Scanner in;
  private PrintWriter out;
  
  public FillerClient(String serverAddress) throws Exception {

      socket = new Socket(serverAddress, 58901);
      in = new Scanner(socket.getInputStream());
      out = new PrintWriter(socket.getOutputStream(), true);

      messageLabel.setBackground(Color.lightGray);
      frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);

      JPanel boardPanel = new JPanel();
      boardPanel.setBackground(Color.black);
      boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
      for (var i = 0; i < board.length; i++) {
          final int j = i;
          board[i] = new Square();
          board[i].addMouseListener(new MouseAdapter() {
              public void mousePressed(MouseEvent e) {
                  currentSquare = board[j];
                  out.println("MOVE " + j);
              }
          });
          boardPanel.add(board[i]);
      }
      frame.getContentPane().add(boardPanel, BorderLayout.CENTER);
  }

  /**
   * Basic code from: https://cs.lmu.edu/~ray/notes/javanetexamples/
   * 
   * The main thread of the client will listen for messages from the server.
   * The first message will be a "WELCOME" message in which we receive our
   * mark. Then we go into a loop listening for any of the other messages,
   * and handling each message appropriately. The "VICTORY", "DEFEAT", "TIE",
   *  and "OTHER_PLAYER_LEFT" messages will ask the user whether or not to
   * play another game. If the answer is no, the loop is exited and the server
   * is sent a "QUIT" message.
   */
  public void play() throws Exception {
      try {
          String response = in.nextLine();
          char mark = response.charAt(8);
          char opponentMark = mark == 'X' ? 'O' : 'X';
          frame.setTitle("Tic Tac Toe: Player " + mark);
          while (in.hasNextLine()) {
              response = in.nextLine();
              if (response.startsWith("VALID_MOVE")) {
                  messageLabel.setText("Valid move, please wait");
                  currentSquare.setText(mark);
                  currentSquare.repaint();
              } else if (response.startsWith("OPPONENT_MOVED")) {
                  var loc = Integer.parseInt(response.substring(15));
                  board[loc].setText(opponentMark);
                  board[loc].repaint();
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

  static class Square extends JPanel {
      JLabel label = new JLabel();

      public Square() {
          setBackground(Color.white);
          setLayout(new GridBagLayout());
          label.setFont(new Font("Arial", Font.BOLD, 40));
          add(label);
      }

      public void setText(char text) {
          label.setForeground(text == 'X' ? Color.BLUE : Color.RED);
          label.setText(text + "");
      }
  }

  public static void main(String[] args) throws Exception {
      if (args.length != 1) {
          System.err.println("Pass the server IP as the sole command line argument");
          return;
      }
      TicTacToeClient client = new TicTacToeClient(args[0]);
      client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      client.frame.setSize(320, 320);
      client.frame.setVisible(true);
      client.frame.setResizable(false);
      client.play();
  }
}
