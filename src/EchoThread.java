import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Summer on 1/28/2018.
 * Edited by Kristoffer on 1/29/2018.
 */
public class EchoThread implements Runnable {
    // initialize variables
    private Socket clientSocket;
    BufferedReader fromClient = null;
    PrintWriter toClient = null;

    // instantiate a thread instance and pass the EchoThread to it
    EchoThread(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {

        try {
            // thread attempts to create readers and writers for the client socket
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            toClient = new PrintWriter(clientSocket.getOutputStream(), true);
            Termination term = new Termination();
            int returned;

            while(true) {
                returned = fromClient.read();
                System.out.println("Read input: "+ returned);
                if (returned == -1) {
                    break;
                }
                char charFromClient = (char)returned;
                if (Character.isLetter(charFromClient)) {
                    System.out.println("Read input: " + charFromClient);
                    toClient.println(charFromClient);
                    System.out.println("Post client write");
                    if (term.terminate(charFromClient)) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to read or write input, or client connection was severed.");
        }
        finally {
            close();
        }
    }

    public void close() {
        try {
            fromClient.close();
            toClient.close();
            clientSocket.close();
        } catch(IOException e) {
            System.out.println("Connection did not close properly.");
        }
    }
}

class Termination {
    int state;

    Termination(){
        state = 0;
    }

    boolean terminate(char input) {
        if (input == 'q') {
            this.state = 1;
        }
        else if (input == 'u' && state == 1) {
            this.state = 2;
        }
        else if (input == 'i' && state == 2) {
            this.state = 3;
        }
        else if (input == 't' && state == 3) {
            this.state = 4;
        }
        else {
            this.state = 0;
        }
        // return a boolean to signal a quit
        return this.state == 4;
    }
}
