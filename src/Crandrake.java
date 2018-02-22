import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;


public class Crandrake extends Player {

    ArrayList<Shape> availableShapes;
    private Move unflippedMove;
    private Move flippedMove;
	private int previousOtherMoves;
	private int laterOtherMoves;

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
                    unflippedMove = new Move(s, false, x, possibleMoves.get(p));
                    if(board.isValidMove(unflippedMove))
                    {
                        if(getColor() == board.ORANGE)
						{
							previousOtherMoves = board.getPurpleMoveLocations().size();
							makeMove(unflippedMove, getColor());
							laterOtherMoves = board.getPurpleMoveLocations().size();
						}
                    }
                    flippedMove = new Move(s, true, x, possibleMoves.get(p));
                    
                }
            }
        }
        return 0;
    }

    public int gradeMove(Move move)
    {
        
    }
    public Player freshCopy() {
        return new Crandrake(getColor(), getName());
    }
}
