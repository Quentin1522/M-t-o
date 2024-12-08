//Scanner class Java qui permet de lire les entrées de l'utilisateur
//getCityName méthode qui affiche un message pour demander à l'utilisateur de saisir le nom d'une ville et renvoir une chaîne de caractère
//NextLine méthode qui récupère toute la ligne que l'utilisateur a saisie
//Requête HTTP : La méthode getWeatherData() envoie une requête GET à l'API OpenWeatherMap
//Lecture des données : Nous utilisons BufferedReader pour lire la réponse de l'API ligne par ligne et construire une chaîne de caractères avec les données météo.
//JsonObject permet d'accéder aux différentes sections du JSON, comme "main" pour la température et l'humidité, ou "wind" pour la vitesse du vent.
//mainObject.get("temp").getAsDouble() : Extrait la température (en degrés Celsius).
//mainObject.get("humidity").getAsInt() : Extrait l'humidité (en pourcentage).
//windObject.get("speed").getAsDouble() : Extrait la vitesse du vent (en m/s).


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {
    public static void main(String[] args) {
        // Demande à l'utilisateur de saisir le nom d'une ville
        String city = getCityName();
        System.out.println("Vous avez entré : " + city);

        // Connexion à l'API OpenWeatherMap
        String apiKey = "bfa764ca8b613b86073147f9184cd673";
        String weatherData = getWeatherData(city, apiKey);

        // Traitement des données JSON
        if (weatherData != null) {
            JsonObject jsonObject = JsonParser.parseString(weatherData).getAsJsonObject();

            // Extraire les informations importantes
            JsonObject mainObject = jsonObject.getAsJsonObject("main");
            double temperature = mainObject.get("temp").getAsDouble();
            int humidity = mainObject.get("humidity").getAsInt();

            JsonObject windObject = jsonObject.getAsJsonObject("wind");
            double windSpeed = windObject.get("speed").getAsDouble();

            String weatherDescription = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
            double pressure = mainObject.get("pressure").getAsDouble(); // pression en hPa

            // Afficher les informations importantes
            System.out.println("Météo à " + city + " :");
            System.out.println("Température : " + temperature + "°C");
            System.out.println("Humidité : " + humidity + "%");
            System.out.println("Vitesse du vent : " + windSpeed + " m/s");
            System.out.println("Description : " + weatherDescription);
            System.out.println("Pression : " + pressure + " hPa");
        }

        // Affichage des données récupérées de l'API
        System.out.println("Donnée météo : " + weatherData);
    }

    // Méthode pour récupérer le nom de la ville via la console
    public static String getCityName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrez le nom de la ville :");
        return scanner.nextLine();
    }

    // Méthode pour récupérer les données météo depuis l'API
    public static String getWeatherData(String city, String apiKey) {
        try {
            // Construire l'URL de la requête avec la ville et la clé API
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Vérifie le code de réponse HTTP
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Erreur : " + responseCode + " - Ville non trouvée ou problème de connexion.";
            }

            // Lire la réponse de l'API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            // Renvoie la réponse de l'API
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la récupération des données.";
        }
    }
}