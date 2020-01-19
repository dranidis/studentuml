
import java.util.*;

public class Player {

  // Generated Attributes
  private int fv;
  private Board board;
  private Player p;
  private Collection<Die> dice;


  //Methods

  //Generated Method
  public boolean takeTurn() {

    // Generated called Methods
    board = new Board(west,why);
    for(Die obj : dice) {
     obj.roll();
    }
    for(Die obj : dice) {
     fv = obj.getFV();
    }
    Square loc = board.getSquare(loc,fv);
    loc.landedOn(p);
    // Generated Return
    return false;
  }

//end of class Player
}