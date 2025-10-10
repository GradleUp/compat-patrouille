import java.util.ArrayList;
public class Main {
  public static void main(String[] args) {

    ArrayList<String> list = new ArrayList<String>();
    list.add("foo");
    // This must fail to compile because it's not in JDK 11
    // list.removeFirst();

  }
}
