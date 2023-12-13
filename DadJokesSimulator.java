import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.prefs.Preferences;

public class DadJokesSimulator {

    private static final Preferences prefs = Preferences.userNodeForPackage(DadJokesSimulator.class);
    private static final String PREF_KEY = "fetchInJson";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DadJokesSimulator::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Dad Jokes Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JCheckBox jsonCheckBox = new JCheckBox("Fetch Joke in JSON Format");
        jsonCheckBox.setSelected(prefs.getBoolean(PREF_KEY, false));

        JButton jokeButton = new JButton("Generate Joke");
        jokeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean fetchInJson = jsonCheckBox.isSelected();
                prefs.putBoolean(PREF_KEY, fetchInJson);
                String joke = fetchJoke(fetchInJson);
                JOptionPane.showMessageDialog(frame, joke);
            }
        });

        frame.getContentPane().add(jsonCheckBox, "North");
        frame.getContentPane().add(jokeButton, "South");

        frame.setVisible(true);
    }

    private static String fetchJoke(boolean fetchInJson) {
        String joke = "No joke available";
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://icanhazdadjoke.com/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "text/plain");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (connection.getResponseCode() != 200) {
                throw new IOException("Failed: HTTP error code : " + connection.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }
            br.close();
            joke = response.toString();

        } catch (IOException e) {
            e.printStackTrace();
            joke = "Error: Could not retrieve joke.";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return joke;
    }
}
