package me.pollos.polloshook.impl.command.util;

import me.pollos.polloshook.api.command.core.Command;
import net.minecraft.client.tutorial.TutorialStep;

public class TutorialStepCommand extends Command {
   public TutorialStepCommand() {
      super(new String[]{"TutorialStep", "tutorial"});
   }

   public String execute(String[] args) {
      mc.getTutorialManager().setStep(TutorialStep.CRAFT_PLANKS);
      return "Ended tutorial";
   }
}
