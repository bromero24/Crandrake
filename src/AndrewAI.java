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

        for(IntPoint p : board.moveLocations(getColor()))
        {
            //Taken from BigMoverAI, stores which pieces have been used already
            boolean[] used = (getColor()==BlokusBoard.ORANGE)?board.getOrangeUsedShapes():board.getPurpleUsedShapes();
            for(int x=0; x<used.length; x++)
            {
                if(!used[x])
                {
                    System.out.print(possibleMoves(board, x, p));
                }
            }
        }

        return move;
    }

    public ArrayList<Move> possibleMoves(BlokusBoard board, int pieceNumber, IntPoint position)
    {
        ArrayList<Move> moves = new ArrayList<Move>();

        boolean[] used = (getColor()==BlokusBoard.ORANGE)?board.getOrangeUsedShapes():board.getPurpleUsedShapes();
        for(int x = 0;x<used.length;x++)
        {
            if(pieceNumber == x)
            {
                Shape shape = board.getShapes().get(x);
                int pieceWidth = shape.original()[0].length;
                int pieceHeight = shape.original().length;

                for(int flip = 0;flip<2;flip++)
                {
                    for(int rotation = 0;rotation < 4;rotation++)
                    {
                        for(int r = 0;r<pieceHeight;r++)
                        {
                            for(int c = 0;c<pieceWidth;c++)
                            {
                                Move temp =  new Move(pieceNumber, flip%2==0?false:true, rotation, new IntPoint(position.getX()+c, position.getY()+r));
                                if(board.isValidMove(temp, getColor()))
                                    moves.add(temp);
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }


    /**
     * Returns the number of corners after a move is made. TODO: Improve by making some corners worth more than others
     *
     * @param board the current state of the board
     * @param m     the move to make
     * @return the number of usable corners available to the AI
     */
    public int getCorners(BlokusBoard board, Move m)
    {
        int corners = 0;

        board.makeMove(m, getColor());

        for(int c = 0; c < board.numCols(); c++)
        {
            for(int r = 0; r < board.numRows(); r++)
            {
                if(board.notOrthogonalToSelf(c, r, getColor()) && board.diagonalToColor(c, r, getColor()))
                {
                    corners++;
                }
            }
        }

        board.undoMovePiece(m, getColor());

        return corners;
    }

    /**
     * TODO: Count the leaks and give different leaks different values
     *
     * @param board the current state of the board
     * @param m     the move to make
     * @return the number of leaks a move would make
     */
    public int countLeaks(BlokusBoard board, Move m)
    {
        return 0;
    }

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
