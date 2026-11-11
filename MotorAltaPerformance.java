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
        
        // Regras de negocio padrao (ANBIMA / CVM)
        BigDecimal patrimonioFundo = new BigDecimal("1000000000.00"); // 1 bilhao
        BigDecimal limiteCnpj = patrimonioFundo.multiply(new BigDecimal("0.20")); // Max de 20%
        BigDecimal limiteRiscoC = patrimonioFundo.multiply(new BigDecimal("0.15")); // Max de 15%

        Set<String> idsProcessados = new HashSet<>(); 
        Map<String, BigDecimal> exposicaoCnpj = new HashMap<>(); 
        
        BigDecimal totalRiscoCAtual = BigDecimal.ZERO;
        int aprovados = 0, recusados = 0, duplicados = 0;
        BigDecimal valorTotalAprovado = BigDecimal.ZERO;

        System.out.println("Iniciando rotina de processamento batch...");
        long tempoInicio = System.currentTimeMillis();

        try (BufferedReader leitor = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            
            while ((linha = leitor.readLine()) != null) {
                String[] dados = linha.split(",");
                String id = dados[0];
                String cnpj = dados[1];
                BigDecimal valor = new BigDecimal(dados[2]);
                String rating = dados[3];

                // Bloquear Duplicados 
                if (!idsProcessados.add(id)) {
                    duplicados++;
                    continue; 
                }

                boolean elegivel = true;

                // Calculo de limite de risco
                if (rating.equals("C")) {
                    BigDecimal novaExposicaoC = totalRiscoCAtual.add(valor);
                    if (novaExposicaoC.compareTo(limiteRiscoC) > 0) {
                        elegivel = false;
                    } else {
                        totalRiscoCAtual = novaExposicaoC;
                    }
                }

                // Regra de concentracao por CNPJ
                if (elegivel) {
                    BigDecimal exposicaoAtualCnpj = exposicaoCnpj.getOrDefault(cnpj, BigDecimal.ZERO);
                    BigDecimal novaExposicaoCnpj = exposicaoAtualCnpj.add(valor);
                    
                    if (novaExposicaoCnpj.compareTo(limiteCnpj) > 0) {
                        elegivel = false;
                    } else {
                        exposicaoCnpj.put(cnpj, novaExposicaoCnpj);
                    }
                }

                if (elegivel) {
                    aprovados++;
                    valorTotalAprovado = valorTotalAprovado.add(valor);
                } else {
                    recusados++;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro fatal na execucao do lote: " + e.getMessage());
        }

        long tempoFim = System.currentTimeMillis();

        // Relatorio final de execucao
        System.out.println("\n--------------------------------------------------");
        System.out.println(" RELATORIO DE LIQUIDACAO FIDC - MODULO BATCH");
        System.out.println("--------------------------------------------------");
        System.out.println("Tempo de Execucao (ms): " + (tempoFim - tempoInicio));
        System.out.println("Total de Linhas Lidas: " + (aprovados + recusados + duplicados));
        System.out.println("Titulos Aprovados: " + aprovados);
        System.out.println("Titulos Recusados (Regra): " + recusados);
        System.out.println("Titulos Duplicados (Drop): " + duplicados);
        System.out.println("Volume Financeiro Aprovado: R$ " + valorTotalAprovado);
        System.out.println("--------------------------------------------------");
    }
}
