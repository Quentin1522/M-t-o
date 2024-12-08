import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherAppGUI {

    public static void main(String[] args) {
        // Créer la fenêtre
        JFrame frame = new JFrame("Application Météo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550, 350);
        frame.setLayout(new BorderLayout());        

        // Crée les composants
        JLabel cityLabel = new JLabel("Entrez le nom de la ville :");
        JTextField cityTextField = new JTextField(10);
        JButton searchButton = new JButton("Chercher la météo");

        // Labels pour afficher les résultats
        JLabel temperatureLabel = new JLabel("Température : ");
        JLabel humidityLabel = new JLabel("Humidité : ");
        JLabel windSpeedLabel = new JLabel("Vitesse du vent : ");
        JLabel descriptionLabel = new JLabel("Description : ");
        JLabel pressureLabel = new JLabel("Pression : ");

        // Label pour afficher les erreurs
        JLabel errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);

        // Icône météo
        JLabel iconLabel = new JLabel();

        // Ajouter des composants au panel principal
        JPanel inputPanel = new JPanel();
        inputPanel.add(cityLabel);
        inputPanel.add(cityTextField);
        inputPanel.add(searchButton);

        JPanel resultPanel = new JPanel(new GridLayout(6, 2));
        resultPanel.add(new JLabel("Résultats :"));
        resultPanel.add(new JLabel(""));
        resultPanel.add(temperatureLabel);
        resultPanel.add(humidityLabel);
        resultPanel.add(windSpeedLabel);
        resultPanel.add(descriptionLabel);
        resultPanel.add(pressureLabel);
        resultPanel.add(errorLabel);

        // Ajouter l'icône et les résultats dans le même panneau
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(iconLabel, BorderLayout.WEST); // Ajout de l'icône
        mainPanel.add(resultPanel, BorderLayout.CENTER);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);

        // Gérer l'événement du bouton
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = cityTextField.getText();
                String weatherData = getWeatherData(city);

                if (weatherData.startsWith("Erreur")) {
                    errorLabel.setText(weatherData); // Afficher l'erreur si ville non trouvée
                    temperatureLabel.setText("Température : ");
                    humidityLabel.setText("Humidité : ");
                    windSpeedLabel.setText("Vitesse du vent : ");
                    descriptionLabel.setText("Description : ");
                    pressureLabel.setText("Pression : ");
                } else {
                    errorLabel.setText(""); // Effacer l'erreur

                    // Extraire et afficher les informations météo
                    JsonObject jsonObject = JsonParser.parseString(weatherData).getAsJsonObject();
                    JsonObject mainObject = jsonObject.getAsJsonObject("main");
                    double temperature = mainObject.get("temp").getAsDouble();
                    int humidity = mainObject.get("humidity").getAsInt();
                    JsonObject windObject = jsonObject.getAsJsonObject("wind");
                    double windSpeed = windObject.get("speed").getAsDouble();
                    String description = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
                    double pressure = mainObject.get("pressure").getAsDouble();

                    temperatureLabel.setText("Température : " + temperature + "°C");
                    humidityLabel.setText("Humidité : " + humidity + "%");
                    windSpeedLabel.setText("Vitesse du vent : " + windSpeed + " m/s");
                    descriptionLabel.setText("Description : " + description);
                    pressureLabel.setText("Pression : " + pressure + " hPa");
                }
            }
        });

        // Afficher la fenêtre
        frame.setVisible(true);
    }

    // Méthode pour récupérer les données météo depuis l'API
    public static String getWeatherData(String city) {
        try {
            String apiKey = "bfa764ca8b613b86073147f9184cd673"; // Remplacer par ta clé API
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Erreur : " + responseCode + " - Ville non trouvée ou problème de connexion.";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la récupération des données.";
        }
    }
}
