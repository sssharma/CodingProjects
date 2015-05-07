#ifndef __BOARD_H__
#define __BOARD_H__

#include <bitset>
#include "common.h"
#include <string>

using namespace std;

class AlBe {
   
public:
    int a,b;
    AlBe(int a, int b) {
        this->a = a;
        this->b = b;        
    }
    ~AlBe() {}
};

class Board {
   
private:
    bitset<64> black;
    bitset<64> taken;    
       
    bool occupied(int x, int y);
    bool get(Side side, int x, int y);
    void set(Side side, int x, int y);
    bool onBoard(int x, int y);
      
public:
    Board();
    ~Board();
    Board *copy();
    std::string boardID(Board * board, Side side);
    bool isDone();
    bool hasMoves(Side side);
    bool checkMove(Move *m, Side side);
    void doMove(Move *m, Side side);
    int count(Side side);
    int countBlack();
    int countWhite();
	int countAll();
	int mobScore(Side side);
	int cellScore(Side side);
	int mobnum(Side side);
};

#endif
