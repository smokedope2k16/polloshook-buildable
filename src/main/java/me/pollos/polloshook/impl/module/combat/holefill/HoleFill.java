package me.pollos.polloshook.impl.module.combat.holefill;

import java.util.Iterator;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.module.BlockPlaceModule;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.util.obj.hole.Hole;
import me.pollos.polloshook.api.util.obj.hole.Hole2x1;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.combat.holefill.mode.HoleFillSortingMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class HoleFill extends BlockPlaceModule {
   protected final Value<Boolean> multitask = new Value(true, new String[]{"MultiTask", "multitasking"});
   protected final Value<Boolean> preferWebs = new Value(false, new String[]{"PreferWebs", "webs"});
   protected final Value<Boolean> twoByOne = new Value(false, new String[]{"2x1", "twobyone", "dobules"});
   protected final Value<Boolean> auto = new Value(false, new String[]{"Auto", "smart"});
   protected final NumberValue<Float> xDist;
   protected final NumberValue<Float> yDist;
   protected final NumberValue<Float> noSelfRange;
   protected final EnumValue<HoleFillSortingMode> sorting;
   protected final NumberValue<Float> targetDistance;
   protected final Value<Boolean> terrain;

   public HoleFill() {
      super(new String[]{"HoleFill", "holefiller", "fillholes", "filler"}, Category.COMBAT);
      this.xDist = (new NumberValue(3.5F, 0.1F, 6.0F, 0.1F, new String[]{"XDist", "horizontal"})).withTag("range").setParent(this.auto);
      this.yDist = (new NumberValue(2.0F, 0.1F, 6.0F, 0.1F, new String[]{"YDist", "vertical"})).withTag("range").setParent(this.auto);
      this.noSelfRange = (new NumberValue(1.8F, 0.0F, 4.0F, 0.1F, new String[]{"NoSelfRange", "selfrange"})).withTag("range").setParent(this.auto);
      this.sorting = (new EnumValue(HoleFillSortingMode.CLOSEST, new String[]{"Sorting", "s", "sort"})).setParent(this.auto);
      this.targetDistance = (new NumberValue(8.0F, 1.0F, 16.0F, 0.1F, new String[]{"TargetDistance", "enemyrange"})).withTag("range").setParent(this.auto);
      this.terrain = new Value(true, new String[]{"Terrain", "terrainholes"});
      this.offerValues(new Value[]{this.multitask, this.preferWebs, this.twoByOne, this.auto, this.xDist, this.yDist, this.noSelfRange, this.sorting, this.targetDistance, this.terrain});
      this.offerListeners(new Listener[]{new ListenerMotion(this)});
   }

   protected int getSlot() {
      return (Boolean)this.preferWebs.getValue() ? ItemUtil.getHotbarItemSlot(Items.COBWEB) : ItemUtil.getHotbarItemSlot(Items.OBSIDIAN);
   }

   protected boolean webCheck(Hole hole) {
      if ((Boolean)this.preferWebs.getValue()) {
         return !mc.player.getBoundingBox().intersects(this.getHoleBB(hole));
      } else {
         return true;
      }
   }

   protected boolean entityCheck(Hole hole) {
      Iterator var2 = mc.world.getOtherEntities((Entity)null, this.getHoleBB(hole), Entity::isAlive).iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (Entity)var2.next();
      } while(entity == null || EntityUtil.isDead(entity));

      return !(entity instanceof ItemEntity);
   }

   protected Box getHoleBB(Hole hole) {
      BlockPos firstPos = hole.getPos();
      Box bb = new Box((double)firstPos.getX(), (double)firstPos.getY(), (double)firstPos.getZ(), (double)(firstPos.getX() + 1), (double)(firstPos.getY() + 1), (double)(firstPos.getZ() + 1));
      if (hole instanceof Hole2x1) {
         Hole2x1 hole2x1 = (Hole2x1)hole;
         BlockPos secondPos = hole2x1.getSecondPos();
         bb = new Box((double)firstPos.getX(), (double)firstPos.getY(), (double)firstPos.getZ(), (double)(secondPos.getX() + 1), (double)(secondPos.getY() + 1), (double)(secondPos.getZ() + 1));
      }

      return bb;
   }

   protected boolean isWebs() {
      return (Boolean)this.preferWebs.getValue();
   }
}
