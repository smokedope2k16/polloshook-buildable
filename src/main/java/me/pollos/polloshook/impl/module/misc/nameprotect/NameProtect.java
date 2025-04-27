package me.pollos.polloshook.impl.module.misc.nameprotect;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.StringValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.misc.nameprotect.mode.SpoofSkinMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.SkinTextures.Model;

public class NameProtect extends ToggleableModule {
   protected final StringValue replacement = new StringValue("uglyN1gga", new String[]{"name", "replace", "user"});
   protected final EnumValue<SpoofSkinMode> spoofSkin;
   protected final Value<Boolean> customSkin;
   protected final StringValue skinName;
   protected final EnumValue<Model> model;

   public NameProtect() {
      super(new String[]{"NameProtect", "nickhider", "media"}, Category.MISC);
      this.spoofSkin = new EnumValue(SpoofSkinMode.OFF, new String[]{"SpoofSkin", "skin"});
      this.customSkin = (new Value(false, new String[]{"CustomSkin", "cskin"})).setParent(this.spoofSkin, SpoofSkinMode.SELF, true);
      this.skinName = (new StringValue("steve", new String[]{"Label", "nick", "location"})).setParent(this.customSkin);
      this.model = (new EnumValue(Model.WIDE, new String[]{"Model", "mod"})).setParent(this.customSkin);
      this.offerValues(new Value[]{this.replacement, this.spoofSkin, this.customSkin, this.skinName, this.model});
      this.offerListeners(new Listener[]{new ListenerSkin(this)});
   }

   public static String nameProtect(String text) {
      return Managers.getModuleManager() != null && ((NameProtect)Managers.getModuleManager().get(NameProtect.class)).isEnabled() && text.contains(MinecraftClient.getInstance().getSession().getUsername()) ? text.replace(EntityUtil.getName(mc.player), (CharSequence)((NameProtect)Managers.getModuleManager().get(NameProtect.class)).getReplacement().getValue()) : text;
   }

   
   public StringValue getReplacement() {
      return this.replacement;
   }

   
   public EnumValue<SpoofSkinMode> getSpoofSkin() {
      return this.spoofSkin;
   }

   
   public Value<Boolean> getCustomSkin() {
      return this.customSkin;
   }

   
   public StringValue getSkinName() {
      return this.skinName;
   }

   
   public EnumValue<Model> getModel() {
      return this.model;
   }
}
