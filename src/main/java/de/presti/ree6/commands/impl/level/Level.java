package de.presti.ree6.commands.impl.level;

import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Main;
import de.presti.ree6.sql.entities.UserLevel;
import de.presti.ree6.utils.data.ImageCreationUtility;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

@Command(name = "level", description = "Show your own Level or the Level of another User in the Guild.", category = Category.LEVEL)
public class Level implements ICommand {

    @Override
    public void onPerform(CommandEvent commandEvent) {

        if (commandEvent.isSlashCommand()) {
            OptionMapping targetOption = commandEvent.getSlashCommandInteractionEvent().getOption("target");
            OptionMapping levelType = commandEvent.getSlashCommandInteractionEvent().getOption("typ");

            if (targetOption != null && targetOption.getAsMember() != null && levelType != null) {
                sendLevel(targetOption.getAsMember(), commandEvent, levelType.getAsString());
            } else {
                sendLevel(commandEvent.getMember(), commandEvent, "chat");
            }
        } else {
            if (commandEvent.getArguments().length <= 2) {
                if (commandEvent.getMessage().getMentions().getMembers().isEmpty()) {
                    sendLevel(commandEvent.getMember(), commandEvent,
                            commandEvent.getArguments().length == 0 ? "chat"
                            : commandEvent.getArguments()[0]);
                } else {
                    sendLevel(commandEvent.getMessage().getMentions().getMembers().get(0), commandEvent,
                            commandEvent.getArguments().length == 0 ? "chat"
                                    : commandEvent.getArguments()[0]);
                }
            } else {
                Main.getInstance().getCommandManager().sendMessage("Not enough Arguments!", commandEvent.getTextChannel(), commandEvent.getInteractionHook());
                Main.getInstance().getCommandManager().sendMessage("Use " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "level chat/voice or " + Main.getInstance().getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "chatprefix").getStringValue() + "level chat/voice @user", commandEvent.getTextChannel(), commandEvent.getInteractionHook());
            }
        }
    }

    @Override
    public CommandData getCommandData() {
        return new CommandDataImpl("level", "Show your own Level or the Level of another User in the Guild.").addOptions(new OptionData(OptionType.STRING, "typ", "Do you want to see chat or voice level?"))
                .addOptions(new OptionData(OptionType.USER, "target", "Show the Level of the User."));
    }

    @Override
    public String[] getAlias() {
        return new String[]{"lvl", "xp", "rank"};
    }

    public void sendLevel(Member member, CommandEvent commandEvent, String type) {

        UserLevel userLevel = type.equalsIgnoreCase("voice") ?
                Main.getInstance().getSqlConnector().getSqlWorker().getVoiceLevelData(commandEvent.getGuild().getId(), member.getId()) :
                Main.getInstance().getSqlConnector().getSqlWorker().getChatLevelData(commandEvent.getGuild().getId(), member.getId());

        userLevel.setUser(member.getUser());
        if (commandEvent.isSlashCommand()) {
            try {
                commandEvent.getInteractionHook().sendFile(ImageCreationUtility.createRankImage(userLevel), "rank.png").queue();
            } catch (Exception ignore) {
                Main.getInstance().getCommandManager().sendMessage("Couldn't generated Rank Image!", commandEvent.getTextChannel(), commandEvent.getInteractionHook());
            }
        } else {
            try {
                commandEvent.getTextChannel().sendFile(ImageCreationUtility.createRankImage(userLevel), "rank.png").queue();
            } catch (Exception ignore) {
                Main.getInstance().getCommandManager().sendMessage("Couldn't generated Rank Image!", commandEvent.getTextChannel(), commandEvent.getInteractionHook());
            }
        }
    }
}
