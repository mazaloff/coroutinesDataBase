package settings

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

object Settings {

    private lateinit var date: MutableMap<String, String>

    val passwordGit: String
        get() {
            if (!::date.isInitialized) readSettings()
            return date["passwordGit"] ?: ""
        }
    val passwordPostgresql: String
        get() {
            if (!::date.isInitialized) readSettings()
            return date["passwordPostgresql"] ?: ""
        }
    val passwordH2: String
        get() {
            if (!::date.isInitialized) readSettings()
            return date["passwordH2"] ?: ""
        }

    private fun readSettings() {

        date = mutableMapOf()
        val fis = try {
            FileInputStream("src\\main\\resources\\local.properties")
        } catch (e: FileNotFoundException) {
            val fileNew = File("src\\main\\resources\\local.properties")
            fileNew.writeText("")
            fileNew.inputStream()
        }

        val prop = Properties()
        prop.load(fis)
        fis.close()

        val enumKeys = prop.keys()
        while (enumKeys.hasMoreElements()) {
            val key = enumKeys.nextElement() as String
            val value = prop.getProperty(key)
            date[key] = value
        }
    }
}

