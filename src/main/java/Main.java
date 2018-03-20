import javafx.beans.binding.DoubleExpression;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import org.scilab.forge.jlatexmath.DefaultTeXFont;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.scilab.forge.jlatexmath.cyrillic.CyrillicRegistration;
import org.scilab.forge.jlatexmath.greek.GreekRegistration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

            //ALL

            if (msg.startsWith("postcard") || msg.startsWith("pc")) { //Screenshot time!
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

            if (msg.startsWith("ping")) {
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

            if (msg.startsWith("math")) {
                String latex = msg.substring(5);
                TeXFormula formula = new TeXFormula(latex);
                formula.createPNG(TeXFormula.SERIF, 200, "src/main/resources/tex.png", Color.WHITE, Color.BLACK);
                channel.sendFile(new File("src/main/resources/tex.png")).queue();
            }

            if (msg.startsWith("eval")) {
                String mafs = msg.substring(5);
                Expression exp = new Expression(mafs);
                double ans = exp.calculate();
                channel.sendMessage(exp.getExpressionString() + ((ans != Double.NaN)? " = " + Double.toString(ans) : "")).queue();
            }

            if (msg.startsWith("help")) {
                channel.sendMessage("`no u`").queue();
            }

            if (msg.startsWith("no u")) {
                String help = "";
                help += "```python\n";

                help += "\n\nChannel-Specific Commands\n\n";

                help += "\n\nGroup-Specific Commands\n\n";

                help += "\n\nDM-Specific Commands\n\n";

                help += "who am i~\n";

                help += "# DETAILS: Gives information pertaining to the sender\n";

                help += "# USAGE: who am i~\n";

                help += "who is~\n";

                help += "# DETAILS: Gives information pertaining to another user\n";

                help += "# USAGE: who is \"[USERNAME]\"~\n";

                help += "slide~\n";

                help += "# DETAILS: Sends an anonymous message to another user\n";

                help += "# USAGE: slide \"[MESSAGE]\" to \"[USER]\"~\n";

                help += "\n\nNon-Specific Commands\n\n";

                help += "ping~\n";

                help += "# DETAILS: Ping but every time it says ping it pongs\n";

                help += "# USAGE: ping~\n";

                help += "postcard~ OR pc~\n";

                help += "# DETAILS: Sends a current screenshot of the computer running the bot\n";

                help += "# USAGE: postcard~ OR pc~\n";

                help += "run~\n";

                help += "# DETAILS: Runs a program on the computer running the bot\n";

                help += "# USAGE: run \"[PROGRAM]\"~\n";

                help += "beepboop~ OR bb~\n";

                help += "# DETAILS: Creates a Robot to control the mouse and keyboard\n";

                help += "# USAGE: beepboop [CODES]~\n";

                help += "math~\n";

                help += "# DETAILS: Prints an image from a LATex math expression\n";

                help += "# USAGE: math [EXPRESSION]~\n";

                help += "```";
                channel.sendMessage(help).queue();
            }

        }
    }
}