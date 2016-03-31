package resource;

import Configuration.AppConfig;
import com.google.gson.Gson;
import morphia.entity.Offer;
import morphia.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.OfferService;
import service.UserService;
import utils.JsonTransformer;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

/**
 * Created by owl on 3/27/16.
 */
public class OfferResource {

    Logger logger = LoggerFactory.getLogger(UserService.class);

    private final OfferService offerService;

    Gson gson = new Gson();

    public OfferResource(OfferService offerService) {
        this.offerService = offerService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        post(AppConfig.API_CONTEXT + "/offer/create", "application/json", (request, response) -> {
            Offer result = offerService.create(request.body());
            response.status(201);
            return result;
        }, new JsonTransformer());

        post(AppConfig.API_CONTEXT + "/offer/update/:id", "application/json", (request, response) -> {
            Offer result = offerService.update(request.params(":id"), request.body());
            response.status(202);
            return result;
        }, new JsonTransformer());

        post(AppConfig.API_CONTEXT + "/offer/delete/:id", "application/json", (request, response) -> {
            Offer result = offerService.delete(request.params(":id"));
            return result;
        }, new JsonTransformer());


        get(AppConfig.API_CONTEXT + "/offer/get/:id", "application/json", (request, response) -> {
            Offer result = offerService.get(request.params(":id"));
            return result;
        }, new JsonTransformer());

        get(AppConfig.API_CONTEXT + "/offer/list", "application/json", (request, response) -> {
            List<Offer> result = offerService.list(request.body());
            return result;
        }, new JsonTransformer());

    }

}
