import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String...args) {
        try {
            Scanner sc = new Scanner(System.in);
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(sc.nextLine())
                    .addEventListener(new LagrangeMessageListener())
                    .buildBlocking();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Success!!!");
    }
}

class LagrangeMessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        JDA jda = event.getJDA();
        User author = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        String msg = message.getContentDisplay();
        System.out.println(msg);

        boolean bot = author.isBot();

        if (msg.endsWith("~")) {
            msg = msg.substring(0, msg.length()-1);
            //Channel
            if (event.isFromType(ChannelType.TEXT)) {
                Guild guild = event.getGuild();
                TextChannel textChannel = event.getTextChannel();
                Member member = event.getMember();

                String name;
                if (message.isWebhookMessage())
                    name = author.getName();
                else
                    name = member.getEffectiveName();






            }

            //Group
            if (event.isFromType(ChannelType.GROUP)) {
                Group group = event.getGroup();
                String groupName = group.getName() != null ? group.getName() : "";


            }

            //DMs
            if (event.isFromType(ChannelType.PRIVATE)) {
                PrivateChannel privateChannel = event.getPrivateChannel();


                //Detail meh
                if (msg.contains("who am i")) {

                    String bio = "```" +
                            "\nName: " + author.getName() +
                            "\nDiscriminator: " + author.getDiscriminator() +
                            "\nAvatar ID: " + author.getAvatarId() +
                            "\nID: " + author.getId() +
                            "\n```";

                    privateChannel.sendMessage(bio).queue();


                }

                if (msg.startsWith("who is")) { //who is "USER"~
                    String name = msg.split("\"")[1];
                    List<User> users = jda.getUsersByName(name, true);

                    for (User u : users) {
                        String bio = "```" +
                                "\nName: " + u.getName() +
                                "\nDiscriminator: " + u.getDiscriminator() +
                                "\nAvatar ID: " + u.getAvatarId() +
                                "\nID: " + u.getId() +
                                "\n```";

                        privateChannel.sendMessage(bio).queue();
                    }

                }

                if (msg.startsWith("slide")) { //slide "MSG" to "USER"~
                    List<String> list = new ArrayList<String>();
                    Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(msg);
                    while (m.find())
                        list.add(m.group(1));

                    String send = list.get(1).replace("\"","");
                    String to = list.get(3).replace("\"", "");


                    List<User> users = jda.getUsersByName(to, true);
                    for (User u : users) {
                        PrivateChannel p = u.openPrivateChannel().complete();
                        p.sendMessage(send).queue();
                    }

                }
            }

            if (msg.startsWith("postcard")) { //Screenshot time!
                try {
                    Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                    BufferedImage img = new Robot().createScreenCapture(screen);
                    File imgFile = new File("src/main/resources/ss.png");
                    ImageIO.write(img, "png", imgFile);
                    channel.sendFile(imgFile).queue();
                    imgFile.delete();
                } catch (AWTException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (msg.toLowerCase().startsWith("run")) { //oof
                try {
                    ProcessBuilder pb = new ProcessBuilder(msg.split("\"")[1].replace("\"", ""));
                    pb.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (msg.toLowerCase().startsWith("beepboop") || msg.toLowerCase().startsWith("bb")) { //robot
                try {
                    Robot r = new Robot();
                    char[] cmds = msg.split(" ")[1].toCharArray();
                    for (int i = 0; i < cmds.length; i++) {
                        switch (cmds[i]) {
                            case 'd' : r.delay((int)(cmds[i+1])*100); i++; break;

                            case 'p' :
                                Color c = r.getPixelColor((int)(cmds[i+1]), (int)(cmds[i+2]));
                                BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
                                for (int j = 0; j < 100; j++) {
                                    for (int k = 0; k < 100; k++) {
                                        img.setRGB(j, k, c.getRGB());
                                    }
                                }
                                File f = new File("src/main/resources/color.png");
                                ImageIO.write(img, "png", f);
                                channel.sendFile(f).queue();
                                f.delete();
                                i+=2;
                                break;

                            case 'K' : r.keyPress((int)(cmds[i+1])); i++; break;
                            case 'k' : r.keyRelease((int)(cmds[i+1])); i++; break;

                            case 'L' : r.mousePress(InputEvent.BUTTON1_DOWN_MASK); break;
                            case 'M' : r.mousePress(InputEvent.BUTTON2_DOWN_MASK); break;
                            case 'R' : r.mousePress(InputEvent.BUTTON3_DOWN_MASK); break;
                            case 'l' : r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); break;
                            case 'm' : r.mouseRelease(InputEvent.BUTTON2_DOWN_MASK); break;
                            case 'r' : r.mouseRelease(InputEvent.BUTTON3_DOWN_MASK); break;

                            case 'w' : r.mouseWheel((int)(cmds[i+1])); i++; break;

                            case 'x' : r.mouseMove((int)(cmds[i+1]), (int)(cmds[i+2])); i+=2; break;
                        }
                    }
                } catch (AWTException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //All
            if (msg.contains("ping")) {
                channel.sendMessage("`ping~`").queue();
            }


            //EEEK
            if (msg.toLowerCase().contains("i love you") || msg.toLowerCase().contains("i love u")) {
                channel.sendMessage("`I love me, too~`").queue();
            }

            if (msg.contains("❤")) {
                channel.sendMessage("`I wish I had a heart~`").queue();
            }

            //DAB
            if (msg.toLowerCase().contains("dab")) {
                channel.sendMessage("*`dabs`*").queue();
            }

            //AAAAA
            if (msg.toLowerCase().matches("a+")) {
                String send = message.getContentDisplay();
                channel.sendMessage("`"+send+send+"~`").queue();
            }

        }
    }
}
