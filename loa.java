import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2004
 * Company:
 * @author:  Bert Peterson
 * @version 1.0 
 *
 * This code was modified on February 2010
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
 * without permission from Dr. Peterson.
 */

public class loa extends Applet implements Runnable {
    Thread animation;
    Graphics offscreen;    // Declaration of offscreen buffer
    Image image;           // Image associated with the buffer
    private Image background, black_piece, white_piece; // The artwork
    private CustomCanvas canvas;   // The canvas that is drawn to, and mouse input received from

    WorkBoard board = new WorkBoard(); // The LOA board
    Board onscreen = new Board();      // The onscreen LOA board.
    public int user_move;              // Variable to track user status
    public int x1,y1,x2,y2;            // User move information

    private String status;
    private int depth;
    private Move lastmove = null;

    Choice choice1 = new Choice();     // All of the wonderfull GUI info that is not used. yet.
    Choice choice2 = new Choice();
    List textPanel = new List();
    Label label1 = new Label();
    Label label2 = new Label();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    
    static final long serialVersionUID = 0;

    /**
     * The init function, instantiates all the miscellaneous variables, and
     * loads the graphics. Note that the double buffer is created in start,
     * this was because the buffer image object requires the Applet window to attach
     * to.
     */
    public void init(){
        System.out.println("<<init>>");
        MediaTracker imageTracker = new MediaTracker( this );

        background = getImage( getDocumentBase(), "LOA-Grid.png" );
        imageTracker.addImage( background, 0 );
        black_piece = getImage( getDocumentBase(), "LOA-Black.png");
        imageTracker.addImage( black_piece, 1 );
        white_piece = getImage( getDocumentBase(), "LOA-White.png" );
        imageTracker.addImage( white_piece, 2);

        user_move = CustomCanvas.NOT_MOVE;
        status = "Your move as Black!";
        depth = 3;
        board.searchtime = 5000;

        try {
            for ( int i = 0; i < 3; i++ )
                imageTracker.waitForID(i);
        } catch( InterruptedException e ) {}
    }

    /**
     * Start method for the thread, creates the offscreen image and Graphic
     * association and then maintains itself.
     */
    public void start() {
        System.out.println(">> start <<");
        image = createImage(300,300);       // allocation of offscreen
        offscreen = image.getGraphics();   //               buffer

        animation = new Thread(this);
        if (animation != null) {
            animation.start();
        }
    }

    /**
     * The overridden paint function, copies the background and all of the other
     * graphics bits to the background Graphic that will be updated when we
     * call canvas.repaint at the end.
     *
     * @param g Graphics the canvase to draw the board on.
     */
    public void paint( Graphics g ){
      showStatus( status );
      // Copy the background image
      offscreen.drawImage( background, 0, 0, 300, 300, this );

      // If the computer moved previously show this move.
      // Doing this here so that when we draw the pieces, it overwrites part of
      // line.
      if ( lastmove != null ) {
        offscreen.setColor(Color.yellow);
        offscreen.drawLine(lastmove.x1 * 35 + 25, (7 - lastmove.y1) * 35 + 25,
                           lastmove.x2 * 35 + 25, (7 - lastmove.y2) * 35 + 25);
      }

      // Place each piece in the correct location.
      for ( int x = 0; x < 8; x++ )
        for ( int y = 0; y < 8; y++ ) {
          if ( onscreen.checker_of( Board.BLACK_CHECKER, x, y ) )
            offscreen.drawImage( black_piece, (x)*35+11, (7-y)*35+11, 30, 30, this );
          if ( onscreen.checker_of( Board.WHITE_CHECKER, x, y ) )
            offscreen.drawImage( white_piece, (x)*35+11, (7-y)*35+11, 30, 30, this );
        }
      // If the player is moving, show him his possible moves.
      if ( user_move == CustomCanvas.PICK_MOVE ) {
        Vector<Move> v = board.genMoves2(x1,y1);
        Move m;
        offscreen.setColor( Color.red );
        for (int i = 0; i < v.size();i++) {
          m = (Move)v.elementAt( i );
          offscreen.drawLine( x1*35+25, (7-y1)*35+25, m.x2*35+25, (7-m.y2)*35+25);
        }
      }

      canvas.repaint();
    }

    /**
     * The run method from the thread, loops through the player order till the
     * game is over. Black player followed by white.
     */
    public void run() {
      repaint();
      showStatus( status );
      while (true) {
    	  make_user_move();
    	  get_comp_move();
        }
    }

    /**
     * The make_user_move method waits on input from the user through the
     * GUI interface, and provides information for the player in once a piece
     * is selected, what are the valid moves and when the player selects, then
     * requests the board be updated.
     */
    public void make_user_move() {
      user_move = CustomCanvas.PICK_PIECE;
      Vector<Move> v;
      int stat;
      while ( user_move != CustomCanvas.NOT_MOVE )
      {
        switch ( user_move ) {
        case CustomCanvas.PICK_PIECE:
        	while ( user_move == CustomCanvas.PICK_PIECE) {
        		Wait.oneSec();
        	}
          	v = board.genMoves2(x1,y1);
          	if ( v.size() == 0 )
          		user_move = CustomCanvas.PICK_PIECE;
          	repaint();
          	break;
        case CustomCanvas.PICK_MOVE:
          while ( user_move == CustomCanvas.PICK_MOVE ) {
        	  Wait.oneSec();
          }
          	v = board.genMoves2(x1,y1);
          	Move m;
          	for (int i = 0; i < v.size();i++) {
          		m = (Move)v.elementAt( i );
          		if ( m.x2 == x2 && m.y2 == y2 ) {
          			// valid move, need to move piece and update screen.
          			stat = board.try_move( m );
          			user_move = CustomCanvas.NOT_MOVE;
          			textPanel.add( "Black Move: " + m.name());
          			status = "Computer's move as White!";
          			lastmove = m;
          			if ( stat == Board.GAME_OVER )
          			{
          				if ( board.referee() == Board.PLAYER_BLACK ) {
          					status = "GAME OVER Black wins!";
          					textPanel.add( "Black wins!");
          				} else {
          					status = "GAME OVER White wins!";
          					textPanel.add( "White wins!");
          				}
          				user_move = CustomCanvas.PICK_MOVE;
          			}
          			break;
          		}
          	}
          	if ( user_move == CustomCanvas.END_MOVE )
          		user_move = CustomCanvas.PICK_MOVE;
          	onscreen.copy_positions( board );
          	repaint();
          	break;
        	}
      	}
    }

    /**
     * The get_comp_move method calls the WorkBoard bestMove method, and then
     * updates both the gameboard, and the system time.
     */
    public void get_comp_move() {
      long startTime;

      startTime = System.currentTimeMillis();
      board.bestMove( depth );
      startTime = System.currentTimeMillis() - startTime;
      int result = board.try_move( board.best_move );
      textPanel.add( "White Move: " + board.best_move.name());
      textPanel.add( "Time: " + (float)(startTime)/1000.0 + " s" );
      System.out.println("Search Time: " + (startTime)/1000.0 + "s  Best move: " + board.best_move.name() + "\n");
      status = "Your move as Black!";
      lastmove = board.best_move;
      if ( result == Board.GAME_OVER )
      {
        if ( board.referee() == Board.PLAYER_BLACK ) {
          status = "GAME OVER Black wins!";
          textPanel.add( "Black wins!");
        } else {
          status = "GAME OVER White wins!";
          textPanel.add( "White wins!");
        }
          user_move = CustomCanvas.PICK_MOVE;
      }
      onscreen.copy_positions( board );
      repaint();
    }

    /**
     * Thread stop function, just recalls itself.
     */
    public void stop() {
        System.out.println(">> stop <<");
            if (animation != null) {
                animation.interrupt();
                animation = null;
            }
    }

    /**
     * Most of this wonderful block of code was written by Borland JBuilder so
     * I wouldn't have to deal with tweaking the stupid GUI items.
     */
  public loa() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    this.setLayout(gridBagLayout2);
    label1.setText("Depth");
    choice1.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        choice1_itemStateChanged(e);
      }
    });
    // The depth choices 3, 5, 7, 8, 11, 13, 15 and 21.
    choice1.add( "3" );
    choice1.add( "5" );
    choice1.add( "7" );
    choice1.add( "9" );
    choice1.add( "11" );
    choice1.add( "13" );
    choice1.add( "15" );
    choice1.add( "21" );
    label2.setText("Time (s)");
    choice2.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        choice2_itemStateChanged(e);
      }
    });
    choice2.add("1");
    choice2.add("3");
    choice2.add("5");
    choice2.add("10");
    choice2.add("20");
    choice2.add("30");
    choice2.add("60");
    canvas = new CustomCanvas( this );
    this.add(canvas,  new GridBagConstraints(0, 0, 1, 3, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(20, 20, 14, 0), 290, 290));
    this.add(textPanel,        new GridBagConstraints(1, 2, 2, 1, 1.0, 0.8
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 14, 9), 0, 0));
    this.add(choice1,    new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(20, 0, 0, 9), 50, 2));
    this.add(choice2,          new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(9, 0, 0, 9), 50, 2));
    this.add(label1,   new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 51, 6));
    this.add(label2,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(9, 10, 0, 0), 51, 6));
  }

  /**
   * If the choice1 box for the depth changes, change the search depth.
   *
   * @param e ItemEvent search depth change from the user
   */
  void choice1_itemStateChanged(ItemEvent e) {
    String D = e.getItem().toString();
    depth = Integer.parseInt( D );
    D = "Depth changed to: " + depth;
    showStatus( D );
    textPanel.add(D);
  }

  /**
   * Whatever I need later.
   *
   * @param e ItemEvent search time change from the user
   */
  void choice2_itemStateChanged(ItemEvent e) {
    String D = e.getItem().toString();
    int temp = Integer.parseInt(D);
    D = "Time changed to: " + temp + " s";
    showStatus( D );
    textPanel.add(D);
    board.searchtime = temp*1000;
  }
}

