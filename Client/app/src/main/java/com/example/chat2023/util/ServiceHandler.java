package com.example.chat2023.util;

//final class ServiceHandler extends Handler {
//
//    public static final int OPEN = 0;
//    public static final int CLOSE = 1;
//    public static final int SEND = 2;
//    private Socket chatSocket;
//    private PrintWriter out;
//    private BufferedReader in;
//
//    public ServiceHandler(Looper looper) {
//        super(looper);
//    }
//
//    @Override
//    public void handleMessage(Message msg) {
//        switch (msg.what) {
//            case OPEN:
//                startConnection();
//                send("\n");
//                break;
//            case CLOSE:
//                stopConnection();
//                break;
//            case SEND:
//                String json = msg.getData().getString("json");
//                System.out.println(json);
//                String resp = send(json);
//                System.out.println(resp);
//                break;
//        }
//        // Stop the service using the startId, so that we don't stop
//        // the service in the middle of handling another job
////        connectionService.stopSelf(msg.arg1);
//    }
//
//    public void startConnection() {
//        String ip = "192.168.0.113";
//        int port = 5002;
//        try {
//            chatSocket = new Socket(ip, port);
//            in = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
//            out = new PrintWriter(chatSocket.getOutputStream(), true);
//            send("");
//        } catch (UnknownHostException e) {
//            System.err.println("Cannot find host called: " + ip);
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.err.println("Could not establish connection for " + ip);
//            e.printStackTrace();
//        }
//    }
//
//    public String send(String msg) {
//        out.println(msg);
//        String resp;
//        try {
//            resp = in.readLine();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return resp;
//    }
//
//    public void stopConnection() {
//        try {
//            in.close();
//            out.close();
//            chatSocket.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//}
