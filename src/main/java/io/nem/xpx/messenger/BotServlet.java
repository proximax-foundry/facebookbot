package io.nem.xpx.messenger;

import com.restfb.*;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingItem;

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
    public static void upload(String msg) throws UploadException {

        final Upload upload = new Upload(remotePeerConnection); // blocking

        final UploadTextDataParameter parameter = UploadTextDataParameter.create()
                .senderPrivateKey(System.getenv("PRIVATE_KEY")).receiverPublicKey(System.getenv("PUBLIC_KEY")).data(msg)
                .encoding("UTF-8").build();

        final UploadResult uploadResult = upload.uploadTextData(parameter);
        String hash = uploadResult.getNemHash(); // prints the Nem Hash to download text

        System.out.println("Hash: '" + hash + "'");
    }

    /*
     * Handles request from FB webhook
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String body = req.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);

        JsonMapper mapper = new DefaultJsonMapper();
        WebhookObject whObject = mapper.toJavaObject(body, WebhookObject.class);

        /*
         * Looks for text message and uploads it to ProximaX
         */
        for (final WebhookEntry entry : whObject.getEntryList()) {
            for (final MessagingItem item : entry.getMessaging()) {
                if (item.isMessage()) {
                    final String msg = item.getMessage().getText();
                    final String sid = item.getMessage().getStickerId();

                    if (sid != null) {
                        System.out.println("Sticker: '" + sid + "'");
                    }
                    if (msg != null) {
                        System.out.println("Msg: '" + msg + "'");

                        try { 
                            upload(msg); 
                        } catch (UploadException e) { 
                            e.printStackTrace(); 
                        }
                    }
                }
            }
        }
    }
}
