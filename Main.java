import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

public class Main implements Runnable {

    private int onScreenX, onScreenY;

    public void run() {
        // Invoked on the event dispatching thread.
        // Construct and show GUI.
        initialize();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main());
    }

    // Initialize User Interface
    private void initialize() {
        JFrame imFrame = new JFrame("Secure Instant Messenger");

        // Icon
        ImageIcon simIcon = new ImageIcon( this.getClass().getResource( "/SecureIM.png" ) );
        ImageIcon titleIcon = new ImageIcon( this.getClass().getResource( "/title.png" ) );
        ImageIcon exitIcon = new ImageIcon( this.getClass().getResource( "/exit.png" ) );
        ImageIcon sendIcon = new ImageIcon( this.getClass().getResource( "/send.png" ) );

        /*                                                Banner                                                 */

        JPanel imBanner = new JPanel( new BorderLayout() );
        imBanner.setPreferredSize( new Dimension( 266, 600 ) );
        imBanner.setBorder( new EmptyBorder( 10, 10, 10, 10) );

        JLabel imIcon = new JLabel( simIcon );
        JLabel imTitle = new JLabel(titleIcon);
        JLabel imExit = new JLabel(exitIcon );

        imBanner.add( imIcon, BorderLayout.NORTH );
        imBanner.add( imTitle, BorderLayout.CENTER );
        imBanner.add( imExit, BorderLayout.SOUTH );
        imBanner.setBackground( new Color( 230, 80, 80 ) );

        /*                                              End Banner                                               */

        /*                                                 Body                                                  */
        // NORTH - Header
        JLabel chatHeader = new JLabel("Chat Room", JLabel.CENTER );
        chatHeader.setForeground( new Color( 255, 153, 51 ) );
        chatHeader.setFont( new Font( "Segoe UI", Font.BOLD, 15 ) );
        // CENTER - Chat area
        JPanel chatArea = new JPanel();
        chatArea.setLayout( new BoxLayout( chatArea, BoxLayout.Y_AXIS ));
        chatArea.setBorder( BorderFactory.createTitledBorder( BorderFactory.createMatteBorder( 10, 10,
                10, 10, new Color( 0, 51, 102 ) ), "Chat Room", TitledBorder.LEFT, TitledBorder.TOP ) );

        JScrollPane chatScrollPane = new JScrollPane( chatArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        // SOUTH - Input
        JPanel imInput = new JPanel();
        imInput.setBackground( new Color(220,220, 220 ) );

        JTextField txtInput = new JTextField();
        txtInput.setPreferredSize( new Dimension( 450, 25 ) );

        JLabel imsend = new JLabel( sendIcon );
        imInput.add( txtInput );
        imInput.add( imsend );

        // Body Panel
        JPanel imBody = new JPanel( new BorderLayout() );
        imBody.setBackground( new Color(16,32, 53 ) );
        imBody.setBorder( new EmptyBorder( 10, 10, 10, 10) );

        imBody.add( chatHeader, BorderLayout.NORTH);
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

        /* Handle sending message
        imsend.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                AbstractBorder txtBubbleOwner = new TextBubbleBorder( Color.BLACK,2,16,16 );

                JPanel hMessageBar = new JPanel();
                hMessageBar.setAlignmentX( Component.CENTER_ALIGNMENT );
                hMessageBar.setLayout( new BoxLayout( hMessageBar, BoxLayout.Y_AXIS ) );
                hMessageBar.setMaximumSize( new Dimension( 500, 75 ) );

                JPanel message = new JPanel();
                message.setLayout( new BoxLayout( message, BoxLayout.Y_AXIS ) );

                JLabel owner = new JLabel("Kyle");
                JLabel text = new JLabel( txtInput.getText() );

                text.setBorder( txtBubbleOwner );

                message.add(owner);
                message.add(text);

                if( txtInput.getText().equalsIgnoreCase( "x" ) ) {
                    message.setAlignmentX( Component.RIGHT_ALIGNMENT );
                }else {
                    if( txtInput.getText().equalsIgnoreCase( "y" ) ) {
                        message.setAlignmentX( Component.CENTER_ALIGNMENT );
                    }else {
                        message.setAlignmentX( Component.LEFT_ALIGNMENT );
                    }
                }

                hMessageBar.add(message);
                chatArea.add( hMessageBar );

                chatArea.revalidate();
                txtInput.setText("");
            }
        });
        */
        // Handle EXIT
        imExit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit( 0 );
            }
        });

        /*                                           End Event Handling                                         */

    }

}