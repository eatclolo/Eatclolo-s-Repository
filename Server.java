import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;


public class Server {
    
    static int onScreenX;
    static int onScreenY;
    
    static byte[] key = null;
    static MessageAuthentication mAuthentication;
    static MessageSignature mSignature;
    static MessageEncryption mEncryption;
    static Communication communication;
    
    static boolean connected = false;
    static OutputStream serverStream;
    static InputStream clientStream;
    static Socket socket;
    
    //Actually read and write to the client
    public static void handleClient(Socket socket, BufferedReader input) throws Exception {
        serverStream = socket.getOutputStream();
        clientStream = socket.getInputStream();
        connected = true;
        
        communication = new Communication();
        mAuthentication = new MessageAuthentication();
        mSignature = new MessageSignature();
        mEncryption = new MessageEncryption();
        
        //generateSecretKey
        key = ServerDH.generateDHKey(clientStream, serverStream);
        
        //generatePublicPrivateKey
        KeyPair keyPair = mSignature.generatePublicPrivateKey();
        PrivateKey privKey = keyPair.getPrivate();
        PublicKey ownPubKey = keyPair.getPublic();
        
        PublicKey otherPubKey = mSignature.exchangePublicKey(ownPubKey, clientStream, serverStream);
        
        JFrame imFrame = new JFrame("Secure Instant Messenger");
        
        // Icon
        java.net.URL secure = Server.class.getResource("SecureIM.png");
        java.net.URL title = Server.class.getResource("title.png");
        java.net.URL exit = Server.class.getResource("exit.png");
        java.net.URL send = Server.class.getResource("send.png");
        java.net.URL lock = Server.class.getResource("lock.png");
        
        ImageIcon simIcon = new ImageIcon( secure );
        ImageIcon exitIcon = new ImageIcon(exit);
        ImageIcon sendIcon = new ImageIcon(send);
        ImageIcon titleIcon = new ImageIcon(title);
        ImageIcon lockIcon = new ImageIcon(lock);
        
        /*                                                Banner                                                 */
        
        JPanel imBanner = new JPanel( new BorderLayout() );
        imBanner.setPreferredSize( new Dimension( 266, 600 ) );
        imBanner.setBorder( new EmptyBorder( 10, 10, 10, 10) );
        
        JLabel imIcon = new JLabel ( simIcon );
        JLabel imExit = new JLabel (exitIcon );
        JLabel imTitle = new JLabel(titleIcon);
        
        imBanner.add( imTitle, BorderLayout.CENTER );
        imBanner.add( imIcon, BorderLayout.NORTH );
        imBanner.add( imExit, BorderLayout.SOUTH );
        imBanner.setBackground( new Color( 230, 80, 80 ) );
        
        /*                                              End Banner                                               */
        
        /*                                                 Body                                                  */
        // North - Header
        JLabel username = new JLabel("User: Bosco");
        username.setForeground( new Color( 230, 80, 80 ) );
        username.setFont( new Font( "Segoe UI", Font.BOLD, 15 ) );
        // CENTER - Chat area
        JPanel chatArea = new JPanel();
        chatArea.setBackground( new Color(16,32, 53 ) );
        chatArea.setLayout( new BoxLayout( chatArea, BoxLayout.Y_AXIS ));

        JScrollPane chatScrollPane = new JScrollPane( chatArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        chatScrollPane.setBackground( new Color(16,32, 53 ) );
        chatScrollPane.setBorder( BorderFactory.createTitledBorder( new MatteBorder( 10, 10 , 10, 10 , lockIcon ), " Chat Room - Kyle ", TitledBorder.CENTER, TitledBorder.TOP, new Font( "Segoe UI", Font.BOLD, 15 ), new Color( 230, 80, 80 ) ) );
        // SOUTH - Input
        JPanel imInput = new JPanel();
        imInput.setBackground( new Color(16,32, 53 ) );

        JTextField txtInput = new JTextField();
        txtInput.setPreferredSize( new Dimension( 450, 25 ) );
        txtInput.setBorder( BorderFactory.createLineBorder( new Color( 230, 80, 80 ), 2, true ) );
        
        JLabel imsend = new JLabel( sendIcon );
        imInput.add( txtInput );
        imInput.add( imsend );
        
        // Body Panel
        JPanel imBody = new JPanel( new BorderLayout() );
        imBody.setBackground( new Color(16,32, 53 ) );
        imBody.setBorder( new EmptyBorder( 10, 10, 10, 10) );

        imBody.add( username, BorderLayout.NORTH );
        imBody.add( chatScrollPane, BorderLayout.CENTER );
        imBody.add( imInput, BorderLayout.SOUTH );
        
        /*                                               End Body                                               */
        
        // Frame
        imFrame.getContentPane().add( BorderLayout.WEST, imBanner );
        imFrame.getContentPane().add( BorderLayout.CENTER, imBody );
        
        imFrame.setSize( 800, 600 );
        imFrame.setUndecorated(true);
        imFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imFrame.setIconImage( imFrame.getToolkit().getImage("SecureIM.png" ) );
        imFrame.setShape( new RoundRectangle2D.Double( 0, 0, 800, 600, 75, 75 ) );
        imFrame.setLocationRelativeTo(null);
        imFrame.setVisible(true);
        
        
        /*                                             Event Handling                                           */
        // Handle user drag the frame ( Move it move )
        imFrame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                onScreenX = e.getX();
                onScreenY = e.getY();
            }
        });
        
        imFrame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                imFrame.setLocation( e.getXOnScreen() - onScreenX, e.getYOnScreen() - onScreenY );
            }
        });
        
        // Handle EXIT
        imExit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try{
                    disconnect();
                } catch (Exception exception){
                    exception.printStackTrace();
                }
                System.exit(0);
            }
        });
        
        //Spins up a thread for reading from input and sending to outputstream
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                imsend.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent event) {
                        
                        String send;
                        byte[] encrypted;
                        try {
                            send = txtInput.getText();
                            if (!socket.isClosed()) {
                                //Format the message.
                                //Path keyPath = Paths.get("server_private", "private.der");
                                //PrivateKey privKey = readPrivateKey(keyPath);
                                byte[] signature = mSignature.sign(send.getBytes(), privKey);
                                byte[] mac = mAuthentication.generateMAC(send.getBytes(), key);
                                byte[] message = mEncryption.encrypt(send.getBytes(), key);
                                byte[] finalMessage = communication.combineMessage(message, signature, mac);
                                serverStream.write(finalMessage);
                                
                                TextBubbleBorder txtBubbleSelf = new TextBubbleBorder(new Color( 255, 153, 51 ),2,16,8 );
                                txtBubbleSelf.setPointerSide(SwingConstants.RIGHT);
                                
                                JPanel hMessageBar = new JPanel();
                                hMessageBar.setAlignmentX( Component.CENTER_ALIGNMENT );
                                hMessageBar.setLayout( new BoxLayout( hMessageBar, BoxLayout.Y_AXIS ) );
                                hMessageBar.setBackground( new Color( 16, 32, 53 ) );
                                hMessageBar.setMaximumSize( new Dimension( 500, 75 ) );
                                
                                JPanel sendMessage = new JPanel();
                                sendMessage.setBackground( new Color( 16, 32, 53 ) );
                                sendMessage.setLayout( new BoxLayout( sendMessage, BoxLayout.Y_AXIS ) );

                                JLabel self = new JLabel("Bosco");
                                self.setForeground( new Color( 255, 153, 51 ) );
                                JLabel text = new JLabel( send );
                                text.setForeground( new Color( 255, 153, 51 ) );
                                text.setFont( new Font( "Segoe UI", Font.BOLD, 20 ) );
                                text.setBorder( txtBubbleSelf );

                                sendMessage.add( self );
                                sendMessage.add( text );
                                
                                sendMessage.setAlignmentX( Component.RIGHT_ALIGNMENT );
                                
                                hMessageBar.add(sendMessage);
                                chatArea.add( hMessageBar );
                                
                                chatArea.revalidate();
                            }
                            txtInput.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        
        //Spin up a thread to read from inputstream and write to command line.
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        byte[] msg = new byte[16 * 1024];
                        int count = clientStream.read(msg);
                        msg = Arrays.copyOf(msg, count);
                        String message = communication.handleMessage(msg, otherPubKey, mAuthentication, mSignature, mEncryption, key);
                        
                        TextBubbleBorder txtBubbleOps = new TextBubbleBorder( new Color( 0, 255, 68 ) ,2,16,8 );
                        txtBubbleOps.setPointerSide( SwingConstants.LEFT );
                        
                        JPanel hMessageBar = new JPanel();
                        hMessageBar.setAlignmentX( Component.CENTER_ALIGNMENT );
                        hMessageBar.setLayout( new BoxLayout( hMessageBar, BoxLayout.Y_AXIS ) );
                        hMessageBar.setBackground( new Color( 16, 32, 53 ) );
                        hMessageBar.setMaximumSize( new Dimension( 500, 75 ) );
                        
                        JPanel readMessage = new JPanel();
                        readMessage.setBackground( new Color( 16, 32, 53 ) );
                        readMessage.setLayout( new BoxLayout( readMessage, BoxLayout.Y_AXIS ) );

                        JLabel ops = new JLabel("Kyle");
                        ops.setForeground( new Color( 0, 255, 68 ) );
                        JLabel text = new JLabel( message );
                        text.setForeground( new Color( 0, 255, 68 ) );
                        text.setFont( new Font( "Segoe UI", Font.BOLD, 20 ) );
                        text.setBorder( txtBubbleOps );

                        readMessage.add( ops );
                        readMessage.add( text) ;
                        
                        readMessage.setAlignmentX( Component.LEFT_ALIGNMENT );
                        
                        hMessageBar.add(readMessage);
                        chatArea.add( hMessageBar );
                        
                        chatArea.revalidate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        sendMessage.start();
        readMessage.start();
    }

/*                                           End Event Handling                                         */
    
    //Clean up streams
    public static void disconnect() throws Exception {
        serverStream.close();
        clientStream.close();
        socket.close();
    }
    
    //reads users input and listens on the specific port.
    public static void main(String[] args) throws Exception {
        int portNumber = 8080;
        /*if (args.length != 1) {
         System.out.println("Usage: java server <port>");
         return;
         }
         portNumber = Integer.parseInt(args[0]);*/
        
        //Setup server
        boolean isOver = false;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        ServerSocket ss = new ServerSocket(portNumber);
        
        //Loops and creates new ClientHandler objects, allows for server to persist even when client closes connection.
        while (!isOver) {
            socket = ss.accept();
            System.err.println("Open session message recieved, comparing protocol.");
            handleClient(socket, input);
        }
        ss.close();
        input.close();
    }
}

