/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import elements.Block;
import elements.Player;
import elements.PowerUp;
import elements.Projectile;
import static enums.BlockEnum.FRAME;
import static enums.BlockEnum.HARD;
import static enums.BlockEnum.MEDIUM;
import enums.PowerUpEnum;
import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.Random;
import static java.lang.String.format;
import static java.lang.Thread.sleep;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Iterator;
import java.util.logging.Level;

/**
 *
 * @author Tomasz
 */
public class Server {
    public static final int DIMENSIONX = 1280;
    public static final int DIMENSIONY = 720;
    private int serverPort = 2137;
    
    private static Logger log = Logger.getLogger(Server.class.getCanonicalName());
    Random random = new Random();
    int id = 0;
    Model model = new Model();

    
   
    
    
   
    
    public Server() {
        

        
                
        

    }
    
    public synchronized void newBoard() {
        model.getBlocks().clear();
        model.getPowerUps().clear();
        model.getProjectiles().clear();
        // ramka
        model.add(new Block(new Point(DIMENSIONX / 2, 0), FRAME, DIMENSIONX, 30));
        model.add(new Block(new Point(DIMENSIONX / 2, DIMENSIONY - 30), FRAME, DIMENSIONX, 30));
        model.add(new Block(new Point(0, DIMENSIONY / 2), FRAME, 30, DIMENSIONY));
        model.add(new Block(new Point(DIMENSIONX, DIMENSIONY / 2), FRAME, 30, DIMENSIONY));

        //Blocks test
        model.add(new Block(new Point(200, 400), HARD, 200, 50));
        model.add(new Block(new Point(450, 350), HARD, 50, 300));
        model.add(new Block(new Point(1000, 300), HARD, 150, 50));
        model.add(new Block(new Point(1000, 450), HARD, 150, 50));
        model.add(new Block(new Point(1100, 250), HARD, 50, 150));
        model.add(new Block(new Point(1100, 500), HARD, 50, 150));
        model.add(new Block(new Point(700, 150), MEDIUM, 80, 80));
        model.add(new Block(new Point(700, 400), MEDIUM, 80, 80));
        model.add(new Block(new Point(700, 600), MEDIUM, 80, 80));

        //PowerUps test
        model.add(new PowerUp(new Point(100, 100), PowerUpEnum.TRIPLESHOT));
        model.add(new PowerUp(new Point(250, 500), PowerUpEnum.BIGGERSHOT));
        model.add(new PowerUp(new Point(700, 70), PowerUpEnum.FASTPLAYER));
        model.add(new PowerUp(new Point(600, 600), PowerUpEnum.SLOWPLAYER));
        model.add(new PowerUp(new Point(1150, 250), PowerUpEnum.SMALLERPLAYER));
        model.add(new PowerUp(new Point(1000, 550), PowerUpEnum.FASTSHOT));
    }
    
    public synchronized void setModel(Model model) {
            this.model = model;
    }
    
    public synchronized Model getModel() {
            return model;
    }
    
    public synchronized void addPlayer(Player p) {
        int index = model.getPlayerIndexById(p.getId());
        if (model.getPlayerIndexById(p.getId()) <0) {
            Point start;
            do {
                start = new Point(random.nextInt(DIMENSIONX / 2), random.nextInt(DIMENSIONY / 2));
                p.setPosition(start);
            } while (!model.searchCollisions(p).isEmpty());
            p.setPrimaryColor(new Color(random.nextInt(0xFFFFFF)));
            
            if (model.getPlayers().size()<=0) {
                //Pierwsze polaczenie
                newBoard();
                model.setMaxPlayers(0);
            }
            model.incrementMaxPlayers();

            model.add(p);
           
            System.out.print("[INFO] Podlaczonych graczy " + model.getPlayers().size() + " ");
            for (Player player : model.getPlayers()) {
                System.out.print(player.getName() + " ");
            }
            System.out.println("");
        }
    }
    
    public synchronized void updatePlayer(Player p) {
        int index = model.getPlayerIndexById(p.getId());
        if (index>=0) {
            model.getPlayers().set(index, p);
            model.collisions();
            model.update();
        }
    }
    
    public void listen() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            log.info(format("Listening on port %d",serverPort));
            
            while (true) {
                final Socket socket = serverSocket.accept();
                
                new Thread(new ConnectingServer(socket, this)).start();
                
                //ExecutorService executor1 = Executors.newSingleThreadExecutor();
                //executor1.submit(()->handleClient(socket));
            }
            
        } catch (IOException e) {
            //log.log(SEVERE, e.getMessage(), e);
            
        }
    }
    
  

    
   

    
}