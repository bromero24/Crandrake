public class AndrewAI extends Player
{

    public AndrewAI(int color, String name)
    {
        super(color, name);
    }



    @Override
    public Move getMove(BlokusBoard board)
    {
        return null;
    }


    @Override
    /**
     * Returns a clone of the player
     * @return a clone of this player
     */
    public Player freshCopy()
    {
        return new AndrewAI(getColor(),getName());
    }
}
