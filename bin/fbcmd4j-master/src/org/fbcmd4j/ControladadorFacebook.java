package org.fbcmd4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.auth.AccessToken;
import facebook4j.internal.org.json.JSONObject;

public class ControladadorFacebook {
	private static final Logger logger = LogManager.getLogger(ControladadorFacebook.class);
	private static final String APP_ID = "183012125816269|a85f966a9382e91c211efa80ad3cc5ac";
	private static final String SCOPE = "scope";
	private static final String ACCESS_TOKEN = "access_token";
	private static final String CODE = "code";
	
	public static Properties cargaArchivo(String folderName, String fileName) throws IOException {
		Properties propiedades = new Properties();
		Path configFolder = Paths.get(folderName);
		Path configFile = Paths.get(folderName, fileName);
		if (!Files.exists(configFile)) {
			logger.info("Creando nuevo archivo de configuración.");
			
			if (!Files.exists(configFolder))
				Files.createDirectory(configFolder);
			
			Files.copy(ControladadorFacebook.class.getResourceAsStream("fbcmd4j.properties"), configFile);
		}

		propiedades.load(Files.newInputStream(configFile));
		BiConsumer<Object, Object> emptyProperty = (p, e) -> {
			if(((String)e).isEmpty())
				logger.info("La propiedad '" + p + "' está vacía");
		};
		propiedades.forEach(emptyProperty);
		return propiedades;
	}
	
	
	//OBTIENE TOKENS MEDIANTE UN METODO HTTP POST
	public static void configTokens(String folderName, String fileName, Properties properties, Scanner scanner) {
		if (properties.getProperty("oauth.appId").isEmpty() || properties.getProperty("oauth.appSecret").isEmpty()) {
			System.out.println("Por favor ingrese appId:");
			properties.setProperty("oauth.appId", scanner.nextLine());
			System.out.println("Por favor ingrese appSecret:");
			properties.setProperty("oauth.appSecret", scanner.nextLine());
		}

		
		try {
			URL url = new URL("https://graph.facebook.com/v2.6/device/login");
	        Map<String,Object> params = new LinkedHashMap<>();
	        params.put(ACCESS_TOKEN, APP_ID);
	        params.put(SCOPE, properties.getProperty("oauth.permissions"));

	        
	        byte[] postDataBytes = sb(params).toString().getBytes("UTF-8");
	        
	        Reader in = new BufferedReader(new InputStreamReader(metodoPostJson(postDataBytes, url).getInputStream(), "UTF-8"));
	        StringBuilder sb = new StringBuilder();
	        for (int c; (c = in.read()) >= 0;)
	            sb.append((char)c);
	        String response = sb.toString();
	        
	        JSONObject jObj = new JSONObject(response);
	        String code = jObj.getString("code");
	        String userCode = jObj.getString("user_code");
	        
	        URI myUri = URI.create("https://www.facebook.com/device");
			try {
				java.awt.Desktop.getDesktop().browse(myUri);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Ingresa el siguiente código en tu navegador: " + userCode);

			String accessToken = "";
			while(accessToken.isEmpty()) {
		        try {
		            TimeUnit.SECONDS.sleep(5);
		        } catch (InterruptedException e) {
					logger.error(e);
		        }

		        URL url1 = new URL("https://graph.facebook.com/v2.6/device/login_status");
		        params = new LinkedHashMap<>();
		        params.put(ACCESS_TOKEN, APP_ID);
		        params.put(CODE, code);
	
		        
		        postDataBytes = sb(params).toString().getBytes("UTF-8");
	
		        try {
		        	in = new BufferedReader(new InputStreamReader(metodoPostJson(postDataBytes, url1).getInputStream(), "UTF-8"));
			        sb = new StringBuilder();
			        for (int c; (c = in.read()) >= 0;)
			            sb.append((char)c);		        
			        response = sb.toString();
			        
			        jObj = new JSONObject(response);
			        accessToken = jObj.getString(ACCESS_TOKEN);
		        } catch(IOException ignore) {
		      }
		    }
			
	        properties.setProperty("oauth.accessToken", accessToken);
	        
			guardaPropiedades(folderName, fileName, properties);
			System.out.println("Configuración guardada exitosamente.");
			logger.info("Configuración guardada exitosamente.");
		} catch(Exception e) {
			logger.error(e);
		}
	}

	public static void guardaPropiedades(String folderName, String fileName, Properties props) throws IOException {
		Path configFile = Paths.get(folderName, fileName);
		props.store(Files.newOutputStream(configFile), "Token generado:");
	}

	public static StringBuilder sb (Map<String,Object> params) throws IOException {
        StringBuilder postData = new StringBuilder();

		for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
		return postData;
	}
	
	public static HttpURLConnection metodoPostJson (byte[] postDataBytes, URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        
        return conn;
	}
	
	public static String guardarPublicacionArchivo(String fileName, List<Post> posts) {
		File file = new File(fileName + ".txt");

		try {
    		if(!file.exists()) {
    			file.createNewFile();
            }
    		
    		FileOutputStream fos = new FileOutputStream(file);
			for (Post p : posts) {
				String msg = "";
				if(p.getStory() != null)
					msg += "Publicacion: " + p.getStory() + "\n";
				if(p.getMessage() != null)
					msg += "Mensaje: " + p.getMessage() + "\n";
				msg += "--------------------------------\n";
				fos.write(msg.getBytes());
			}
			fos.close();
			logger.info("Se guardaron las publicaciones en: " + file.getName() + ".");
			System.out.println("Publicaciones guardadas exitosamente en " + file.getName() + ".");
		} catch (IOException e) {
			logger.error(e);
		}
        
        return file.getName();
	}	
	
	public static Facebook configFacebook(Properties props) {
		Facebook fb = new FacebookFactory().getInstance();
		fb.setOAuthAppId(props.getProperty("oauth.appId"), props.getProperty("oauth.appSecret"));
		fb.setOAuthPermissions(props.getProperty("oauth.permissions"));
		if(props.getProperty("oauth.accessToken") != null)
			fb.setOAuthAccessToken(new AccessToken(props.getProperty("oauth.accessToken"), null));
		return fb;
	}
	
	public static void printPost(Post p) {
		if(p.getStory() != null)
			System.out.println("Publicacion: " + p.getStory());
		if(p.getMessage() != null)
			System.out.println("Mensaje: " + p.getMessage());
		System.out.println("--------------------------------");
	}
	
	
	
	public static void postEstado(String msg, Facebook fb) {
		try {
			fb.postStatusMessage(msg);
		} catch (FacebookException e) {
			logger.error(e);
		}		
	}
	
	public static void postLink(String link, Facebook fb) {
		try {
			fb.postLink(new URL(link));
		} catch (MalformedURLException e) {
			logger.error(e);
		} catch (FacebookException e) {
			logger.error(e);
		}
	}

	
	
	//metodo que guarda las publicaciones
	
	
}	//	final	main