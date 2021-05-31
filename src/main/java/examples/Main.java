package examples;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.arynxd.button_utils.builder.menu.StandardMenuBuilder;
import me.arynxd.button_utils.builder.pagination.StandardPaginatorBuilder;
import me.arynxd.button_utils.menu.Menu;
import me.arynxd.button_utils.pagination.Paginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws LoginException {
        if (args.length < 1) {
            System.out.println("No token provided!");
            System.exit(0);
        }

        String token = args[0];

        EventWaiter waiter = new EventWaiter();
        JDA jda = JDABuilder.createDefault(token)
                .addEventListeners(waiter)
                .build();

        List<MessageEmbed> embeds = Arrays.asList(
                new EmbedBuilder().setTitle("Page 1").build(),
                new EmbedBuilder().setTitle("Page 2").build(),
                new EmbedBuilder().setTitle("Page 3").build(),
                new EmbedBuilder().setTitle("Page 4").build(),
                new EmbedBuilder().setTitle("Page 5").build()
        );

        Paginator paginator = new StandardPaginatorBuilder()
                .setWaiter(waiter)
                .setEmbeds(embeds)
                .setJDA(jda)
                .setChannel(0)
                .setPredicate(event -> event.getMember().getIdLong() == 0)
                .build();

        paginator.paginate();

        Menu menu = new StandardMenuBuilder()
                .setWaiter(waiter)
                .setEmbeds(embeds)
                .setJDA(jda)
                .setChannel(0)
                .setPredicate(event -> event.getMember().getIdLong() == 0)
                .build();
        menu.start();
    }
}
