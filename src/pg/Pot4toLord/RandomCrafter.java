package pg.Pot4toLord;


import org.bukkit.Material;
import org.bukkit.block.EnchantingTable;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;


public class RandomCrafter extends JavaPlugin implements Listener{
    @Override
    public void onEnable(){
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "RandomizerCrafter has been successfully loaded.");
        //get the config info to chose random items
        getConfig().options().copyDefaults(true);
        saveConfig();
        //register sever events
        this.getServer().getPluginManager().registerEvents(this,this);

    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event){

        event.setDroppedExp(30);
        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), getRandomItem(null));
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Mob has been Slain");
        event.getDrops().clear();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.getInventory().addItem(getRandomItem(null));
    }

    private int getRandomTier(){
        int tier;
        double chance = new Random().nextDouble()*100;
        if (chance<=1){
            if (chance<=.5){
                tier = 3;
            }
            else
                tier = 2;
        }
        else
            tier = 1;
        return tier;
    }
    private ItemStack getRandomItem(ItemStack expectedItem){
        //grab a random item from the list of items
        String TierString = "Tier" + getRandomTier() + "_Items";
        String TestList = "Small_List";
        List<String> RandomItemList = this.getConfig().getStringList(TierString);

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + (RandomItemList.size()+"") +'\n');
        int index = new Random().nextInt(RandomItemList.size());

        String items = RandomItemList.get(index);
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + (items+"") );
        ItemStack newItem = expectedItem; //set this as expected item for if matchMaterial() comes back as null
        //try catch to test if the code being tried through matchMaterial is a legacy code or just invalid.
        try{
            newItem = new ItemStack(Material.matchMaterial(items.toUpperCase()));

        } //rename the intended crafted item to the name of the faulty legacy code for easy identifying broken codes
        catch(IllegalArgumentException ex){
            //rename the item to the name of broken minecraft item codes
            ItemMeta meta = newItem.getItemMeta();

            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + expectedItem.getItemMeta().getDisplayName() );
            meta.setDisplayName(items);
            newItem.setItemMeta(meta);
        }
        return newItem;


    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){

    if(event.getInventory().getType() != InventoryType.ANVIL && event.getInventory().getType() != InventoryType.GRINDSTONE){
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "ANVIL" );

       if(event.getSlotType() == InventoryType.SlotType.RESULT && event.getCurrentItem()!= null ) {

           try {
               Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + event.getCurrentItem().getItemMeta().getDisplayName());
               ItemStack expectedItem = event.getCurrentItem();
               event.setCurrentItem(getRandomItem(expectedItem));
           } catch (NullPointerException ex) {
               Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Empty Crafting Slot Click detected");
           }
       }
       }
    }
}
