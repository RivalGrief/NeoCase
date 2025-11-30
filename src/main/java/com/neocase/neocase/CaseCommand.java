package com.neocase.neocase;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CaseCommand implements CommandExecutor {
    private final NeoCase plugin;

    public CaseCommand(NeoCase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "open":
                handleOpenCommand(sender, args);
                break;
            case "list":
                handleListCommand(sender);
                break;
            case "givekey":
                handleGiveKeyCommand(sender, args);
                break;
            case "reload":
                handleReloadCommand(sender);
                break;
            case "help":
                sendHelp(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        if (!(sender instanceof Player) || sender.hasPermission("neocase.admin")) {
            sender.sendMessage("§6=== NeoCase Admin Commands ===");
            sender.sendMessage("§e/neocase givekey <player> <case> <amount> §7- Выдать ключи");
            sender.sendMessage("§e/neocase reload §7- Перезагрузить плагин");
            sender.sendMessage("§e/neocase list §7- Список кейсов");
        }

        sender.sendMessage("§6=== NeoCase Player Commands ===");
        sender.sendMessage("§e/neocase open <case> §7- Открыть кейс");
        sender.sendMessage("§e/neocase list §7- Список кейсов");
        sender.sendMessage("§e/neocase help §7- Показать помощь");
    }

    private void handleOpenCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда только для игроков!");
            return;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage("§cИспользование: /neocase open <кейс>");
            return;
        }

        String caseId = args[1];
        openCase(player, caseId);
    }

    private void handleListCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            // Консольная команда
            sender.sendMessage("§6Доступные кейсы:");
            for (GameCase gameCase : plugin.getCaseManager().getCases()) {
                sender.sendMessage("§e- " + gameCase.getId() + " §7(" + gameCase.getName() + ")");
            }
            return;
        }

        Player player = (Player) sender;
        player.openInventory(CaseInventory.createCaseMenu());
    }

    private void handleGiveKeyCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("neocase.admin")) {
            sender.sendMessage("§cУ вас нет прав для этой команды!");
            return;
        }

        if (args.length < 4) {
            sender.sendMessage("§cИспользование: /neocase givekey <игрок> <кейс> <количество>");
            sender.sendMessage("§cПример: /neocase givekey Steve default 5");
            return;
        }

        String targetName = args[1];
        String caseId = args[2];
        String amountStr = args[3];

        giveKeyCommand(sender, targetName, caseId, amountStr);
    }

    private void handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("neocase.admin")) {
            sender.sendMessage("§cУ вас нет прав для этой команды!");
            return;
        }

        plugin.reloadConfig();
        plugin.getCaseManager().loadCases();
        sender.sendMessage("§aКонфигурация NeoCase перезагружена!");
    }

    private void openCase(Player player, String caseId) {
        GameCase gameCase = plugin.getCaseManager().getCase(caseId);
        if (gameCase == null) {
            player.sendMessage("§cКейс не найден! Используйте §e/neocase list §cдля списка кейсов.");
            return;
        }

        // Проверяем есть ли ключи у игрока
        if (!plugin.getKeyManager().hasKey(player, caseId)) {
            player.sendMessage("§cУ вас нет ключей для этого кейса!");
            player.sendMessage("§cПопросите администратора выдать ключи: §e/neocase givekey " + player.getName() + " " + caseId + " 1");
            return;
        }

        // Используем ключ
        plugin.getKeyManager().takeKey(player, caseId);

        // Запускаем анимацию открытия
        plugin.getCaseManager().openCaseForPlayer(player, caseId);
    }

    private void giveKeyCommand(CommandSender sender, String targetName, String caseId, String amountStr) {
        try {
            int amount = Integer.parseInt(amountStr);

            if (amount <= 0) {
                sender.sendMessage("§cКоличество ключей должно быть положительным числом!");
                return;
            }

            if (amount > 1000) {
                sender.sendMessage("§cНельзя выдать больше 1000 ключей за раз!");
                return;
            }

            Player target = Bukkit.getPlayer(targetName);
            if (target == null) {
                sender.sendMessage("§cИгрок §e" + targetName + " §cне в сети или не найден!");
                return;
            }

            GameCase gameCase = plugin.getCaseManager().getCase(caseId);
            if (gameCase == null) {
                sender.sendMessage("§cКейс §e" + caseId + " §cне найден!");
                sender.sendMessage("§cДоступные кейсы: §e" + String.join(", ", plugin.getCaseManager().getCases().stream()
                        .map(GameCase::getId)
                        .toArray(String[]::new)));
                return;
            }

            // Выдаем ключи
            plugin.getKeyManager().giveKey(target, caseId, amount);

            // Сообщения
            String caseName = gameCase.getName();
            sender.sendMessage("§aВы выдали §e" + amount + " §aключей кейса §6" + caseName + " §aигроку §e" + target.getName() + "§a!");

            if (!sender.equals(target)) {
                target.sendMessage("§aВы получили §e" + amount + " §aключей для кейса §6" + caseName + "§a!");
            }

            // Логирование действия
            plugin.getLogger().info(sender.getName() + " выдал " + amount + " ключей кейса " + caseId + " игроку " + target.getName());

        } catch (NumberFormatException e) {
            sender.sendMessage("§cНеверное количество ключей! Используйте число.");
        }
    }
}