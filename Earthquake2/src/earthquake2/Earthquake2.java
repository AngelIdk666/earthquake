package earthquake2;
import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Earthquake2 extends JFrame {

    private JComboBox<String> comboPeriodoTiempo;
    private JTextArea areaRespuesta;
    private JButton botonObtenerDatos;

    public Earthquake2() {
        setTitle("Información sobre Terremotos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] periodosTiempo = { "Última Hora", "Último Día", "Última Semana" };
        comboPeriodoTiempo = new JComboBox<>(periodosTiempo);
        
        areaRespuesta = new JTextArea();
        areaRespuesta.setLineWrap(true);
        areaRespuesta.setWrapStyleWord(true);
        areaRespuesta.setEditable(false);
        
        botonObtenerDatos = new JButton("Obtener datos de Terremotos");
        botonObtenerDatos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obtenerDatosTerremotos();
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Período de tiempo:"));
        panel.add(comboPeriodoTiempo);
        panel.add(botonObtenerDatos);
        
        JScrollPane panelDesplazable = new JScrollPane(areaRespuesta);
        panelDesplazable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(panel, "North");
        add(panelDesplazable, "Center");
    }

    private void obtenerDatosTerremotos() {
        String periodoSeleccionado = (String) comboPeriodoTiempo.getSelectedItem();
        String urlString = "";

        switch (periodoSeleccionado) {
            case "Última Hora":
                urlString = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";
                break;
            case "Último Día":
                urlString = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";
                break;
            case "Última Semana":
                urlString = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.geojson";
                break;
        }

        try {
            URL url = new URL(urlString);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            BufferedReader entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String lineaEntrada;
            StringBuilder contenido = new StringBuilder();

            while ((lineaEntrada = entrada.readLine()) != null) {
                contenido.append(lineaEntrada);
            }
            entrada.close();

            analizarYMostrarDatosTerremotos(contenido.toString());
        } catch (Exception e) {
            areaRespuesta.setText("Error: " + e.getMessage());
        }
    }

    private void analizarYMostrarDatosTerremotos(String datosJson) {
        JSONObject jsonObject = new JSONObject(datosJson);
        JSONArray features = jsonObject.getJSONArray("features");

        areaRespuesta.setText("");  // Limpiar el contenido anterior

        for (int i = 0; i < features.length(); i++) {
            JSONObject feature = features.getJSONObject(i);
            JSONObject properties = feature.getJSONObject("properties");
            double magnitud = properties.getDouble("mag");
            String lugar = properties.getString("place");
            long tiempo = properties.getLong("time");

            areaRespuesta.append("Magnitud: " + magnitud + "\n");
            areaRespuesta.append("Ubicación: " + lugar + "\n");
            areaRespuesta.append("Hora: " + new java.util.Date(tiempo) + "\n\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Earthquake2 app = new Earthquake2();
            app.setVisible(true);
        });
    }
}
