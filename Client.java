import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
 
public class Client {
   public static void main(String[] args) {
       int g = 30;
       int a = 40;
       int cal = g*a;
      try {
         Socket s = new Socket("127.0.0.1",8888);
         
         //io setup
         InputStream is = s.getInputStream();
         OutputStream os = s.getOutputStream();
         
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
         //flush to server
         bw.write(cal + "\n");
         bw.flush();
         //read from server
         BufferedReader br = new BufferedReader(new InputStreamReader(is));
         String mess = br.readLine();
         System.out.println("server¡G"+mess);
         int b = Integer.parseInt(mess);
         System.out.println(cal * b);
         
         /*BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(os));
         //flush to server
         bw2.write("testing message\n");
         bw2.flush();
         //read from server
         BufferedReader br2 = new BufferedReader(new InputStreamReader(is));
         String mess2 = br2.readLine();
         System.out.println("server¡G"+mess2);*/
      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}