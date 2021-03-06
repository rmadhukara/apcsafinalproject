class GameLogic 
{
  private static int height = FillerClient.HEIGHT;
  private static int width = FillerClient.WIDTH;

  public void logic(int[][] colorInts, int[][] statusInts, int playerStatus, int theColor)
  {
      // Change status of blocks after player selects color
      int[][] newStatusInts = statusInts;

      for (int row = 0; row < height; row++)
      {
        for (int col = 0; col < width; col++)
        {
          if (colorInts[row][col] == theColor && statusInts[row][col] == 0)
          {
            //testing the block above
            if (row-1 >= 0 && statusInts[row-1][col] == playerStatus)
            {
              newStatusInts[row][col] = playerStatus;
            }
            //testing the block below
            else if (row+1 < height && statusInts[row+1][col] == playerStatus)
            {
              newStatusInts[row][col] = playerStatus;
            }
            //testing the block left
            else if (col-1 >= 0 && statusInts[row][col-1] == playerStatus)
            {
              newStatusInts[row][col] = playerStatus;
            }
            //testing the block right
            else if (col+1 < width && statusInts[row][col+1] == playerStatus)
            {
              newStatusInts[row][col] = playerStatus;
            }
          }
        }
      }

      statusInts = newStatusInts;

      for (int row = 0; row < statusInts.length; row++)
      {
        for (int col = 0; col < statusInts[0].length; col++)
        {
          if (statusInts[row][col] == playerStatus)
          {
            colorInts[row][col] = theColor;
          }
        } 
      }
  }
  
  public int countScore(int[][] statusNum, int mark) {
    int score = 0;
    
    for (int row = 0; row < statusNum.length; row++)
    {
      for (int col = 0; col < statusNum[0].length; col++)
      {
        if (statusNum[row][col] == mark)
        {
          score++;
        }
      } 
    }
    
    return score;
  }
}
