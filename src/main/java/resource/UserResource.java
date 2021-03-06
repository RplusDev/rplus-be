package resource;
/**
 * Created by owl on 3/23/16.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.AppConfig;
import service.UserService;
import entity.User;


public class UserResource {

    Logger logger = LoggerFactory.getLogger(UserService.class);
    Gson gson = new Gson();

    private final UserService userService;


    public UserResource(UserService userService) {

        this.userService = userService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        get(AppConfig.API_CONTEXT + "/user/list", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            int page = 0;
            int perPage = 32;
            Long accountId = null;
            String role = null;
            Long superiorId = null;


            String pageStr = request.queryParams("page");
            if (pageStr != null && StringUtils.isNumeric(pageStr)) {
                page = Integer.parseInt(pageStr);
            }

            String perPageStr = request.queryParams("perPage");
            if (perPageStr != null && StringUtils.isNumeric(perPageStr)) {
                perPage = Integer.parseInt(perPageStr);
            }

            String accountIdStr = request.queryParams("accountId");
            if (accountIdStr != null && StringUtils.isNumeric(accountIdStr)) {
                accountId = Long.parseLong(accountIdStr);
            }

            String roleStr = request.queryParams("role");
            if (roleStr != null) {
                role = roleStr;
            }

            String superiorIdStr = request.queryParams("superiorId");
            if (superiorIdStr != null && StringUtils.isNumeric(superiorIdStr)) {
                superiorId = Long.parseLong(superiorIdStr);
            }

            String searchQuery = request.queryParams("searchQuery");


            List<User> userList = userService.list(accountId, page, perPage, role, superiorId, searchQuery);

            result.put("response", "ok");
            result.put("result", userList);

            return result;
        }, gson::toJson);

        get(AppConfig.API_CONTEXT + "/user/get/:id", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            String userIdStr = request.params(":id");
            if (userIdStr != null && StringUtils.isNumeric(userIdStr)) {
                long id = Long.parseLong(userIdStr);
                User user = userService.get(id);

                result.put("response", "ok");
                result.put("result", user);
            } else {
                result.put("response", "fail");
                result.put("result", "id is not numeric");
            }

            return result;
        }, gson::toJson);

        post(AppConfig.API_CONTEXT + "/user/save", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            User user = gson.fromJson(request.body(), User.class);
            // check user
            List<String> errors = userService.check(user);
            if (errors.size() == 0) {
                User res = userService.save(user);

                result.put("response", "ok");
                result.put("result", res);
                response.status(202);
            } else {
                result.put("response", "fail");
                result.put("result", errors);
                // should be 400, but its problematic to process it later so fck it
                response.status(200);
            }

            return result;
        }, gson::toJson);

        post(AppConfig.API_CONTEXT + "/user/delete/:id", "application/json", (request, response) -> {

            Map<String, Object> result = new HashMap<>();

            String userIdStr = request.params(":id");
            if (userIdStr != null && StringUtils.isNumeric(userIdStr)) {
                long id = Integer.parseInt(userIdStr);
                User user = userService.delete(id);

                result.put("response", "ok");
                result.put("result", user);
            } else {
                result.put("response", "fail");
                result.put("result", "id is not numeric");
            }

            return result;
        }, gson::toJson);

    }

}
