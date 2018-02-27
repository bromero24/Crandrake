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

    public Crandrake(int color, String name) {
        super(color, name);
    }

    public Move getMove(BlokusBoard board) {
        //Look ahead
        Stack<Move> moves= new Stack<>();
        Stack<Integer> depth = new Stack<>();

        Move[] b = getBestMoves(STARTING_BRANCHES, board, getColor());
        for(Move i : b){
            if(i==null) break;
            moves.push(i);
            depth.push(0);
        }

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
                board.changeTurns();
                //DOUBLE POP
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
                Move[] m = getBestMoves(STARTING_BRANCHES-(BRANCH_DECAY*(rDepth/2)), board, cColor);
                for(Move i : m){
                    if(i==null) break;
                    moves.push(i);
                    depth.push(rDepth+1);
                    //EVEN PUSHES
                }
                board.changeTurns();
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
                moves.pop();
                //DOUBLE POP
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
                    if(board.isValidMove(unflippedMove, color))
                    {
                        boardGrade += (availableShapes.get(s).getSquareCount()*(gradeMove(unflippedMove,board)+1));
                    }
                    flippedMove = new Move(s, true, x, possibleMoves.get(p));
                    if(board.isValidMove(flippedMove, color))
                    {
                        boardGrade += (availableShapes.get(s).getSquareCount()*(gradeMove(flippedMove,board)+1));
                    }
                }
            }
        }
        return boardGrade;
    }
    /**
     *
     * @param count Amount of moves that should be returned that are advantageous to grade.
     * @return Move array of size count of best moves according to grade.
     */
    private Move[] getBestMoves(int count, BlokusBoard board, int color){
        if (availableShapes == null) {
            availableShapes = board.getShapes();
        }
        boolean changeTurns = color!=board.getTurn();
        if(changeTurns) board.changeTurns();
        ArrayList<Move> best = new ArrayList<>();
        ArrayList<Integer> bestGrades = new ArrayList<>();
        ArrayList<IntPoint> possibleMoves = board.moveLocations(color);

        bestGrades.add(Integer.MAX_VALUE);
        best.add(null);
        int tempGrade;
        for(int p = 0; p < possibleMoves.size();p++)
        {
            for(int s = 0; s < availableShapes.size();s++)
            {
                for(int x = 0; x <= 3; x++)
                {
                    unflippedMove = new Move(s, false, x, possibleMoves.get(p));
                    if(board.isValidMove(unflippedMove, color))
                    {
                        System.out.println("CT");
                        tempGrade = availableShapes.get(s).getSquareCount()*(gradeMove(unflippedMove,board)+1);
                        if(best.size()<=count){
                            bestGrades.add(tempGrade);
                            best.add(unflippedMove);
                        }else if(tempGrade>bestGrades.get(bestGrades.size()-1)){
                            for(int i=count;i>=0;i--){
                                if(tempGrade<bestGrades.get(i)){
                                    bestGrades.add(i+1, tempGrade);
                                    best.add(i+1, unflippedMove);
                                    best.remove(count+1);
                                    bestGrades.remove(count+1);
                                }
                            }
                        }
                    }
                    flippedMove = new Move(s, true, x, possibleMoves.get(p));
                    if(board.isValidMove(flippedMove, color))
                    {
                        System.out.println("CT");
                        tempGrade = availableShapes.get(s).getSquareCount()*(gradeMove(flippedMove,board)+1);
                        if(best.size()<=count){
                            bestGrades.add(tempGrade);
                            best.add(flippedMove);
                        }else if(tempGrade>bestGrades.get(bestGrades.size()-1)){
                            for(int i=count;i>=0;i--){
                                if(tempGrade<bestGrades.get(i)){
                                    bestGrades.add(i+1, tempGrade);
                                    best.add(i+1, flippedMove);
                                    best.remove(count+1);
                                    bestGrades.remove(count+1);
                                }
                            }
                        }
                    }
                }
            }
        }
        best.remove(0);
        bestGrades.remove(0);

        Move[] ret = new Move[count];
        System.out.println("C: " + count + " " + ret.length + " " + best.size());
        System.out.println(best);
        for(int i=0;i<best.size()&&i<ret.length;i++){
            ret[i] = best.get(i);
        }
        if(changeTurns) board.changeTurns();
        return ret;
    }


    public int gradeMove(Move move, BlokusBoard board)
    {
        int previousOpponentMoves, laterOpponentMoves, movesLostByOpponent = 0, previousMoves, laterMoves, movesGained = 0;
        if(getColor() == board.ORANGE)
        {
            previousMoves = board.getOrangeMoveLocations().size();
            previousOpponentMoves = board.getPurpleMoveLocations().size();
            board.makeMove(move, getColor());
            laterMoves = board.getOrangeMoveLocations().size();
            laterOpponentMoves = board.getPurpleMoveLocations().size();
            movesGained = laterMoves - previousMoves;
            movesLostByOpponent = laterOpponentMoves - previousOpponentMoves;
            board.undoMovePiece(move,getColor());
        }
        else if(getColor() == board.PURPLE)
        {
            previousMoves = board.getPurpleMoveLocations().size();
            previousOpponentMoves = board.getOrangeMoveLocations().size();
            board.makeMove(move, getColor());
            laterMoves = board.getPurpleMoveLocations().size();
            laterOpponentMoves = board.getOrangeMoveLocations().size();
            movesGained = laterMoves - previousMoves;
            movesLostByOpponent = laterOpponentMoves - previousOpponentMoves;
            board.undoMovePiece(move,getColor());
        }
        return movesGained + movesLostByOpponent;
    }
    public Player freshCopy() {
        return new Crandrake(getColor(), getName());
    }
}
