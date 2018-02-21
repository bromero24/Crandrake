import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;


public class Crandrake extends Player {
    
    public Crandrake(int color, String name) {
        super(color, name);
    }

    public Move getMove(BlokusBoard board) {

    }

    public Player freshCopy() {
        return new BigMoverAI(getColor(), getName());
    }
}
