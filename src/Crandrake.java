import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;


public class Crandrake extends Player {

    ArrayList<Shape> availableShapes;
    Move unflippedMove;
    Move flippedMove;

    public Crandrake(int color, String name) {
        super(color, name);
    }

    public Move getMove(BlokusBoard board) {
        return null;
    }

    public int grade(BlokusBoard board) {
        if(availableShapes == null) {
            availableShapes = board.getShapes();
        }
        int boardGrade = 0;
        ArrayList<IntPoint> possibleMoves = board.moveLocations(getColor());
        for(int p = 0; p < possibleMoves.size();p++)
        {
            for(int s = 0; s < availableShapes.size();s++)
            {
                for(int x = 0; x <= 3; x++)
                {

                }
            }
        }
        return 0;
    }

    public Player freshCopy() {
        return new Crandrake(getColor(), getName());
    }
}
