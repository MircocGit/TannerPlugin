package net.unethicalite.plugins.Tanner;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.info.InfoPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.game.Prices;
import net.unethicalite.api.input.Keyboard;
import net.unethicalite.api.magic.Magic;
import net.unethicalite.api.magic.SpellBook;
import net.unethicalite.api.packets.WidgetPackets;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.input.Mouse;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.items.*;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.widgets.Tab;
import net.unethicalite.api.widgets.Tabs;
import net.unethicalite.api.widgets.Widgets;
import org.pf4j.Extension;

import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Extension
@PluginDescriptor(
		name = "0 Tanner v13.5",
		description = " ",
		enabledByDefault = false
)
@Slf4j
public class TannerPlugin extends LoopedPlugin
{
	@Inject
	private Client client;
	@Inject
	private Mircoc mircoc;
	@Inject
	private TannerConfig config ;
//	public boolean a=false;
//	@Inject
//	private ChopperConfig config;
//
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TannerOverlay overlay;

//	@Inject
//	private GlobalCollisionMap collisionMap;
//
//	private int fmCooldown = 0;

	@Getter(AccessLevel.PROTECTED)
	private List<Tile> fireArea;

	private WorldPoint startLocation = null;

	@Getter(AccessLevel.PROTECTED)
	private boolean scriptStarted;
	long times;

	@Inject
	private ClientToolbar clientToolbar;
	private TannerPanel panel;
	private NavigationButton navButton;
	@Override
	protected void startUp()
	{
		panel = injector.getInstance(TannerPanel.class);
		BufferedImage icon = ImageUtil.loadImageResource(getClass(),"leather.png");
		panel.init();
		navButton = NavigationButton.builder()
				.tooltip("Tanner")
				.icon(icon)
				.priority(10)
				.panel(panel)
				.build();
		clientToolbar.addNavigation(navButton);
		overlayManager.add(overlay);
	}
	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
		panel.deinit();
		panel = null;
		navButton = null;
	}

	public int My_BankValue =0;
	public int My_BankPrice(){
		if (Widgets.get(12,13)!=null)
		{
			My_BankValue = 0;
			if (Widgets.get(12,13).isVisible())
			{
				for (int i = 0 ; i<70;i++)
				{
					if (Widgets.get(12,13,i).getItemId()!=6512) {
						My_BankValue = My_BankValue + (Prices.getItemPrice(Widgets.get(12, 13, i).getItemId())*Widgets.get(12,13,i).getItemQuantity());
					}
				}
			}
		}
		return My_BankValue;
	}
	public int My_InvValue =0;
	public int My_InvPrice(){
		if (Widgets.get(15,3)!=null)
		{
			My_InvValue = 0;
			if (Widgets.get(15,3).isVisible())
			{
				for (int i = 0 ; i<28;i++)
				{
					if (Widgets.get(15,3,i).getItemId()!=6512) {
						My_InvValue = My_InvValue + (Prices.getItemPrice(Widgets.get(15, 3, i).getItemId())*Widgets.get(15,3,i).getItemQuantity());
					}
				}
			}
		}
		return My_InvValue;
	}
	@Override
	public void stop()
	{
		super.stop();
		overlayManager.remove(overlay);
	}
	public void My_Deposit(int itemid){
		for (int i = 0; i < 27; i++) {
			if (Widgets.get(15, 3, i).getItemId() != 6512) {
				if (Widgets.get(15, 3, i).getItemId() == itemid) {
					WidgetPackets.widgetAction(Widgets.get(15,3,i),"Deposit-All");
					break;
				}
			}
		}
	}
	public void My_Bankwithdraw(int itemid , String mode)
	{
		for (int i = 0 ; i < 50 ; i++)
		{
			if (Widgets.get(12,13,i).getItemId() == itemid)
			{
				MessageUtils.addMessage("Withdraw");
				WidgetPackets.widgetAction(Widgets.get(12,13,i),mode);
				break;
			}
		}
	}

	public boolean Run = true;
	@Subscribe
	public void onConfigButtonPressed(ConfigButtonClicked event)
	{
		if (!event.getGroup().contains("Tanner") || !event.getKey().toLowerCase().contains("start"))
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


	private static final WorldPoint BANK_TILE = new WorldPoint(3268, 3167, 0);
	private static final WorldPoint GE_TILE = new WorldPoint(3164, 3487, 0);
	private static final WorldPoint CLEAR_TILE = new WorldPoint(3210, 3356, 0);
	private static final WorldPoint CLEAR2_TILE = new WorldPoint(3275, 3322, 0);
	WorldArea bankarea = new WorldArea(3269, 3164, 9, 11, 0);
	public int rnd(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}

	String status = "initializing...";


	public int Valueacc=0;

	int countTan=0;
	public void info()
	{
		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String resulttime = dtf.format(now);
//			if (countTan<1) {
//				//######################## READ FROM FILE
//				try {
//					File file = new File("/root/info.txt");
//					BufferedReader br = new BufferedReader(new FileReader(file));
//					String line;
//					line = br.readLine();
//					String[] items = line.split(";");
//					countTan=Integer.parseInt(items[0].substring(5));
//					br.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				//########################
//			}
			FileWriter myWriter = new FileWriter("/root/info.txt");
			myWriter.write("Tan: " + countTan +";"+"Val: " + Valueacc + ";" +"GST: " + Game.getState()+";"+resulttime+";" + "TIM: " + Game.getMembershipDays());
			myWriter.close();
		} catch (IOException e) {

		}
	}

	boolean work = true;
	boolean sell = false;
	boolean buy = false;
	boolean potion = false;
	boolean clear = false;
	boolean clear2 = false;
	int hide =1749; // RED
	int hidenoted = 1750 ;//RED
	int leather = 2507; // RED
	int leathenoted = 2508; //RED
	int OP = 21233794 ; //RED TAN ALL OP CODE
	private void LeatherTypeCheck(){
		if (config.leathertype()==LeatherType.BLACK) {
			hide = 1747;
			hidenoted = 1748;
			leather = 2509;
			leathenoted = 2510;
			OP = 21233795;
		}
		else if (config.leathertype()==LeatherType.BLUE){
			hide = 1751;
			hidenoted = 1752;
			leather = 2505;
			leathenoted = 2506;
			OP = 21233793;
		}
		else if (config.leathertype()==LeatherType.GREEN) {
			hide = 1753;
			hidenoted = 1754;
			leather = 1745;
			leathenoted = 1746;
			OP = 21233792;
		}
		else if (config.leathertype()==LeatherType.RED){
			hide = 1749;
			hidenoted = 1750;
			leather = 2507;
			leathenoted =2508;
			OP = 21233794;
		}
		else if (config.leathertype()==LeatherType.COW){
			hide = 1739;
			hidenoted = 1740;
			leather = 1741;
			leathenoted =1742;
			OP = 21233788;
		}
	}
	WorldArea badarea = new WorldArea(3251, 3232, 16, 12, 0);
	int priceover = 10;
	int price = 0;
	int overpreice= 10;
	int inventoryvalue=0;
	void slp(){
		int x = rnd(1,300);
		if (x<5){
			Time.sleep(5000,14000);
		}
		else if (x<20){
			Time.sleep(2000,3000);
		}
		else if (x<70){
			Time.sleep(700,2000);
		}
		else {
			Time.sleep(200,750);
		}
	}
	String antistatus="Not in Anti ban";
	void Antiban(){
		int x = rnd(1,10);
		switch (x){
			case 1:
				antistatus="Sleeping";
				int z = rnd(1,20);
				if (z<3){
					Time.sleep(42000,69000);
				} else if (z<7) {
					Time.sleep(12000,29000);
				}
				else {
					Time.sleep(2000,25000);
				}
				antistatus="Not in Anti ban";
				break;
			case 2:
				antistatus="Camera switch";
				Time.sleep(2000,25000);
				antistatus="Not in Anti ban";
				break;
			case 3:
				antistatus="Open Some Tabs";
				int y = rnd(1,20);
				if (y<3){
					Tabs.open(Tab.EQUIPMENT);
					Time.sleep(2000,7000);
				} else if (y<7) {
					Tabs.open(Tab.MAGIC);
					Time.sleep(2000,7000);
				}
				else {
					Tabs.open(Tab.EMOTES);
					Time.sleep(2000,7000);
				}
				Tabs.open(Tab.INVENTORY);
				Time.sleep(2000,7000);
				antistatus="Not in Anti ban";
				break;
			case 4:
				antistatus="Type Something";
				for (int v=0; v<5;v++) {
					Keyboard.type(rnd(0,10));
					Time.sleep(200,5000);
				}
				Keyboard.sendEnter();
				Time.sleep(200,5000);
				antistatus="Not in Anti ban";
				break;
			case 5:
				antistatus="Teleport To Lumb";
				Tabs.open(Tab.MAGIC);
				Time.sleep(2000,5000);
				Magic.cast(SpellBook.Standard.LUMBRIDGE_TELEPORT);
				Time.sleep(10000,15000);
				antistatus="Not in Anti ban";
				break;
			case 6:
				break;
			case 7:

				break;
			case 8:

				break;
			case 9:
				break;
			default:
				break;
		}
	}
	int rndtime;
	long timecurrent;


	public void CamSet(){
		if (client.getCameraYawTarget()!=0)
			client.setCameraYawTarget(0);
		if (client.getCameraPitchTarget()!=512)
			client.setCameraPitchTarget(512);
		if (client.getScale()!=176)
			client.runScript(ScriptID.CAMERA_DO_ZOOM, -32, -32);
	}

	@Override
	protected int loop()
	{
		if (config.antiban()) {
			if (!buy) {
				if (!sell) {
					if (rndtime == 0) {
						rndtime = rnd(300000, 1800000);
					}
					if (timecurrent == 0) {
						timecurrent = System.currentTimeMillis();
					}
					if (System.currentTimeMillis() - timecurrent > rndtime) {
						Antiban();
						rndtime = 0;
						timecurrent = 0;
					}
				}
			}
		}
		if (Run) {
			//CamSet();
			info();
			LeatherTypeCheck();
				if (work) {
					if (Players.getLocal().isMoving())
					{
						if (config.usePortion()) {
							if (Movement.getRunEnergy() < 60 && !Movement.isStaminaBoosted()) {
								if (Inventory.contains(12625)) {
									Inventory.getFirst(12625).interact("Drink");
									mircoc.UNTILSPLEEP(() -> Movement.isStaminaBoosted());
									slp();
								} else if (Inventory.contains(12627)) {
									Inventory.getFirst(12627).interact("Drink");
									mircoc.UNTILSPLEEP(() -> Movement.isStaminaBoosted());
									slp();
								} else if (Inventory.contains(12629)) {
									Inventory.getFirst(12629).interact("Drink");
									mircoc.UNTILSPLEEP(() -> Movement.isStaminaBoosted());
									slp();
								} else if (Inventory.contains(12631)) {
									Inventory.getFirst(12631).interact("Drink");
									mircoc.UNTILSPLEEP(() -> Movement.isStaminaBoosted());
									slp();
								}
							}
						}
					}
					if (badarea.contains(Players.getLocal()))
					{
						Movement.walkTo(3263,3226,0);
					}
					MessageUtils.addMessage("Work= " + work);
					MessageUtils.addMessage("---------------");
					if (!Inventory.contains(hide)) {
						TileObject booth = TileObjects.getFirstAt(BANK_TILE, x -> x.hasAction("Bank", "Collect"));
						if (Bank.isOpen() && Inventory.contains(leather)) {
							Valueacc = My_BankPrice()+My_InvPrice();
							//	Bank.depositAll(leather);
							for (int i = 0; i < 27; i++) {
								if (Widgets.get(15, 3, i).getItemId() != 995) { //Coin
									if (Widgets.get(15,3,i).getItemId() != 12625) {
										if (Widgets.get(15,3,i).getItemId() != 12627) {
											if (Widgets.get(15,3,i).getItemId() != 12629) {
												if (Widgets.get(15,3,i).getItemId() != 12631) {
													if (Widgets.get(15, 3, i).getItemId() != 6512) { // null
														if (Widgets.get(15, 3, 1).getItemId() != hide) {
															WidgetPackets.widgetAction(Widgets.get(15, 3, i), "Deposit-All");
														}
													}
												}
											}
										}
									}
								}
							}
							Time.sleepUntil(() -> !Inventory.contains(leather), 5000);
							slp();
						}
						if (Bank.isOpen() && !Inventory.contains(hide)) {
							if (Bank.contains(hide) || Inventory.contains(hidenoted)) {
								if (config.usePortion())
								{
									if (!Inventory.contains(12625)
											&& !Inventory.contains(12627)
											&& !Inventory.contains(12629)
											&& !Inventory.contains(12631)) {
										if (Bank.contains(12625)) {
											Bank.withdraw(12625,1, Bank.WithdrawMode.ITEM);
											mircoc.UNTILSPLEEP(() -> Inventory.contains(12625));
											slp();
										} else if (Bank.contains(12627)) {
											Bank.withdraw(12627,1, Bank.WithdrawMode.ITEM);
											mircoc.UNTILSPLEEP(() -> Inventory.contains(12627));
											slp();
										} else if (Bank.contains(12629)) {
											Bank.withdraw(12629,1, Bank.WithdrawMode.ITEM);
											mircoc.UNTILSPLEEP(() -> Inventory.contains(12629));
											slp();
										} else if (Bank.contains(12631)) {
											Bank.withdraw(12631,1, Bank.WithdrawMode.ITEM);
											mircoc.UNTILSPLEEP(() -> Inventory.contains(12631));
											slp();
										}
									}
								}
								for (int i = 0; i < 27; i++) {
									if (Widgets.get(15, 3, i).getItemId() != 995) { //Coin
										if (Widgets.get(15,3,i).getItemId() != 12625) {
											if (Widgets.get(15, 3, i).getItemId() != 12627) {
												if (Widgets.get(15, 3, i).getItemId() != 12629) {
													if (Widgets.get(15, 3, i).getItemId() != 12631) {
														if (Widgets.get(15, 3, i).getItemId() != 6512) { // null
															if (Widgets.get(15, 3, 1).getItemId() != hide) {
																WidgetPackets.widgetAction(Widgets.get(15, 3, i), "Deposit-All");
															}
														}
													}
												}
											}
										}
									}
								}
								Time.sleepUntil(() -> Bank.contains(hide), 5000);
								slp();
								//Bank.withdrawAll(hide, Bank.WithdrawMode.ITEM);
								for (int i = 0; i < 50; i++) {
									if (Widgets.get(12, 13, i).getItemId() == hide) {
										MessageUtils.addMessage("Withrdarw");
										WidgetPackets.widgetAction(Widgets.get(12, 13, i), "Withdraw-All");
										break;
									}
								}
								Time.sleepUntil(() -> Inventory.contains(hide), 5000);
								slp();
								Bank.close();
								slp();
							} else {
								if (Bank.contains(leather)) {
									Bank.withdrawAll(leather, Bank.WithdrawMode.NOTED);
									Time.sleepUntil(() -> Inventory.contains(leathenoted), 5000);
									slp();
								}
								if (config.usePortion()) {
									if (!Bank.contains(12625)
											&& !Bank.contains(12627)
											&& !Bank.contains(12629)
											&& !Bank.contains(12631)) {
										potion = true;
									}
								}
								Bank.close();
								slp();
								work = false;
								sell = true;
								buy = false;
								clear = false;
								clear2 = false;
							}
						} else {
							if (bankarea.contains(Players.getLocal())) {
								NPCs.getNearest("Banker").interact("Bank");
								//NPCs.getAll("Banker").get(rnd(1,NPCs.getAll("Banker").size())).interact("Bank");
								mircoc.UNTILSPLEEP(() -> Bank.isOpen());
							}
							else {
								Movement.walkTo(bankarea);
								slp();
							}
//							if (booth == null || booth.distanceTo(Players.getLocal()) > 20 || !Reachable.isInteractable(booth)) {
//								Movement.walkTo(BANK_TILE);
//								slp();
//							} else {
//								booth.interact("Bank");
//								slp();
//							}
						}
					} else {
						if (Widgets.isVisible(Widgets.get(324, 106))) {
							Time.sleep(rnd(100, 250));
							WidgetPackets.queueWidgetTypePacket(OP); // red tan // black ##95
							countTan++;
							return rnd(250, 750);
						} else {
							NPC karim = NPCs.getNearest("Ellis");
							if (karim == null) {
								Movement.walkTo(3276, 3190, 0);
								slp();
							} else {
								karim.interact("Trade");
								slp();
							}
						}
					}
				}
			MessageUtils.addMessage("price= " + price);
				if (sell) {
					if (GrandExchange.isOpen()) {
						if (GrandExchange.isEmpty()) {
							if (!Inventory.contains(leathenoted)) {
								work = false;
								sell = false;
								if (!potion) {
									buy = true;
								} else {
									buy = false;
								}
								clear = false;
								clear2 = false;
							}
						}
					}
					MessageUtils.addMessage("Sell= " + sell);
					MessageUtils.addMessage("---------------");
					NPC GE_NPC = NPCs.getNearest("Grand Exchange Clerk");
					int quantity = Inventory.getCount(true, leathenoted)+Inventory.getCount(leather);
					if (GE_NPC != null) {
						if (GrandExchange.isOpen()) {
							if (GrandExchange.isEmpty()) {
								if (price!=0) {
									if (priceover > 500)
										priceover = 500;
									if (GrandExchange.sell(leathenoted, quantity, price - 3)) {
										slp();
									}
								}
								else {
									inventoryvalue= Inventory.getCount(true,995);
									if (GrandExchange.buy(leather, 1, Prices.getItemPrice(leather)*2)) {
										slp();
									}
								}
							} else {
								long breaktimer= System.currentTimeMillis();
								while (!GrandExchange.isEmpty() && System.currentTimeMillis()-breaktimer<150000) {
									if (GrandExchange.canCollect()) {
										if (Widgets.get(465,7,22).getTextColor() == 0x8f0000) {
											GrandExchange.collect();
											price=0;
											Time.sleepUntil(() -> GrandExchange.isEmpty(), 5000);
											slp();
										} else if (Widgets.get(465, 7, 22).getTextColor() == 0x5F00) {
											GrandExchange.collect();
											slp();
											if (price!=0) {
												priceover = 10;
												work = false;
												sell = false;
												if (!potion) {
													buy = true;
												} else {
													buy = false;
												}
												price=0;
												clear = false;
												clear2 = false;
											}
											else {
												price = inventoryvalue-Inventory.getCount(true,995);
											}
										}
									}
									slp();
								}
								if (!GrandExchange.isEmpty()) {
									GrandExchange.abortOffer(leather);
									priceover = priceover *2;
									Time.sleepUntil(() -> Widgets.get(465,7,22).getTextColor()==0x8f0000,5000);
								}
							}
						} else {
							GrandExchange.open();
							slp();
						}
					} else {
//						Map<WorldPoint, List<Transport>> transports =Walker.buildTransportLinks();
//						List<WorldPoint> path = Walker.buildPath();
//						MessageUtils.addMessage("BUG");
						Movement.walkTo(GE_TILE);
//						Walker.walkAlong( Walker.buildPath(GE_TILE, true),Walker.buildTransportLinks());
						slp();
					}
				}
				if (potion && !sell) {
					if (mircoc.BUYGE(12625,35,Prices.getItemPrice(12625)+500,overpreice,100000)){
						if (Inventory.contains(12625+1)) {
							work = false;
							sell = false;
							potion = false;
							buy = true;
							clear = false;
							clear2 = false;
							return rnd(50, 100);
						}
					}
				}
				if (buy) {
					if (Inventory.getCount(true,hidenoted)>4000)
					{
						work = false;
						sell = false;
						buy = false;
						clear = true;
						clear2 = false;
						return rnd(20,50);
					}
					MessageUtils.addMessage("Buy= " + buy);
					MessageUtils.addMessage("---------------");
					NPC GE_NPC = NPCs.getNearest("Grand Exchange Clerk");
					if (GE_NPC != null) {
						if (GrandExchange.isOpen()) {
							if (GrandExchange.isEmpty()) {
								int quant = (Inventory.getCount(true, "Coins") / (Prices.getItemPrice(hide)+priceover+400));
								if (quant > 13000)
									quant = 13000;
								price = Prices.getItemPrice(hide);
								if (price>5000)
									price=5000;
								if (priceover>250)
								{
									work = false;
									sell = false;
									buy = false;
									clear = true;
									clear2 = false;
									return rnd(20,50);
								}
								if (GrandExchange.buy(hide, quant, price+ priceover)) {
									slp();
								}
							}
							else {
								long breaktimer = System.currentTimeMillis();
								while (!GrandExchange.isEmpty() && System.currentTimeMillis() - breaktimer < 100000) {
									if (GrandExchange.canCollect()) {
										if (Widgets.get(465, 7, 22).getTextColor() == 0x8f0000) {
											GrandExchange.collect();
											Time.sleepUntil(() -> GrandExchange.isEmpty(), 5000);
											slp();
										} else if (Widgets.get(465, 7, 22).getTextColor() == 0x5F00) {
											GrandExchange.collect();
											slp();
											priceover = 10;
											work = false;
											sell = false;
											buy = false;
											clear = true;
											clear2 = false;
										}
									}
									Time.sleep(rnd(200, 500));
								}
								if (!GrandExchange.isEmpty()) {
									GrandExchange.abortOffer(hide);
									priceover= priceover*2;
									Time.sleepUntil(() -> Widgets.get(465, 7, 22).getTextColor() == 0x8f0000, 5000);
								}
							}
						} else {
							GrandExchange.open();
							slp();
						}
					} else {
						Movement.walk(GE_TILE.getWorldLocation());
						slp();
					}
				}
				if (clear) {
					MessageUtils.addMessage("Clear= " + clear);
					MessageUtils.addMessage("---------------");
					if (Players.getLocal().getWorldLocation().equals(CLEAR_TILE.getWorldLocation())) {
						work = false;
						sell = false;
						buy = false;
						clear = false;
						clear2 = true;
					} else {
						Movement.walkTo(CLEAR_TILE);
						slp();
					}
				}
			if (clear2) {
				MessageUtils.addMessage("Clear2= " + clear2);
				MessageUtils.addMessage("---------------");
				if (Players.getLocal().getWorldLocation().equals(CLEAR2_TILE.getWorldLocation())) {
					work = true;
					sell = false;
					buy = false;
					clear = false;
					clear2 = false;
				} else {
					Movement.walkTo(CLEAR2_TILE);
					slp();
				}
			}
			}
		return rnd(100,500);
	}

	@Provides
	TannerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TannerConfig.class);
	}
}
