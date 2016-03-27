import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;

public class Game extends JPanel
{
   public static void main(String[] args) throws Exception
   {
      JFrame frame = new JFrame("2048");
      frame.setSize(700, 700);
      frame.setLocation(800, 0);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(new Game());
      frame.setVisible(true);
   }
   JLabel[][] board;
   int[][] config;
   int boardSize = 4;
   Listener l;
   JLabel label;
   int score = 0;
   int highscore;
   BufferedWriter writer;
   public Game() throws Exception
   {  
      BufferedReader reader = new BufferedReader(new FileReader("gameOutput.txt"));
      reader.readLine();
      while(reader.ready())
         highscore = Integer.parseInt(reader.readLine());
      reader.close();
   
      writer = new BufferedWriter(new FileWriter("gameOutput.txt")); 
      
      setLayout(new BorderLayout());
   
      JPanel north = new JPanel();
      north.setLayout(new FlowLayout());
      add(north, BorderLayout.NORTH);
      label = new JLabel("Score: 0 | Highscore: 0");
      north.add(label);
   
      JPanel center = new JPanel();
      center.setLayout(new GridLayout(boardSize,boardSize));
      add(center, BorderLayout.CENTER);
   
      JPanel bottom = new JPanel();
      bottom.setLayout(new GridLayout(1,3));
      add(bottom, BorderLayout.SOUTH);
      
      board = new JLabel[boardSize][boardSize];
      config = new int[boardSize][boardSize];
   
      Border border = new LineBorder(new Color(131, 126, 124), 5, true);
   
      for(int r = 0; r < boardSize; r++){
         for(int c = 0; c < boardSize; c++)
         {
            board[r][c] = new JLabel();
            board[r][c].setOpaque(true);
            board[r][c].setBorder(border);
            board[r][c].setBackground(new Color(182, 182, 180));
            center.add(board[r][c]);                         
         }
      }  
   
      l = new Listener();
   
      JButton reset = new JButton("Reset");
      reset.setFocusable(false);
      reset.addActionListener( new resetHandler() );
      bottom.add(reset, BorderLayout.SOUTH);
      
      newGame();
   } 
   public class Listener implements KeyListener
   {
      public Listener()
      {
         addKeyListener(this);
         setFocusable(true);
         setFocusTraversalKeysEnabled(false);
      }
      public void keyPressed(KeyEvent event)
      {
         if (event.getKeyCode() == KeyEvent.VK_LEFT) {
            left();
         }
         else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
            down();
         }
         else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
            right();
         }
         else if (event.getKeyCode() == KeyEvent.VK_UP) {
            up();
         } 
      }
      public void keyTyped(KeyEvent event){}
      public void keyReleased(KeyEvent event){}
   }
   
   public void checkIfGameOver()
   {
      if(checkFull())
      {
         if(possibleMoves() == false)
         {
            gameOver();
         }
      }
      else
         move();
   }  
      
   public void gameOver()
   {
      for(int r = 0; r < boardSize; r++)
      {
         for(int c = 0; c < boardSize; c++)
         {
            board[r][c].setText("");
            config[r][c] = 0;
            board[r][c].setBackground(Color.gray);                         
         }
      }
      label.setText("You lose!");
      try
      {
         if(score > highscore)
         {
            writer.newLine();
            writer.write(String.valueOf(score)); 
            writer.flush();
         }
         else
         {
            writer.newLine();
            writer.write(String.valueOf(highscore)); 
            writer.flush();
         }
      }
      catch(IOException e)
      {
         System.out.println("Error with file");
      }
      score = 0;
      label.setText("Score: 0 | Highscore: " + highscore);
      try {
         Thread.sleep(1000); 
      } 
      catch(InterruptedException ex) {
         Thread.currentThread().interrupt();
      }
      for(int r = 0; r < boardSize; r++)
      {
         for(int c = 0; c < boardSize; c++)
         {
            board[r][c].setText("");
            config[r][c] = 0;
            board[r][c].setBackground(new Color(182,182,182));                         
         }
      }
      move();
      move();
   }
   
   //pre-condition: board is full
   public boolean possibleMoves()
   {
      for(int r = 0; r < boardSize; r++){
         for(int c = 0; c < boardSize; c++)
         {
            if(r-1 > 0)
            {
               if(getValue(r-1,c) == getValue(r,c))
                  return true;
            }
            if(r+1 < boardSize)
            {
               if(getValue(r+1,c) == getValue(r,c))
                  return true;
            }
            if(c-1 > 0)
            {
               if(getValue(r,c-1) == getValue(r,c))
                  return true;
            }
            if(c+1 < boardSize)
            {
               if(getValue(r,c+1) == getValue(r,c))
                  return true;
            }
         }
      }
      return false;
   }
   
   public void left()
   { 
      leftSlide();
      int r = 0;
      int c = 0;
      while(r < boardSize)
      {
         while(c < boardSize)
         {
            if(c+1 < boardSize)
            {
               if(getValue(r, c) == getValue(r, c+1) && getValue(r, c) != 0)
               {
                  score += getValue(r, c)*2;
                  if(score > highscore)
                  {
                     highscore = score;
                     try
                     {
                        writer.newLine();
                        writer.write(String.valueOf(highscore)); 
                        writer.flush();
                     }
                     catch(IOException e)
                     {
                        System.out.println("Error with file");
                     }
                  }
                  label.setText("Score: " + score + " | Highscore: " + highscore);
                  setSquare(r, c, getValue(r, c) * 2);
                  setSquare(r, c+1, 0);
               }
            }
            c++; 
         }
         r++;
         c = 0;
      }
      leftSlide();
      checkIfGameOver();
   }     
   public void leftSlide()
   {
      int r = 0;
      int c = 1;
      while(r < boardSize)
      {  
         while(c < boardSize)
         {
            if(c > 0)
            {
               if(getValue(r, c-1) == 0)
               {
                  setSquare(r, c-1, getValue(r, c));
                  setSquare(r, c, 0);
                  if(getValue(r, c-1) != 0)
                     c-=2;
               }
            }
            c++;
         }
         r++;
         c = 1;
      }
   }
   public void right()
   { 
      rightSlide();
      int r = 0;
      int c = boardSize-1;
      while(r < boardSize)
      {
         while(c >= 0)
         {
            if(c-1 >= 0)
            {
               if(getValue(r, c) == getValue(r, c-1) && getValue(r, c) != 0)
               {
                  score += getValue(r, c)*2;
                  if(score > highscore)
                  {
                     highscore = score;
                     try
                     {
                        writer.newLine();
                        writer.write(String.valueOf(highscore)); 
                        writer.flush();
                     }
                     catch(IOException e)
                     {
                        System.out.println("Error with file");
                     }
                  }                  label.setText("Score: " + score + " | Highscore: " + highscore);
                  setSquare(r, c, getValue(r, c) * 2);
                  setSquare(r, c-1, 0);
               }
            }
            c--; 
         }
         r++;
         c = boardSize-1;
      }
      rightSlide();
      checkIfGameOver();
   }     

   public void rightSlide()
   {
      int r = 0, c = boardSize - 1;
      while(r < boardSize)
      {  
         while(c >= 0)
         {
            if(c+1 < boardSize)
            {
               if(getValue(r, c+1) == 0)
               {
                  setSquare(r, c+1, getValue(r, c));
                  setSquare(r, c, 0);
                  if(getValue(r, c+1) != 0)
                     c+=2;
               }
            }
            c--;
         }
         r++;
         c = boardSize - 1;
      }
   }

   public void down()
   { 
      downSlide();
      int r = boardSize - 1;
      int c = 0;
      while(c < boardSize)
      {
         while(r >= 0)
         {
            if(r-1 >= 0)
            {
               if(getValue(r, c) == getValue(r-1, c) && getValue(r, c) != 0)
               {
                  score += getValue(r, c)*2;
                  if(score > highscore)
                  {
                     highscore = score;
                     try
                     {
                        writer.newLine();
                        writer.write(String.valueOf(highscore)); 
                        writer.flush();
                     }
                     catch(IOException e)
                     {
                        System.out.println("Error with file");
                     }
                  }
                  label.setText("Score: " + score + " | Highscore: " + highscore);
                  setSquare(r, c, getValue(r, c) * 2);
                  setSquare(r-1, c, 0);
               }
            }
            r--; 
         }
         c++;
         r = boardSize - 1;
      }
      downSlide();
      checkIfGameOver();
   } 
   
   public void downSlide()
   {
      int r = boardSize -1, c = 0;
      while(c < boardSize)
      {  
         while(r >= 0)
         {
            if(r+1 < boardSize)
            {
               if(getValue(r+1, c) == 0)
               {
                  setSquare(r+1, c, getValue(r, c));
                  setSquare(r, c, 0);
                  if(getValue(r+1, c) != 0)
                     r+=2;
               }
            }
            r--;
         }
         c++;
         r = boardSize - 1;
      }
   }
   
   public void up()
   { 
      upSlide();
      int r = 0;
      int c = 0;
      while(c < boardSize)
      {
         while(r < boardSize)
         {
            if(r+1 < boardSize)
            {
               if(getValue(r, c) == getValue(r+1, c) && getValue(r, c) != 0)
               {
                  score += getValue(r, c)*2;
                  if(score > highscore)
                  {
                     highscore = score;
                     try
                     {
                        writer.newLine();
                        writer.write(String.valueOf(highscore)); 
                        writer.flush();
                     }
                     catch(IOException e)
                     {
                        System.out.println("Error with file");
                     }
                  }                  label.setText("Score: " + score + " | Highscore: " + highscore);
                  setSquare(r, c, getValue(r, c) * 2);
                  setSquare(r+1, c, 0);
               }
            }
            r++; 
         }
         c++;
         r = 0;
      }
      upSlide();
      checkIfGameOver();
   }
   
   public void upSlide()
   {
      int r = 1, c = 0;
      while(c < boardSize)
      {  
         while(r < boardSize)
         {
            if(r > 0)
            {
               if(getValue(r-1, c) == 0)
               {
                  setSquare(r-1, c, getValue(r, c));
                  setSquare(r, c, 0);
                  if(getValue(r-1, c) != 0)
                     r-=2;
               }
            }
            r++;
         }
         c++;
         r = 1;
      }
   }
   
   public int getValue(int r, int c)
   {
      if(board[r][c].getText().equals(""))
      {
         return 0;
      }
      else
         return (Integer.parseInt(board[r][c].getText()));
   }
   
   private class resetHandler implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         newGame();
         setFocusable(true);
      }
   }
      
   public void newGame()
   {
      score = 0;
      label.setText("Score: 0 | Highscore: " + highscore);
      for(int r = 0; r < boardSize; r++)
      {
         for(int c = 0; c < boardSize; c++)
         {
            board[r][c].setText("");
            config[r][c] = 0;
            board[r][c].setBackground(new Color(182, 182, 180));
         }
      }
      move();
      move();
   }
   
   public void move()
   {
      int start1Row = (int)(Math.random()*4);
      int start1Col = (int)(Math.random()*4);
            
      int start1Val = (int)(Math.floor(Math.random() * 10) + 1);
      if(start1Val < 10)
         start1Val = 2;
      else
         start1Val = 4;
               
      while(getValue(start1Row, start1Col) != 0)
      {
         start1Row = (int)(Math.random()*4);
         start1Col = (int)(Math.random()*4);
      }
      setSquare(start1Row, start1Col, start1Val);
      
      checkFull();
   }
   
   public boolean checkFull()
   {
      boolean full = true;
      for(int r = 0; r < boardSize; r++)
      {
         for(int c = 0; c < boardSize; c++)
         {
            if(getValue(r, c) == 0)
               full = false;
         }
      }
      return full;
   }
   
   public void setSquare(int r, int c, int val)
   {
      board[r][c].setText(val + "");
      board[r][c].setFont(new Font("Lucida Sans", Font.BOLD, 60));
      board[r][c].setHorizontalAlignment(SwingConstants.CENTER);
      board[r][c].setVerticalAlignment(SwingConstants.CENTER);
      config[r][c] = val;
      switch(val)
      {
         case 1: board[r][c].setBackground(Color.black);
            board[r][c].setForeground(Color.black);
            board[r][c].setText("");
            break;
         case 2: board[r][c].setBackground(new Color(204, 217, 230));
            board[r][c].setForeground(new Color(70, 62, 63));
            break;
         case 4: board[r][c].setBackground(new Color(213, 255, 209));
            board[r][c].setForeground(new Color(70, 62, 63));
            break;
         case 8: board[r][c].setBackground(new Color(243, 192, 128));
            board[r][c].setForeground(Color.white);
            break;
         case 16: board[r][c].setBackground(new Color(212, 106, 36));
            board[r][c].setForeground(Color.white);
            break;
         case 32: board[r][c].setBackground(new Color(235, 80, 80));
            board[r][c].setForeground(Color.white);
            break;
         case 64: board[r][c].setBackground(new Color(201, 18, 18));
            board[r][c].setForeground(Color.white);
            break;
         case 128: board[r][c].setBackground(new Color(235, 235, 80));
            board[r][c].setForeground(Color.white);
            break;
         case 256: board[r][c].setBackground(new Color(255, 255, 40));
            board[r][c].setForeground(Color.white);
            break;
         case 512: 
          case 1024: 
            case 2048: board[r][c].setBackground(new Color(214, 214, 0));
            board[r][c].setForeground(Color.white);
            break;
         default:
            board[r][c].setBackground(new Color(182, 182, 180));
            board[r][c].setText("");
            break;
      }
   }
}