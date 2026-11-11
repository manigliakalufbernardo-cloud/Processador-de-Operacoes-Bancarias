import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MotorAltaPerformance {

    public static void main(String[] args) {
        String caminhoArquivo = "lote_500k.csv";
        
        // Regras do Fundo (ANBIMA / CVM)
        BigDecimal patrimonioFundo = new BigDecimal("1000000000.00"); // 1 Bilhão
        BigDecimal limiteCnpj = patrimonioFundo.multiply(new BigDecimal("0.20")); // Máx 20%
        BigDecimal limiteRiscoC = patrimonioFundo.multiply(new BigDecimal("0.15")); // Máx 15%

        // Estruturas de Memória para Alta Velocidade
        Set<String> idsProcessados = new HashSet<>(); 
        Map<String, BigDecimal> exposicaoCnpj = new HashMap<>(); 
        
        BigDecimal totalRiscoCAtual = BigDecimal.ZERO;
        int aprovados = 0, recusados = 0, duplicados = 0;
        BigDecimal valorTotalAprovado = BigDecimal.ZERO;

        System.out.println("🚀 Iniciando Motor Kanastra (Leitura em Buffer)...");
        long tempoInicio = System.currentTimeMillis();

        try (BufferedReader leitor = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            
            while ((linha = leitor.readLine()) != null) {
                String[] dados = linha.split(",");
                String id = dados[0];
                String cnpj = dados[1];
                BigDecimal valor = new BigDecimal(dados[2]);
                String rating = dados[3];

                // 1. IDEMPOTÊNCIA (Bloqueia Duplicados em tempo O(1))
                if (!idsProcessados.add(id)) {
                    duplicados++;
                    continue; 
                }

                boolean elegivel = true;

                // 2. REGRA DE RISCO C
                if (rating.equals("C")) {
                    BigDecimal novaExposicaoC = totalRiscoCAtual.add(valor);
                    if (novaExposicaoC.compareTo(limiteRiscoC) > 0) {
                        elegivel = false;
                    } else {
                        totalRiscoCAtual = novaExposicaoC;
                    }
                }

                // 3. REGRA DE CONCENTRAÇÃO CNPJ
                if (elegivel) {
                    BigDecimal exposicaoAtualCnpj = exposicaoCnpj.getOrDefault(cnpj, BigDecimal.ZERO);
                    BigDecimal novaExposicaoCnpj = exposicaoAtualCnpj.add(valor);
                    
                    if (novaExposicaoCnpj.compareTo(limiteCnpj) > 0) {
                        elegivel = false;
                    } else {
                        exposicaoCnpj.put(cnpj, novaExposicaoCnpj);
                    }
                }

                // FECHAMENTO DA TRANSAÇÃO
                if (elegivel) {
                    aprovados++;
                    valorTotalAprovado = valorTotalAprovado.add(valor);
                } else {
                    recusados++;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro fatal no processamento: " + e.getMessage());
        }

        long tempoFim = System.currentTimeMillis();

        // O DASHBOARD FINAL
        System.out.println("\n==================================================");
        System.out.println(" RELATÓRIO DE LIQUIDAÇÃO FIDC - KANASTRA ENGINE");
        System.out.println("==================================================");
        System.out.println("⏱️ Tempo de Execução: " + (tempoFim - tempoInicio) + " ms");
        System.out.println("📂 Linhas Processadas: " + (aprovados + recusados + duplicados));
        System.out.println("✅ Títulos Aprovados: " + aprovados);
        System.out.println("❌ Títulos Recusados (Regras): " + recusados);
        System.out.println("⚠️ Títulos Duplicados (Ignorados): " + duplicados);
        System.out.println("💰 Volume Financeiro Aprovado: R$ " + valorTotalAprovado);
        System.out.println("==================================================");
    }
}