
import java.util.*;

public class ProductCatalog {

  // Generated Attributes
  private int id;
  private int price;
  private String description;
  private List<ProductSpecification> specifications;


  //Methods

  //Generated Method
  public ProductCatalog() {

    // Generated called Methods
    specifications = new ArrayList<ProductSpecification>();
    this.loadProdSpecs();
    ProductSpecification ps = new ProductSpecification(id,price,description);
    specifications.add(ps);
  }

  //Generated Method
  public void loadProdSpecs() {
  }

  //Generated Method
  public ProductSpecification getSpecification(int id) {

    // Generated called Methods
    ProductSpecification spec = specifications.get(id);
    // Generated Return
    return null;
  }

  //Generated Method
  public void createProdSpecs() {
  }

//end of class ProductCatalog
}