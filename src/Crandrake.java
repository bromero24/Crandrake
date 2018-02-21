import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;


public class Crandrake extends Player {

    public Crandrake(int color, String name) {
        super(color, name);
    }

    public Move getMove(BlokusBoard board) {
        return null;
    }

    public int grade(BlokusBoard board) {
        int boardGrade = 0;
        ArrayList<IntPoint> possibleMoves = board.moveLocations(getColor());
        return 0;
    }

    public int pieceSpots(BlokusBoard board, int index){
        return 0;
    }
    public Player freshCopy() {
        return new BigMoverAI(getColor(), getName());
    }
}
