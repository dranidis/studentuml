
import java.util.*;

public class Sale {

  // Generated Attributes
  private String date;
  private boolean isComplete;
  private int time;
  private int s;
  private List<SalesLineItem> salesLine;
  private int t;


  //Methods

  //Generated Method
  public void makePayment() {

    // Generated called Methods
    Payment p = new Payment();
    for(SalesLineItem obj : salesLine) {
     t = obj.getTotal();
    }
    p.payTotal(t);
  }

  //Generated Method
  public Sale() {
  }

  //Generated Method
  public void createSaleLine() {

    // Generated called Methods
    salesLine = new ArrayList<SalesLineItem>();
  }

  //Generated Method
  public void makeLineItem(ProductSpecification spec, int qty) {

    // Generated called Methods
    SalesLineItem sl = new SalesLineItem();
    salesLine.add(sl);
    int price = spec.getPrice();
    sl.setTotal(price);
    sl.setQuantity(qty);
  }

//end of class Sale
}