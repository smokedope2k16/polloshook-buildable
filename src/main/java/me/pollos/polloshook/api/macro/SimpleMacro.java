package me.pollos.polloshook.api.macro;

import java.util.Objects;
import me.pollos.polloshook.api.interfaces.Labeled;
import me.pollos.polloshook.api.util.obj.AbstractSendable;
import me.pollos.polloshook.api.util.obj.MessageSender;

public class SimpleMacro extends AbstractSendable implements Labeled {
   private final String label;
   private final int key;
   private final MessageSender chatMacro;
   private boolean paused = false;

   public SimpleMacro(String label, int key, MessageSender chatMacro) {
      this.label = label;
      this.key = key;
      this.chatMacro = chatMacro;
   }

   public String getLabel() {
      return this.label;
   }

   public int getKey() {
      return this.key;
   }

   public MessageSender getChatMacro() {
      return this.chatMacro;
   }

   public boolean isPaused() {
      return this.paused;
   }

   public void setPaused(boolean paused) {
      this.paused = paused;
   }

   public void send() {
      this.chatMacro.send();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      SimpleMacro that = (SimpleMacro) o;
      return key == that.key && paused == that.paused && Objects.equals(label, that.label) && Objects.equals(chatMacro, that.chatMacro);
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), label, key, chatMacro, paused);
   }

   @Override
   public String toString() {
      return "SimpleMacro(label=" + label + ", key=" + key + ", chatMacro=" + chatMacro + ", paused=" + paused + ")";
   }
}