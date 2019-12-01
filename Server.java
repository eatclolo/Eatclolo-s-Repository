import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
 
public class Server {
   public static void main(String[] args) {
	   int g = 30;
	   int a = 20;
	   int cal = g * a;
      try {
         ServerSocket ss = new ServerSocket(8888);
         System.out.println("server start....");
         Socket s = ss.accept();
         System.out.println("client:"+s.getInetAddress().getLocalHost()+"connected");
         
         BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
         //read flush
         String mess = br.readLine();
         System.out.println("client¡G"+ mess);
         int b = Integer.parseInt(mess);
         System.out.println(cal * b);
         
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
         bw.write(cal +"\n");
         bw.flush();
         
         /*BufferedReader br2 = new BufferedReader(new InputStreamReader(s.getInputStream()));
         //read flush
         String mess2 = br2.readLine();
         System.out.println("client¡G"+mess2);
         BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
         bw2.write(mess2+"\n");
         bw2.flush();*/
         
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}