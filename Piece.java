/**
 * Title:
 * Description:
 * Copyright: Copyright (c) 2004
 * Company:
 * @author: Bert Peterson
 * @version 1.0
 * 
 * This code is in its original format
 * 
 * Benjamin Rodriguez, Ph.D. 
 * Johns Hopkins University
 * Engineering for Professionals
 * 605.421 - Foundations of Algorithms 
 * Course Homepage: http://ep.jhu.edu/course-homepages/viewpage.php?homepage_id=3231
 *
 * The code was originally developed by 
 * Dr. Gilbert Peterson
 * Air Force Institute of Technology
 * Wright Patterson AFB, OH
 * 
 * The code is used for instructional purposes only and is not to be redistributed 
 * without permission from Dr. Gilbert Peterson.
 */

public class Piece {
  public int x;
  public int y;
  public int owner;
  public Piece next;

  // Constructor
  Piece(int X, int Y, int own) {
      x = X;
      y = Y;
      owner = own;
      next = null;
  }

  // Constructor
  Piece(int X, int Y, int own, Piece n) {
      x = X;
      y = Y;
      owner = own;
      next = n;
  }
}
