import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GeradorDeMassa {
    
    public static void main(String[] args) {
        String nomeArquivo = "lote_500k.csv";
        int totalLinhas = 500000;
        String[] ratings = {"A", "A", "B", "B", "C"}; 
        Random random = new Random();

        System.out.println("⏳ Gerando " + totalLinhas + " transações...");
        long tempoInicio = System.currentTimeMillis();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            for (int i = 1; i <= totalLinhas; i++) {
                String id = "TX-" + i;
                String cnpj = "11222333000" + (random.nextInt(9) + 1) + "99";
                String valor = (random.nextInt(50000) + 1000) + ".00"; 
                String rating = ratings[random.nextInt(ratings.length)];

                // Força um boleto duplicado a cada 1000 linhas
                if (i % 1000 == 0) { 
                    id = "TX-" + (i - 1); 
                }

                writer.write(id + "," + cnpj + "," + valor + "," + rating);
                writer.newLine();
            }

            long tempoFim = System.currentTimeMillis();
            File arquivo = new File(nomeArquivo);
            
            System.out.println("✅ Arquivo gerado em " + (tempoFim - tempoInicio) + "ms!");
            System.out.println("📂 SALVO EM: " + arquivo.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Erro ao gerar arquivo: " + e.getMessage());
        }
    }
}