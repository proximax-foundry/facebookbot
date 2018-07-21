package io.nem.xpx.messenger;

import com.restfb.*;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingItem;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.Message;
import com.restfb.types.GraphResponse;

import io.nem.xpx.facade.connection.RemotePeerConnection;
import io.nem.xpx.facade.upload.Upload;
import io.nem.xpx.facade.upload.UploadException;
import io.nem.xpx.facade.upload.UploadResult;
import io.nem.xpx.facade.upload.UploadTextDataParameter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BotServlet extends AbstractFacebookBotServlet {
    /*
     * Establish connection to ProximaX
     * 
     * PROXIMAX_ADDR - URL address of ProximaX gateway
     */
    static RemotePeerConnection remotePeerConnection = new RemotePeerConnection(System.getenv("PROXIMAX_ADDR"));

    /*
     * Uploads text message `msg` to ProximaX
     * 
     * PRIVATE_KEY - Private key of a sender account PUBLIC_KEY - Public key of a
     * receiver account
     */
    public static String upload(String msg) throws UploadException {

        final Upload upload = new Upload(remotePeerConnection); // blocking

        final UploadTextDataParameter parameter = UploadTextDataParameter.create()
                .senderPrivateKey(System.getenv("PRIVATE_KEY")).receiverPublicKey(System.getenv("PUBLIC_KEY")).data(msg)
                .encoding("UTF-8").build();

        final UploadResult uploadResult = upload.uploadTextData(parameter);
        String hash = uploadResult.getNemHash(); // prints the Nem Hash to download text

        return hash;
    }

    /*
     * Handles request from FB webhook
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String body = req.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        String hash;
        
        System.out.println(body);
        
        JsonMapper mapper = new DefaultJsonMapper();
        WebhookObject whObject = mapper.toJavaObject(body, WebhookObject.class);

        for (final WebhookEntry entry : whObject.getEntryList()) {
            for (final MessagingItem item : entry.getMessaging()) {
                final String senderId = item.getSender().getId();
                Message simpleTextMessage = null;

                if (item.getMessage() != null) {
                    // build the recipient
                    final IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        
                    // send response if it is a message and not send a "echo"
                    if (!item.getMessage().isEcho()) {
                        try { 
                            hash = upload(body); 
                        } catch (UploadException e) { 
                            e.printStackTrace();
                            
                            return;
                        }
         
                        simpleTextMessage = new Message(System.getenv("PROXIMAX_ADDR") + "/xpxfs/" + hash);
                        
                        System.out.println("sending");
                        final FacebookClient sendClient = new DefaultFacebookClient(System.getenv("ACCESS_TOKEN"), Version.VERSION_2_7);
                        sendClient.publish("me/messages", GraphResponse.class, Parameter.with("recipient", recipient), Parameter.with("message", simpleTextMessage));
                    }
                }
            }
        }
    }
}
