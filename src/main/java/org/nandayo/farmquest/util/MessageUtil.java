package org.nandayo.farmquest.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.nandayo.dapi.HexUtil;

public class MessageUtil {

    @SuppressWarnings("deprecation")
    static public void actionBar(@NotNull Player player, @NotNull String message) {
        // deprecated since 1.20.3 but still works
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(HexUtil.parse(message)));
    }

    static public void sendTitle(@NotNull Player player, @NotNull String title, @NotNull String subTitle) {
        player.sendTitle(title, subTitle, 10, 70, 20);
    }
}
