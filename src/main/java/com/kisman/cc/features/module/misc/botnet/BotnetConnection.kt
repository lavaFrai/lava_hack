package com.kisman.cc.features.module.misc.botnet

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.misc.botnet.api.command.CommandExecutor
import com.kisman.cc.features.module.misc.botnet.api.WebsiteConnection
import com.kisman.cc.features.module.misc.botnet.api.command.BotCommandManager
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.chat.cubic.ChatUtility
import java.text.SimpleDateFormat
import java.util.*



class BotnetConnection : Module(
    "BotnetConnection",
    "Connects you to the botnet using telegra.ph service",
    Category.MISC)
{
    override fun isBeta(): Boolean {
        return true
    }


    var wc: WebsiteConnection? = null
    var last_cmd = ""




    override fun onEnable() {

        when(mode.valEnum) {
            Modes.Optimized -> {
                var i = 1
                while(true) {
                    wc = WebsiteConnection("https://telegra.ph/botnet-input-${SimpleDateFormat("MM/dd").format(Date())}-$i")
                    if(wc!!.checkConnection()) i++
                    else {
                        ChatUtility.message().printClientModuleMessage("Connected to the botnet ${wc!!.getURL().replace("https://telegra.ph/", "")}")
                        break
                    }
                }
            }

            Modes.Slow -> {
                wc = WebsiteConnection(input_url.valString)
                if (!wc!!.checkConnection()) isToggled = false
            }
        }


        BotCommandManager.init()
    }

    override fun onDisable() {
        ChatUtility.message().printClientModuleMessage("Left the botnet")
        wc = null
    }

    override fun update() {
        val cmd = wc!!.getInput()

        if(cmd != last_cmd) CommandExecutor.execute(cmd)
    }

    private enum class Modes {
        Optimized, Slow
    }

    var mode = register(Setting("Mode", this, Modes.Optimized))
    var input_url = /*register*/(Setting("Input URL", this, ""))
}