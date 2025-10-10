import java.util.ArrayList;

class Foo {
  void main() {
    ArrayList<String> list = new ArrayList<String>();
    list.add("foo");
    // Uncommenting this should fail to compile
    // list.removeFirst();
  }
}
