package helloworld;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.Endpoint;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Database, Object> {

    AmazonRDS getAmazonRDSInstance(){

        return AmazonRDSClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    List<DBInstance> getDbInstances(){
        AmazonRDS amazonRDS = getAmazonRDSInstance();
        DescribeDBInstancesResult result = amazonRDS.describeDBInstances();
        return result.getDBInstances();
    }

    public Object handleRequest(final Database input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
       // String output = String.format("{\"message\":\"Mariusz\"}");

        System.out.println(input.name);
        System.out.println(input.status);


        List<DBInstance> list = getDbInstances();
        for(DBInstance instance : list){
            String identifier = instance.getDBInstanceIdentifier();
            String  status = instance.getDBInstanceStatus();

            if(identifier.equals("pzz711") && status.equals("stopped")){
                String output = String.format("{\"DatabaseInstanceName\": \"%s\",\"Status\":\"%s\",\"message\":\"Instance has been run\"}",identifier,status);
                return new GatewayResponse(output, headers,200);
            }
        }

        return new GatewayResponse("{\"message\":\"Mariusz\"}", headers,404);
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}

class Database{
    String name;
    String status;

    public Database(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}