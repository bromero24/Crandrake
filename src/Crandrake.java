import java.util.*;


public class Crandrake extends Player {

    public final static int MAX_DEPTH = 4;
    public final static int STARTING_BRANCHES = 12;
    public final static int BRANCH_DECAY = 2;
    ArrayList<Shape> availableShapes;
    private Move unflippedMove;
    private Move flippedMove;
	private int previousOtherMoves;
	private int laterOtherMoves;

    public Crandrake(int color, String name) {
        super(color, name);
    }

    public Move getMove(BlokusBoard board) {
        //Look ahead
        Stack<Move> moves= new Stack<>();
        Stack<Integer> depth = new Stack<>();
        depth.push(0);

        int bestGrade;
        Move best = null;

        int rDepth;
        do{
            rDepth = depth.pop();
            if(rDepth < MAX_DEPTH){
                Move[] m = getBestMoves(STARTING_BRANCHES-(BRANCH_DECAY*(rDepth/2)), board);
                for(Move i : m){
                    moves.push(i);
                    depth.push(rDepth+1);
                }
            }else{

            }

        }while(depth.size()>0);
        return best;
    }

    public int grade(BlokusBoard board) {
        if (availableShapes == null) {
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
    /**
     *
     * @param count Amount of moves that should be returned that are advantageous to grade.
     * @return Move array of size count of best moves according to grade.
     */
    private Move[] getBestMoves(int count, BlokusBoard board){
        return null;
    }

    public int pieceSpots(BlokusBoard board, int index){
        return 0;
    }

    public int gradeMove(Move move)
    {
        
    }
    public Player freshCopy() {
        return new Crandrake(getColor(), getName());
    }
}
