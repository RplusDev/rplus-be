/**
 * Created by owl on 3/23/16.
 */

import configuration.AppConfig;

import hibernate.entity.Organisation;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.*;
import service.*;
import spark.Spark;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static spark.Spark.exception;


public class App {
    static Logger logger = LoggerFactory.getLogger(App.class);

    static TransportClient ec;

    static UserService userService;
    static OrganisationService orgService;
    static PersonService personService;
    static OfferService offerService;
    static RequestService requestService;


    public static void main(String[] args) throws Exception {

        AppConfig.LoadConfig();
        //Spark.externalStaticFileLocation(AppConfig.STATIC_FILE_LOCATION);

        ec = getElasticClient();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("rplus-be-dev.jpa.hibernate");

        // services
        App.userService = new UserService(emf);
        App.orgService = new OrganisationService(emf);
        App.personService = new PersonService(emf);
        App.offerService = new OfferService(emf, ec);
        App.requestService = new RequestService(emf, ec);


        // resources
        new Authorisation();
        new Maintenance(App.offerService, App.userService, App.personService);
        new UserResource(App.userService);
        new OrganisationResource(App.orgService);
        new PersonResource(App.personService);
        new OfferResource(App.offerService);
        new RequestResource(App.requestService);

        //new PhotoResource(App.photoService);
        //new PersonResource(App.person_service);
        //new RealtyResource(App.realty_service);
        //new RequestResource(App.request_service);

        exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            response.body("Ex:" + exception.getMessage());
            exception.printStackTrace();
        });
    }

    private static TransportClient getElasticClient() throws UnknownHostException {

        Settings settings = Settings.builder().put("cluster.name", "rplus-dev").build();
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

        return client;

    }

}
