package io.openshift.booster;

import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class VertxMongoVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(VertxMongoVerticle.class);
	
	public static MongoClient mongoClient = null;
	
    public static void main( String[] args )
    {    	
        Vertx vertx = Vertx.vertx();
        
        vertx.deployVerticle(new VertxMongoVerticle());
        
    }

    @Override
    public void start() {
    	LOGGER.info("Verticle VertxMongoVerticle Started");
    	
    	Router router = Router.router(vertx);
    	
    	router.get("/mongofind").handler(this::getAllProjects);
    	
    	JsonObject dbConfig = new JsonObject();
    	
    	dbConfig.put("connection_string", "mongodb://localhost:27017/projectsdb");
    	dbConfig.put("username", "admin");
    	dbConfig.put("password", "nOah18VRoc2BxKbx");
    	dbConfig.put("authSource", "admin");
    	dbConfig.put("useObjectId", true);
    	
    	mongoClient = MongoClient.createShared(vertx, dbConfig);
    	
    	vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    
    }
    
    // Get all products as array of products
    private void getAllProjects(RoutingContext routingContext) {
    	
    	mongoClient.find("projects", new JsonObject(), results -> {
    		
    		try {
    			List<JsonObject> objects = results.result();
    			
    			if (objects != null && objects.size() != 0) {
    				System.out.println("Got some data len=" + objects.size());
    				
    				JsonObject jsonResponse = new JsonObject();
    				
    				jsonResponse.put("projects", objects);
    				
    				routingContext.response()
    					.putHeader("content-type", "application/json; charset=utf-8")
    					.setStatusCode(200)
    					.end(Json.encodePrettily(jsonResponse));
    			}
    			else {
    				JsonObject jsonResponse = new JsonObject();
        			
        			jsonResponse.put("error", "No items found");
        			
        			routingContext.response()
        				.putHeader("content-type", "application/json; charset=utf-8")
        				.setStatusCode(400)
        				.end(Json.encodePrettily(jsonResponse));
    			}
    		}
    		catch(Exception e) {
    			LOGGER.info("getAllProducts Failed exception e=",e.getLocalizedMessage());
    			
    			JsonObject jsonResponse = new JsonObject();
    			
    			jsonResponse.put("error", "Exception and No items found");
    			
    			routingContext.response()
    				.putHeader("content-type", "application/json; charset=utf-8")
    				.setStatusCode(400)
    				.end(Json.encodePrettily(jsonResponse));

    		}
    	});
    		
    }
    
    @Override
    public void stop() {
    	LOGGER.info("Verticle VertxMongoVerticle Stopped");    	
    }

}
