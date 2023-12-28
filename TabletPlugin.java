package net.unethicalite.plugins.Tablet;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.camera.CameraPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.game.Prices;
import net.unethicalite.api.input.Keyboard;
import net.unethicalite.api.input.Mouse;
import net.unethicalite.api.items.GrandExchange;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Extension
@PluginDescriptor(
		name = "0 Tablet v 1.7",
		description = " ",
		enabledByDefault = false
)
@Slf4j
public class TabletPlugin extends LoopedPlugin
{
	@Inject
	private Mircoc mircoc;
	@Inject
	private TabletConfig config;
	@Inject
	private OverlayManager overlayManager;
//
	@Inject
	private TabletOverlay TabletOverlay;
//
//	@Inject
//	private GlobalCollisionMap collisionMap;
	@Inject
	private Client client;
	@Inject
	private ClientToolbar clientToolbar;
	private TabletPanel panel;
	private NavigationButton navButton;
	@Override
	protected void startUp()
	{
		panel = injector.getInstance(TabletPanel.class);
		BufferedImage icon = ImageUtil.loadImageResource(getClass(),"IMG.png");
		panel.init();
		navButton = NavigationButton.builder()
				.tooltip("Tablet")
				.icon(icon)
				.priority(9)
				.panel(panel)
				.build();
		clientToolbar.addNavigation(navButton);
		//	times = System.currentTimeMillis();
		overlayManager.add(TabletOverlay);
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
		panel.deinit();
		panel = null;
		navButton = null;

	}



	@Override
	public void stop()
	{
		super.stop();
		overlayManager.remove(TabletOverlay);
	}

	public boolean Run = true;
	@Subscribe
	public void onConfigButtonPressed(ConfigButtonClicked event)
	{
		if (!event.getGroup().contains("Tabletgroup") || !event.getKey().toLowerCase().contains("start"))
		{
			return;
		}

		if (Run)
		{
			this.Run = false;
		}
		else
		{
			this.Run = true;
		}
	}
	public void slp(){
		Time.sleep(rnd(140,550));
	}

	public int rnd(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}

	public void info()
	{
		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String resulttime = dtf.format(now);
			FileWriter myWriter = new FileWriter("/root/info.txt");
			myWriter.write("XPs: " + xps +";"+"Val: " + Valueacc + ";" +"GST: " +Game.getState()+";"+resulttime+";" + "TIM: " + Game.getMembershipDays());
			myWriter.close();
		} catch (IOException e) {

		}
	}

	String status = "Work";

	int xps = 0;
	int Valueacc = 0;
	int overpreice =  10;

	int rune = 554;
	int clay = 1761;
	int lawrune = 563;
	int teleporthouse = 8013;
	int worktp =8007;
	int widgetGP = 79;
	int widgetID = 23;
	long GETime=0;

	void checkstyle(){
		if (config.TabletTypes()==Types.VARROCK) {
			rune = 554;
			worktp = 8007;
			widgetID = 23;
		}else if (config.TabletTypes()==Types.FALAOR){
			rune = 555;
			worktp = 8009;
			widgetID = 18;
		}else if (config.TabletTypes()==Types.HOUSE){
			rune = 557;
			worktp = 8013;
			widgetID = 15;
		}
	}
	WorldArea badarea = new WorldArea(2958, 3195, 20, 41, 0);
	@Override
	protected int loop()
	{
		if (Run) {
			if (mircoc.Loggined()) {
					if (badarea.contains(Players.getLocal().getWorldLocation())) {
						Movement.walkTo(2953, 3218, 0);
						slp();
					}
				info();
				checkstyle();
				MessageUtils.addMessage(status);
				switch (status){
					case "Work":
						WORK();
						break;
					case "Teleport":
						TELEPORT();
						break;
					case "Sell":
						SELL();
						break;
					case "Buy":
						BUY();
						break;
					default:
						break;
				}
			}
		}
		slp();
		return rnd(20,40);
	}

	public Widget CheckWidget(){
		for (int i = 0 ; i < 30 ;i++ ) {
			if (Widgets.get(52,19,i)!=null) {
				if (Widgets.get(52, 19, i).getOriginalY() == 2) {
					return Widgets.get(52, 19, i);
				}
			}
		}
		return null;
	}

	public Widget TabletWidget(){
		return Widgets.get(widgetGP,widgetID);
	}

	WorldArea GEAREA = new WorldArea(3156, 3475, 18, 17, 0);
	WorldArea OHAREA = new WorldArea(2946, 3210, 11, 18, 0);

	public int My_InvValue =0;
	public int My_InvPrice(){
		My_InvValue = 0;
		for (int i = 0 ; i<28 ; i++) {
			if (Widgets.get(149,0,i)!=null){
				My_InvValue = My_InvValue+ (Prices.getItemPrice(Widgets.get(149,0,i).getItemId()) * Widgets.get(149,0,i).getItemQuantity());
			}
		}
		return My_InvValue;
	}
	void WORK(){
		if (GrandExchange.isOpen()) {
			GrandExchange.close();
			mircoc.UNTILSPLEEP(() -> !GrandExchange.isOpen());
			slp();
		}
		Valueacc=My_InvPrice();
		if (GEAREA.contains(Players.getLocal())){
			if (Inventory.contains(clay+1) && Inventory.contains(lawrune) && Inventory.contains(rune) && Inventory.contains(995)){
				if (Inventory.contains(teleporthouse)){
					Inventory.getFirst(teleporthouse).interact("Outside");
					mircoc.UNTILSPLEEP(() -> OHAREA.contains(Players.getLocal()));
					slp();
				}
			}
		}
		else if (OHAREA.contains(Players.getLocal())){
			if (Inventory.contains(clay)) {
				if (Inventory.contains(lawrune) && Inventory.contains(995) && Inventory.contains(rune)) {
					if (Widgets.get(52,19)!=null) {
						if (Widgets.get(52,19).isVisible()){
							//CheckWidget().interact("Enter House");
							Mouse.pressed(CheckWidget().getCanvasLocation().getX() +2,CheckWidget().getCanvasLocation().getY()+2,client.getCanvas(),rnd(10,200),1);
							Mouse.released(CheckWidget().getCanvasLocation().getX() +2,CheckWidget().getCanvasLocation().getY()+2,client.getCanvas(),rnd(10,200),1);

							//GameThread.invoke(() -> Static.getClient().invokeMenuAction("","",1,MenuAction.CC_OP.getId(),11,3407891));
							mircoc.UNTILSPLEEP(() -> !OHAREA.contains(Players.getLocal()));
							slp();
							mircoc.UNTILSPLEEP(() -> !Players.getLocal().isHidden());
							slp();
						}
						else {
							TileObjects.getNearest("House Advertisement").interact("View");
							mircoc.UNTILSPLEEP(() -> Widgets.get(52,19).isVisible());
							slp();
						}
					}
					else {
						TileObjects.getNearest("House Advertisement").interact("View");
						mircoc.UNTILSPLEEP(() -> Widgets.get(52,19)!=null);
						slp();
					}
				}
			}
			else if (Inventory.contains(clay+1)){
				if (Dialog.isOpen()) {
					if (Dialog.canContinue()) {
						Dialog.continueSpace();
						slp();
					} else if (Dialog.isViewingOptions()){
						Dialog.chooseOption(3);
						mircoc.UNTILSPLEEP(() -> Inventory.contains(clay));
						slp();
					}
					else {
						Keyboard.type(rnd(1,10));
						slp();
						Keyboard.sendEnter();
						slp();
					}
				}
				else {
					if (NPCs.getNearest("Phials")!=null) {
						Inventory.getFirst(clay + 1).useOn(NPCs.getNearest("Phials"));
						mircoc.UNTILSPLEEP(() -> Dialog.isOpen());
						slp();
					}
				}
			}
			else {
				if (!Inventory.contains(clay+1)){
					status = "Teleport";
				}
			}
		} else {
			if (Inventory.contains(clay)) {
				if (Movement.getRunEnergy()<30){
					if (TileObjects.getNearest("Frozen ornate pool of Rejuvenation")!=null) {
						TileObjects.getNearest("Frozen ornate pool of Rejuvenation").interact("Drink");
						mircoc.UNTILSPLEEP(() -> Movement.getRunEnergy() == 100);
						slp();
					}
					else if (TileObjects.getNearest("Ornate pool of Rejuvenation")!=null)
					{
						TileObjects.getNearest("Ornate pool of Rejuvenation").interact("Drink");
						mircoc.UNTILSPLEEP(() -> Movement.getRunEnergy() == 100);
						slp();
					}
					if (!Movement.isRunEnabled()) {
						Movement.toggleRun();
						mircoc.UNTILSPLEEP(() -> Movement.isRunEnabled());
						slp();
					}
				}
				else if (!Players.getLocal().isAnimating()) {
					if (TabletWidget() != null && !TabletWidget().isHidden()) {
						TabletWidget().interact(0);
						Time.sleepUntil(() -> Widgets.get(122,4)==null || !Inventory.contains(clay) || Dialog.isOpen(),60000);
						slp();
					}
					if (TileObjects.getNearest("Lectern") != null) {
						if (Inventory.contains(clay)) {
							TileObjects.getNearest("Lectern").interact("Study");
							mircoc.UNTILSPLEEP(() -> TabletWidget() != null);
							slp();
						}
					}
				}
			}
			else {
				if (TileObjects.getNearest("Portal") != null) {
					TileObjects.getNearest("Portal").interact("Enter");
					mircoc.UNTILSPLEEP(() -> OHAREA.contains(Players.getLocal()));
					slp();
				}
			}
		}
	}

	void TELEPORT(){
		if (OHAREA.contains(Players.getLocal())) {
			if (Widgets.get(52, 19) != null) {
				if (Widgets.get(52, 19).isVisible()) {
					Mouse.pressed(CheckWidget().getCanvasLocation().getX() + 2, CheckWidget().getCanvasLocation().getY() + 2, client.getCanvas(), rnd(10, 200), 1);
					Mouse.released(CheckWidget().getCanvasLocation().getX() + 2, CheckWidget().getCanvasLocation().getY() + 2, client.getCanvas(), rnd(10, 200), 1);
					mircoc.UNTILSPLEEP(() -> !OHAREA.contains(Players.getLocal()));
					slp();
					mircoc.UNTILSPLEEP(() -> !Players.getLocal().isHidden());
					slp();
				} else {
					TileObjects.getNearest("House Advertisement").interact("View");
					mircoc.UNTILSPLEEP(() -> Widgets.get(52, 19).isVisible());
					slp();
				}
			} else {
				TileObjects.getNearest("House Advertisement").interact("View");
				mircoc.UNTILSPLEEP(() -> Widgets.get(52, 19) != null);
				slp();
			}
		} else if (GEAREA.contains(Players.getLocal())){
			status = "Sell";
		}
		else {
			if (Widgets.get(590,6,6)!=null && !Widgets.get(590,6,6).isHidden()){
				//Widgets.get(590,6,6).interact("Grand Exchange");
				GameThread.invoke(() -> Static.getClient().invokeMenuAction("","",1,MenuAction.CC_OP.getId(),6,38666246));
				mircoc.UNTILSPLEEP(() -> GEAREA.contains(Players.getLocal()));
				slp();
			}
			else {
				if (TileObjects.getNearest("Ornate Jewellery Box") != null) {
						TileObjects.getNearest("Ornate Jewellery Box").interact("Teleport Menu");
						mircoc.UNTILSPLEEP(() -> Widgets.get(590,6,6) != null);
						slp();
				}
			}
		}
	}
	void SELL(){
		if (mircoc.SELLGE(worktp,Inventory.getCount(true,worktp)-1, Prices.getItemPrice(worktp),overpreice,100000)){
			slp();
		}
		if (Inventory.getCount(true,worktp)<=1){
			if (GrandExchange.isOpen())
				if (GrandExchange.isEmpty())
					status="Buy";
		}
	}
	void BUY(){
		int quant = Inventory.getCount(true,995)/(Prices.getItemPrice(clay)*2);
		if (quant>6000)
			quant = rnd(5000,6000);
		if (!Inventory.contains(clay+1)){
			if (mircoc.BUYGE(clay,quant,Prices.getItemPrice(clay),overpreice,100000)){
				slp();
			}
		}
		else if (Inventory.getCount(true,lawrune)<Inventory.getCount(true,clay+1)) {
			if (mircoc.BUYGE(lawrune,Inventory.getCount(true,clay+1),Prices.getItemPrice(lawrune),overpreice,100000)){
				slp();
			}
		}
		else if (Inventory.getCount(true,rune)<Inventory.getCount(true,clay+1)) {
			if (mircoc.BUYGE(rune,Inventory.getCount(true,clay+1),Prices.getItemPrice(rune)+2,overpreice,10000)){
				slp();
			}
		}
		else if (!Inventory.contains(teleporthouse)) {
			if (mircoc.BUYGE(teleporthouse,1,Prices.getItemPrice(teleporthouse)+20,overpreice,10000)){
				slp();
			}
		}
		else if (GrandExchange.isOpen()){
			if (GrandExchange.isEmpty())
				status="Work";
		}
		else {
			GrandExchange.open();
			mircoc.UNTILSPLEEP(() -> GrandExchange.isOpen());

		}
	}

	@Provides
	TabletConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TabletConfig.class);
	}
}
