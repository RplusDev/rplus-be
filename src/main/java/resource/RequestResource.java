package resource;
/**
 * Created by owl on 5/3/16.
 */

import configuration.AppConfig;
import com.google.gson.Gson;

import hibernate.entity.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.RequestService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;


public class RequestResource {

    Logger logger = LoggerFactory.getLogger(RequestResource.class);
    Gson gson = new Gson();

    private final RequestService requestService;


    public RequestResource(RequestService requestService) {
        this.requestService = requestService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        get(AppConfig.API_CONTEXT + "/request/list", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            int page = 0;
            int perPage = 32;
            String personId = "";
            String searchQuery = "";

            if (request.queryParams("page") != null) {
                page = Integer.parseInt(request.queryParams("page"));
            }
            if (request.queryParams("per_page") != null) {
                perPage = Integer.parseInt(request.queryParams("per_page"));
            }
            if (request.queryParams("person_id") != null) {
                personId = request.queryParams("person_id");
            }
            if (request.queryParams("search_query") != null) {
                searchQuery = request.queryParams("search_query");
            }

            List<Request> requestList = requestService.list(page, perPage, personId, searchQuery);

            result.put("response", "ok");
            result.put("result", requestList);

            return result;
        }, gson::toJson);

        get(AppConfig.API_CONTEXT + "/request/get/:id", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();
            long id = Long.parseLong(request.params(":id"));
            Request _request = requestService.get(id);

            result.put("response", "ok");
            result.put("result", _request);

            return result;
        }, gson::toJson);

        post(AppConfig.API_CONTEXT + "/request/save", "application/json", (request, response) -> {
            Map<String, Object> result = new HashMap<>();

            Request req = gson.fromJson(request.body(), Request.class);
            Request res = requestService.save(req);

            result.put("response", "ok");
            result.put("result", res);
            response.status(200);

            return result;
        }, gson::toJson);


        post(AppConfig.API_CONTEXT + "/request/delete/:id", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();
            Request _request = requestService.delete(request.params(":id"));

            result.put("response", "ok");
            result.put("result", _request);

            return result;
        }, gson::toJson);

    }

}
