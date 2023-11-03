package com.amg.dinningphilosophers.Client;

import com.amg.dinningphilosophers.JSon.JSonController;
import com.amg.dinningphilosophers.request.Request;
import com.amg.dinningphilosophers.request.RequestType;
import com.amg.dinningphilosophers.response.Response;
import com.amg.dinningphilosophers.response.ResponseType;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public
class ServerHandler {
    public static int SERVER_PORT = 8087;
    static DataOutputStream dataOut;
    private static Socket socket;
    private static DataInputStream dataIn;
    private static PrintWriter printWriter;
    private static
    BufferedReader bufferedReader;

    public static DataOutputStream getDataOut() {
        return dataOut;
    }

    public static void setDataOut(DataOutputStream dataOut) {
        ServerHandler.dataOut = dataOut;
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket socket1) {
        socket = socket1;

        try {
            if (socket1 != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printWriter = new PrintWriter(socket.getOutputStream(), true);
            } else {
                bufferedReader = null;
                printWriter = null;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static DataInputStream getDataIn() {
        return dataIn;
    }

    public static void setDataIn(DataInputStream dataIn) {
        ServerHandler.dataIn = dataIn;

    }

    private static Lock lock = new ReentrantLock();

    public static Response transmitter(Request request) {
        lock.lock();
        if (request != null) {
            if (printWriter == null) {
                lock.unlock();
                return new Response(ResponseType.ERROR_NO_SERVER, "No Server");
            }
            String data;
            data = JSonController.objectToStringMapper(request);
            printWriter.println(data);
          //  System.out.println("sending request: " + request.getType());
            if (bufferedReader == null) {
                lock.unlock();
                return new Response(ResponseType.ERROR_NO_SERVER, "No Server");
            }

        }
        String responseJson = null;

        try {

            responseJson = bufferedReader.readLine();
            if (responseJson.equals("") || responseJson == null) {
                disconnectFromServer();
                lock.unlock();
                return new Response(ResponseType.ERROR_NO_SERVER, "Error");
            }
        } catch (IOException e) {
            lock.unlock();
            System.out.println("Couldn't get response from server");
            disconnectFromServer();

        }


        Response response = JSonController.stringToObjectMapper(responseJson, Response.class);
        if (response == null) {
            lock.unlock();
            System.out.println("null Response");
            return new Response(ResponseType.ERROR_NO_SERVER, "No Server");
        }
      //  System.out.println("getting response: " + response.getType());
        lock.unlock();
        return response;


    }

    public static Response getResponse2() {
        if (dataIn == null) {
            return new Response(ResponseType.ERROR_NO_SERVER, "No Server");
        }

        byte[] jsonBytes;
        String responseJson;
        try {
            sleep(300);
        } catch (InterruptedException interruptedException) {
        }
        try {
            int length = dataIn.readInt();
            jsonBytes = new byte[length];
            dataIn.readFully(jsonBytes, 0, length);
            responseJson = new String(jsonBytes, "UTF-8");

            Response response = JSonController.stringToObjectMapper(responseJson, Response.class);

            return response;

        } catch (IOException ioException) {


            disconnectFromServer();

        }
        return null;
    }

    public static void disconnectFromServer() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                setSocket(null);
                System.out.println("Disconnected from server.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String reconnectToServer() {

        if (socket == null) {
            System.out.println("reconnecting to server");
            connectToServer();

        }
        return "Already Connected";
    }

    public static void connectToServer() {
        try {
            System.out.println("Connecting to server");
            setSocket(new Socket(InetAddress.getLocalHost(), SERVER_PORT));
            System.out.println("Connected to Server.");

        } catch (IOException e) {
            System.out.println("Cannot connect to server");

        }
    }

    public static PrintWriter getPrintWriter() {
        return printWriter;
    }

    public static void setPrintWriter(PrintWriter printWriter) {
        ServerHandler.printWriter = printWriter;
    }

    public static BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public static void setBufferedReader(BufferedReader bufferedReader) {
        ServerHandler.bufferedReader = bufferedReader;
    }

    public static Boolean isConnectionAlive() {
        Response response = ServerHandler.transmitter(new Request(RequestType.ALIVE_CONNECTION, "am I Alive?"));

        if (response.getType() == ResponseType.ALIVE_CONNECTION) return true;
        if (response.getType() == ResponseType.ERROR_NO_SERVER) return null;
        return false;

    }


    public synchronized static void sendRequest2(Request request) {
        if (dataIn == null) {
            return;
        }
        String data;
        data = JSonController.objectToStringMapper(request);
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        try {
            dataOut.writeInt(dataBytes.length);
            dataOut.write(dataBytes, 0, dataBytes.length);
            dataOut.flush();
        } catch (IOException ioException) {
            disconnectFromServer();

        }
    }
}


