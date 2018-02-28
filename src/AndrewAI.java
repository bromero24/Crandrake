import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class AndrewAI extends Player
{
    public static final int LOOK_AHEAD_MOVES = 3;
    private int lookingAhead = 0;

    public final static int MAX_DEPTH = 4;
    public final static int STARTING_BRANCHES = 12;
    public final static int BRANCH_DECAY = 2;

    public final static int AGGREGATE_SCORE = 0;
    public final static int SINGLE_SCORE = 1;

    ArrayList<Shape> availableShapes;

    private int scoringMethod = SINGLE_SCORE;

    public AndrewAI(int color, String name)
    {
        super(color, name);
        availableShapes = BlokusBoard.getShapes();
    }

    public Move getMove(BlokusBoard board) {
        //Look ahead
        Stack<Move> moves= new Stack<>();
        Stack<Integer> depth = new Stack<>();

        Move[] b = getBestMoves(STARTING_BRANCHES, board);
        if(b!=null) for(Move i : b){
            if(i==null) break;
            moves.push(i);
            depth.push(0);
        }

        int bestGrade = Integer.MIN_VALUE;
        Move best = null, cMove = null;

        int rDepth = 0, tGrade, aGrade = Integer.MIN_VALUE;
        do{
            System.out.println("RUN");
            if(rDepth>depth.peek()){
                //IF GOING UP
                //UNMOVE PIECE
                board.undoMovePiece(moves.pop(), board.getTurn());
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
                board.makeMove(moves.peek(),board.getTurn());
                board.changeTurns();
                Move[] m = getBestMoves(STARTING_BRANCHES-(BRANCH_DECAY*(rDepth/2)), board);
                for(Move i : m){
                    if(i==null) break;
                    moves.push(i);
                    depth.push(rDepth+1);
                    //EVEN PUSHES
                }
            }else{
                //AT MAX DEPTH
                tGrade = gradeBoard(board);
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
        availableShapes.remove(board.getShapes().get(best.getPieceNumber()));
        return best;
    }

    public Move[] getBestMoves(int count, BlokusBoard board){
        ArrayList<Move> allMoves = new ArrayList<>();
        Move[] ret = new Move[count];
        for(int i=0;i<availableShapes.size();i++){
            int index = 0;
            for(;index<availableShapes.size();index++){
                if(board.getShapes().get(index)==availableShapes.get(i)){
                    for(int r=0;r<board.getBoard().length;r++){
                        for(int c=0;c<board.getBoard()[r].length;c++){
                            allMoves.addAll(possibleMoves(board, index, new IntPoint(c, r)));
                        }
                    }

                }
            }

        }
        ArrayList<Move> best = new ArrayList<>();
        ArrayList<Integer> bestGrades = new ArrayList<>();
        int g;
        bestGrades.add(Integer.MAX_VALUE);
        for(int i=0;i<allMoves.size();i++){
            board.makeMove(allMoves.get(i), board.getTurn());
            g=gradeBoard(board);
            board.undoMovePiece(allMoves.get(i), board.getTurn());
            for(int j=best.size();j>0;j--){
                if(g<bestGrades.get(i)){
                    best.add(i, allMoves.get(i));
                    bestGrades.add(i+1, g);
                }
            }
            if(best.size()>count){
                best.remove(count);
                bestGrades.remove(count+1);
            }
        }
        return null;
    }

    /**
     * @param board - the board that a move should be made on
     * @return the best move that can be made
     */
    public Move getMoveSecondary(BlokusBoard board)
    {
        Move move = null;
        int maxScore = 0;

        for (int x1 = 0; x1 < board.moveLocations(board.getTurn()).size(); x1++)
        {
            IntPoint p = board.moveLocations(board.getTurn()).get(x1);
            //Taken from BigMoverAI, stores which pieces have been used already
            boolean[] used = (board.getTurn() == BlokusBoard.ORANGE) ? board.getOrangeUsedShapes() : board.getPurpleUsedShapes();
            for (int x = 0; x < used.length; x++)
            {
                if (!used[x])
                {
                    ArrayList<Move> moves = possibleMoves(board, x, p);

                    for (Move m : moves)
                    {
                        board.makeMove(m, board.getTurn());
                        board.changeTurns();
                        int score = gradeBoard(board);
                        if (score > maxScore)
                        {
                            move = m;
                            maxScore = score;
                        }
                        board.undoMovePiece(m, board.getTurn());
                    }
                }
            }
        }

        if (move == null) //copied from random AI
        {
            ArrayList<IntPoint> avaiableMoves = board.moveLocations(board.getTurn());
            Collections.shuffle(avaiableMoves);
            ArrayList<Integer> usableShapePositions = new ArrayList<>();
            boolean[] used = (board.getTurn() == BlokusBoard.ORANGE) ? board.getOrangeUsedShapes() : board.getPurpleUsedShapes();
            for (int x = 0; x < used.length; x++)
                if (!used[x])
                    usableShapePositions.add(x);
            Collections.shuffle(usableShapePositions);
            if (usableShapePositions.isEmpty() || avaiableMoves.isEmpty())
                return null;
            else
            {
                for (int x = 0; x < avaiableMoves.size(); x++)
                {
                    IntPoint movLoc = avaiableMoves.get(x);
                    for (Integer position : usableShapePositions)
                    {
                        for (int i = 0; i < 8; i++)
                        {
                            boolean flip = i > 3;
                            int rotation = i % 4;
                            boolean[][] shape = board.getShapes().get(position).manipulatedShape(flip, rotation);
                            for (int r = -shape.length + 1; r < shape.length; r++)
                            {
                                for (int c = -shape[0].length + 1; c < shape[0].length; c++)
                                {
                                    IntPoint topLeft = new IntPoint(movLoc.getX() + c, movLoc.getY() + r);
                                    Move test = new Move(position, flip, rotation, topLeft);
                                    if (board.isValidMove(test, board.getTurn()))
                                    {
                                        System.out.println("MADE RANDOM MOVE");
                                        move = test;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

//        if(lookingAhead >= LOOK_AHEAD_MOVES)
//        {
//            return move;
//        }
//
//        board.makeMove(move,board.getTurn());
//        board.changeTurns();
//        lookingAhead++;
//
//        move = getMove(board);
//
//        board.undoMovePiece(move,board.getTurn());
//        board.changeTurns();
//        lookingAhead--;

        return move;
    }


    /**
     * @param board the current state of the board
     * @param r     the row to grade
     * @param c     the column to grade
     * @return the grade of the square
     * <p>
     * TODO: Figure out grading values
     */
    private int gradeSquare(BlokusBoard board, int r, int c)
    {
        int value = 0;

        //if either player has no moves

        if (board.getBoard()[r][c] == BlokusBoard.EMPTY)
        {
            if (!board.notOrthogonalToSelf(c, r, board.getTurn()) && !board.notOrthogonalToSelf(c, r, board.getTurn() == getColor() ? getOtherColor() : getColor())) // true if touching both
            {
                value -= 10;
            }
            else if (!board.notOrthogonalToSelf(c, r, board.getTurn())) // true if touching self TODO: Bigger leaks are worse for us
            {
                value -= 90;
                if (board.diagonalToColor(c, r, board.getTurn() == getColor() ? getOtherColor() : getColor())) // true if square is playable for opponent
                {
                    value -= 50;
                }
            }
            else if (!board.notOrthogonalToSelf(c, r, board.getTurn() == getColor() ? getOtherColor() : getColor())) // true if touching opponent TODO: Bigger leaks are better for us
            {
                value += 100;
                if (board.diagonalToColor(c, r, board.getTurn())) // true if square is playable for us
                {
                    value += 60;
                }
            }
            else if (board.notOrthogonalToSelf(c, r, board.getTurn()) && board.notOrthogonalToSelf(c, r, board.getTurn() == getColor() ? getOtherColor() : getColor())) // true if touching neither
            {
                if (board.diagonalToColor(c, r, board.getTurn() == getColor() ? getOtherColor() : getColor()) && board.diagonalToColor(c, r, board.getTurn())) // true if square is playable for both
                {
                    value -= 10;
                }
                else if (board.diagonalToColor(c, r, board.getTurn() == getColor() ? getOtherColor() : getColor())) // true if square is playable for opponent
                {
                    value -= 70;
                }
                else if (board.diagonalToColor(c, r, board.getTurn())) // true if square is playable for us
                {
                    value += 80;
                }
            }
//            TODO: Reserved
//            if (/*reserved*/ true)
//            {
//                //increases the value. Multiplier is subject to change. Reserved is a very good score.
//                value *= 2;
//            }
        }
        else if (board.getBoard()[r][c] == board.getTurn())
        {
            value += 170;

            if (r > 5 && r < 10 && c > 5 && c < 10) //piece in center
            {
                value += 120;
            }
            else if (r < 5 && c < 5 || r < 5 && c > 10 || r > 10 && c < 5 || r > 10 && c > 10)
            {
                value -= 60;
            }
        }
        else if (board.getBoard()[r][c] != board.getTurn())
        {
            value -= 160;

            if (r > 5 && r < 10 && c > 5 && c < 10) //piece in center
            {
                value -= 110;
            }
            else if (r < 5 && c < 5 || r < 5 && c > 10 || r > 10 && c < 5 || r > 10 && c > 10)
            {
                value += 50;
            }
        }
        return value;
    }

    /*
    private int gradeBoard(BlokusBoard board)
    {
        int grade = 0;
        int[][] b = board.getBoard();

        for (int r = 0; r < b.length; r++)
        {
            for (int c = 0; c < b[0].length; c++)
            {
                grade += gradeSquare(board, r, c);
            }
        }

        int count = 0;

        if (board.getTurn() == getColor())
        {
            board.changeTurns();
            count++;
        }

        if (board.moveLocations(board.getTurn()).size() == 0) // always places if it stops opponent from moving
        {
            grade = Integer.MAX_VALUE;
        }

        if (board.getTurn() != getColor())
        {
            board.changeTurns();
            count++;
        }

        if (board.moveLocations(board.getTurn()).size() == 0) // never places if it stops us from moving
        {
            grade = Integer.MIN_VALUE;
        }

        if (board.getTurn() == getColor())
        {
            board.changeTurns();
            count++;
        }

        if (board.moveLocations(board.getTurn()).size() <= 3) // better move the less places the opponent can move
        {
            grade += 2400 - board.moveLocations(board.getTurn() == getColor() ? getOtherColor() : getColor()).size() * 800;
        }

        if (board.getTurn() != getColor())
        {
            board.changeTurns();
            count++;
        }

        if (board.moveLocations(board.getTurn()).size() <= 3) // worse move the less places we have to move
        {
            grade -= 2400 - board.moveLocations(board.getTurn()).size() * 800;
        }

        if (board.getTurn() == getColor() && count < 4)
        {
            board.changeTurns();
        }

        return grade;
    }
    */

    private int gradeBoard(BlokusBoard board){
        int grade = 0;
        for(int i=0;i<board.getBoard().length;i++){
            for(int j=0;j<board.getBoard()[i].length;j++){
                grade+=gradeSquare(board, i, j);
            }
        }
        return grade;
    }

    /**
     * @param board the current state of the board
     * @return a 2d boolean array that stores true for all reserved
     */
    public boolean[][] findReserved(BlokusBoard board)
    {
        return null;
    }

    public ArrayList<Move> possibleMoves(BlokusBoard board, int pieceNumber, IntPoint position)
    {
        ArrayList<Move> moves = new ArrayList<>();

        boolean[] used = (board.getTurn() == BlokusBoard.ORANGE) ? board.getOrangeUsedShapes() : board.getPurpleUsedShapes();
        for (int x = 0; x < used.length; x++)
        {
            if (pieceNumber == x)
            {
                Shape shape = board.getShapes().get(x);
                int pieceWidth = shape.original()[0].length;
                int pieceHeight = shape.original().length;

                for (int flip = 0; flip < 2; flip++)
                {
                    for (int rotation = 0; rotation < 4; rotation++)
                    {
                        for (int r = -pieceHeight + 1; r < pieceHeight; r++)
                        {
                            for (int c = -pieceWidth + 1; c < pieceWidth; c++)
                            {
                                Move temp = new Move(pieceNumber, flip % 2 != 0, rotation, new IntPoint(position.getX() + c, position.getY() + r));
                                if (board.isValidMove(temp, board.getTurn()))
                                {
                                    moves.add(temp);
                                }
                            }
                        }
                    }
                }
            }
        }

        return moves;
    }

    //  Planning on incorporating these ideas into gradeSquare(). May come back to this
//    /**
//     * Returns the number of corners after a move is made. TODO: Improve by making some corners worth more than others
//     *
//     * @param board the current state of the board
//     * @param m     the move to make
//     * @return the number of usable corners available to the AI
//     */
//    public int getCorners(BlokusBoard board, Move m)
//    {
//        int corners = 0;
//
//        board.makeMove(m, getColor());
//
//        for(int c = 0; c < board.numCols(); c++)
//        {
//            for(int r = 0; r < board.numRows(); r++)
//            {
//                if(board.notOrthogonalToSelf(c, r, getColor()) && board.diagonalToColor(c, r, getColor()))
//                {
//                    corners++;
//                }
//            }
//        }
//
//        board.undoMovePiece(m, getColor());
//
//        return corners;
//    }
//
//    /**
//     * TODO: Count the leaks and give different leaks different values
//     *
//     * @param board the current state of the board
//     * @param m     the move to make
//     * @return the number of leaks a move would make
//     */
//    public int countLeaks(BlokusBoard board, Move m)
//    {
//        return 0;
//    }

    @Override
    /**
     * Returns a clone of the player
     * @return a clone of this player
     */
    public Player freshCopy()
    {
        return new AndrewAI(getColor(), getName());
    }
}