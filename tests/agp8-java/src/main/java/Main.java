import java.util.ArrayList;

public class Main {
  public static void main(String[] args) {
    ArrayList<String> list = new ArrayList<String>();
    list.add("foo");
    // This is in android.jar so it compiles fine
    list.removeFirst();
  }
}
