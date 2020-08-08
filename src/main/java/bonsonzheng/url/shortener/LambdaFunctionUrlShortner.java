package bonsonzheng.url.shortener;

import bonsonzheng.url.shortener.data.UrlShortenReq;
import bonsonzheng.url.shortener.service.UrlShortenService;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class LambdaFunctionUrlShortner implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        String input = IOUtils.toString(inputStream, "UTF-8");

        UrlShortenReq req = new Gson().fromJson(input, UrlShortenReq.class);

        if ("POST".equalsIgnoreCase(req.getHttpMethod())) {
            handlePost(outputStream, req);
        } else if ("GET".equalsIgnoreCase(req.getHttpMethod())) {
            handleGet(outputStream, req);
        }

    }


    private void handleGet(OutputStream outputStream, UrlShortenReq req) throws IOException {
        String originalUrl = UrlShortenService.getInstance().getUrl(req.getShortUrl());
        JSONObject response;

        if(originalUrl==null){
            response = new JSONObject().put("statusCode", 404);

        }else{
            response = new JSONObject().put("statusCode", 301).put("headers", new JSONObject().put("Location", originalUrl));
        }

        outputStream.write(response.toString().getBytes(Charset.forName("UTF-8")));
    }

    private void handlePost(OutputStream outputStream, UrlShortenReq req) {
        String longUrl = req.getPayload().getLongUrl();

        try {
            String shortenUrl = UrlShortenService.getInstance().shortenUrl(longUrl);

            JSONObject response = new JSONObject().put("longUrl", longUrl).put("shortUrl", shortenUrl);
            outputStream.write(response.toString().getBytes(Charset.forName("UTF-8")));

        } catch (Exception e) {
            throw new RuntimeException("Failed to shorted url ", e);
        }
    }

}
