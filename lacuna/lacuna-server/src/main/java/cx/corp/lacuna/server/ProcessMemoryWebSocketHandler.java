package cx.corp.lacuna.server;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.StringValue;
import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.domain.NativeProcessImpl;
import cx.corp.lacuna.server.data.ReadRequest;
import cx.corp.lacuna.server.data.ReadResponse;
import cx.corp.lacuna.server.data.StatusCode;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@WebSocket
public class ProcessMemoryWebSocketHandler {

    private final MemoryReader memoryReader;

    public ProcessMemoryWebSocketHandler(MemoryReader reader) {
        this.memoryReader = reader;
    }

    @OnWebSocketConnect
    public void onConnect(Session user) {

        System.out.println("OnConnect " + user);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        System.out.println("OnClose:   " + statusCode + " - " + reason);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, byte buf[], int offset, int length) throws IOException {
        ReadRequest request;

        try {
            request = ReadRequest
                .newBuilder()
                .mergeFrom(buf, offset, length)
                .build();
        } catch (InvalidProtocolBufferException ex) {
            ex.printStackTrace();
            return;
        }

        ReadResponse response = getResponse(request);
        System.out.println("Sending response: "  + response);
        user.getRemote().sendBytes(ByteBuffer.wrap(response.toByteArray()));
    }

    private ReadResponse getResponse(ReadRequest request) {
        switch (request.getRequestType()) {
            case BUFFER:
                break;
            case BOOLEAN:
                break;
            case BYTE:
                break;
            case CHAR_UTF8:
                break;
            case CHAR_UTF16:
                break;
            case SHORT:
                break;
            case INT:
                int readMemory = memoryReader.readInt(new NativeProcessImpl(
                    request.getPid(), null, null
                ), request.getOffset());

                ReadResponse response = ReadResponse.newBuilder()
                    .setStatus(StatusCode.OK)
                    .setRequest(request)
                    .setData(Any.pack(Int32Value.newBuilder().setValue(readMemory).build()))
                    .build();

                break;
            case FLOAT:
                break;
            case LONG:
                break;
            case DOUBLE:
                break;
            case STRING_UTF8:
                break;
            case STRING_UTF16:
                break;
            case UNRECOGNIZED:
                break;
        }

        return ReadResponse.newBuilder()
            .setStatus(StatusCode.ERROR)
            .setRequest(request)
            .setData(Any.pack(StringValue.newBuilder().setValue("Type not supported yet").build()))
            .build();
    }
}
