import java.util.ArrayList;

public class AndrewAI extends Player
{

    public AndrewAI(int color, String name)
    {
        super(color, name);
    }


    @Override
    /**
     *
     */
    public Move getMove(BlokusBoard board)
    {
        Move move = null;
        int maxScore = 0;
        int pieceSize = 0;

        for (IntPoint p : board.moveLocations(getColor()))
        {
            //Taken from BigMoverAI, stores which pieces have been used already
            boolean[] used = (getColor() == BlokusBoard.ORANGE) ? board.getOrangeUsedShapes() : board.getPurpleUsedShapes();
            for (int x = 0; x < used.length; x++)
            {
                if (!used[x])
                {
                    ArrayList<Move> moves = possibleMoves(board, x, p);
                    for (Move m : moves)
                    {
                        board.makeMove(m, getColor());
                        int score = gradeBoard(board);
                        if (score - maxScore > 500)
                        {
                            move = m;
                            maxScore = score;
                            pieceSize = board.getShapes().get(x).getSquareCount();
                        }
                        else if(score > maxScore)
                        {

                        }
                        board.undoMovePiece(m, getColor());
                    }
                }
            }
        }

        return move;
    }

    /**
     * @param board the current state of the board
     * @param r     the row to grade
     * @param c     the column to grade
     * @return the grade of the square
     * <p>
     * TODO: Figure out grading values. Might add instead of multiplying
     */
    public int gradeSquare(BlokusBoard board, int r, int c)
    {
        int value = 100;
        if (board.getBoard()[r][c] == BlokusBoard.EMPTY)
        {
            if (board.notOrthogonalToSelf(c, r, getColor()) && board.notOrthogonalToSelf(c, r, getOtherColor())) // true if touching both
            {
                //square has no value
                value = 0;
                return value;
            }
            else if (board.notOrthogonalToSelf(c, r, getColor())) // true if touching self TODO: Bigger leaks are worse for us
            {
                //value becomes negative
                value *= -1;
                if (board.diagonalToColor(c, r, getOtherColor())) // true if square is playable for opponent
                {
                    //decreases the value. Multiplier is subject to change
                    value *= 1.75;
                }
            }
            else if (board.notOrthogonalToSelf(c, r, getOtherColor())) // true if touching opponent TODO: Bigger leaks are better for us
            {
                //increases the value. Multiplier is subject to change
                value *= 1.25;

                if (board.diagonalToColor(c, r, getColor())) // true if square is playable for self
                {
                    //increases the value. Multiplier is subject to change
                    value *= 2;
                }
            }
            else if (board.diagonalToColor(c, r, getColor()) && board.diagonalToColor(c, r, getOtherColor())) // true if contested
            {
                //TODO: Figure out if this is good or bad
            }
//            TODO: Reserved
//            if (/*reserved*/ true)
//            {
//                //increases the value. Multiplier is subject to change. Reserved is a very good score.
//                value *= 2;
//            }
        }
        else if (board.getBoard()[r][c] == getColor())
        {
            value *= 1.2;
        }
        else if (board.getBoard()[r][c] != getColor())
        {
            value *= .8;
        }
        return value;
    }

    public int gradeBoard(BlokusBoard board)
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
        System.out.println(grade);
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
        ArrayList<Move> moves = new ArrayList<Move>();

        boolean[] used = (getColor() == BlokusBoard.ORANGE) ? board.getOrangeUsedShapes() : board.getPurpleUsedShapes();
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
                                System.out.println(r + "\t" + c);
                                Move temp = new Move(pieceNumber, flip % 2 == 0 ? false : true, rotation, new IntPoint(position.getX() + c, position.getY() + r));
                                if (board.isValidMove(temp, getColor()))
                                {
                                    System.out.println("ADDED");
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
