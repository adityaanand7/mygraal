public class CompilationTest {
  public static int hash(Object input) {
    if (input instanceof Exception) {
      return System.identityHashCode(input);
    } else {
      return input.hashCode();
    }
  }
  public static void main(String[] args) throws InterruptedException {
    for (int i = 0; i < 500000; i++) {
      hash(i);
    }
    Thread.sleep(2000);
  }
}

