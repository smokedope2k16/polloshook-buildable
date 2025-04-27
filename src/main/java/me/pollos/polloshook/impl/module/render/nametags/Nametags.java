package me.pollos.polloshook.impl.module.render.nametags;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.network.NetworkUtil;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.minecraft.render.utils.FontUtil;
import me.pollos.polloshook.api.minecraft.world.EnchantUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.manager.friend.Friend;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.other.irc.IrcModule;
import me.pollos.polloshook.impl.module.render.nametags.mode.UserSymbolMode;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.ColorHelper.Argb;
import org.joml.Matrix4f;

public class Nametags extends ToggleableModule {
   private final NumberValue<Float> scale = new NumberValue(0.3F, 0.1F, 1.0F, 0.1F, new String[]{"Scale", "scaling"});
   private final Value<Boolean> self = new Value(false, new String[]{"Self", "me", "you", "avery4"});
   private final Value<Boolean> armor = new Value(true, new String[]{"Armor", "armour"});
   private final Value<Boolean> enchants = new Value(true, new String[]{"Enchantments", "enchants"});
   private final Value<Boolean> lowercaseEnchants;
   private final Value<Boolean> simplify;
   private final Value<Boolean> simplifySwords;
   private final Value<Boolean> durability;
   private final Value<Boolean> heldItem;
   private final Value<Boolean> health;
   private final Value<Boolean> pops;
   private final Value<Boolean> id;
   private final Value<Boolean> gameMode;
   private final Value<Boolean> ping;
   private final Value<Boolean> sneak;
   private final Value<Boolean> burrow;
   private final Value<Boolean> bordered;
   private final Value<Boolean> outline;
   private final Value<Boolean> holeColor;
   private final Value<Boolean> syncBorder;
   private final Value<Boolean> borderedFriend;
   private final EnumValue<UserSymbolMode> userSymbol;
   private final Value<Boolean> useFriendAlias;
   protected boolean ignore;
   protected List<PlayerEntity> playerEntities;

   public Nametags() {
      super(new String[]{"Nametags", "nametag"}, Category.RENDER);
      this.lowercaseEnchants = (new Value(true, new String[]{"Lowercase"})).setParent(this.enchants);
      this.simplify = (new Value(true, new String[]{"Simplify", "clean"})).setParent(this.enchants);
      this.simplifySwords = (new Value(true, new String[]{"SimplifySwords", "simpleswords"})).setParent(this.simplify);
      this.durability = new Value(true, new String[]{"Durability", "dura"});
      this.heldItem = new Value(true, new String[]{"HeldItemName", "itemstack"});
      this.health = new Value(true, new String[]{"Health", "hp"});
      this.pops = new Value(true, new String[]{"TotemPops", "pops", "totem"});
      this.id = new Value(false, new String[]{"ID", "eid", "entityid"});
      this.gameMode = new Value(false, new String[]{"GameMode", "gmd", "game"});
      this.ping = new Value(true, new String[]{"Ping", "latency"});
      this.sneak = new Value(true, new String[]{"Sneak", "sneaking"});
      this.burrow = new Value(true, new String[]{"Burrowed", "burrow"});
      this.bordered = new Value(false, new String[]{"Bordered", "border"});
      this.outline = new Value(false, new String[]{"Outline", "outliner"});
      this.holeColor = (new Value(false, new String[]{"SafetyOutline", "holeoutline"})).setParent(this.outline);
      this.syncBorder = (new Value(false, new String[]{"SyncBorder", "borderSync"})).setParent(this.holeColor, true);
      this.borderedFriend = (new Value(false, new String[]{"BorderedFriend", "friendborder"})).setParent(this.syncBorder);
      this.userSymbol = (new EnumValue(UserSymbolMode.CHICKEN, new String[]{"IRCSymbol", "usersymbol"})).setParent(() -> {
         return !Managers.getTextManager().isCustom() && Managers.getIrcManager().isConnected();
      }, false);
      this.useFriendAlias = new Value(false, new String[]{"UseFriendAlias", "usefriendlabel", "useralias"});
      this.ignore = false;
      this.playerEntities = new ArrayList();
      this.offerValues(new Value[]{this.scale, this.self, this.armor, this.enchants, this.lowercaseEnchants, this.simplify, this.simplifySwords, this.durability, this.heldItem, this.id, this.gameMode, this.health, this.pops, this.ping, this.sneak, this.burrow, this.bordered, this.outline, this.holeColor, this.syncBorder, this.borderedFriend, this.useFriendAlias, this.userSymbol});
      this.offerListeners(new Listener[]{new ListenerRender(this), new ListenerNametag(this), new ListenerHand(this), new ListenerTick(this)});
   }

   protected void renderNametags(MatrixStack matrix, PlayerEntity player, double x, double y, double z, Vec3d mcPlayerInterpolation) {
      double tempY = y + (player.isSneaking() ? 0.4D : (player.isInSwimmingPose() ? -0.3D : 0.7D));
      double xDist = mcPlayerInterpolation.x - x;
      double yDist = mcPlayerInterpolation.y - y;
      double zDist = mcPlayerInterpolation.z - z;
      y = (double)MathHelper.sqrt((float)(xDist * xDist + yDist * yDist + zDist * zDist));
      String displayTag = this.getDisplayTag(player);
      double s = 0.0018D + (double)MathUtil.fixedNametagScaling((Float)this.scale.getValue()) * y;
      if (y <= 8.0D) {
         s = 0.0245D;
      }

      matrix.push();
      int width = (int)(Managers.getTextManager().getWidth(displayTag) / 2.0F);
      matrix.translate((float)x, (float)tempY + 1.4F, (float)z);
      matrix.multiply(mc.getEntityRenderDispatcher().getRotation());
      matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
      matrix.scale((float)(-s), (float)(-s), (float)(-s));
      int borderCol = (Boolean)this.syncBorder.getValue() ? (Managers.getFriendManager().isFriend(player) && (Boolean)this.borderedFriend.getValue() ? Colours.get().getFriendColor().getRGB() : Colours.get().getColor().getRGB()) : 855638016;
      int holeCol = EntityUtil.isSafe(player) ? Color.GREEN.getRGB() : Color.RED.getRGB();
      int outlineCol = (Boolean)this.holeColor.getValue() ? holeCol : borderCol;
      float scale = Managers.getTextManager().getHeightScale(displayTag);
      RenderMethods.enable3D();
      Render2DMethods.drawNameTagRect(matrix, (float)(-width - 2), (float)(-Managers.getTextManager().getHeight()), (float)width + 2.0F, scale, (Boolean)this.bordered.getValue() ? 1426064384 : 0, (Boolean)this.outline.getValue() ? outlineCol : 0, 1.4F);
      RenderMethods.disable3D();
      Managers.getTextManager().drawString(matrix, displayTag, (double)((float)(-width)), (double)(-Managers.getTextManager().getHeight() + 1), this.getNameColor(player));
      ItemStack heldItemMainhand = player.getStackInHand(Hand.MAIN_HAND);
      ItemStack heldItemOffhand = player.getStackInHand(Hand.OFF_HAND);
      int xOffset = 0;
      int enchantOffset = 0;
      int i = 3;

      int armorOffset;
      for(int armorSize = 3; i >= 0; i = armorSize) {
         ItemStack itemStack;
         if (!(itemStack = player.getInventory().getArmorStack(armorSize)).isEmpty()) {
            xOffset -= 8;
            if ((Boolean)this.enchants.getValue() && !(Boolean)this.simplify.getValue() && (armorOffset = EnchantmentHelper.getEnchantments(itemStack).getSize()) > enchantOffset) {
               enchantOffset = armorOffset;
            }
         }

         --armorSize;
      }

      int i2;
      if (!heldItemOffhand.isEmpty() && (Boolean)this.armor.getValue() || (Boolean)this.durability.getValue() && heldItemOffhand.isDamageable()) {
         xOffset -= 8;
         if ((Boolean)this.enchants.getValue() && !(Boolean)this.simplify.getValue() && (i2 = EnchantmentHelper.getEnchantments(heldItemOffhand).getSize()) > enchantOffset) {
            enchantOffset = i2;
         }
      }

      int fixedEnchantOffsetI;
      if (!heldItemMainhand.isEmpty()) {
         if ((Boolean)this.enchants.getValue() && !(Boolean)this.simplify.getValue() && (i2 = EnchantmentHelper.getEnchantments(heldItemMainhand).getSize()) > enchantOffset) {
            enchantOffset = i2;
         }

         armorOffset = this.getOffset(enchantOffset);
         if ((Boolean)this.armor.getValue() || (Boolean)this.durability.getValue() && heldItemMainhand.isDamageable()) {
            xOffset -= 8;
         }

         if ((Boolean)this.armor.getValue()) {
            fixedEnchantOffsetI = armorOffset;
            armorOffset -= 32;
            this.renderStack(matrix, heldItemMainhand, xOffset, fixedEnchantOffsetI, enchantOffset);
         }

         if ((Boolean)this.durability.getValue() && heldItemMainhand.isDamageable()) {
            this.renderDurability(matrix, heldItemMainhand, (float)xOffset, (float)armorOffset);
         }

         if ((Boolean)this.heldItem.getValue()) {
            this.renderText(matrix, heldItemMainhand, (float)(armorOffset - ((Boolean)this.durability.getValue() && this.anyDamageableArmor(player) ? 10 : 2)));
         }

         if ((Boolean)this.armor.getValue() || (Boolean)this.durability.getValue() && heldItemMainhand.isDamageable()) {
            xOffset += 16;
         }
      }

      i2 = 3;

      int fixedEnchantOffset;
      for(armorOffset = 3; i2 >= 0; i2 = armorOffset) {
         ItemStack itemStack3;
         if (!(itemStack3 = player.getInventory().getArmorStack(armorOffset)).isEmpty()) {
            fixedEnchantOffset = this.getOffset(enchantOffset);
            if ((Boolean)this.armor.getValue()) {
               int oldEnchantOffset = fixedEnchantOffset;
               fixedEnchantOffset -= 32;
               this.renderStack(matrix, itemStack3, xOffset, oldEnchantOffset, enchantOffset);
            }

            if ((Boolean)this.durability.getValue() && itemStack3.isDamageable()) {
               this.renderDurability(matrix, itemStack3, (float)xOffset, (float)fixedEnchantOffset);
            }

            xOffset += 16;
         }

         --armorOffset;
      }

      if (!heldItemOffhand.isEmpty()) {
         fixedEnchantOffsetI = this.getOffset(enchantOffset);
         if ((Boolean)this.armor.getValue()) {
            fixedEnchantOffset = fixedEnchantOffsetI;
            fixedEnchantOffsetI -= 32;
            this.renderStack(matrix, heldItemOffhand, xOffset, fixedEnchantOffset, enchantOffset);
         }

         if ((Boolean)this.durability.getValue() && heldItemOffhand.isDamageable()) {
            this.renderDurability(matrix, heldItemOffhand, (float)xOffset, (float)fixedEnchantOffsetI);
         }
      }

      matrix.pop();
   }

   private boolean anyDamageableArmor(PlayerEntity player) {
      Iterator var2 = player.getArmorItems().iterator();

      ItemStack s;
      do {
         if (!var2.hasNext()) {
            return player.getMainHandStack().isDamageable();
         }

         s = (ItemStack)var2.next();
      } while(!s.isDamageable());

      return true;
   }

   protected String getDisplayTag(PlayerEntity player) {
      StringBuilder sb = new StringBuilder();
      String displayName = EntityUtil.getName(player);
      if (((IrcModule)Managers.getModuleManager().get(IrcModule.class)).isEnabled() && Managers.getIrcManager().isClientUser(displayName)) {
         if (this.userSymbol.getParent().isVisible()) {
            sb.append(((UserSymbolMode)this.userSymbol.getValue()).getSymbol());
         } else {
            sb.append("[P] ");
         }
      }

      String name;
      if ((Boolean)this.useFriendAlias.getValue()) {
         name = player.getName().getString();
         if (Managers.getFriendManager().isFriend(player)) {
            Friend fr = Managers.getFriendManager().getFriend(player.getName().getString());
            if (!fr.getAlias().equals(name)) {
               displayName = fr.getAlias();
            }
         }

         if (player.equals(mc.player)) {
            displayName = "You";
         }
      }

      sb.append(displayName);
      if ((Boolean)this.id.getValue()) {
         sb.append(" ID: %s".formatted(new Object[]{player.getId()}));
      }

      if ((Boolean)this.gameMode.getValue() && mc.getNetworkHandler() != null) {
         PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
         if (entry != null) {
            sb.append(" ");
            String var10001;
            switch(entry.getGameMode()) {
            case SURVIVAL:
               var10001 = "[S]";
               break;
            case CREATIVE:
               var10001 = "[C]";
               break;
            case ADVENTURE:
               var10001 = "[A]";
               break;
            case SPECTATOR:
               var10001 = "[I]";
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
            }

            sb.append(var10001);
         }
      }

      if ((Boolean)this.ping.getValue()) {
         int pingInt = NetworkUtil.getPing(player);
         sb.append(" ").append(pingInt).append("ms");
      }

      if ((Boolean)this.health.getValue()) {
         double healthVal = Math.ceil((double)EntityUtil.getHealth(player));
         int hel = (int)healthVal;
         Formatting color;
         if (hel > 18) {
            color = Formatting.GREEN;
         } else if (hel > 16) {
            color = Formatting.DARK_GREEN;
         } else if (hel > 12) {
            color = Formatting.WHITE;
         } else if (hel > 8) {
            color = Formatting.GOLD;
         } else if (hel > 5) {
            color = Formatting.RED;
         } else {
            color = Formatting.DARK_RED;
         }

         String currentHealth = String.valueOf(hel == 0 ? 1 : hel);
         sb.append(" ").append(color).append(currentHealth);
      }

      if ((Boolean)this.pops.getValue()) {
         name = EntityUtil.getName(player);
         if (Managers.getPopManager().getPopMap().containsKey(name)) {
            int popsInt = (Integer)Managers.getPopManager().getPopMap().get(name);
            if (popsInt != 0) {
               Formatting var10000;
               switch(popsInt) {
               case 1:
                  var10000 = Formatting.GREEN;
                  break;
               case 2:
                  var10000 = Formatting.DARK_GREEN;
                  break;
               case 3:
                  var10000 = Formatting.WHITE;
                  break;
               case 4:
                  var10000 = Formatting.GOLD;
                  break;
               case 5:
                  var10000 = Formatting.RED;
                  break;
               default:
                  var10000 = Formatting.DARK_RED;
               }

               Formatting color = var10000;
               sb.append(color);
               sb.append(" -").append(popsInt);
            }
         }
      }

      return sb.toString();
   }

   private int getOffset(int offset) {
      int fixedOffset = (Boolean)this.armor.getValue() ? -26 : -27;
      if (offset > 4) {
         fixedOffset -= (offset - 4) * 8;
      }

      return fixedOffset;
   }

   private void renderStack(MatrixStack matrix, ItemStack stack, int x, int y, int enchHeight) {
      int height = enchHeight > 4 ? (enchHeight - 4) * 8 / 2 : 0;
      BakedModel bakedModel = mc.getItemRenderer().getModel(stack, mc.world, (LivingEntity)null, 0);
      matrix.push();
      matrix.translate((float)x + 8.0F, (float)y + 8.0F + (float)height, -0.1F);
      matrix.multiplyPositionMatrix((new Matrix4f()).scaling(1.0F, -1.0F, 0.1F));
      matrix.scale(16.0F, 16.0F, 0.1F);
      Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
      mc.getItemRenderer().renderItem(stack, ModelTransformationMode.GUI, false, matrix, immediate, 15728880, OverlayTexture.DEFAULT_UV, bakedModel);      immediate.draw();
      matrix.pop();
      this.drawItemDurabilityBar(matrix, stack, x, y + height);
      matrix.scale(0.5F, 0.5F, 0.5F);
      if ((Boolean)this.enchants.getValue()) {
         this.renderEnchants(matrix, stack, x, y - 24);
      }

      matrix.scale(2.0F, 2.0F, 2.0F);
   }

   private void renderEnchants(MatrixStack matrix, ItemStack stack, int xOffset, int yOffset) {
      Set<RegistryEntry<Enchantment>> e = EnchantmentHelper.getEnchantments(stack).getEnchantments();
      List<String> enchantTexts = new ArrayList(e.size());
      Iterator var7 = e.iterator();

      while(var7.hasNext()) {
         RegistryEntry<Enchantment> enchantment = (RegistryEntry)var7.next();
         String eName = I18n.translate(enchantment.getIdAsString().toLowerCase(), new Object[0]);
         if (this.isValidEnchant(eName) && enchantment.getKey().isPresent()) {
            enchantTexts.add(this.getEnchantText(enchantment, EnchantUtil.getLevel((RegistryKey)enchantment.getKey().get(), stack)));
         }
      }

      var7 = enchantTexts.iterator();

      while(var7.hasNext()) {
         String enchantment = (String)var7.next();
         if (enchantment != null) {
            Managers.getTextManager().drawString((MatrixStack)matrix, (Boolean)this.lowercaseEnchants.getValue() ? enchantment.toLowerCase() : TextUtil.capitalize(enchantment), (double)((float)xOffset * 2.0F), (double)yOffset, -1);
            yOffset += 8;
         }
      }

      if (stack.getItem().equals(Items.ENCHANTED_GOLDEN_APPLE)) {
         Managers.getTextManager().drawString(matrix, "God", (double)((float)xOffset * 2.0F), (double)yOffset, -3977919);
      }

   }

   private boolean isValidEnchant(String eName) {
      if (!(Boolean)this.simplify.getValue()) {
         return true;
      } else {
         return eName.contains("knockback") && !(Boolean)this.simplifySwords.getValue() || eName.contains("fire aspect") && !(Boolean)this.simplifySwords.getValue() || eName.contains("sharpness") || eName.contains("power") || eName.contains("blast protection") || eName.contains("feather falling") || eName.contains("unbreaking") || eName.contains("protection") || eName.contains("efficiency") || eName.contains("mending");
      }
   }

   private String getEnchantText(RegistryEntry<Enchantment> ench, int lvl) {
      String text = I18n.translate(Enchantment.getName(ench, lvl).getString(), new Object[0]);
      String var10000 = text.substring(0, Math.min(text.length(), 2));
      return var10000 + lvl;
   }

   private void renderDurability(MatrixStack matrix, ItemStack stack, float x, float y) {
      int percent = (int)ItemUtil.getDamageInPercent(stack);
      matrix.scale(0.5F, 0.5F, 0.5F);
      Managers.getTextManager().drawString(matrix, percent + "%", (double)(x * 2.0F), (double)y, stack.getItem().getItemBarColor(stack));
      matrix.scale(2.0F, 2.0F, 2.0F);
   }

   private void renderText(MatrixStack matrix, ItemStack stack, float y) {
      matrix.scale(0.5F, 0.5F, 0.5F);
      String name = stack.getName().getString();
      Managers.getTextManager().drawString((MatrixStack)matrix, name, (double)((float)(-mc.textRenderer.getWidth(name) >> 1)), (double)y, -1);
      matrix.scale(2.0F, 2.0F, 2.0F);
   }

   protected int getNameColor(LivingEntity entity) {
      if (entity instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)entity;
         if (Managers.getFriendManager().isFriend(player)) {
            return Colours.get().getFriendColor().getRGB();
         } else {
            if ((Boolean)this.burrow.getValue()) {
               BlockPos pos = player.getBlockPos();
               BlockState state = BlockUtil.getState(pos);
               Box box = state.getCollisionShape(mc.world, pos).isEmpty() ? null : state.getCollisionShape(mc.world, pos).getBoundingBox();
               if (box != null && EntityUtil.BURROW_BLOCKS.contains(state.getBlock()) && box.offset(pos).maxY > player.getY()) {
                  return -10611240;
               }
            }

            return player.isSneaking() && (Boolean)this.sneak.getValue() ? -6676491 : -1;
         }
      } else {
         return -1;
      }
   }

   private void drawItemDurabilityBar(MatrixStack matrixStack, ItemStack stack, int x, int y) {
      if (stack.getCount() != 1) {
         String count = String.valueOf(stack.getCount());
         FontUtil.drawStringWithShadow(matrixStack, count, (float)(x + 19 - 2 - mc.textRenderer.getWidth(count)), (float)(y + 6 + 3), 16777215);
      }

      if (stack.isItemBarVisible()) {
         int i = stack.getItemBarStep();
         int j = stack.getItemBarColor();
         int k = x + 2;
         int l = y + 13;
         this.fill(matrixStack, k, l, k + 13, l + 2, -16777216);
         this.fill(matrixStack, k, l, k + i, l + 1, j | -16777216);
      }

   }

   private void fill(MatrixStack matrixStack, int x, int y, int width, int height, int color) {
      Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
      int i;
      if (x < width) {
         i = x;
         x = width;
         width = i;
      }

      if (y < height) {
         i = y;
         y = height;
         height = i;
      }

      float alpha = (float)Argb.getAlpha(color) / 255.0F;
      float red = (float)Argb.getRed(color) / 255.0F;
      float green = (float)Argb.getGreen(color) / 255.0F;
      float blue = (float)Argb.getBlue(color) / 255.0F;
      Immediate immediate = VertexConsumerProvider.immediate(new BufferAllocator(1536));
      VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getGuiOverlay());
      vertexConsumer.vertex(matrix4f, (float)x, (float)y, 0.0F).color(red, green, blue, alpha);
      vertexConsumer.vertex(matrix4f, (float)x, (float)height, 0.0F).color(red, green, blue, alpha);
      vertexConsumer.vertex(matrix4f, (float)width, (float)height, 0.0F).color(red, green, blue, alpha);
      vertexConsumer.vertex(matrix4f, (float)width, (float)y, 0.0F).color(red, green, blue, alpha);
      immediate.draw();
   }

   
   public Value<Boolean> getSelf() {
      return this.self;
   }

   
   public void setIgnore(boolean ignore) {
      this.ignore = ignore;
   }

   
   public boolean isIgnore() {
      return this.ignore;
   }
}