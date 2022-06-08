package com.kisman.cc;

import com.kisman.cc.api.cape.CapeAPI;
import com.kisman.cc.features.catlua.ScriptManager;
import com.kisman.cc.features.catlua.lua.utils.LuaRotation;
import com.kisman.cc.features.catlua.mapping.*;
import com.kisman.cc.features.command.CommandManager;
import com.kisman.cc.event.*;
import com.kisman.cc.features.plugins.Plugin;
import com.kisman.cc.features.plugins.managers.PluginManager;
import com.kisman.cc.features.plugins.utils.Environment;
import com.kisman.cc.gui.other.music.MusicGui;
import com.kisman.cc.gui.other.search.SearchGui;
import com.kisman.cc.util.manager.ServerManager;
import com.kisman.cc.util.manager.file.ConfigManager;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.gui.MainGui;
import com.kisman.cc.gui.console.ConsoleGui;
import com.kisman.cc.gui.halq.Frame;
import com.kisman.cc.gui.halq.HalqHudGui;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.features.hud.HudModuleManager;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.module.*;
import com.kisman.cc.gui.csgo.ClickGuiNew;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.mainmenu.sandbox.SandBoxShaders;
import com.kisman.cc.gui.vega.Gui;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.SettingsManager;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.math.vectors.VectorUtils;
import com.kisman.cc.util.optimization.aiimpr.MainAiImpr;
import com.kisman.cc.util.protect.*;
import com.kisman.cc.util.manager.Managers;
import com.kisman.cc.util.render.shader.ShaderShell;
import com.kisman.cc.util.world.RotationUtils;
import me.zero.alpine.bus.EventManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.*;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.HashMap;

public class Kisman {
    public static final String NAME = "kisman.cc+";
    public static final String MODID = "kisman";
    public static final String VERSION = "b0.1.6.5";
    public static final String HWIDS_LIST = "https://pastebin.com/raw/yM7s0G4u";
    public static final String fileName = "kisman.cc/";
    public static final String moduleName = "Modules/";
    public static final String hudName = "Hud/";
    public static final String mainName = "Main/";
    public static final String miscName = "Misc/";
    public static final String luaName = "Lua/";
    public static final String mappingName = "Mapping/";
    public static final String imagesName = "Images/";
    public static final String pluginsName = "Plugins/";

    public static Kisman instance;
    public static final EventManager EVENT_BUS = new EventManager();
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final HashMap<GuiScreen, Float> map = new HashMap<>();

    public static EntityPlayer target_by_click = null;

    public static boolean allowToConfiguredAnotherClients, remapped = false;
    public static boolean isOpenAuthGui;
    public static boolean canUseImprAstolfo = false;
    public static boolean canInitializateCatLua = true;

    public static String currentConfig = null;

    static {
        allowToConfiguredAnotherClients = HWID.getHWID().equals("42d17b8fbbd970b9f4db02f9a65fca3b") || HWID.getHWID().equals("4b7985cf9a97b4a82743c480e337259c");

        try {
            Minecraft.class.getDeclaredField("player");
        } catch (NoSuchFieldException e) {
            remapped = true;
        }
    }

    public boolean init = false;

    private static Minecraft mc;

    public VectorUtils vectorUtils;

    public ModuleManager moduleManager;
    public FriendManager friendManager;
    public HudModuleManager hudModuleManager;
    public SettingsManager settingsManager;
    public ClickGuiNew clickGuiNew;
    public ConsoleGui consoleGui;
    public Gui gui;
    public HalqGui halqGui;
    public HalqHudGui halqHudGui;
    public MainGui.SelectionBar selectionBar;
    public MainGui.GuiGradient guiGradient;
    public SearchGui searchGui;
    public MusicGui musicGui;
    public CommandManager commandManager;
    public RPC discord;
    public RotationUtils rotationUtils;
    public EventProcessor eventProcessor;
    public ServerManager serverManager;
    public SandBoxShaders sandBoxShaders;
    public Managers managers;
    public CapeAPI capeAPI;
    public PluginManager pluginManager;

    public MainAiImpr aiImpr;

    //catlua
    public EventProcessorLua eventProcessorLua;
    public Remapper3000 remapper3000;
    public ForgeMappings forgeMappings;
    public LuaRotation luaRotation;
    public ScriptManager scriptManager;

    //Config
    public ConfigManager configManager;




    public Kisman() {
        instance = this;
    }

    public void preInit() throws IOException, NoSuchFieldException, IllegalAccessException {
        try {
            Minecraft.class.getDeclaredField("player");
        } catch(Exception e) {
            remapped = true;
        }

        AntiDump.check();
    }

    public void init() throws IOException, NoSuchFieldException, IllegalAccessException {
        Environment.loadEnvironment();
        PluginManager.getInstance().createPluginConfigs(PluginManager.class.getClassLoader());
        PluginManager.getInstance().instantiatePlugins();
        for (Plugin plugin : PluginManager.getInstance().getPlugins().values()) {
            System.out.println("Plugin injecting");
            plugin.load();
        }
        aiImpr = new MainAiImpr();
        eventProcessor = new EventProcessor();
        managers = new Managers();
        managers.init();
        aiImpr.init();

        Display.setTitle(NAME + " | " + VERSION);
        MinecraftForge.EVENT_BUS.register(this);
        mc = Minecraft.getMinecraft();

        vectorUtils = new VectorUtils();
        pluginManager = new PluginManager();


        friendManager = new FriendManager();
    	settingsManager = new SettingsManager();
    	moduleManager = new ModuleManager();
        hudModuleManager = new HudModuleManager();
        clickGuiNew = new ClickGuiNew();
        consoleGui = new ConsoleGui();
        commandManager = new CommandManager();
        discord = new RPC();
        rotationUtils = new RotationUtils();
        serverManager = new ServerManager();
        sandBoxShaders = new SandBoxShaders();
        capeAPI = new CapeAPI();

        configManager = new ConfigManager("config");
        configManager.getLoader().init();


        //load glow shader
        ShaderShell.init();

        //catlua
        catlua: {
            eventProcessorLua = new EventProcessorLua();
            remapper3000 = new Remapper3000();
            remapper3000.init();
            luaRotation = new LuaRotation();
            scriptManager = new ScriptManager();
        }

        //gui's
        clickGuiNew = new ClickGuiNew();
        gui = new Gui();
        halqGui = new HalqGui();
        halqHudGui = new HalqHudGui();

        selectionBar = new MainGui.SelectionBar(MainGui.Guis.ClickGui);
        guiGradient = new MainGui.GuiGradient();

        //For test
        searchGui = new SearchGui(new Setting("Test"), null);
        musicGui = new MusicGui();

        init = true;
    }
    
    @SubscribeEvent
    public void key(KeyInputEvent e) {
    	if (mc.world == null || mc.player == null) return;
    	try {
            if (Keyboard.isCreated()) {
                if (Keyboard.getEventKeyState()) {
                    int keyCode = Keyboard.getEventKey();
                    if (keyCode <= 1) return;
                    for (Module m : moduleManager.modules) if (m.getKey() == keyCode) m.toggle();
                    for (HudModule m : hudModuleManager.modules) if (m.getKey() == keyCode) m.toggle();
                    for (Setting s : settingsManager.getSettings()) if(s.getKey() == keyCode && s.isCheck()) {
                        s.setValBoolean(!s.getValBoolean());
                        if(init && Config.instance.notification.getValBoolean()) ChatUtility.message().printClientMessage(TextFormatting.GRAY + "Setting " + (s.getValBoolean() ? TextFormatting.GREEN : TextFormatting.RED) + s.getParentMod().getName() + "->" + s.getName() + TextFormatting.GRAY + " has been " + (s.getValBoolean() ? "enabled" : "disabled") + "!");
                    }
                } else if(Keyboard.getEventKey() > 1) onRelease(Keyboard.getEventKey());
            }
        } catch (Exception ignored) {}
    }

    private void onRelease(int key) {
        for(Module m : moduleManager.modules) if(m.getKey() == key) if(m.hold) m.toggle();
        for(HudModule m : hudModuleManager.modules) if(m.getKey() == key) if(m.hold) m.toggle();
    }

    public static String getName() {
        return instance.name();
    }

    public String name() {
        if(init) {
            switch (Config.instance.nameMode.getValString()) {
                case "kismancc": return NAME;
                case "LavaHack": return "LavaHack";
                case "TheKisDevs": return "TheKisDevs";
                case "kidman": return "kidman.club";
                case "TheClient": return "TheClient";
                case "BloomWare": return "BloomWare";
                case "kidmad": return "kidmad.sex";
                case "UwU": return "UwU";
                case "EarthHack": return "3arthH4ck";
                case "custom": return Config.instance.customName.getValString();
            }
        }
        return NAME;
    }

    public static String getVersion() {
        if(instance.init) {
            switch (Config.instance.nameMode.getValString()) {
                case "BloomWare": return "1.0";
                case "EarthHack": return "1.6.2";
                default : return VERSION;
            }
        }
        return VERSION;
    }

    public static void initDirs() throws IOException {
        if (!Files.exists(Paths.get(fileName))) {
            Files.createDirectories(Paths.get(fileName));
            LOGGER.info("Root dir created");
        }
        if (!Files.exists(Paths.get(fileName + imagesName))) {
            Files.createDirectories(Paths.get(fileName + imagesName));
            LOGGER.info("Images dir created");
        }
        if (!Files.exists(Paths.get(fileName + luaName))) {
            Files.createDirectories(Paths.get(fileName + luaName));
            LOGGER.info("Lua dir created");
        }
        if (!Files.exists(Paths.get(fileName + mappingName))) {
            Files.createDirectories(Paths.get(fileName + mappingName));
            LOGGER.info("Mapping dir created");
        }
        if (!Files.exists(Paths.get(fileName + pluginsName))) {
            Files.createDirectories(Paths.get(fileName + pluginsName));
            LOGGER.info("Plugins dir created");
        }
    }

    public static void openLink(String link) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) desktop.browse(new URI(link));
        } catch (IOException | URISyntaxException e) {e.printStackTrace();}
    }

    //lua
    public static void reloadGUIs() {
        if(mc.player != null || mc.world != null) mc.displayGuiScreen(null);
        instance.halqGui.frames.forEach(Frame::reload);
        instance.clickGuiNew = new ClickGuiNew();
    }
}