import java.io.InputStreamReader;
import java_cup.runtime.Symbol;

public class Scanner {
  private Lexer lex = null;

  public Scanner() {
    lex = new Lexer(new InputStreamReader(System.in));
  }

  public Symbol getNextToken() throws java.io.IOException {
    return lex.next_token();
  }

  public static void main(String[] argv) {
    try {
      Scanner scanner = new Scanner();
      Symbol token = null;
      while((token = scanner.getNextToken()) != null) {
        String symbolName = sym.terminalNames[token.sym];
        String loc = "[" + token.left + ":" + token.right + "]";
          if (symbolName.equals("ID") || symbolName.equals("NUM")) {
            System.out.println(loc + "\t" + symbolName + "(" + token.value + ")");
          } else {
            System.out.println(loc + "\t" + symbolName);
          }
      }
    }
    catch (Exception e) {
      System.out.println("Unexpected exception:");
      e.printStackTrace();
    }
  }
}
