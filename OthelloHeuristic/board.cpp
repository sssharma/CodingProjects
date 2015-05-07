#include "board.h"

/*
 * Make a standard 8x8 othello board and initialize it to the standard setup.
 */
Board::Board() {
    taken.set(3 + 8 * 3);
    taken.set(3 + 8 * 4);
    taken.set(4 + 8 * 3);
    taken.set(4 + 8 * 4);
    black.set(4 + 8 * 3);
    black.set(3 + 8 * 4);
}

/*
 * Destructor for the board.
 */
Board::~Board() {
}

/*
 * Returns a copy of this board.
 */
Board *Board::copy() {
    Board *newBoard = new Board();
    newBoard->black = black;
    newBoard->taken = taken;
    return newBoard;
}

bool Board::occupied(int x, int y) {
    return taken[x + 8*y];
}

bool Board::get(Side side, int x, int y) {
    return occupied(x, y) && (black[x + 8*y] == (side == BLACK));
}

void Board::set(Side side, int x, int y) {
    taken.set(x + 8*y);
    black.set(x + 8*y, side == BLACK);
}

bool Board::onBoard(int x, int y) {
    return(0 <= x && x < 8 && 0 <= y && y < 8);
}

 
/*
 * Returns true if the game is finished; false otherwise. The game is finished 
 * if neither side has a legal move.
 */
bool Board::isDone() {
    return !(hasMoves(BLACK) || hasMoves(WHITE));
}

/*
 * Returns true if there are legal moves for the given side.
 */
bool Board::hasMoves(Side side) {
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            Move move(i, j);
            if (checkMove(&move, side)) return true;
        }
    }
    return false;
}

/*
 * Returns true if a move is legal for the given side; false otherwise.
 */
bool Board::checkMove(Move *m, Side side) {
    // Passing is only legal if you have no moves.
    if (m == NULL) return !hasMoves(side);

    int X = m->getX();
    int Y = m->getY();

    // Make sure the square hasn't already been taken.
    if (occupied(X, Y)) return false;

    Side other = (side == BLACK) ? WHITE : BLACK;
    for (int dx = -1; dx <= 1; dx++) {
        for (int dy = -1; dy <= 1; dy++) {
            if (dy == 0 && dx == 0) continue;

            // Is there a capture in that direction?
            int x = X + dx;
            int y = Y + dy;
            if (onBoard(x, y) && get(other, x, y)) {
                do {
                    x += dx;
                    y += dy;
                } while (onBoard(x, y) && get(other, x, y));

                if (onBoard(x, y) && get(side, x, y)) return true;
            }
        }
    }
    return false;
}

/*
 * Modifies the board to reflect the specified move.
 */
void Board::doMove(Move *m, Side side) {
    // A NULL move means pass.
    if (m == NULL) return;

    // Ignore if move is invalid.
    if (!checkMove(m, side)) return;

    int X = m->getX();
    int Y = m->getY();
    Side other = (side == BLACK) ? WHITE : BLACK;
    for (int dx = -1; dx <= 1; dx++) {
        for (int dy = -1; dy <= 1; dy++) {
            if (dy == 0 && dx == 0) continue;

            int x = X;
            int y = Y;
            do {
                x += dx;
                y += dy;
            } while (onBoard(x, y) && get(other, x, y));

            if (onBoard(x, y) && get(side, x, y)) {
                x = X;
                y = Y;
                x += dx;
                y += dy;
                while (onBoard(x, y) && get(other, x, y)) {
                    set(side, x, y);
                    x += dx;
                    y += dy;
                }
            }
        }
    }
    set(side, X, Y);
}


	
/*
 * Current count of given side's stones.
 */
int Board::count(Side side) {
    return (side == BLACK) ? countBlack() : countWhite();
}

/*
 * Current count of black stones.
 */
int Board::countBlack() {
    return black.count();
}

/*
 * Current count of white stones.
 */
int Board::countWhite() {
    return taken.count() - black.count();
}

/*
 * Current count of stones.
 */
int Board::countAll() {
    return taken.count();
}

/*
 * Return the score of the current  board.
 */

std::string Board::boardID(Board * board, Side side) {
	std::string s1;

	//Loops through board and checks how many valid moves are 
	// available for the opposite side
	for (int row = 0; row < 8; row++) {
		for (int col = 0; col < 8; col++)
		{
			if (!occupied(col, row)) {
				s1 += "0";
			} else {
				if (board->get(side, col, row)) {
					s1 += "1";
				} else {
					s1 += "2";
				}
			}
		}
	}
	return s1;
}
/* Attempt at transposition
std::string Board::boardID(Board * board, Side side) {
	std::string s1; //returns smallest ID
	std::string s2;
	std::string s3;
	std::string s0;
	std::string s1_sym;
	std::string s1_sym2;
	std::string s2_sym;
	std::string s2_sym2;
	std::string s3_sym;
	std::string s3_sym2;
	s0 = boardID(board, side);

	for (int row = 0; row < 8; row++) {
	for (int col = 0; col < 8; col++)
	{
		int mod_col = 0; // 1 rotation
		int mod_row = col;
		if (row <= 3) {
			mod_col = 3 - row;;
		} else {		
			mod_col = 3 + row;
		}
		if (board->occupied(mod_col, mod_row)) {
			s1 += "0";
		} else {
			if (board->get(side, mod_col, mod_row)) {
				s1 += "1";
			} else {
				s1 += "2";
			}
		}

		 // 1 rotation symmetry
		mod_row = row;
		if (row <= 3) {
			mod_col = 3 - row;;
		} else {		
			mod_col = 3 + row;
		}
		if (board->occupied(mod_col, mod_row)) {
			s1_sym += "0";
		} else {
			if (board->get(side, mod_col, mod_row)) {
				s1_sym += "1";
			} else {
				s1_sym += "2";
			}
		}
		 // 1 rotation symmetry 2
		mod_col = col;
		if (col <= 3) {
			mod_row = 3 - col;;
		} else {		
			mod_row = 3 + col;
		}
		if (board->occupied(mod_col, mod_row)) {
			s1_sym2 += "0";
		} else {
			if (board->get(side, mod_col, mod_row)) {
				s1_sym2 += "1";
			} else {
				s1_sym2 += "2";
			}
		}



		//2 rotations

		if (row <= 3) {
			mod_col = 3 - row;;
		} else {		
			mod_col = 3 + row;
		}
		if (col <= 3) {
			mod_row = 3 - col;;
		} else {		
			mod_row = 3 + col;

		if (board->occupied(mod_col, mod_row)) {
			s2 += "0";
		} else {
			if (board->get(side, mod_col, mod_row)) {
				s2 += "1";
			} else {
				s2 += "2";
			}
		}

		 // 1 rotation symmetry
		//mod_row is same
		if (row <= 3) {
			mod_col = 3 - row;;
		} else {		
			mod_col = 3 + row;
		}
		if (board->occupied(mod_col, mod_row)) {
			s1_sym += "0";
		} else {
			if (board->get(side, mod_col, mod_row)) {
				s1_sym += "1";
			} else {
				s1_sym += "2";
			}
		}
		 // 1 rotation symmetry 2
		mod_col = col;
		if (col <= 3) {
			mod_row = 3 - col;;
		} else {		
			mod_row = 3 + col;
		}
		if (board->occupied(mod_col, mod_row)) {
			s1_sym2 += "0";
		} else {
			if (board->get(side, mod_col, mod_row)) {
				s1_sym2 += "1";
			} else {
				s1_sym2 += "2";
			}
		}

		//3 rotations
	
		int mod_col = row;
		if (col <= 3) {
			mod_row = 3 - col;;
		} else {		
			mod_row = 3 + col;

		if (board->occupied(mod_col, mod_row)) {
			s2 += "0";
		} else {
			if (board->get(side, mod_col, mod_row)) {
				s2 += "1";
			} else {
				s2 += "2";
			}
		}

	}
	}



	
	//Loops through board and checks how many valid moves are 
	// available for the opposite side
/*
	for (int row = 0; row < 8; row++) {
		for (int col = 0; col < 8; col++)
		{
			if (!occupied(col, row)) {
				s3 += "0";
			} else {
				if (board->get(side, col, row)) {
					s3 += "1";
				} else {
					s3 += "2";
				}
			}
		}
	}
	
	std::string retstr;
	if (s0.compare(s1) 
}
*/

int Board::cellScore(Side side) {
	//First set the score for each cell.
	static int weight[64] = { 99, -8,   8, 6, 6,  8,  -8, 99,
								-8, -24, -4, 3, 3, -4, -24, -8,
								 8,  -4,  7, 4, 4,  7,  -4,  8, 
								 6,  -3,  4, 0, 0,  4,  -3,  6,
								 6,  -3,  4, 0, 0,  4,  -3,  6,
								 8,  -4,  7, 4, 4,  7,  -4,  8, 
								-8, -24, -4, 3, 3, -4, -24, -8,
								99,  -8,  8, 6, 6,  8,  -8, 99};
	int sum = 0; //The sum of all cell scores we are going to return.
	for (int row = 0; row < 8; row++)
		for (int col = 0; col < 8; col++)
		{
			if (occupied(col, row))
			{
				if (get(side, col, row))
				{
					sum += weight[col + 8 * row];
				}
				else
				{
					sum -= weight[col + 8 * row];
				}
			}
		}

	return sum;
}


int Board::mobScore(Side side) {

		//count for each move will cause how many other moves for the other side.
		//Creates a tempBoard with the playBoard's state
		/*Board * tempBoard = playBoard.copy();
		tempBoard->doMove(move, side);*/

		int mobility = 0;
		int frontier = 0;
		Side oppSide;
		if (side == BLACK)
		{
			oppSide = WHITE;
		}
		else
		{
			oppSide = BLACK;
		}
	
		//Loops through board and checks how many valid moves are 
		// available for the opposite side
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++)
			{
				Move *currMove = new Move(col, row);
				if (checkMove(currMove, oppSide))
				{
					//Adds to mobility counter
					mobility+=1;
				}
				else if (occupied(col, row))
				{
					for (int x= -1; x <= 1; x ++)
						for (int y=-1; y<= 1; y ++)
							{
								if ((x != 0 && y != 0)&& (col + x >= 0) && (col + x < 8) && (row + y >= 0) && (row + y < 8))
								{
									if (!occupied(col + x, row + y))
									{ frontier+=1;}
								}
							}
				}
				delete currMove;

			}
		}
		
		if (mobility <= 3 || frontier <= 8)
			{return 0;}

		return mobility + frontier;
	
}

int Board::mobnum(Side side) {

		//count for each move will cause how many other moves for the other side.
		//Creates a tempBoard with the playBoard's state
		/*Board * tempBoard = playBoard.copy();
		tempBoard->doMove(move, side);*/

		int mobility = 0;

		Side oppSide;
		if (side == BLACK)
		{
			oppSide = WHITE;
		}
		else
		{
			oppSide = BLACK;
		}
	
		//Loops through board and checks how many valid moves are 
		// available for the opposite side
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++)
			{
				Move *currMove = new Move(col, row);
				if (checkMove(currMove, oppSide))
				{
					//Adds to mobility counter
					mobility+=1;
				}

			}
		}
		
		return mobility;
}
    
