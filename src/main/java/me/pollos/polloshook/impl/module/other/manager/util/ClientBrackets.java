package me.pollos.polloshook.impl.module.other.manager.util;

public enum ClientBrackets {
   NONE(new String[]{"", ""}),
   CARET(new String[]{"<", ">"}),
   PARENTHESES(new String[]{"(", ")"}),
   BRACKET(new String[]{"[", "]"}),
   CURLY(new String[]{"{", "}"});

   final String[] bracket;

   private ClientBrackets(String[] bracket) {
      this.bracket = bracket;
   }

   public String[] getBrackets() {
      return this.bracket;
   }

   // $FF: synthetic method
   private static ClientBrackets[] $values() {
      return new ClientBrackets[]{NONE, CARET, PARENTHESES, BRACKET, CURLY};
   }
}
