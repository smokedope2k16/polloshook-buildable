package me.pollos.polloshook.impl.module.misc.middleclick.action.core;


import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.module.misc.middleclick.MiddleClick;
import me.pollos.polloshook.impl.module.misc.middleclick.action.actiontype.ActionType;

public abstract class MiddleClickAction extends MiddleClick implements IMiddleClickAction, Minecraftable {
   private final ActionType type;
   private Value<Boolean> parent;

   public void execute() {
      if (this.check()) {
         this.run();
      }

   }

   
   public ActionType getType() {
      return this.type;
   }

   
   public Value<Boolean> getParent() {
      return this.parent;
   }

   
   public MiddleClickAction(ActionType type) {
      this.type = type;
   }

   
   public MiddleClickAction setParent(Value<Boolean> parent) {
      this.parent = parent;
      return this;
   }
}
