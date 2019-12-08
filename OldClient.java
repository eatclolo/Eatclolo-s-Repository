import java.io.*;
import java.net.Socket;

public class OldClient {
   public static void main(String[] args) throws Exception {
     Socket s = new Socket("127.0.0.1",8888);
     System.out.println("Host:" + s.getInetAddress().getHostAddress() + "connected");
     OutputStream clientStream = s.getOutputStream();
     InputStream serverStream = s.getInputStream();

     //byte[] key = ClientDH.generateDHKey(serverStream, clientStream);
   }

   public static void input(Socket s) throws IOException
   {
       InputStream is = s.getInputStream();
       OutputStream os = s.getOutputStream();
       BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
       //read flush
       String mess = br.readLine();
       System.out.println("clientG"+ mess);
   }

   public static void output(Socket s , String input) throws IOException
   {
       InputStream is = s.getInputStream();
       OutputStream os = s.getOutputStream();
       BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
       bw.write(input + "\n");
       bw.flush();
   }
}
