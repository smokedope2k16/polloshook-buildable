package me.pollos.polloshook.api.value.value;


import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.impl.manager.internal.CommandManager;

public class StringValue extends Value<String> {
   private String typingStr;
   private boolean typingObserver = false;

   public StringValue(String value, String... names) {
      super(value, names);
   }

   public StringValue setParent(Value<Boolean> parent) {
      super.setParent(parent);
      return this;
   }

   public StringValue setParent(Value<Boolean> parent, boolean opposite) {
      super.setParent(parent, opposite);
      return this;
   }

   public String returnValue(String[] args) {
      Module mod = Managers.getCommandManager().getModule(args);
      String str = String.join(" ", args);
      str = str.replace(args[0] + " ", "").replace(args[1] + " ", "");
      this.setValue(str);
      return CommandManager.setMessage(mod, this, this.getValue());
   }

   
   public String getTypingStr() {
      return this.typingStr;
   }

   
   public StringValue setTypingStr(String typingStr) {
      this.typingStr = typingStr;
      return this;
   }

   
   public boolean isTypingObserver() {
      return this.typingObserver;
   }

   
   public StringValue setTypingObserver(boolean typingObserver) {
      this.typingObserver = typingObserver;
      return this;
   }
}
