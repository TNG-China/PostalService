package com.gmail.erikbigler.postalservice.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.gmail.erikbigler.postalservice.PostalService;
import com.gmail.erikbigler.postalservice.config.Config;
import com.gmail.erikbigler.postalservice.config.Language.Phrases;
import com.gmail.erikbigler.postalservice.mailbox.MailboxManager;
import com.gmail.erikbigler.postalservice.mailbox.MailboxManager.MailboxSelect;

public class MailboxCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!Config.ENABLE_MAILBOXES) return true;
		Player player = (Player) sender;
		if(commandLabel.equalsIgnoreCase(Phrases.COMMAND_MAILBOX.toString()) || commandLabel.equalsIgnoreCase("mailbox")) {
			if(args.length == 0) {
				//FancyMenu.showClickableCommandList(sender, commandLabel, "Postal Service", commandData, 1);
			} else if(args.length == 1) {
				if(args[0].equalsIgnoreCase(Phrases.COMMAND_ARG_SET.toString())) {
					MailboxManager.getInstance().mailboxSelectors.put((Player) sender, MailboxSelect.SET);
					sender.sendMessage(Phrases.ALERT_MAILBOX_SET.toPrefixedString());
				} else if(args[0].equalsIgnoreCase(Phrases.COMMAND_ARG_REMOVE.toString())) {
					MailboxManager.getInstance().mailboxSelectors.put((Player) sender, MailboxSelect.REMOVE);
					sender.sendMessage(Phrases.ALERT_MAILBOX_REMOVE.toPrefixedString());
				} else if(args[0].equalsIgnoreCase(Phrases.COMMAND_ARG_REMOVEALL.toString())) {
					MailboxManager.getInstance().removeAllMailboxes(sender.getName());
					sender.sendMessage(Phrases.ALERT_MAILBOX_REMOVE_ALL.toPrefixedString());
				} else if(args[0].equalsIgnoreCase(Phrases.COMMAND_ARG_FIND.toString())) {
					if(MailboxManager.getInstance().markNearbyMailboxes(player)) {
						sender.sendMessage(Phrases.ALERT_MAILBOX_FIND.toPrefixedString());
					} else {
						sender.sendMessage(Phrases.ERROR_MAILBOX_FIND_NONE.toPrefixedString());
					}
				}

				if(args[0].equalsIgnoreCase(Phrases.COMMAND_ARG_SET.toString()) || args[0].equalsIgnoreCase(Phrases.COMMAND_ARG_REMOVE.toString())) {
					BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
					scheduler.scheduleSyncDelayedTask(PostalService.getPlugin(), new Runnable() {
						private Player player;

						@Override
						public void run() {
							if(MailboxManager.getInstance().mailboxSelectors.containsKey(player)) {
								MailboxManager.getInstance().mailboxSelectors.remove(player);
								player.sendMessage(Phrases.ALERT_MAILBOX_TIMEOUT.toPrefixedString());
							}
						}

						private Runnable init(Player player) {
							this.player = player;
							return this;
						}
					}.init(player), 100L);
				}
			}
		}
		return true;
	}
}