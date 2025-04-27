package me.pollos.polloshook.impl.module.render.chams.util;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.player.PlayerEntity;

public record TotemPopPlayer(OtherClientPlayerEntity player, PlayerEntityModel<PlayerEntity> model, long time) {
   public TotemPopPlayer(OtherClientPlayerEntity player, PlayerEntityModel<PlayerEntity> model, long time) {
      this.player = player;
      this.model = model;
      this.time = time;
   }

   public OtherClientPlayerEntity player() {
      return this.player;
   }

   public PlayerEntityModel<PlayerEntity> model() {
      return this.model;
   }

   public long time() {
      return this.time;
   }
}
