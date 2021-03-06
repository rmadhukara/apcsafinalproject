import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
  
  private Socket socket;
  private Scanner in;
  private PrintWriter out;
  
  private GameBoard board;
  private Panel[] buttons;
  private Panel current;
  
  private String username;
  private int wins;

  public static final Color newRed = new Color(244, 53, 83);
  public static final Color newGreen = new Color(161, 212, 80);
  public static final Color newYellow = new Color(255, 224, 26);
  public static final Color newBlue = new Color(72, 176, 246);
  public static final Color newPurple = new Color(107, 75, 162);
  public static final Color newBlack = new Color(68, 68, 68);
  
  public static final Color[] COLORS = {newRed, newGreen, newYellow, newBlue, newPurple, newBlack};
  public static final int HEIGHT = 7;
  public static final int WIDTH = 8;
  
  public FillerClient(String serverAddress, String user) throws Exception 
  {
      board = new GameBoard();
      buttons = new Panel[6];
      
      username = user;
      wins = 0;
      
      socket = new Socket(serverAddress, 58901);
      in = new Scanner(socket.getInputStream());
      out = new PrintWriter(socket.getOutputStream(), true);

      messageLabel.setBackground(Color.lightGray);
      frame.getContentPane().add(messageLabel, BorderLayout.SOUTH);
      
      //Check file to see if username has won any games previously
      Scanner file =  new Scanner(new File("UserScore.dat"));
      
      while (file.hasNextLine()) {
        String read = file.nextLine();
        
        if(read.indexOf(username) > -1) {
          wins = Integer.parseInt(read.substring(read.indexOf(" ") + 1));
          break;
        }
      }
      
      file.close();
      
      //Send user info (username and wins) to server
      out.println("USER " + username + "-" + wins);
      
      
      //Display Game Board
      board.setBounds(0,0,500,440);
      frame.getContentPane().add(board);

      //Display Buttons
      JPanel boardPanel = new JPanel();
      
      boardPanel.setLayout(new GridLayout(1, 6, 10, 1));
      boardPanel.setBackground(Color.WHITE);
      
      for (int i = 0; i < buttons.length; i++) 
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
   *  and "OTHER_PLAYER_LEFT" messages will ask the user whether or not to
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
            board.setWins1(wins);
          }
          else {
            board.setUser2(username);
            board.setWins2(wins);
          }
          
          while (in.hasNextLine()) 
          {
              response = in.nextLine();
              if (response.startsWith("VALID_MOVE")) 
              {
                  messageLabel.setText("Valid move, please wait");
              } 
              else if (response.startsWith("OPPONENT_MOVED")) 
              {
                  int loc = Integer.parseInt(response.substring(15));
                  messageLabel.setText("Opponent moved, your turn");
              } 
              else if (response.startsWith("MESSAGE")) 
              {
                  messageLabel.setText(response.substring(8));
              } 
              else if (response.startsWith("VICTORY")) 
              {
                  board.save();
                  JOptionPane.showMessageDialog(frame, "You won!");
                  break;
              } 
              else if (response.startsWith("DEFEAT")) 
              {
                  board.save();
                  JOptionPane.showMessageDialog(frame, "Sorry you lost :(");
                  break;
              } 
              else if (response.startsWith("TIE")) 
              {
                  board.save();
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
                //Update Colors
                String colorTurnToArray = response.substring(13, response.indexOf("-"));
                updateColors(colorTurnToArray);
                
                //Update Status
                String statusTurnToArray = response.substring(response.indexOf("-") + 2);
                updateStatus(statusTurnToArray);
                
                board.run();
              }
              else if (response.startsWith("UPDATE_USER"))
              {
                String user1 = response.substring(12, response.indexOf("-"));
                String user2 = response.substring(response.indexOf("-") + 1);
                updateUser(user1, user2);
              }
              else if (response.startsWith("UPDATE_SCORE"))
              {
                int score1 = Integer.parseInt(response.substring(13, response.indexOf("-")));
                int score2 = Integer.parseInt(response.substring(response.indexOf("-") + 1));
                updateScore(score1, score2);
              }
              else if (response.startsWith("UPDATE_WINS"))
              {
                int wins1 = Integer.parseInt(response.substring(12, response.indexOf("-")));
                int wins2 = Integer.parseInt(response.substring(response.indexOf("-") + 1));
                updateWins(wins1, wins2);
              }
              else if (response.startsWith("INVALID"))
              {
                JOptionPane.showMessageDialog(frame, "Invalid color to pick");
              }
              else if (response.startsWith("REPAINT"))
              {
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
  
  public void updateColors(String colors) {
    String[] colorToSplit = colors.split(" ");

    int[] colorBoardInts = new int[colorToSplit.length];
    for (int i = 0; i < colorToSplit.length; i++)
    {
      colorBoardInts[i] = Integer.parseInt(colorToSplit[i]);
    }
    
    board.setColorInGrid(colorBoardInts);
  }
  
  public void updateStatus(String status) {
    String[] statusToSplit = status.split(" ");

    int[] statusBoardInts = new int[statusToSplit.length];
    for (int i = 0; i < statusToSplit.length; i++)
    {
      statusBoardInts[i] = Integer.parseInt(statusToSplit[i]);
    }

    board.setStatusInGrid(statusBoardInts);
  }
  
  public void updateUser(String user1, String user2) {
    board.setUser1(user1);
    board.setUser2(user2);
    board.run();
  }
  
  public void updateScore(int score1, int score2) {
    board.setScore1(score1);
    board.setScore2(score2);
    board.run();
  }
  
  public void updateWins(int wins1, int wins2) {
    board.setWins1(wins1);
    board.setWins2(wins2);
    board.run();
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
      keyboard.close();
      
      FillerClient client = new FillerClient(args[0], username);
      client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      client.frame.setSize(500, 550);
      client.frame.setVisible(true);
      client.frame.setResizable(false);
      client.play();
  }
}
