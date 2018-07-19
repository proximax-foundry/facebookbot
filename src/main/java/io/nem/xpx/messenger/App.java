package io.nem.xpx.messenger;
 
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class App {
		
    public static void main( String[] args) throws Exception {
    	  	
        Server server = new Server();
        
        /* 
         * HTTP connector
         * 
         * SERVER_HTTP_PORT - port to listen on
         * SERVER_HOST - address to listen on
         */
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(Integer.parseInt(System.getenv("SERVER_HTTP_PORT")));
        connector.setHost(System.getenv("SERVER_HOST"));
        
        /*
         * HTTPS connector
         * 
         * CERT_PATH - path to pkcs12 keystore file
         * CERT_PASSWORD - password for accesing keystore file
         * SERVER_HOST - address to listen on
         * SERVER_HTTPS_PORT - port to listen on
         */
        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(System.getenv("CERT_PATH"));
        sslContextFactory.setKeyStorePassword(System.getenv("CERT_PASSWORD"));
        sslContextFactory.setKeyStoreType("pkcs12");
        
        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
        sslConnector.setHost(System.getenv("SERVER_HOST"));
        sslConnector.setPort(Integer.parseInt(System.getenv("SERVER_HTTPS_PORT")));

        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder echoServlet = context.addServlet(BotServlet.class, "/echo");
        echoServlet.setInitOrder(0);

        /*
         * Server will listen both on http and https port
         */
        server.setConnectors(new Connector[] { connector, sslConnector });
        
        /*
         * Start the server
         */
        try {
        	server.start();
        	server.join();
        } finally {
        	server.destroy();
        }
    }
}
