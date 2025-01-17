package de.presti.ree6.commands.impl.mod;

import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Command(name = "unban", description = "Unban a specific user from the Server, with his User ID.", category = Category.MOD)
public class Unban implements ICommand {

    @Override
    public void onPerform(CommandEvent commandEvent) {

        if (commandEvent.getMember().hasPermission(Permission.ADMINISTRATOR)) {

            if (commandEvent.isSlashCommand()) {

                OptionMapping targetOption = commandEvent.getSlashCommandInteractionEvent().getOption("id");

                if (targetOption != null) {
                    try {
                        commandEvent.getGuild().unban(UserSnowflake.fromId(targetOption.getAsString())).queue();
                        Main.getInstance().getCommandManager().sendMessage("User <@" + targetOption.getAsString() + "> has been unbanned!", 5, commandEvent.getTextChannel(), commandEvent.getInteractionHook());
                    } catch (Exception ignored) {
                        Main.getInstance().getCommandManager().sendMessage("Received a Invalid UserID", 5, commandEvent.getTextChannel(), commandEvent.getInteractionHook());
                    }
                } else {
                    Main.getInstance().getCommandManager().sendMessage("No User was given to Unban!", 5, commandEvent.getTextChannel(), commandEvent.getInteractionHook());
                }

            } else {
                if (commandEvent.getArguments().length == 1) {
                    try {
                        String userId = commandEvent.getArguments()[0];
                        commandEvent.getGuild().unban(UserSnowflake.fromId(userId)).queue();
                        Main.getInstance().getCommandManager().sendMessage("User <@" + userId + "> has been unbanned!", 5, commandEvent.getTextChannel(), commandEvent.getInteractionHook());
                    } catch (Exception ignored) {
                        Main.getInstance().getCommandManager().sendMessage("Received a Invalid UserID", 5, commandEvent.getTextChannel(), commandEvent.getInteractionHook());
                    }
                } else {
                    Main.getInstance().getCommandManager().sendMessage("Not enough Arguments!", 5, commandEvent.getTextChannel(), commandEvent.getInteractionHook());
                    Main.getInstance().getCommandManager().sendMessage("Use " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "unban @user", 5, commandEvent.getTextChannel(), commandEvent.getInteractionHook());
                }
            }
        } else {
            Main.getInstance().getCommandManager().sendMessage("You dont have the Permission for this Command!", 5, commandEvent.getTextChannel(), commandEvent.getInteractionHook());
        }

        Main.getInstance().getCommandManager().deleteMessage(commandEvent.getMessage(), commandEvent.getInteractionHook());
    }

    @Override
    public CommandData getCommandData() {
        return new CommandDataImpl("unban", "Unban a User from the Server!").addOptions(new OptionData(OptionType.STRING, "id", "Which User should be unbanned.").setRequired(true));
    }

    @Override
    public String[] getAlias() {
        return new String[0];
    }
}