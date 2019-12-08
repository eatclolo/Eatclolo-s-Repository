import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;

public class Client {
    
    static int onScreenX;
    static int onScreenY;
    static byte[] key = null;
    static MessageAuthentication mAuthentication;
    static MessageSignature mSignature;
    static MessageEncryption mEncryption;
    static Communication communication = new Communication();
    
    //Starts the client and connects to the specific server:port
    public static void startClient(String serverName, int serverPort) throws Exception {
        Socket s = new Socket(serverName, serverPort);
        OutputStream clientStream = s.getOutputStream();
        InputStream serverStream = s.getInputStream();
        
        //Make the socket and get the I/O streams.
        //BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        
        mAuthentication = new MessageAuthentication();
        mSignature = new MessageSignature();
        mEncryption = new MessageEncryption();
        
        //generateSecretKey
        key = ClientDH.generateDHKey(serverStream, clientStream);
        
        //generatePublicPrivateKey
        KeyPair keyPair = mSignature.generatePublicPrivateKey();
        PrivateKey privKey = keyPair.getPrivate();
        PublicKey ownPubKey = keyPair.getPublic();
        
        PublicKey otherPubKey = mSignature.exchangePublicKey(ownPubKey, serverStream, clientStream);
        
        JFrame imFrame = new JFrame("Secure Instant Messenger");
        
        // Icon
        java.net.URL secure = Server.class.getResource("SecureIM.png");
        java.net.URL title = Server.class.getResource("title.png");
        java.net.URL exit = Server.class.getResource("exit.png");
        java.net.URL send = Server.class.getResource("send.png");
        java.net.URL lock = Server.class.getResource("lock.png");

        ImageIcon simIcon = new ImageIcon(secure);
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
        JLabel username = new JLabel("User: Kyle");
        username.setForeground( new Color( 230, 80, 80 ) );
        username.setFont( new Font( "Segoe UI", Font.BOLD, 15 ) );
        // CENTER - Chat room
        JPanel chatArea = new JPanel();
        chatArea.setBackground( new Color(16,32, 53 ) );
        chatArea.setLayout( new BoxLayout( chatArea, BoxLayout.Y_AXIS ));
        
        JScrollPane chatScrollPane = new JScrollPane( chatArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        chatScrollPane.setBackground( new Color(16,32, 53 ) );
        chatScrollPane.setBorder( BorderFactory.createTitledBorder( new MatteBorder( 10, 10 , 10, 10 , lockIcon ), " Chat Room - Bosco ", TitledBorder.CENTER, TitledBorder.TOP, new Font( "Segoe UI", Font.BOLD, 15 ), new Color( 230, 80, 80 ) ) );
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
                    disconnect(serverStream, clientStream, s);
                } catch (Exception exception){
                    exception.printStackTrace();
                }
                System.exit( 0 );
            }
        });
        
        //thread for sending formatted messages to the server
        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                imsend.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent event) {
                        
                        try{
                            String send;
                            send = txtInput.getText();
                            //we need private key and public key for instances
                            //Path keyPath = Paths.get("client_private", "private.der");
                            //PrivateKey privKey = readPrivateKey(keyPath);
                            byte[] signature = mSignature.sign(send.getBytes(), privKey);
                            byte[] mac = mAuthentication.generateMAC(send.getBytes(), key);
                            byte[] message = mEncryption.encrypt(send.getBytes(), key);
                            byte[] formattedMessage = communication.combineMessage(message, signature, mac);
                            clientStream.write(formattedMessage);
                            
                            TextBubbleBorder txtBubbleSelf = new TextBubbleBorder( new Color( 255, 153, 51 ),2,16,8 );
                            txtBubbleSelf.setPointerSide(SwingConstants.RIGHT);
                            
                            JPanel hMessageBar = new JPanel();
                            hMessageBar.setAlignmentX( Component.CENTER_ALIGNMENT );
                            hMessageBar.setLayout( new BoxLayout( hMessageBar, BoxLayout.Y_AXIS ) );
                            hMessageBar.setBackground( new Color( 16, 32, 53 ) );
                            hMessageBar.setMaximumSize( new Dimension( 500, 75 ) );
                            
                            JPanel sendMessage = new JPanel();
                            sendMessage.setBackground( new Color( 16, 32, 53 ) );
                            sendMessage.setLayout( new BoxLayout( sendMessage, BoxLayout.Y_AXIS ) );

                            JLabel self = new JLabel("Kyle");
                            self.setForeground( new Color( 255, 153, 51 ) );
                            JLabel text = new JLabel( send );
                            text.setForeground( new Color( 255, 153, 51 ) );
                            text.setFont( new Font( "Segoe UI", Font.BOLD, 20 ) );
                            text.setBorder( txtBubbleSelf );

                            sendMessage.add( self );
                            sendMessage.add( text );
                            
                            sendMessage.setAlignmentX( Component.RIGHT_ALIGNMENT );
                            
                            hMessageBar.add( sendMessage );
                            chatArea.add( hMessageBar );
                            
                            chatArea.revalidate();
                            
                            txtInput.setText("");
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                
            }
        });
        
        //thread for reading from the input socket stream
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true) {
                        byte[] msg = new byte[16 * 1024];
                        int count = serverStream.read(msg);
                        msg = Arrays.copyOf(msg, count);
                        //Path keyPath = Paths.get("client_private", "publicServer.der");
                        String message = communication.handleMessage(msg, otherPubKey, mAuthentication, mSignature, mEncryption, key);
                        
                        TextBubbleBorder txtBubbleOps = new TextBubbleBorder( new Color( 0, 255, 68 ),2,16,8 );
                        txtBubbleOps.setPointerSide(SwingConstants.LEFT);
                        
                        JPanel hMessageBar = new JPanel();
                        hMessageBar.setAlignmentX( Component.CENTER_ALIGNMENT );
                        hMessageBar.setLayout( new BoxLayout( hMessageBar, BoxLayout.Y_AXIS ) );
                        hMessageBar.setBackground( new Color( 16, 32, 53 ) );
                        hMessageBar.setMaximumSize( new Dimension( 500, 75 ) );
                        
                        JPanel readMessage = new JPanel();
                        readMessage.setBackground( new Color( 16, 32, 53 ) );
                        readMessage.setLayout( new BoxLayout( readMessage, BoxLayout.Y_AXIS ) );

                        JLabel ops = new JLabel("Bosco");
                        ops.setForeground( new Color( 0, 255, 68 ) );
                        JLabel text = new JLabel( message );
                        text.setForeground( new Color( 0, 255, 68 ) );
                        text.setFont( new Font( "Segoe UI", Font.BOLD, 20 ) );
                        text.setBorder( txtBubbleOps );

                        readMessage.add( ops );
                        readMessage.add( text );
                        
                        readMessage.setAlignmentX( Component.LEFT_ALIGNMENT );
                        
                        hMessageBar.add(readMessage);
                        chatArea.add( hMessageBar );
                        
                        chatArea.revalidate();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        
        sendMessage.start();
        readMessage.start();
    }
    
    //Clean up connection with server
    public static void disconnect(InputStream serverStream, OutputStream clientStream, Socket socket) throws Exception {
        serverStream.close();
        clientStream.close();
        socket.close();
    }
    
    //Reads from the command line and starts the client.
    public static void main(String args[]) throws Exception {
        /*if (args.length != 2) {
         System.out.println("Usage: java Client <host> <port>");
         return;
         }
         String hostName = args[0];
         int portNumber = Integer.parseInt(args[1]);*/
        
        startClient("127.0.0.1", 8080);
        
    }
}
