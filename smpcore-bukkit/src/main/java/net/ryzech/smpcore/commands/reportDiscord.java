package net.ryzech.smpcore.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;

public class reportDiscord extends SlashCommand {
    public JDA jda;
    public reportDiscord(JDA jda) {
        this.jda = jda;
    }

    public void ReportCommand() {
        this.name = "report"; // This has to be lowercase
        this.help = "Report players on the SMP or on the Discord here!"; // Describe the command here
        this.options = Arrays.asList(
                new OptionData(OptionType.STRING, "user", "Player you want to report.").setRequired(true),
                new OptionData(OptionType.STRING, "message", "Message you want to send in the report.").setRequired(true)
        );
        ReportCommand();
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        OptionMapping userOption = event.getOption("user");
        OptionMapping messageOption = event.getOption("message");
        TextChannel modLog = jda.getTextChannelById("843594609682284576");

        event.reply("Reported " + userOption.getAsString() + "! We'll be in touch shortly! <3")
                .setEphemeral(true)
                .queue();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("User Report (Discord)");
        eb.addField("Reported Player", userOption.getAsString(), true);
        eb.addField("Sender", event.getUser().getAsMention(), true);
        eb.addField("Message Content", messageOption.getAsString(), true);
        modLog.sendMessageEmbeds(eb.build()).queue();
    }
}
