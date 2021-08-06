package com.kisman.cc.module;

import java.util.ArrayList;

import com.kisman.cc.module.chat.AutoEZ;
import com.kisman.cc.module.client.*;
import com.kisman.cc.module.combat.AntiBot;
import com.kisman.cc.module.combat.Criticals;
import com.kisman.cc.module.dl.DLGui;
import com.kisman.cc.module.movement.*;
import com.kisman.cc.module.player.AntiKnokBack;
import com.kisman.cc.module.render.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ModuleManager {
	public ArrayList<Module> modules;
	
	public ModuleManager() {
		modules = new ArrayList<Module>();
		MinecraftForge.EVENT_BUS.register(this);
		init();
	}

	public void init() {
		modules.add(new AutoEZ());
		modules.add(new AntiBot());
		modules.add(new AntiKnokBack());
		modules.add(new AutoJump());
		modules.add(new CustomFov());
		modules.add(new Criticals());
		modules.add(new ClickGUI());
		modules.add(new Color());
		modules.add(new CustomFont());
		modules.add(new DLGui());
		modules.add(new HUD());
		modules.add(new NotificationModule());
		modules.add(new RPCModule());
		modules.add(new Fly());
		modules.add(new FullBright());
		modules.add(new TestRenderModule());
		modules.add(new Sprint());
		modules.add(new Step());
	}
	
	public Module getModule(String name) {
		for (Module m : this.modules) {
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;
	}
	
	public ArrayList<Module> getModuleList() {
		return modules;
	}
	
	public ArrayList<Module> getModulesInCategory(Category c) {
		ArrayList<Module> mods = new ArrayList<Module>();
		for (Module m : this.modules) {
			if (m.getCategory() == c) {
				mods.add(m);
			}
		}
		return mods;
	}

	@SubscribeEvent
	public void onKey(InputEvent.KeyInputEvent event) {}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		for(Module m : modules) {
			if(m.isToggled()) {
				m.update();
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent event) {
		for(Module m : modules) {
			if(m.isToggled()) {
				m.render();
			}
		}
	}
}
