package main;


import elements.Player;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.UUID;

/**
 *
 * @author Komabjn
 */
public class ConnectingServer implements Runnable {

    private Socket socket;
    private Server server;
    private Player player;
    private Model model;
    UUID id;
    
    public ConnectingServer(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            int index =0;
            try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
                out.flush();
                
                try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                    player = (Player) in.readObject();
                    id = player.getId();
                    System.out.println("Player connected: " + player.getName());
                    server.addPlayer(player);
                    out.writeObject(server.getModel());
                    out.flush();
                    
                    while (index>=0) {
                        player = (Player) in.readUnshared();
                        server.updatePlayer(player);
                        
                        //System.out.println("Odebralem");
                        model = server.getModel();
                        out.writeUnshared(model);
                        //System.out.println("Wyslalem");
                        out.flush();
                        out.reset();
                        index = model.getPlayerIndexById(id);
                        //model = null;
                        
                    }
                }
            } catch (ClassNotFoundException e2) {
                System.out.println("ObjectInputStream read failed");
                System.out.println(e2.toString());
            } catch (SocketException e) {
                if (index>=0) {
                    System.out.println("Gracz " + player.getName() + " rozłączył się");
                    server.getModel().remove(player);
                } else {
                    System.out.println("Gracz " + player.getName() + " przegral, zabil go " + player.getKillerName());
                }
            } catch (IOException e) {
                if (index>=0) {
                    System.out.println("Gracz " + player.getName() + " rozłączył się");
                    server.getModel().remove(player);
                } else {
                    System.out.println("Gracz " + player.getName() + " przegral, zabil go " + player.getKillerName());
                }
                System.out.println("ObjectInputStream initilization failed");
                System.out.println(e.toString());
            }
        } finally {
            try {
                //model.removePlayer(player);
                socket.close();
            } catch (IOException e) {
                System.out.println("Socket close failure");
                System.out.println(e.toString());
            }
        }

    }


    
}
