package me.pollos.polloshook.impl.gui.chat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.StringJoiner;

import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.command.syntax.ColorSyntax;
import me.pollos.polloshook.api.command.syntax.EnemyFindingSyntax;
import me.pollos.polloshook.api.command.syntax.ModuleSyntax;
import me.pollos.polloshook.api.command.syntax.listvalue.BlockListSyntax;
import me.pollos.polloshook.api.command.syntax.listvalue.ItemListSyntax;
import me.pollos.polloshook.api.command.syntax.listvalue.ListSyntax;
import me.pollos.polloshook.api.command.util.CommandUtil;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.CommandModule;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.preset.Preset;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.list.toggleable.block.BlockListValue;
import me.pollos.polloshook.api.value.value.list.toggleable.item.ItemListValue;
import me.pollos.polloshook.api.value.value.targeting.TargetValue;
import me.pollos.polloshook.asm.ducks.gui.ITextFieldWidget;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;

public class PollosChatScreen extends ChatScreen {
   private int x = 0;

   public PollosChatScreen(String originalChatText) {
      super(originalChatText);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.client == null) {
         return true;
      } else {
         String prefix = Managers.getCommandManager().getPrefix();
         if (this.tickHotkeys(keyCode)) {
            return true;
         } else {
            if (keyCode == 258) {
               if (this.chatField.getText().length() > 1) {
                  String[] args = this.chatField.getText().replaceAll("(\\s)\\1+", "$1").split(" ");
                  String args0;
                  Command command;
                  Module module;
                  String latestArg; 

                  if (args.length > 1 && !this.chatField.getText().endsWith(" ")) {
                     args0 = args[0].substring(1).toLowerCase();
                     command = CommandUtil.getCommand(args0);
                     String completedArg;

                     if (command != null && args.length - 2 <= command.getArguments().length - 1 && command.hasArguments()) {
                        Argument argument = command.getArguments()[args.length - 2];
                        if (argument != null) {
                           latestArg = args[args.length - 1];
                           completedArg = argument.predict(latestArg);
                           String currentText = this.chatField.getText();
                           currentText = currentText.substring(0, currentText.length() - latestArg.length());
                           currentText = currentText.concat(completedArg);
                           this.chatField.setText(currentText);
                           return true;
                        }
                     }

                     try {
                        module = CommandUtil.getModule(args0);
                        if (module instanceof CommandModule) {
                           CommandModule commandModule = (CommandModule)module;
                           if (commandModule.matching2Args(args)) {
                              StringBuilder builder = new StringBuilder();

                              for(int j = 0; j < args.length - 1; ++j) {
                                 builder.append(args[j]).append(" ");
                              }

                              String[] split = args[args.length - 1].split(" ");

                              for(int i = 0; i < commandModule.getCommand().getArguments().length; ++i) {
                                 Argument arg = commandModule.getCommand().getArguments()[i];
                                 if (split.length > i) {
                                    String prediction = arg.predict(split[split.length - 1]);
                                    if (prediction != null && prediction.startsWith(split[split.length - 1]) && !prediction.equals(split[split.length - 1])) {
                                       builder.append(prediction);
                                       this.chatField.setText(builder.toString());
                                       return true;
                                    }
                                 }
                              }
                           }
                        }

                        if (module != null && !module.getValues().isEmpty()) {
                           Argument[] moduleArguments = this.getModuleArguments(module, args);
                           Argument argument;
                           if (args.length == 4 && moduleArguments.length == 2) {
                              argument = moduleArguments[1];
                              latestArg = String.join(" ", args);
                              completedArg = args[0] + " " + args[1] + " " + args[2] + " ";
                              if (argument instanceof BlockListSyntax) {
                                 BlockListSyntax blockListSyntax = (BlockListSyntax)argument;
                                 this.chatField.setText(completedArg + blockListSyntax.predict(latestArg));
                                 return true;
                              }

                              if (argument instanceof ItemListSyntax) {
                                 ItemListSyntax itemListSyntax = (ItemListSyntax)argument;
                                 this.chatField.setText(completedArg + itemListSyntax.predict(latestArg));
                                 return true;
                              }
                           }

                           if (args.length - 2 < moduleArguments.length) {
                              argument = moduleArguments[args.length - 2];
                              if (argument != null) {
                                 latestArg = args[args.length - 1];
                                 completedArg = argument.predict(latestArg);
                                 String text = this.chatField.getText();
                                 text = text.substring(0, text.length() - latestArg.length());
                                 text = text.concat(completedArg);
                                 this.chatField.setText(text);
                                 return true;
                              }
                           }
                        }
                     } catch (ArrayIndexOutOfBoundsException var15) {
                        ClientLogger.getLogger().error(var15.toString());
                     }
                  } else if (args.length == 1) {
                     args0 = args[0].substring(1).toLowerCase();
                     command = CommandUtil.getCommand(args0);
                     if (command != null) {
                        this.chatField.setText(prefix + command.getLabel());
                        return true;
                     }

                     module = CommandUtil.getModule(args0);
                     if (module != null) {
                        this.chatField.setText(prefix + module.getLabel());
                        return true;
                     }
                  }
               } else {
                  this.chatField.keyPressed(keyCode, scanCode, modifiers);
               }
            }

            return true;
         }
      }
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      String[] args = this.chatField.getText().split(" ");
      StringJoiner sj = new StringJoiner(" ");
      String[] var7 = args;
      int tx = args.length;

      int ty;
      for(ty = 0; ty < tx; ++ty) {
         String arg = var7[ty];
         sj.add(arg.replaceAll(" ", ""));
      }

      String space = this.chatField.getText().endsWith(" ") && args.length == 1 ? " " : "";
      tx = ((ITextFieldWidget)this.chatField).textRenderer().getWidth(sj.toString()) + 4;
      ty = this.chatField.drawsBackground() ? this.chatField.getY() + (this.chatField.getHeight() - 8) / 2 : this.chatField.getY();
      this.x = ((ITextFieldWidget)this.chatField).textRenderer().getWidth(this.getHelpString(this.chatField.getText()));
      context.drawTextWithShadow(((ITextFieldWidget)this.chatField).textRenderer(), space + this.getHelpString(this.chatField.getText()).toLowerCase(), tx, ty, 6316128);
      int x = this.chatField.getX() - 2;
      int y = this.chatField.getY() - 2;
      int width = this.chatField.getWidth();
      int height = this.chatField.getHeight();
      if (this.chatField.getText().startsWith(Managers.getCommandManager().getPrefix())) {
         context.drawBorder(x, y, width, height, Colours.get().getColorRGB());
      }
   }

   public String getHelpString(String currentText) {
      if (this.chatField.getText().isEmpty()) {
         return "";
      } else {
         String prefix = Managers.getCommandManager().getPrefix();
         if (!currentText.startsWith(prefix)) {
            return "";
         } else {
            String[] args = this.chatField.getText().split(" ");
            String arg0;
            Command command;
            Module module;
            if (args.length > 1 || this.chatField.getText().length() > 2 && this.chatField.getText().endsWith(" ")) {
               arg0 = args[0].substring(1).toLowerCase();
               command = CommandUtil.getCommand(arg0);
               int var32;
               if (command != null && command.hasArguments()) {
                  Argument[] arguments = command.getArguments();
                  StringJoiner str = new StringJoiner(" ");
                  int i = 0;
                  Argument[] var29 = arguments;
                  int var31 = arguments.length;

                  for(var32 = 0; var32 < var31; ++var32) {
                     Argument argument = var29[var32];
                     if (i == args.length - 2) {
                        String text = argument.predict(args[i + 1]);
                        int i1 = args[i + 1].length();
                        if (i1 > text.length()) {
                           continue;
                        }

                        try {
                           text = text.substring(i1);
                        } catch (StringIndexOutOfBoundsException var18) {
                           ClientLogger.getLogger().error(var18.getMessage());
                        }

                        str.add(text);
                     } else if (i >= args.length - 1) {
                        str.add(argument.getLabel());
                     }

                     ++i;
                  }

                  return str.toString();
               }

               module = CommandUtil.getModule(arg0);
               int i;
               if (module instanceof CommandModule) {
                  CommandModule commandModule = (CommandModule)module;
                  if (commandModule.matching2Args(args)) {
                     String[] split = args[args.length - 1].split(" ");

                     for(i = 0; i < commandModule.getCommand().getArguments().length; ++i) {
                        Argument arg = commandModule.getCommand().getArguments()[i];
                        if (split.length > i) {
                           String prediction = arg.predict(split[split.length - 1]);
                           if (prediction != null && prediction.startsWith(split[split.length - 1]) && !prediction.equals(split[split.length - 1])) {
                              return prediction.substring(split[split.length - 1].length());
                           }
                        }
                     }
                  }
               }

               if (module != null && !module.getValues().isEmpty()) {
                  Argument[] arguments = this.getModuleArguments(module, args);
                  StringJoiner str = new StringJoiner(" ");
                  i = 0;
                  Argument[] var30 = arguments;
                  var32 = arguments.length;

                  for(int var12 = 0; var12 < var32; ++var12) {
                     Argument argument = var30[var12];
                     String text;
                     if (args.length != 4) {
                        if (i == args.length - 2) {
                           text = argument.predict(args[i + 1]);
                           int i1 = args[i + 1].length();

                           try {
                              text = text.substring(i1);
                           } catch (StringIndexOutOfBoundsException var20) {
                              ClientLogger.getLogger().error(var20.getMessage());
                           }

                           str.add(text);
                        } else if (i >= args.length - 1) {
                           str.add(argument.getLabel());
                        }

                        ++i;
                     } else if (argument instanceof BlockListSyntax || argument instanceof ItemListSyntax) {
                        try {
                           text = TextUtil.isNullOrEmpty(args[3]) ? "" : args[3];
                           String text2 = argument.predict(text);
                           int i1 = args[3].length();

                           try {
                              text = text2.substring(i1);
                           } catch (StringIndexOutOfBoundsException var19) {
                              ClientLogger.getLogger().error(var19.getMessage());
                           }

                           return text2;
                        } catch (Exception var21) {
                        }
                     }
                  }

                  return str.toString();
               }
            } else if (args.length == 1) {
               arg0 = args[0].substring(1).toLowerCase();
               command = CommandUtil.getCommand(arg0);
               if (command != null) {
                  String text = command.getLabel();
                  text = text.substring(arg0.length());
                  return text;
               }

               module = CommandUtil.getModule(arg0);
               if (module != null) {
                  String text = module.getLabel();
                  text = text.substring(arg0.length());
                  return text;
               }
            }

            return "";
         }
      }
   }

   private Argument[] getModuleArguments(Module module, String[] args) {
      Argument argument = null;
      if (args.length > 1) {
         Value<?> value = (Value)CommandUtil.getLabeledStartingWith(args[1], module.getValues());
         if (value != null) {
            if (value instanceof NumberValue) {
               argument = new Argument("[number]");
            } else if (value instanceof EnumValue) {
               final Enum<?>[] array = (Enum[])value.getValue().getClass().getEnumConstants();
               StringJoiner sj = new StringJoiner("/");
               Enum[] var7 = array;
               int var8 = array.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  Enum<?> enumArray = var7[var9];
                  String label = TextUtil.capitalize(enumArray.name().toLowerCase());
                  sj.add(label);
               }

               argument = new Argument(String.format("[%s]", sj)) {
                  public String predict(String currentArg) {
                     Enum[] var2 = array;
                     int var3 = var2.length;

                     for(int var4 = 0; var4 < var3; ++var4) {
                        Enum<?> enums = var2[var4];
                        String label = enums.name().toLowerCase();
                        if (currentArg.toLowerCase().startsWith(label.substring(0, 1).toLowerCase())) {
                           return label;
                        }
                     }

                     return currentArg;
                  }
               };
            } else if (value instanceof ColorValue) {
               argument = new ColorSyntax();
            } else if (!(value instanceof ItemListValue) && !(value instanceof BlockListValue)) {
               if (value instanceof TargetValue) {
                  argument = new EnemyFindingSyntax();
               }
            } else if (args.length != 4) {
               argument = new ListSyntax();
            } else {
               argument = value instanceof ItemListValue ? new ItemListSyntax() : new BlockListSyntax();
            }
         } else if (!TextUtil.isNullOrEmpty(args[1]) && args[1].equalsIgnoreCase("preset")) {
            StringJoiner sj = new StringJoiner("/");
            Iterator var15 = module.getPresets().iterator();

            while(var15.hasNext()) {
               Preset preset = (Preset)var15.next();
               String label = TextUtil.capitalize(preset.getLabel().toLowerCase());
               sj.add(label);
            }

            argument = new Argument(String.format("[%s]", sj)) {
               public String predict(String currentArg) {
                  Iterator var2 = module.getPresets().iterator();

                  String label;
                  do {
                     if (!var2.hasNext()) {
                        return super.predict(currentArg);
                     }

                     Preset preset = (Preset)var2.next();
                     label = preset.getLabel().toLowerCase();
                  } while(!currentArg.toLowerCase().startsWith(label.substring(0, 1).toLowerCase()));

                  return label;
               }
            };
         } else if (module instanceof CommandModule) {
            CommandModule commandModule = (CommandModule)module;
            if (!TextUtil.isNullOrEmpty(args[1])) {
               Command command = commandModule.getCommand();
               boolean equals = Arrays.stream(command.getAliases()).toList().stream().anyMatch((keyCodec) -> {
                  return args[1].equalsIgnoreCase(keyCodec);
               });
               if (command.hasArguments() && equals) {
                  return command.getArguments();
               }
            }
         }
      }

      return argument != null ? new Argument[]{new ModuleSyntax(module), (Argument)argument} : new Argument[]{new ModuleSyntax(module)};
   }

   private boolean tickHotkeys(int keyCode) {
      if (keyCode == 256) {
         this.client.setScreen((Screen)null);
         return true;
      } else if (keyCode != 257 && keyCode != 335) {
         if (keyCode == 265) {
            this.setChatFromHistory(-1);
            return true;
         } else if (keyCode == 264) {
            this.setChatFromHistory(1);
            return true;
         } else if (keyCode == 266) {
            this.client.inGameHud.getChatHud().scroll(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1);
            return true;
         } else {
            ITextFieldWidget widget = (ITextFieldWidget)this.chatField;
            switch(keyCode) {
            case 259:
               if (widget.canEdit()) {
                  widget.hook$erase(-1);
               }

               return true;
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               if (Screen.isSelectAll(keyCode)) {
                  this.chatField.setCursorToEnd(false);
                  this.chatField.setSelectionEnd(0);
                  return true;
               } else if (Screen.isCopy(keyCode)) {
                  MinecraftClient.getInstance().keyboard.setClipboard(this.chatField.getSelectedText());
                  return true;
               } else if (Screen.isPaste(keyCode)) {
                  if (widget.canEdit()) {
                     this.chatField.write(MinecraftClient.getInstance().keyboard.getClipboard());
                  }

                  return true;
               } else if (Screen.isCut(keyCode)) {
                  MinecraftClient.getInstance().keyboard.setClipboard(this.chatField.getSelectedText());
                  if (widget.canEdit()) {
                     this.chatField.write("");
                  }

                  return true;
               } else {
                  if (keyCode == 267) {
                     this.client.inGameHud.getChatHud().scroll(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1);
                     return true;
                  }

                  return false;
               }
            case 261:
               if (widget.canEdit()) {
                  widget.hook$erase(1);
               }

               return true;
            case 262:
               if (Screen.hasControlDown()) {
                  this.chatField.setCursor(this.chatField.getWordSkipPosition(1), Screen.hasShiftDown());
               } else {
                  this.chatField.moveCursor(1, Screen.hasShiftDown());
               }

               return true;
            case 263:
               if (Screen.hasControlDown()) {
                  this.chatField.setCursor(this.chatField.getWordSkipPosition(-1), Screen.hasShiftDown());
               } else {
                  this.chatField.moveCursor(-1, Screen.hasShiftDown());
               }

               return true;
            case 268:
               this.chatField.setCursorToStart(Screen.hasShiftDown());
               return true;
            case 269:
               this.chatField.setCursorToEnd(Screen.hasShiftDown());
               return true;
            }
         }
      } else {
         this.sendMessage(this.chatField.getText(), true);
         this.client.setScreen((Screen)null);
         return true;
      }
   }

   
   public int getX() {
      return this.x;
   }
}
