package ca.nightfury.minecraft.plugin.services;

import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PrettyMessages
{
    ///////////////////////////////////////////////////////////////////////////
    // Public Method(s).
    ///////////////////////////////////////////////////////////////////////////

    public static void sendMessage(final Player player, final String format, final Object... arguments)
    {
        final String message = String.format(format, arguments);
        final StringBuilder colouredMessageBuilder = new StringBuilder();

        ChatColor colourState = null;
        ChatColor colour = null;

        for (final char messageChar : message.toCharArray())
        {
            if (Character.isLowerCase(messageChar))
            {
                colour = ChatColor.DARK_GREEN;
            }
            else if (Character.isUpperCase(messageChar))
            {
                colour = ChatColor.GREEN;
            }
            else if (Character.isDigit(messageChar))
            {
                colour = ChatColor.BLUE;
            }
            else if (!Character.isWhitespace(messageChar))
            {
                colour = ChatColor.YELLOW;
            }

            if (!Objects.equals(colour, colourState))
            {
                final String colourCode = colour.toString();

                colourState = colour;
                colouredMessageBuilder.append(colourCode);
            }

            colouredMessageBuilder.append(messageChar);
        }

        final String colouredMessage = colouredMessageBuilder.toString();

        player.sendMessage(colouredMessage);
    }
}
