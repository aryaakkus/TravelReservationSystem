package Server.RMI;

import java.util.*;
import java.io.*;
import java.net.*;
import Server.Interface.*;
import Server.Common.*;

public class TCPMiddleware extends ResourceManager {
    private ServerSocket serSocket;
    private Socket flightSocket;
    private Socket carSocket;
    private Socket roomSocket;

    public TCPMiddleware(String p_name) {
        super(p_name);
        // TODO Auto-generated constructor stub
    }

}
