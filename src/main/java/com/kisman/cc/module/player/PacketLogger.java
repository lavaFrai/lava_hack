package com.kisman.cc.module.player;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

import i.gishreloaded.gishcode.utils.visual.ChatUtils;

import java.lang.reflect.Field;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;

import me.zero.alpine.listener.*;

public class PacketLogger extends Module{
    private final Setting client = new Setting("Client", this, true);
    private final Setting server = new Setting("Server", this, true);
    private final Setting values = new Setting("Values", this, false);

    public PacketLogger() {
        super("PacketLogger", Category.PLAYER);

        setmgr.rSetting(client);
        setmgr.rSetting(server);
        setmgr.rSetting(values);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if(!client.getValBoolean()) return;

        String message = "Cient -> " + event.getPacket().getClass().getName();

        if(values.getValBoolean()) for(Field field : event.getPacket().getClass().getDeclaredFields()) message += " " + field.getName() + "[" + field.toString() + "]";
        
        ChatUtils.simpleMessage(message);
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> listener1 = new Listener<>(event -> {
        if(!server.getValBoolean()) return;

        String message = "Server -> " + event.getPacket().getClass().getName();

        if(values.getValBoolean()) for(Field field : event.getPacket().getClass().getDeclaredFields()) message += " " + field.getName() + "[" + field.toString() + "]";
        
        ChatUtils.simpleMessage(message);
    });
}
