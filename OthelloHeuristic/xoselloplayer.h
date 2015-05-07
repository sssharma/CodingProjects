#ifndef __XOSELLOPLAYER_H__
#define __XOSELLOPLAYER_H__

#include <iostream>
#include "common.h"
#include "board.h"
#include <vector>
#include <climits>
#include <time.h>
#include <stdio.h>
#include <map>
#include <string>

using namespace std;

class XoSelloPlayer {

private:
	Board playBoard;
	Side playSide, oppSide;

	

public:

	map<string, AlBe> memo;
	
    XoSelloPlayer(Side side);
    ~XoSelloPlayer();
    int AlphaBetaPruning(int alpha, int beta, Side side, Board* tempBoard, int depth);
    Move *doMove(Move *opponentsMove, int msLeft);
	int mobilityMove(Move *playSideMove);
};

#endif
