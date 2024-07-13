package com.amorgens.wallet.data

import java.net.Socket
import java.util.Scanner

class WalletService {

    // send data to network via TCP
    suspend fun sendData(message:String ):String{
        val address = "10.0.2.2:100"
        // split ip string
        val ipData = address.split(":")
        val host = ipData.get(0)
        val port = Integer.parseInt(ipData.get(1))


        val connection = Socket(host, port)
        // get writer
        val writer = connection.getOutputStream()
        // send data
        writer.write(message.toByteArray())

        // receieve response
        val reader = Scanner(connection.getInputStream())
        var data = ""

        while (reader.hasNext()){
            data += reader.nextLine()+"\n"
        }
        reader.close()
        writer.close()
        connection.close()
        return data
    }
}