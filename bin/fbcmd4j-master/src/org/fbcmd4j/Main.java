package org.fbcmd4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;

public class Main {
	static final Logger logger = LogManager.getLogger(Main.class);
	private static final String CONFIG_DIR = "config";
	private static final String CONFIG_FILE = "fbcmd4j.properties";

	public static void main(String[] args) {
		logger.info("Iniciando programa");
		Facebook fb =  null;
		Properties props = null;

		try {
			props = ControladadorFacebook.cargaArchivo(CONFIG_DIR, CONFIG_FILE);
		} catch (IOException ex) {
			logger.error(ex);
		}
		
		int option = 1;
		try {
			Scanner scan = new Scanner(System.in);
			while(true) {
				fb = ControladadorFacebook.configFacebook(props);
				System.out.println("Cliente de Facebook en línea de comando por David Servín \n\n"
								+  "Opciones: \n"
								+  "(0) --> Obtener Token \n"
								+  "(1) --> Noticias \n"
								+  "(2) --> Perfil \n"
								+  "(3) --> Publicar Estado \n"
								+  "(4) --> Publicar Link \n"
								+  "(5) --> Salir \n"
								+  "\nIngrese opcion:");
				try {
					option = scan.nextInt();
					scan.nextLine();
					switch (option) {
					case 0:
						ControladadorFacebook.configTokens(CONFIG_DIR, CONFIG_FILE, props, scan);
						props = ControladadorFacebook.cargaArchivo(CONFIG_DIR, CONFIG_FILE);
						break;
					case 1:
						System.out.println("Mostrando Noticias...");
						ResponseList<Post> newsFeed = fb.getFeed();
						for (Post p : newsFeed) {
							ControladadorFacebook.printPost(p);
						}
						cantDePost("Noticias", newsFeed, scan);
						break;
					case 2:
						System.out.println("Mostrando Perfil...");
						ResponseList<Post> wall = fb.getPosts();
						for (Post p : wall) {
							ControladadorFacebook.printPost(p);
						}		
						cantDePost("Perfil", wall, scan);
						break;
					case 3:
						System.out.println("Escribe tu estado: ");
						String estado = scan.nextLine();
						ControladadorFacebook.postEstado(estado, fb);
						break;
					case 4:
						System.out.println("Ingresa el link: ");
						String link = scan.nextLine();
						ControladadorFacebook.postLink(link, fb);
						break;
					case 5:
						System.out.println("Saliendo de programa.");
						System.exit(0);
						break;
					default:
						break;
					}
				} catch (InputMismatchException ex) {
					System.out.println("Ocurrió un error, favor de revisar log.");
					logger.error("Opción inválida. %s. \n", ex.getClass());
				} catch (FacebookException ex) {
					System.out.println("Ocurrió un error, favor de revisar log.");
					logger.error(ex.getErrorMessage());
				} catch (Exception ex) {
					System.out.println("Ocurrió un error, favor de revisar log.");
					logger.error(ex);
				}
				System.out.println();
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}	//	fin	main
	
	public static void cantDePost(String fileName, ResponseList<Post> posts, Scanner scan) {
		System.out.println("Guardar resultados en un archivo de texto? Si/No");
		String option = scan.nextLine();
		
		if (option.contains("Si") || option.contains("si")) {
			List<Post> ps = new ArrayList<>();
			int n = 0;

			while(n <= 0) {
				try {
					System.out.println("Cuantas publicaciones deseas guardar?");
					n = Integer.parseInt(scan.nextLine());					
					if(n <= 0) {
						System.out.println("Ingresa un numero mayor de 0.");
					} else {
						for(int i = 0; i<n; i++) {
							if(i>posts.size()-1) break;
							ps.add(posts.get(i));
						}
					}
				} catch(NumberFormatException e) {
					logger.error(e);
				}
			}
			ControladadorFacebook.guardarPublicacionArchivo(fileName, ps);
		}
	}	//	fin clase cantidad de post
}	//	fin	clase	main