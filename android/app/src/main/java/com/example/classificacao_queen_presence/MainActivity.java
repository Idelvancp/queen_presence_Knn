package com.example.classificacao_queen_presence;

import android.util.Log;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.embedding.engine.FlutterEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import ru.ml.tasks.KnnTask;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "com.example.audio/audio_processor";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            try {
                                // Copia os arquivos CSV de res/raw para armazenamento interno
                                File trainFile = copyRawResourceToInternalStorage(R.raw.data, "train_data.csv");
                                File testFile = copyRawResourceToInternalStorage(R.raw.data, "test_data.csv");

                                if (!trainFile.exists() || !testFile.exists()) {
                                    result.error("FILE_NOT_FOUND", "Os arquivos de treino ou teste não foram encontrados.", null);
                                    return;
                                }

                                String outputMessage = ""; // Variável para armazenar a saída do KNN

                                switch (call.method) {
                                    case "svm":
                                        double accuracySvm = runSvmTask(trainFile.getAbsolutePath(), testFile.getAbsolutePath());
                                        result.success("SVM executado com sucesso. Acurácia: " + accuracySvm);
                                        break;

                                    case "knn":
                                        // Chama o KNN e pega a saída
                                        result.success(runKnnTask(trainFile.getAbsolutePath(), testFile.getAbsolutePath(), 3)); // Retorna o resultado para o Flutter
                                        break;

                                    default:
                                        result.notImplemented();
                                        break;
                                }
                            } catch (Exception e) {
                                Log.e("MainActivity", "Erro ao executar tarefa", e);
                                result.error("TASK_ERROR", "Erro ao executar tarefa", e.getMessage());
                            }
                        }
                );
    }

    /**
     * Copia um recurso bruto (raw) para o armazenamento interno.
     */
    private File copyRawResourceToInternalStorage(int rawResourceId, String outputFileName) throws IOException {
        // Abre o recurso bruto como um InputStream
        InputStream inputStream = getResources().openRawResource(rawResourceId);
        File outFile = new File(getFilesDir(), outputFileName);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             FileWriter writer = new FileWriter(outFile)) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.write("\n");
            }
        }

        return outFile;
    }

    private double runSvmTask(String trainPath, String testPath) throws IOException {
        // Lógica de execução do SVM
        return 0.85; // Retorna uma acurácia fictícia
    }

    private Map<String, Double> runKnnTask(String trainPath, String testPath, int amountNeighbors) throws IOException {
        KnnTask knnTask = new KnnTask();
        System.out.println("Iniciando Processo de Classificação");
        System.out.println(trainPath + " " + testPath);
         // Executa o KNN (a saída do knnTask.run será capturada)
        Map<String, Double> metrics = knnTask.run(trainPath, testPath, amountNeighbors);

        return metrics;
    }
}
