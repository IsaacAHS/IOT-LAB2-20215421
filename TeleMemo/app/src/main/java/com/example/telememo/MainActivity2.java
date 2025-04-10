
//El codigo del juego en sí fue realizado por IA: ChaGPT y Claude

package com.example.telememo;

import android.os.Bundle;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private final String[] oracionCorrecta = {
            "La","fibra","optica","envía","datos", "a", "gran",
            "velocidad", "evitando", "cualquier","interferencia", "electrica"
    };
    private List<String> palabrasDesordenadas;
    private List<String> seleccionUsuario = new ArrayList<>();
    private int intentos = 0;
    private Chronometer cronometro;
    private TextView intentosText, resultadoText;
    private GridLayout gridLayout;
    private List<Button> botonesPalabras = new ArrayList<>();
    private Button jugarBtn;
    private boolean juegoIniciado = false;
    private boolean juegoActivo = false;
    private Handler handler = new Handler();
    private boolean juegoFinalizado = false;
    private long tiempoInicio = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        cronometro = findViewById(R.id.cronometro);
        intentosText = findViewById(R.id.intentosText);
        gridLayout = findViewById(R.id.gridPalabras);
        resultadoText = findViewById(R.id.resultadoText);
        jugarBtn = findViewById(R.id.jugarBtn);

        jugarBtn.setOnClickListener(v -> {
            String textoBoton = jugarBtn.getText().toString();

            if (textoBoton.equals("Jugar")) {
                iniciarJuego();
                jugarBtn.setText("Nuevo Juego");
            } else if (textoBoton.equals("Nuevo Juego")) {
                // Si el juego estaba activo pero no finalizado, registramos como cancelado
                if (juegoActivo && !juegoFinalizado) {
                    registrarJuegoCancelado();
                }
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        inicializarPalabras();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Flecha de retroceso
            // Si el juego estaba activo pero no finalizado, registramos como cancelado
            if (juegoActivo && !juegoFinalizado) {
                registrarJuegoCancelado();
            }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.estadisticas) {
            // Si el juego estaba activo pero no finalizado, registramos como cancelado
            if (juegoActivo && !juegoFinalizado) {
                registrarJuegoCancelado();
            }
            // Ir a estadísticas
            Intent intent = new Intent(this, MainActivity5.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void registrarJuegoCancelado() {
        cronometro.stop();
        long tiempo = (SystemClock.elapsedRealtime() - cronometro.getBase()) / 1000;
        String entradaHistorial = "Juego " + (historialJuegos.size() + 1) + ": Cancelado / Tiempo transcurrido: " + tiempo + " segundos / Intentos: " + intentos;
        historialJuegos.add(entradaHistorial);
        juegoActivo = false;
    }

    private void iniciarJuego() {
        juegoActivo = true;
        juegoFinalizado = false;
        cronometro.setBase(SystemClock.elapsedRealtime());
        cronometro.start();
        intentos = 0;
        intentosText.setText("Intentos: 0/3");
        resultadoText.setText("");
        reiniciarSeleccion();
    }

    private void inicializarPalabras() {
        palabrasDesordenadas = new ArrayList<>();
        Collections.addAll(palabrasDesordenadas, oracionCorrecta);
        Collections.shuffle(palabrasDesordenadas);

        for (String palabra : palabrasDesordenadas) {
            Button btn = new Button(this);
            btn.setText("?");
            btn.setTag(palabra);
            btn.setOnClickListener(view -> manejarSeleccion(btn));
            gridLayout.addView(btn);
            botonesPalabras.add(btn);
        }
    }

    private void manejarSeleccion(Button btn) {
        if (!juegoActivo || !btn.isEnabled()) return;

        String palabraReal = (String) btn.getTag();
        btn.setText(palabraReal);

        if (palabraReal.equals(oracionCorrecta[seleccionUsuario.size()])) {
            btn.setEnabled(false);
            seleccionUsuario.add(palabraReal);

            if (seleccionUsuario.size() == oracionCorrecta.length) {
                mostrarResultado(true);
            }
        } else {
            intentos++;
            intentosText.setText("Intentos: " + intentos + "/3");
            juegoActivo = false;

            handler.postDelayed(() -> {
                reiniciarSeleccion();
                if (intentos >= 3) {
                    mostrarResultado(false);
                } else {
                    juegoActivo = true;
                }
            }, 900);
        }
    }

    private void reiniciarSeleccion() {
        for (Button btn : botonesPalabras) {
            btn.setEnabled(true);
            btn.setText("?");
        }
        seleccionUsuario.clear();
    }

    private void mostrarResultado(boolean gano) {
        cronometro.stop();
        long tiempo = (SystemClock.elapsedRealtime() - cronometro.getBase()) / 1000;

        String resultado = gano
                ? "¡Ganaste! Tiempo: " + tiempo + " segundos. Intentos usados: " + intentos
                : "¡Perdiste! Tiempo: " + tiempo + " segundos.";

        resultadoText.setText(resultado);
        juegoActivo = false;
        juegoFinalizado = true;

        // Guardar en historial
        String entradaHistorial = gano
                ? "Juego " + (historialJuegos.size() + 1) + ": Ganó / Terminó en " + tiempo + " segundos / Intentos: " + intentos
                : "Juego " + (historialJuegos.size() + 1) + ": Perdió / Terminó en " + tiempo + " segundos";
        historialJuegos.add(entradaHistorial);
    }

    @Override
    public void onBackPressed() {
        // Si el juego estaba activo pero no finalizado, registramos como cancelado
        if (juegoActivo && !juegoFinalizado) {
            registrarJuegoCancelado();
        }
        super.onBackPressed();
    }

    // En caso de que la aplicación se cierre o se ponga en segundo plano
    @Override
    protected void onPause() {
        if (juegoActivo && !juegoFinalizado) {
            registrarJuegoCancelado();
        }
        super.onPause();
    }

    public static List<String> historialJuegos = new ArrayList<>();
}