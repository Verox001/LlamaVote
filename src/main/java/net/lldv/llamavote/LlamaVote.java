package net.lldv.llamavote;

import cn.nukkit.Player;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamavote.commands.VoteCommand;
import net.lldv.llamavote.components.language.Language;
import net.lldv.llamavote.components.listener.EventListener;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author LlamaDevelopment
 * @project LlamaVote
 * @website http://llamadevelopment.net/
 */
public class LlamaVote extends PluginBase {

    @Getter
    private boolean checkOnJoin;

    @Getter
    private int cooldown;

    private String getURL, setURL;

    private double money;
    private final List<String> commands = new ArrayList<>();

    @Override
    public void onEnable() {
        Language.init(this);
        this.saveDefaultConfig();
        final Config c = this.getConfig();

        final String apiKey = c.getString("ApiKey");
        this.checkOnJoin = c.getBoolean("CheckOnJoin");

        this.getURL = "https://minecraftpocket-servers.com/api/?object=votes&element=claim&key=" + apiKey + "&username=";
        this.setURL = "https://minecraftpocket-servers.com/api/?action=post&object=votes&element=claim&key=" + apiKey + "&username=";

        this.money = c.getDouble("VoteRewards.Money");
        this.commands.addAll(c.getStringList("VoteRewards.Commands"));
        this.cooldown = c.getInt("CommandCooldown");

        this.getServer().getCommandMap().register("vote", new VoteCommand(this, c.getSection("Commands.Vote")));
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    public void reward(final Player player) {
        if (this.money > 0) LlamaEconomy.getAPI().addMoney(player, money);
        this.commands.forEach((cmd) -> {
            this.getServer().getCommandMap().dispatch(new ConsoleCommandSender(), cmd.replace("%p", player.getName()));
        });
    }

    public void getVote(final String player, final Consumer<String> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                final CloseableHttpClient httpClient = HttpClients.createDefault();
                final HttpGet request = new HttpGet(this.getURL + player);

                request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9) Gecko/2008052906 Firefox/3.0");

                CloseableHttpResponse response = httpClient.execute(request);
                final String code = new BufferedReader(new InputStreamReader(response.getEntity().getContent())).readLine();
                callback.accept(code);

                httpClient.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public void setVoted(final String player) {
        CompletableFuture.runAsync(() -> {
            try {
                final CloseableHttpClient httpClient = HttpClients.createDefault();

                final HttpPost send = new HttpPost(this.setURL + player);
                send.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9) Gecko/2008052906 Firefox/3.0");
                httpClient.execute(send);

                httpClient.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

}
