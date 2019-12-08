import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class OldServer {
   public static void main(String[] args) throws Exception {
     ServerSocket ss = new ServerSocket(8888);
     System.out.println("Server start....");
     Socket s = ss.accept();
     System.out.println("Client:"+s.getInetAddress().getLocalHost()+"connected");
     OutputStream serverStream = s.getOutputStream();
     InputStream clientStream = s.getInputStream();

     //byte[] key = ServerDH.generateDHKey(clientStream, serverStream);
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
