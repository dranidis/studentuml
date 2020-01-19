
import java.util.*;

public class Register {

  // Generated Attributes
  private Sale sale;
  private ProductCatalog pc;


  //Methods

  //Generated Method
  public void makePayment() {

    // Generated called Methods
    sale.makePayment();
  }

  //Generated Method
  public Register(ProductCatalog pc) {

    //Generated constructor setter
    this.pc = pc;
  }

  //Generated Method
  public void makeNewSale() {

    // Generated called Methods
    sale = new Sale();
    sale.createSaleLine();
  }

  //Generated Method
  public void enterItem(int id, int qty) {

    // Generated called Methods
    ProductSpecification spec = pc.getSpecification(id);
    sale.makeLineItem(spec,qty);
  }

//end of class Register
}