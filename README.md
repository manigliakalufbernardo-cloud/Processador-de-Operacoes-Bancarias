# Processador-de-Operações-Bancarias
# FIDC Batch Processor 🚀

Código de processamento em lote (Batch Processing Engine) desenvolvido em Java para processar e analisar de ativos de Fundos de Investimento em Direitos Creditórios (FIDC). 

## 📌 O Desafio
Processar grandes volumes de transações financeiras aplicando regras de compliance da ANBIMA/CVM com alta performance e baixo consumo de memória.

## ⚙️ Arquitetura e Performance
- **Leitura e Escrita em Buffer (Otimização de RAM):** O arquivo bruto não é carregado na memória, o processamento e a geração dos logs de auditoria ocorrem linha a linha via streams (I/O).
- **Idempotência O(1):** Utilização de hashset para detecção e bloqueio de transações duplicadas
- **Auditoria de Dados:** Separação automática entre títulos aprovados e recusados, gravando o motivo exato da recusa no log de saída.
- **Teste de Estresse:** Capacidade de processar um lote de **500.000 transações** em aproximadamente **3 segundos**.

## 🛡️ Regras de Negócios (Compliance)
1. **Trava de Concentração:** Bloqueia exposição financeira superior a 20% do patrimônio do fundo para um mesmo CNPJ.
2. **Controle de Risco:** Limita a 15% a exposição máxima a títulos com classificação de Rating "C".
3. **Prevenção de Fraude:** Rejeita e isola qualquer `ID_TRANSACAO` já processado.

## 🚀 Como Executar
1. Compile os arquivos do projeto.
2. Execute a classe `Main`.
3. O sistema invocará o `GeradorDeMassa` para fabricar 500.000 registros locais de teste e, em seguida, acionará o `MotorAltaPerformance` para processar e gerar os relatórios de auditoria.
