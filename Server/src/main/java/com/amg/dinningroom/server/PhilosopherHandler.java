package com.amg.dinningroom.server;

import com.amg.dinningroom.JSon.JSonController;
import com.amg.dinningroom.Timer;
import com.amg.dinningroom.models.Philosopher;
import com.amg.dinningroom.models.Status;
import com.amg.dinningroom.request.Request;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public
class PhilosopherHandler extends Thread {


    private Socket socket;
    private DinningRoom dinningRoom;
    // the game this connection is associated with
    private
    InputStream inputStream;
    private
    OutputStream outputStream;
    private
    DataOutputStream dataOutputStream;
    private
    DataInputStream dataInputStream;

    public RequestHandler requestHandler;
    private
    BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private Philosopher philosopher;
    private static Lock lock = new ReentrantLock();
    Timer timer;
    Timer eatingTime;

  public PhilosopherHandler() {

  }
  public PhilosopherHandler(int id, DinningRoom dinningRoom, Socket socket) {
        this.dinningRoom = dinningRoom;
        this.socket = socket;
        philosopher = new Philosopher(id);
        this.timer = new Timer(1000, Timer.DURATION_INFINITY) {
            @Override
            protected void onTick() {

            }

            @Override
            protected void onFinish() {

            }
        };
      this.eatingTime = new Timer(1000, Timer.DURATION_INFINITY) {
          @Override
          protected void onTick() {

          }

          @Override
          protected void onFinish() {

          }
      };

    }


    public void doneEating() {
        com.amg.dinningroom.models.ChopStick r = dinningRoom.chopSticks[philosopher.getRightC()];
        com.amg.dinningroom.models.ChopStick l = dinningRoom.chopSticks[philosopher.getLeftC()];

        r.setInUse(false);
        r.setHandler(-1);
        l.setRightWants(false);
        r.setLeftWants(false);
        l.setInUse(false);
        l.setHandler(-1);
        getPhilosopher().setStatus(Status.THINKING);
        eatingTime.pause();


    }

    public boolean startEating() {

        com.amg.dinningroom.models.ChopStick r = dinningRoom.chopSticks[philosopher.getRightC()];
        com.amg.dinningroom.models.ChopStick l = dinningRoom.chopSticks[philosopher.getLeftC()];
        if (r.handler == getPhilosopher().getpId() && l.handler == getPhilosopher().getpId()) {
            getPhilosopher().setStatus(Status.EATING);
            eatingTime.resume();
            return true;
        }
        return false;

    }

    @Override
    public void run() {
        System.out.println("start thread");
        try {
            inputStream = socket.getInputStream();
        } catch (IOException ioException) {
            System.out.println("error in inputstream");
        }
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException ioException) {
            System.out.println("error in outstream");
        }
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        printWriter = new PrintWriter(outputStream, true);

        requestHandler = new RequestHandler(this, dinningRoom);
        System.out.println("start loop");
        while (true) {
            // System.out.println("wait");

            Request request = getRequest(bufferedReader);

            if (request == null) {
                System.out.println(this.getName() + " disconnected");
                return;

            }
            //  System.out.println("getting request:" + request.getType() + " from " + philosopher.getpId());
            requestHandler.executeRequest(request);

        }
    }


    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public PhilosopherHandler setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
        return this;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public PhilosopherHandler setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
        return this;
    }

    public Philosopher getPhilosopher() {
        return philosopher;
    }

    public void setPhilosopher(Philosopher philosopher) {
        this.philosopher = philosopher;
    }

    public Request getRequest(BufferedReader bufferedReader) {
        //  System.out.println("start getting request");
        byte[] jsonBytes;
        String requestJson = null;
        try {
            requestJson = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("error in reading");
        }
        Request request = null;
        if (requestJson != null)
            request = JSonController.stringToObjectMapper(requestJson, Request.class);
        return request;
    }

    public Timer getEatingTime() {
        return eatingTime;
    }
}

