package edu.kindergarten.registration.chat_.internal;

import edu.kindergarten.registration.chat_.model.Reply;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


public class ReplyEncoder implements Encoder.Text<Reply> {

    @Override
    public String encode(Reply reply) throws EncodeException {
        System.out.println("reply.getMsg() "+reply.getMsg());
        return reply.getMsg();
    }

    @Override
    public void init(EndpointConfig ec) {
        //no-op
    }

    @Override
    public void destroy() {
        //no-op
    }
    
}
