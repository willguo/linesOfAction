/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2004
 * Company:
 * @author: Bert Peterson
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

import java.util.ArrayList;
import java.util.List;

public class WorkBoard extends Board {
    static final int INF = 5000;
    static final int WINDOW = 300;
    Move best_move = null;  // Put your best move in here!
	int start_depth = 0;
    int totalNodesSearched = 0;
    int numLeafNodes = 0;
    boolean stoptime = true;
    public long searchtime = 0L;

    public WorkBoard() {
    }

    public WorkBoard(WorkBoard w) {
    	super(w);
    }
    
    /**
     * Ranks board states.  The larger the value, the better.
     * @return
     */
    int h_value() {
    	numLeafNodes++;
    	int result=0;
    	
    	if(areAllPiecesConnected(PLAYER_WHITE)){
    		result = Integer.MAX_VALUE;
    	}else if(areAllPiecesConnected(PLAYER_BLACK)){
    		result = Integer.MIN_VALUE;
    	}else{
    		ArrayList<Piece> humanPieces = getAllPlayerPieces(BLACK_CHECKER);
        	ArrayList<Piece> compPieces = getAllPlayerPieces(WHITE_CHECKER);
    		int compDist = getDistanceBetweenPieces(compPieces);
    		int humDist = getDistanceBetweenPieces(humanPieces);
    		result = humDist - compDist; // minimize computer distance while maximizing human distance
    	}
    	return result;
    }
    
    /**
     * finds all of a player's pieces and returns them in an ArrayList
     * @param player
     * @return
     */
    private ArrayList<Piece> getAllPlayerPieces(int player){
    	ArrayList<Piece> pieces = new ArrayList<Piece>();
    	Piece piece = piece_list[player];
    	if(piece != null){
    		while(piece.next !=null){
        		pieces.add(piece);
    			piece = piece.next;
        	}
    		pieces.add(piece); // add last piece
    	}
    	return pieces;
    }
    
    /**
     * measures the aggregate distance between all pieces
     * @param pieces the pieces to measure
     * @return
     */
    private int getDistanceBetweenPieces(ArrayList<Piece> pieces){
    	int runningSum = 0;
    	for(int i = 0; i < pieces.size();i++){
    		for(int j = 0; j < i;j++){
    			runningSum += calcDistanceBetweenPieces(pieces.get(i), pieces.get(j));
    		}
    	}
    	return runningSum;
    }
    
    /**
     * calculates the distance between 2 Pieces
     * @param one
     * @param two
     * @return
     */
    private double calcDistanceBetweenPieces(Piece one, Piece two){
    	int xSq = (int) Math.pow(one.x-two.x,2);
    	int YSq = (int) Math.pow(one.y-two.y,2);
    	return Math.sqrt(xSq+YSq);
    }
    
    /**
     *Finds a good move for the computer by evaluating possible moves with heuristic.
     * @parama depth int the depth of the search to conduct
     * @return maximum heuristic board found value
     */
    void min_max(int depth, double alpha, double beta) 
    {
    	totalNodesSearched++;
    	Move moves = genMoves();
	   	double standingBest = Integer.MIN_VALUE;
	   	double canidate = Integer.MIN_VALUE;
	   	while (moves != null)
	   	{
	   		makeMove(moves);
	   		canidate = min_value(depth -1 , alpha, beta);
		   	if(canidate > standingBest){ 	//is the candidate move better?
		   		best_move = moves;			//take it if it is
		   		standingBest = canidate;
		   	}
		   	reverseMove(moves);
		   	moves = moves.next;
	   	}
    }
    
    /**
     * Finds the maximum acceptable move value below in the tree
     * @param depth how much farther to dig into the tree
     * @param alpha	The current largest max
     * @param beta	The competing smallest value
     * @return
     */
    private double max_value(int depth, double alpha, double beta) 
    {
    	totalNodesSearched++;
   	 	Move moves = genMoves();
     	double v = -INF;   	 
     	if (depth == 1)
     	{
     		return h_value();
     	}
     	while (moves != null && alpha < beta)
     	{
     		makeMove(moves);
     		v = Math.max(v, min_value(depth -1 , alpha, beta));
     		alpha = Math.max(alpha, v);
     		reverseMove(moves);
     		moves = moves.next;
     	}
     	return alpha; 
    }

    /**
     * Finds the smallest acceptable move value below in the tree
     * @param depth how much farther to dig into the tree
     * @param alpha	The current largest max
     * @param beta	The competing smallest value
     * @return
     */
	private double min_value(int depth, double alpha, double beta) 
	{
		totalNodesSearched++;
		Move moves = genMoves();
  	 	double v = INF;  
  	 	while (moves != null && beta > alpha)
  	 	{
  	 		makeMove(moves);
  	 		v = Math.min(v, max_value(depth -1 , alpha, beta));
  	 		beta = Math.min(beta, v);		
     		reverseMove(moves);
     		moves = moves.next;
  	 	}
  	 	return beta; 
	}
    /**
     * This function is called to perform search. All it does is call min_max.
     *
     * @param depth int the depth to conduct search
     */
    void bestMove(int depth) {
      best_move = null;
      totalNodesSearched = numLeafNodes = moveCount = 0;
      long startTime = System.currentTimeMillis();
      long elapsedTime = 0;
      stoptime = false;
      totalNodesSearched = numLeafNodes = moveCount = 0;
      min_max(depth, -INF, INF); // Call Search!
      elapsedTime = System.currentTimeMillis()-startTime;
      System.out.println("Depth: " + depth +" Time: " + elapsedTime/1000.0 + " Nodes Searched: " + totalNodesSearched + " Leaf Nodes: " + numLeafNodes +
                           " MC: " + moveCount);

      System.out.println("Nodes per Second = " + totalNodesSearched/(elapsedTime/1000.0));
      if (best_move == null  || best_move.piece == null) {
        throw new Error ("No Move Available - Search Error!");
      }
    }
}
