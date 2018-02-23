import java.util.*;


public class Crandrake extends Player {

    public final static int MAX_DEPTH = 4;
    public final static int STARTING_BRANCHES = 12;
    public final static int BRANCH_DECAY = 2;

    public final static int AGGREGATE_SCORE = 0;
    public final static int SINGLE_SCORE = 1;

    private int scoringMethod = SINGLE_SCORE;

    ArrayList<Shape> availableShapes;
    private Move unflippedMove, flippedMove;
	private int previousOtherMoves, laterOtherMoves;

    public Crandrake(int color, String name) {
        super(color, name);
    }

    public Move getMove(BlokusBoard board) {
        //Look ahead
        Stack<Move> moves= new Stack<>();
        Stack<Integer> depth = new Stack<>();
        depth.push(0);

        int bestGrade = Integer.MIN_VALUE;
        Move best = null, cMove = null;

        int rDepth = 0, tGrade, aGrade = Integer.MIN_VALUE, cColor;
        do{
            cColor = (depth.peek()%2==0 ? getOpponentColor(getColor()) : getColor());
            if(rDepth>depth.peek()){
                //IF GOING UP
                //UNMOVE PIECE
                board.undoMovePiece(moves.pop(), cColor);
                rDepth = depth.pop();
                continue;
            }
            rDepth = depth.pop();
            if(rDepth < MAX_DEPTH){
                //NOT MAX DEPTH
                if(rDepth==1){
                    //STORES CURRENT MOVE THAT IS BEING DECIDED
                    if(scoringMethod==AGGREGATE_SCORE){
                        if(bestGrade<aGrade){
                            best = cMove;
                            bestGrade = aGrade;
                        }
                        aGrade = 0;
                    }
                    cMove = moves.peek();
                }
                board.makeMove(moves.peek(),cColor);
                Move[] m = getBestMoves(STARTING_BRANCHES-(BRANCH_DECAY*(rDepth/2)), board);
                for(Move i : m){
                    moves.push(i);
                    depth.push(rDepth+1);
                }
            }else{
                //AT MAX DEPTH
                tGrade = gradeBoard(board, cColor);
                switch(scoringMethod){
                    case AGGREGATE_SCORE:
                        aGrade+=tGrade;
                        break;
                    case SINGLE_SCORE:
                        if(bestGrade<tGrade){
                            bestGrade = tGrade;
                            best = cMove;
                        }
                        break;
                }

            }

        }while(depth.size()>0);
        return best;
    }

    private int getOpponentColor(int color){
        if(getColor()==BlokusBoard.ORANGE) return BlokusBoard.PURPLE;
        else return BlokusBoard.ORANGE;
    }

    public int gradeBoard(BlokusBoard board, int color) {
        if (availableShapes == null) {
            availableShapes = board.getShapes();
        }
        int boardGrade = 0;
        ArrayList<IntPoint> possibleMoves = board.moveLocations(color);
        for(int p = 0; p < possibleMoves.size();p++)
        {
            for(int s = 0; s < availableShapes.size();s++)
            {
                for(int x = 0; x <= 3; x++)
                {
                    unflippedMove = new Move(s, false, x, possibleMoves.get(p));
                    if(board.isValidMove(unflippedMove))
                    {
                        if(color == board.ORANGE)
						{
							previousOtherMoves = board.getPurpleMoveLocations().size();
							makeMove(unflippedMove, color);
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
        return 0;
    }
    public Player freshCopy() {
        return new Crandrake(getColor(), getName());
    }
}
