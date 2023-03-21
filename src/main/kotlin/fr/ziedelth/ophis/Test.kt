package fr.ziedelth.ophis

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.StringEntity
import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.Paths

fun queries(text: String, speakerId: Int): CloseableHttpResponse {
    val textEncoded = URLEncoder.encode(text, "UTF-8")
    val httpPost = HttpPost("http://localhost:50021/audio_query?text=$textEncoded&speaker=$speakerId")
    val httpClient = HttpClients.createDefault()
    return httpClient.execute(httpPost)
}

fun synthesis(body: String, speakerId: Int): CloseableHttpResponse {
    val httpPost = HttpPost("http://localhost:50021/synthesis?speaker=$speakerId")
    val entity = StringEntity(body, ContentType.APPLICATION_JSON)
    httpPost.entity = entity
    httpPost.setHeader("Accept", "application/json")
    httpPost.setHeader("Content-type", "application/json")
    val httpClient = HttpClients.createDefault()
    return httpClient.execute(httpPost)
}

fun main() {
    val gson = Gson()
//    val text = "こんにちは。\n私の名前はJaïsです。あなたの探索をお手伝いするために、自由にお使いください。"

    val text = "みなさん、こんにちは。\n" +
            "今日のアニメスケジュールをご紹介します。\n" +
            "2023年3月21日（火）の放送は、「アイスガイ＆クールガール」第12話と「人間風神」第12話が最終回です。\n" +
            "そして最後に、「ヒーロースキル～オンラインショッピング～」の第11話です。\n" +
            "\n" +
            "それでは、良い一日と良い春をお過ごしください。"

    val speakerId = 16
    val speedScale = 1.4

    val queriesResponse = queries(text, speakerId)
    val responseCode = queriesResponse.code
    val responseText = queriesResponse.entity.content.bufferedReader().readText()

    if (responseCode != 200) {
        throw Exception("Error while requesting audio from TTS server")
    }

    val json = gson.fromJson(responseText, JsonObject::class.java)
    json.addProperty("speedScale", speedScale)
    json.addProperty("volumeScale", 1.0)
    json.addProperty("intonationScale", 1.5)
    json.addProperty("prePhonemeLength", 1.0)
    json.addProperty("postPhonemeLength", 1.0)

    val body = gson.toJson(json)
    val synthesisResponse = synthesis(body, speakerId)

    if (synthesisResponse.code != 200) {
        throw Exception("Error while requesting synthesis from TTS server")
    }

    val bytes = synthesisResponse.entity.content.readBytes()
    // Save bytes to file
    Files.write(Paths.get("output.wav"), bytes)

    // Execute command to compress audio
    // ffmpeg -i output.wav -map 0:a:0 -b:a 96k output.mp3
}