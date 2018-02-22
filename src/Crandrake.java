import java.util.*;


public class Crandrake extends Player {

    public final static int MAX_DEPTH = 4;
    public final static int STARTING_BRANCHES = 12;
    public final static int BRANCH_DECAY = 2;

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
        int boardGrade = 0;
        ArrayList<IntPoint> possibleMoves = board.moveLocations(getColor());
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
    public Player freshCopy() {
        return new BigMoverAI(getColor(), getName());
    }
}
