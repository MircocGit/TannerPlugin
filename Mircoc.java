package net.unethicalite.plugins.Tanner;

import com.google.inject.Inject;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.input.Mouse;
import net.unethicalite.api.items.GrandExchange;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.pathfinder.Walker;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;

import java.io.IOException;
import java.util.Random;
import java.util.function.BooleanSupplier;

//mircocversion = v3.0
public class Mircoc {

    public int rnd(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }


    public boolean UNTILSPLEEP(BooleanSupplier supplier){
        return Time.sleepUntil(supplier, 10, 5000);
    }

    public boolean Loggined(){
        if (Game.getState() != GameState.LOGIN_SCREEN)
        {
            if (Game.getState() != GameState.LOADING)
            {
                return true;
            }
        }
        return false;
    }



    private TannerPlugin TannerPlugin;


    @Inject
    private Mircoc(TannerPlugin plugin, TannerConfig config) throws IOException {
        this.TannerPlugin = plugin;
    }
    private void OPENGE(){
        NPC GE_NPC = NPCs.getNearest("Grand Exchange Clerk");
        if (GE_NPC !=null ) {
            GrandExchange.open();
            Time.sleepUntil(() -> GrandExchange.isOpen() , 5000);
            Time.sleep(100,500);
        }
    }
    private void EXCHANGEBUY(int itemid,int quantity , int price , int overpirce){
        if (GrandExchange.buy(itemid, quantity, price + overpirce)) {
            Time.sleep(200, 2500);
        }
    }
    private void EXCHANGESELL(int itemid,int quantity , int price , int overpirce){
        if (GrandExchange.sell(itemid, quantity, price - overpirce)) {
            Time.sleep(200, 2500);
        }
    }
    private boolean WAITINGGE( int timewait){
        long breaktimer = System.currentTimeMillis();
        while (!GrandExchange.isEmpty() && System.currentTimeMillis() - breaktimer < timewait &&
                TannerPlugin.Run ) {
            MessageUtils.addMessage(System.currentTimeMillis() - breaktimer +"");
            if (GrandExchange.canCollect()) {
                if (Widgets.get(465, 7, 22).getTextColor() == 0x8f0000) {
                    GrandExchange.collect();
                    Time.sleepUntil(() -> GrandExchange.isEmpty(), 5000);
                    Time.sleep(rnd(200, 2500));
                } else if (Widgets.get(465, 7, 22).getTextColor() == 0x5F00) {
                    GrandExchange.collect();
                    Time.sleep(rnd(200, 2500));
                    TannerPlugin.overpreice=10;
                    return true;
                }
            }
            Time.sleep(rnd(200, 500));
        }
        return false;
    }
    private void ABORTGE(int itemid){
        if (!GrandExchange.isEmpty()) {
            GrandExchange.abortOffer(itemid);
            TannerPlugin.overpreice=TannerPlugin.overpreice*2;
            Time.sleepUntil(() -> Widgets.get(465, 7, 22).getTextColor() == 0x8f0000, 5000);
        }
    }
    public boolean BUYGE(int itemid,int quantity , int price , int overpirce , int timewait){
        if (!GrandExchange.isOpen()) {
            OPENGE();
            return false;
        }
        if (GrandExchange.isEmpty()){
            EXCHANGEBUY(itemid,quantity,price,overpirce);
            return false;
        }
        if (!WAITINGGE(timewait)){
            ABORTGE(itemid);
            return false;
        }
        else {
            Widgets.get(465, 2, 11).interact(0);
            return true;
        }
    }


    public boolean SELLGE(int itemid,int quantity , int price , int overprice , int timewait){
        if (!GrandExchange.isOpen()) {
            OPENGE();
            return false;
        }
        if (GrandExchange.isEmpty()){
            EXCHANGESELL(itemid,quantity,price,overprice);
            return false;
        }
        if (!WAITINGGE(timewait)){
            ABORTGE(itemid-1);
            return false;
        }
        else {
            Widgets.get(465, 2, 11).interact(0);
            return true;
        }
    }
    public boolean CHECKAVAILABLE(int i1 , int i2){
        if (Widgets.get(i1,i2)!=null)
            if (Widgets.get(i1,i2).isVisible())
                return true;
        return false;
    }
    public boolean CHECKAVAILABLE(int i1 , int i2,int i3){
        if (Widgets.get(i1,i2,i3)!=null)
            if (Widgets.get(i1,i2,i3).isVisible())
                return true;
        return false;
    }
    public boolean selectitem(int id){
        for (int i = 0 ; i < 27 ; i++) {
            if (Widgets.get(149,0,i)!=null) {
                if (Widgets.get(149, 0, i).getItemId() == id) {
                    Mouse.click(Widgets.get(149,0,i).getCanvasLocation().getX()
                                    +rnd(1,Widgets.get(149,0,i).getWidth()),
                            Widgets.get(149,0,i).getCanvasLocation().getY()
                                    +rnd(1,Widgets.get(149,0,i).getHeight()),
                            true);
                    return true;
                }
            }
        }
        return false;
    }
    public void RUNTOGGLE(){
        if (Movement.getRunEnergy()>10)
            if (!Movement.isRunEnabled())
                Movement.toggleRun();
    }
    public boolean BANKOPEN(){
        TileObject booth = TileObjects.getFirstSurrounding(3268, 3495, 0, 3, (x) -> {
            return x.hasAction(new String[]{"Bank"});
        });
        if (booth != null) {
            booth.interact("Bank");
            return true;
        }
        return false;
    }
    public boolean bankopen(){
        int x = rnd(1,3);
        switch (x) {
            case 1:
                if (TileObjects.getNearest("Grand Exchange booth") != null) {
                    TileObjects.getNearest("Grand Exchange booth").interact("Bank");
                }
                else {
                    NPCs.getNearest("Banker").interact("Bank");
                    return true;
                }
                break;
            case 2:
                NPCs.getNearest("Banker").interact("Bank");
                return true;
            default:
                break;
        }
        return false;
    }

    public int ITEMPOSX(int itemID){
        for (int i = 0; i < 27; i++) {
            if (Widgets.get(149,0,i)!=null) {
                if (Widgets.get(149, 0, i).getItemId()==itemID) {
                    return Widgets.get(149,0,i).getOriginalX();
                }
            }
        }
        return 0;
    }
    public int ITEMPOSY(int itemID){
        for (int i = 0; i < 27; i++) {
            if (Widgets.get(149,0,i)!=null) {
                if (Widgets.get(149, 0, i).getItemId()==itemID) {
                    return Widgets.get(149,0,i).getOriginalY();
                }
            }
        }
        return 0;
    }
    public int SLOTPOSX(int toslot){
        for (int i = 0; i < 27; i++) {
            if (Widgets.get(149,0,i)!=null) {
                return Widgets.get(149,0,i).getOriginalX();
            }
        }
        return 0;
    }
    public int SLOTPOSY(int toslot){
        for (int i = 0; i < 27; i++) {
            if (Widgets.get(149,0,i)!=null) {
                return Widgets.get(149,0,i).getOriginalY();
            }
        }
        return 0;
    }

    public boolean DRAGGINGITEM(int itemID , int Toslot){
        Mouse.pressed(ITEMPOSX(itemID),ITEMPOSY(itemID), Static.getClient().getCanvas(),0,0);
        Mouse.moved(SLOTPOSX(Toslot)+10,SLOTPOSY(Toslot)+10, Static.getClient().getCanvas(),200);
        Mouse.released(SLOTPOSX(Toslot)+10,SLOTPOSY(Toslot)+10, Static.getClient().getCanvas(),0);
        if (Inventory.getFirst(itemID).getSlot()==Toslot)
        {
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean isselecteditem()
    {
        for (int i = 0; i < 27; i++) {
            if (Widgets.get(149,0,i)!=null) {
                if (Widgets.get(149, 0, i).getBorderType() == 2) {
                    return true;
                }
            }
        }
        return false;
    }

}
