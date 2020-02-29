
import java.util.*;

public class Store {

  // Generated Attributes
  private Register register;
  private ProductCatalog pc;
  private List<Sale> sales;


  //Methods

  //Generated Method
  public void createStore() {

    // Generated called Methods
    pc = new ProductCatalog();
    register = new Register(pc);
  }

  //Generated Method
  public Register getRegister() {
    //Generated getter
    return this.register;
  }

//end of class Store
}