package me.pollos.polloshook.api.value.value.list.mode;

public enum ListEnum {
   WHITELIST,
   BLACKLIST,
   ANY;

   public static final String[] BLOCKS_LIST_ALIAS = new String[]{"Blocks", "blockslist", "blocklist", "block"};
   public static final String[] ITEM_LIST_ALIAS = new String[]{"Items", "itemslist", "itemlist", "item"};

   // $FF: synthetic method
   private static ListEnum[] $values() {
      return new ListEnum[]{WHITELIST, BLACKLIST, ANY};
   }
}
