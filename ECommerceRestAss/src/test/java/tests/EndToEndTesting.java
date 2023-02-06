package tests;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import pojo.CreateOrder;
import pojo.CreateOrderResp;
import pojo.CreateProductRes;
import pojo.LoginPojo;
import pojo.LoginResponse;
import pojo.Order;

public class EndToEndTesting {

	public static void main(String[] args) {
		
	//Login
		RequestSpecification req= new RequestSpecBuilder()
				.setBaseUri("https://rahulshettyacademy.com")
				.setContentType(ContentType.JSON)
				.build()
				.log().all();
		
		ResponseSpecification res= new ResponseSpecBuilder()
				.expectStatusCode(200)
				.expectContentType(ContentType.JSON)
				.build();
				
		ResponseSpecification res1= new ResponseSpecBuilder()
				.expectStatusCode(201)
				.expectContentType(ContentType.JSON)
				.build();
				
		LoginPojo lp = new LoginPojo();
		lp.setUserEmail("manali.kulkarni@cogniwize.com");
		lp.setUserPassword("Manali@123");
		
		LoginResponse lr=given()
			.spec(req)
			.body(lp)
		.when()
			.post("/api/ecom/auth/login")
		.then()
			.log().all()
			.spec(res)
			.extract().response().as(LoginResponse.class);
		String Token = lr.getToken();
		String UserID = lr.getUserId();
		
		System.out.println(lr.getToken());

	//Create Product
		CreateProductRes cpr=given()
			.spec(req)
			.header("Authorization",Token)
			.header("Content-Type","multipart/form-data")
			.formParam("productName", "qwerty")
			.formParam("productAddedBy",UserID )
			.formParam("productCategory","fashion" )
			.formParam("productSubCategory","shirts" )
			.formParam("productPrice","11500" )
			.formParam("productDescription","Addias Originals" )
			.formParam("productFor","women" )
			.multiPart("productImage",new File("E:\\Documents\\my photo.jpg"))
		.when()
			.post("/api/ecom/product/add-product")
		.then()
			.log().all()
			.spec(res1)
			.extract().response().as(CreateProductRes.class);
		
		String ProductID = cpr.getProductId();
		System.out.println("ID is--------------="+ProductID);
		
	//Create Order
		
		Order op=new Order();
        op.setCountry("India");
        op.setProductOrderedId(ProductID);    
        
        List<Order> ol = new ArrayList<Order>();
        ol.add(op);  
        
        CreateOrder orders = new CreateOrder();
        orders.setOrders(ol);
		
	CreateOrderResp orderRes =	given()	
			.spec(req)
			.header("Authorization",Token )
			.body(orders)
		.when()
			.post("/api/ecom/order/create-order")
		.then()
			.log().all()
			.spec(res1)
			.extract().response().as(CreateOrderResp.class);
        
         List<String> a=new ArrayList<String>();
         a.addAll(orderRes.getOrders()) ;
		
		List<String> OrderID = orderRes.getOrders();
		System.out.println("Order ID is:---------"+OrderID);
		
   //View Order
		given()
			.spec(req)
			.header("Authorization",Token)
			.queryParam("id", OrderID)
		.when()
			.get("/api/ecom/order/get-orders-details")
		.then()
			.log().all()
			.spec(res)
			.extract().response().asString();
		
   //Delete Product
		given()
			.spec(req)
			.header("Authorization",Token)
			.pathParam("productId", ProductID)
		.when()
			.delete("/api/ecom/product/delete-product/{productId}")
		.then()
			.log().all()
			.spec(res)
			.extract().response().asString();
	}
}
