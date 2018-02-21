public class CrandrakeAI extends Player{

    public CrandrakeAI(int color, String name){
        super(color, name);
    }

    public Move getMove(BlokusBoard initial){

    }

    /**
     *
     * @param board The board the moves will be extrapolated from
     * @param top The top however many moves will be returned
     * @return The top however many moves will be 
     */
    public Move[] getMoves(BlokusBoard board, int top){

    }

    public CrandrakeAI freshCopy(){
        return new CrandrakeAI(getColor(), getName());
    }

}
