import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sun.tools.javac.util.List;
import javax.security.auth.login.LoginException;
import me.arynxd.button_pagination.ButtonPaginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

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

        List<MessageEmbed> embeds = List.of(
                new EmbedBuilder().setTitle("Page 1").build(),
                new EmbedBuilder().setTitle("Page 2").build(),
                new EmbedBuilder().setTitle("Page 3").build(),
                new EmbedBuilder().setTitle("Page 4").build(),
                new EmbedBuilder().setTitle("Page 5").build()

        );
        ButtonPaginator paginator = new ButtonPaginator.Builder()
            .setWaiter(waiter)
            .setEmbeds(embeds)
            .setJDA(jda)
            .setChannel(0)
            .setPredicate(event -> { return event.getMember().getIdLong() == 0; })
            .build();

        paginator.paginate();
    }
}
