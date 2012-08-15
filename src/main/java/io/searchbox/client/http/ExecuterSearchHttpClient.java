package io.searchbox.client.http;

import io.searchbox.*;
import io.searchbox.client.ElasticSearchResult;
import io.searchbox.core.Index;
import org.elasticsearch.action.*;
import org.elasticsearch.action.Action;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.common.Unicode;
import org.elasticsearch.common.io.stream.BytesStreamInput;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.threadpool.ThreadPool;

import java.io.IOException;
import java.util.Map;

public class ExecuterSearchHttpClient extends AbstractClient {

    private ElasticSearchHttpClient httpClient;

    public ExecuterSearchHttpClient(ElasticSearchHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ThreadPool threadPool() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public AdminClient admin() {
        return null;
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response>> ActionFuture<Response> execute(Action<Request, Response, RequestBuilder> action, Request request) {

        io.searchbox.Action restAction;

        PlainActionFuture<Response> future = PlainActionFuture.newFuture();

        if (request instanceof IndexRequest) {
            restAction = new Index(request);
        } else {
            throw new RuntimeException("Given request" + request.toString() + " is not supported by JEST");
        }

        ElasticSearchResult result;

        try {
            result = httpClient.execute(restAction);
            Map jsonMap = result.getJsonMap();
            Response response = action.newResponse();
            response.readFrom(new BytesStreamInput(restAction.createByteResult(jsonMap), true));
            future.onResponse(response);

        } catch (IOException e) {
            e.printStackTrace();
            future.onFailure(e);
        }

        return future;
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response>> void execute(Action<Request, Response, RequestBuilder> action, Request request, ActionListener<Response> listener) {

        io.searchbox.Action restAction;

        if (request instanceof IndexRequest) {
            restAction = new Index(request);
        } else {
            throw new RuntimeException("Given request" + request.toString() + " is not supported by JEST");
        }

        ElasticSearchResult result;

        try {
            result = httpClient.execute(restAction);
            Map jsonMap = result.getJsonMap();
            Response response = action.newResponse();
            response.readFrom(new BytesStreamInput(restAction.createByteResult(jsonMap), true));
            listener.onResponse(response);

        } catch (IOException e) {
            e.printStackTrace();
            listener.onFailure(e);
        }

    }
}