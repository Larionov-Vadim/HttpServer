package handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import request.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by vadim on 23.02.15.
 */
public class HttpRequestDecoder extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        StringBuilder request = new StringBuilder();
        ByteBuf in = (ByteBuf) msg;

        while (in.isReadable()) {
            byte[] bytes = new byte[in.readableBytes()];
            in = in.readBytes(bytes);
            request.append(new String(bytes));
        }
        in.release();

        String requestMethod = getRequestMethod(request.toString());
        String requestPath = null;
        boolean errors = false;
        try {
            requestPath = getRequestPath(request.toString());
        }
        catch (UnsupportedEncodingException e) {
            errors = true;
        }

        HttpRequest httpRequest = new HttpRequest(requestMethod, correctURI(requestPath));
        if (errors)
            httpRequest.setValid(false);
        ctx.fireChannelRead(httpRequest);
    }


    private String getRequestPath(String request) throws UnsupportedEncodingException {
        int beginPath = request.indexOf(" ") + 1;
        int endPath = request.indexOf(" ", beginPath);

        String path = request.substring(beginPath, endPath);
        path = URLDecoder.decode(path, "UTF-8");

        int queryIndex = path.indexOf("?");
        if (queryIndex != -1)
            path = path.substring(0, queryIndex);

        return path;
    }

    private String getRequestMethod(String request) {
        return request.substring(0, request.indexOf(" ")).toUpperCase();
    }

    private String correctURI(String uri) {
        return uri.replace("/..", "");
    }
}
