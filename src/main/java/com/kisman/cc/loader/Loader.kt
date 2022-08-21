@file:Suppress("NON_EXHAUSTIVE_WHEN", "UNCHECKED_CAST")

package com.kisman.cc.loader

import com.formdev.flatlaf.FlatDarkLaf
import com.kisman.cc.Kisman
import com.kisman.cc.loader.LavaHackLoaderCoreMod.Companion.loaded
import com.kisman.cc.loader.gui.Gui
import com.kisman.cc.loader.objectweb.Injector
import com.kisman.cc.sockets.client.SocketClient
import com.kisman.cc.sockets.data.SocketMessage.Type.*
import net.minecraft.launchwrapper.Launch.classLoader
import net.minecraft.launchwrapper.LaunchClassLoader
import net.minecraftforge.fml.common.Loader
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.random.Random

/**
 * @author _kisman_
 * @since 12:33 of 04.07.2022
 */

private const val address = "localhost"
private const val port = 4321

const val version = "1.0"

val client = SocketClient(address, port)
var gui : Gui? = null
var answer : String? = null
var loaded = false
var versions = emptyArray<String>()

var oldLogs = ArrayList<String>()

var status = "Idling"
    set(value) {
        if(gui != null) {
            gui?.log(value)
        } else {
            oldLogs.add(value)
        }
        field = value
    }

private const val validAnswer = "2"

fun load(
    key : String,
    version : String,
    properties : String,
    processors : String,
    versionToLoad : String
) {
    if(Utility.runningFromIntelliJ()) {
        Kisman.LOGGER.debug("Not loading due to running in debugging environment!")
        return
    }

    var haveJar = false
    var haveValidAnswer = false

    var bytes : ByteArray? = null

    var state = 0

    var needToBreak = false

    client.onMessageReceived = {
        when(it.type) {
            Text -> {
                if(state == 1) {
                    when (it.text) {
                        "0" -> {
                            status = "Invalid arguments of \"getpublicjar\" command!"
                            needToBreak = true
                        }
                        "1" -> {
                            status = "Invalid key or HWID | Loader is outdated!"
                            needToBreak = true
                        }
                        "2" -> status = "Key and HWID is valid!"
                        "3" -> {
                            status = "You have no access for selected version!"
                            needToBreak = true
                        }
                    }
                }
            }
            File -> {
                bytes = it.file?.byteArray
            }
            Bytes -> {
                bytes = it.byteArray
            }
        }
    }


    state = 1
    client.writeMessage { text = "getpublicjar $key $version $properties $processors $versionToLoad" }

    println("LavaFalcon is downloading classes...")

    status = "Waiting for LavaHack"

    while(client.connected) {
        if(bytes != null) {
            loadIntoResourceCache(bytes!!)
            bytes = null
            loaded = true
            LavaHackLoaderCoreMod.resume()
            gui?.isVisible = false
            break
        }

        if(needToBreak) {
            break
        }
    }

    state = 2
}

fun createGui() {
    println("Creating the gui")
    val font = Font.createFont(Font.TRUETYPE_FONT, classLoader.getResourceAsStream("assets/loader/font.ttf"))
    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font)
    FlatDarkLaf.setup()
    gui = Gui(355, 270, "LavaHack Loader | $version", Font(font.fontName, 0, 18))
    for(log in oldLogs) {
        gui?.log(log)
    }
    gui?.isVisible = true
}

fun initLoader() {
    Thread {
        try {
            setupSocketClient()
            versionCheck(version)
            versions(version)
            createGui()
        } catch(e : Exception) {
            e.printStackTrace()
            Utility.unsafeCrash()
        }
    } .start()
}

fun setupSocketClient(client : SocketClient) {
    client.connect()
    client.writeMessage { text = "LavaHack-Client" }
}

fun setupSocketClient() {
    setupSocketClient(client)
}

fun versionCheck(version : String) {
    println("VersionCheck was started!")

    Thread {
        val client = SocketClient(address, port)

        setupSocketClient(client)

        client.onMessageReceived = {
            when(it.type) {
                Text -> {
                    val answer = it.text!!
                    println("VersionCheck: raw answer is \"$answer\"")
                    when (answer) {
                        "0" -> status = "Invalid arguments of \"checkversion\" command!"
                        "1" -> status = "Your loader is outdated! Please update it!"
                        "2" -> status = "Loader is on latest version!"
                    }
                    println("VersionCheck: answer is \"$status\"")

                    client.close()
                }
            }
        }

        client.writeMessage { text = "checkversion $version" }
    } .start()

    println("VersionCheck finished creating new thread")
}

fun versions(version : String) {
    println("VersionsList was started!")

    val client = SocketClient(address, port)

    setupSocketClient(client)

    client.onMessageReceived = {
        when(it.type) {
            Text -> {
                val answer = it.text!!
                var flag = true
                println("VersionsList: raw answer is \"$answer\"")
                when (answer) {
                    "0" -> status = "Invalid arguments of \"getversions\" command!"
                    "1" -> status = "Invalid loader version!"
                    else -> {
                        if(answer.startsWith("2")) {
                            status = "Successfully received version list"
                            versions = answer.split("|")[1].split("&").toTypedArray()
                            for(version in versions) {
                                println(version)
                            }
                            flag = false
                        }
                    }
                }

                println("VersionsList: answer is \"$status\"")

                if(flag) {
                    Utility.unsafeCrash()
                } else {
                    client.close()
                }
            }
        }
    }

    client.writeMessage { text = "getversions $version" }

    while(client.connected) {
        if(versions.isNotEmpty()) {
            break
        }
    }

    println("VersionsList finished")
}

fun loadIntoClassLoader(bytes : ByteArray) {
    val tempFile = File.createTempFile("LavaHack-Main-Class", ".jar")
    tempFile.writeBytes(bytes)
    tempFile.deleteOnExit()
    classLoader.addURL(tempFile.toURI().toURL())
}

fun loadIntoResourceCache(bytes : ByteArray) {
    val resourceCacheField = LaunchClassLoader::class.java.getDeclaredField("resourceCache")
    resourceCacheField.isAccessible = true
    val resourceCache = resourceCacheField[classLoader] as MutableMap<String, ByteArray>
    val resources = HashMap<String, ByteArray>()

    println("LavaFalcon is injecting classes...")

    status = "Injecting classes..."

    var classesCount = 0
    var resourcesCount = 0

    ZipInputStream(bytes.inputStream()).use { zipStream ->
        var zipEntry: ZipEntry?
        while (zipStream.nextEntry.also { zipEntry = it } != null) {
            var name = zipEntry!!.name
            if (name.endsWith(".class")) {
                println("Injecting class \"${name.removeSuffix(".class")}\"")
                name = name.removeSuffix(".class")
                name = name.replace('/', '.')

                if(name == "Main") {
                    loadIntoClassLoader(zipStream.readBytes())
                } else {
                    resourceCache[name] = zipStream.readBytes()
                }

                classesCount++
                status = "Injecting $name"
            } else if(Utility.validResource(name)) {
                println("Found new resource \"$name\"")
                resources[name] = Utility.getBytesFromInputStream(zipStream)
                resourcesCount++
                status = "Found \"$name\" resource."
            }
        }
    }

    println("Injected $classesCount classes, Found $resourcesCount resources")

    println("LavaFalcon is injecting resources...")

    if(resources.isNotEmpty()) {
        val tempFile = File.createTempFile("lavahackResources-${Random(5000)}", ".jar")
        val fos = FileOutputStream(tempFile)
        val jos = JarOutputStream(fos)

        for(entry in resources.entries) {
            status = "Injecting \"${entry.key}\" resource."
            jos.putNextEntry(ZipEntry(entry.key))
            jos.write(entry.value)
            jos.closeEntry()
            //classCache.put(entry.key, entry.value)
        }

        jos.close()
        fos.close()

        tempFile.deleteOnExit()

        classLoader.addURL(tempFile.toURI().toURL())
    }

    println("LavaFalcon is setting resourceCache!")
    status = "Setting \"resourceCache\""

    resourceCacheField[classLoader] = resourceCache

    status = "Done!"

    println("LavaFalcon is done!")
}

fun initLavaHack() {
    println("uwu")

    if (Utility.runningFromIntelliJ() || !loaded) {
        return
    }

    println("owo")

    try {
        val lavahack = Class.forName("com.kisman.cc.Kisman")
        lavahack.getMethod("init").invoke(lavahack.getField("instance")[null])
    } catch (e : Exception) {
        e.printStackTrace()
        Utility.unsafeCrash()
    }
}

fun initHook() {
    val injector = Injector(
        Loader::class.java,
        "initializeMods"
    )

    injector.injectTAIL(Runnable {
        initLavaHack()
    })
}