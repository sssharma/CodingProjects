#include "xoselloplayer.h"
//small change...

// another change

/*
 * Constructor for the player; initialize everything here. The side your AI is
 * on (BLACK or WHITE) is passed in as "side". The constructor must finish 
 * within 30 seconds.
 */


XoSelloPlayer::XoSelloPlayer(Side side) {

	//Creates board, and sets playSide and oppSide depending on valud of 'side'
	playBoard = Board();
	playSide = side;
	if (playSide == BLACK)
	{
		oppSide = WHITE;
	}
	else
	{
		oppSide = BLACK;
	}
	
	
}

/*
 * Destructor for the player.
 */
XoSelloPlayer::~XoSelloPlayer() {
}

int XoSelloPlayer::AlphaBetaPruning(int alpha, int beta, Side side, Board* tempBoard, int depth) {

	if (tempBoard->isDone()){
		return tempBoard->cellScore(playSide);
	} else if (depth == 0) { //Initial case:
		//Calculate score of the board
		if (side == playSide)		
			{
				return tempBoard->cellScore(playSide) + tempBoard->mobScore(playSide);
			}		
		else {
			return tempBoard->cellScore(playSide) - tempBoard->mobScore(oppSide);
		}
	}

	if (side == playSide) { //our side
	
		//Loops through board and checks how many valid moves are 
		// available for the opposite side

		bool noM = true;

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {

				
				Move * currMove = new Move(col, row);
				if (tempBoard->checkMove(currMove, playSide))
				{
					noM = false;
					Board * tBoard = tempBoard->copy();
					tBoard->doMove(currMove, playSide);					
					//Calculate alpha

					int new_alpha = AlphaBetaPruning(alpha, beta, oppSide, tBoard, (depth-1));
					delete tBoard;					
		
					if (new_alpha > alpha) {
						alpha = new_alpha;
					}
					if (alpha >= beta) { //Will not look at this possibility anymore
						return alpha;
					}
				
				}

				delete currMove;
			}
		}

		if (noM)
		{
			Board * tBoard = tempBoard->copy();
			int new_alpha = AlphaBetaPruning(alpha, beta, oppSide, tBoard, (depth-1));		
			delete tBoard;
			if (new_alpha > alpha){
				alpha = new_alpha;
			}	
		}

		return alpha;
	} else { //opp side

		//Loops through board and checks how many valid moves are 
		// available for the opposite side

		bool noM = true;

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++)
			{
				
				Move *currMove = new Move(col, row);
				if (tempBoard->checkMove(currMove, oppSide))
				{
					noM = false;
					Board * tBoard = tempBoard->copy();
					tBoard->doMove(currMove, oppSide);		
					int new_beta = 0; //Calculate beta

					new_beta = AlphaBetaPruning(alpha, beta, playSide, tBoard, (depth-1));
					delete tBoard;

					if (new_beta < beta) {
						beta = new_beta;
					} if (beta <= alpha) { //Will not look at this possibility anymore
						return beta;
					}
				
				}
				delete currMove;
			}
		}

		if (noM)
		{
			Board * tBoard = tempBoard->copy();
			int new_beta = AlphaBetaPruning(alpha, beta, playSide, tBoard, (depth-1));	
			delete tBoard;	
			if (new_beta < beta){
				beta = new_beta;
			}	
		}

		return beta;
	}
	
}	
/*
 * Compute the next move given the opponent's last move. Each AI is
 * expected to keep track of the board on its own. If this is the first move,
 * or if the opponent passed on the last move, then opponentsMove will be NULL.
 *
 * If there are no valid moves for your side, doMove must return NULL.
 *
 * Important: doMove must take no longer than the timeout passed in 
 * msLeft, or your AI will lose! The move returned must also be legal.
 */
Move *XoSelloPlayer::doMove(Move *opponentsMove, int msLeft) {
    
    playBoard.doMove(opponentsMove, oppSide);

	//Case where the are no moves for playside - returns null (passes move)
	if (!playBoard.hasMoves(playSide))
    	{
		return NULL;
	}
	else
	{
		Move *maxMove = NULL;
		int maxScore = INT_MIN;

		//time_t starts;
		//time_t ends;

		
		for (int row = 0; row < 8; row ++)
			for (int col = 0; col < 8; col ++)
			{
				Move *currMove = new Move(col, row);
				int depth = 0;
				
				if ((64 - playBoard.countAll()) <= 12)
				{
					depth = 12;
				} 
				else
				{
					int m = playBoard.mobnum(oppSide);

					if (m >= 6)
					{
						depth = 6;
					}
					else if (m>= 4)
					{
						depth = 6;
					}
					else
					{
						depth = 6;
					}
				}

				

				if (playBoard.checkMove(currMove, playSide))
				{
					Board * tempBoard = playBoard.copy();
					tempBoard->doMove(currMove, playSide);

					//Check mobility of our side
					

					int tempScore = AlphaBetaPruning(INT_MIN, INT_MAX, oppSide, tempBoard, depth);


					if (tempScore > maxScore)
					{
						delete maxMove;
						maxMove = currMove;
						maxScore = tempScore;
					}
					else
					{
						delete currMove;
					}
					delete tempBoard;
				}
				
				
			}
		playBoard.doMove(maxMove, playSide);
		return new Move(maxMove->x, maxMove->y);


	}
}
/*
XoSelloPlayer::OpeningTable(std::string id) {
	Move bestMove;
	if (id == "0000000000000000002000000012100000012000000000000000000000000000") {
		bestMove = newMove(3,2); //Symmetries?
	} else if (id == "0000000002000000002200000012100000012000000000000000000000000000") {
		bestMove = newMove(3,2);


} */

    
